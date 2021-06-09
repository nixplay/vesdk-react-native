#import "RNVideoEditorSDK.h"
#import "RNImglyKit.h"
#import "RNImglyKitSubclass.h"

#import <Photos/Photos.h>

#define UIColorFromRGB(rgbValue) [UIColor colorWithRed:((float)((rgbValue & 0xFF0000) >> 16))/255.0 green:((float)((rgbValue & 0xFF00) >> 8))/255.0 blue:((float)(rgbValue & 0xFF))/255.0 alpha:1.0]

@interface RNVideoEditorSDK () <PESDKVideoEditViewControllerDelegate>

@end

@implementation RNVideoEditorSDK

RCT_EXPORT_MODULE();

+ (RNVESDKConfigurationBlock)configureWithBuilder {
  return RN_IMGLY_ImglyKit.configureWithBuilder;
}

+ (void)setConfigureWithBuilder:(RNVESDKConfigurationBlock)configurationBlock {
  RN_IMGLY_ImglyKit.configureWithBuilder = configurationBlock;
}

static RNVESDKWillPresentBlock _willPresentVideoEditViewController = nil;

+ (RNVESDKWillPresentBlock)willPresentVideoEditViewController {
  return _willPresentVideoEditViewController;
}

+ (void)setWillPresentVideoEditViewController:(RNVESDKWillPresentBlock)willPresentBlock {
  _willPresentVideoEditViewController = willPresentBlock;
}

- (void)present:(nonnull PESDKVideo *)video withConfiguration:(nullable NSDictionary *)dictionary andSerialization:(nullable NSDictionary *)state
        resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject
{
  [self present:^PESDKMediaEditViewController * _Nullable(PESDKConfiguration * _Nonnull configuration, NSData * _Nullable serializationData) {

    PESDKPhotoEditModel *photoEditModel = [[PESDKPhotoEditModel alloc] init];

    if (serializationData != nil) {
      PESDKDeserializationResult *deserializationResult = [PESDKDeserializer deserializeWithData:serializationData imageDimensions:video.size assetCatalog:configuration.assetCatalog];
      photoEditModel = deserializationResult.model ?: photoEditModel;
    }

    [VESDK setBundleImageBlock:^UIImage * _Nullable(NSString * _Nonnull imageName) {
        if ([imageName isEqualToString:@"imgly_icon_save"]) {
            return [UIImage imageNamed:@"ic_approve"];
        }
        return nil;
    }];

    self.mainController = [PESDKVideoEditViewController videoEditViewControllerWithVideoAsset:video configuration:configuration photoEditModel:photoEditModel];
    self.mainController.modalPresentationStyle = UIModalPresentationFullScreen;
    self.mainController.delegate = self;
    RNVESDKWillPresentBlock willPresentVideoEditViewController = RNVideoEditorSDK.willPresentVideoEditViewController;
    if (willPresentVideoEditViewController != nil) {
      willPresentVideoEditViewController(self.mainController);
    }
    return self.mainController;

  } withUTI:^CFStringRef _Nonnull(PESDKConfiguration * _Nonnull configuration) {

    return configuration.videoEditViewControllerOptions.videoContainerFormatUTI;

  } configuration:dictionary serialization:state controller:self resolve:resolve reject:reject];
}

RCT_EXPORT_METHOD(unlockWithLicenseURL:(nonnull NSURL *)url)
{
  dispatch_async(dispatch_get_main_queue(), ^{
    NSError *error = nil;
    [VESDK unlockWithLicenseFromURL:url error:&error];
    [self handleLicenseError:error];
  });
}

RCT_EXPORT_METHOD(unlockWithLicenseString:(nonnull NSString *)string)
{
  dispatch_async(dispatch_get_main_queue(), ^{
    NSError *error = nil;
    [VESDK unlockWithLicenseFromString:string error:&error];
    [self handleLicenseError:error];
  });
}

RCT_EXPORT_METHOD(unlockWithLicenseObject:(nonnull NSDictionary *)dictionary)
{
  dispatch_async(dispatch_get_main_queue(), ^{
    NSError *error = nil;
    [VESDK unlockWithLicenseFromDictionary:dictionary error:&error];
    [self handleLicenseError:error];
  });
}

RCT_EXPORT_METHOD(unlockWithLicense:(nonnull id)json)
{
  [super unlockWithLicense:json];
}

RCT_EXPORT_METHOD(present:(nonnull NSURLRequest *)request
                  configuration:(nullable NSDictionary *)configuration
                  serialization:(nullable NSDictionary *)state
                  resolve:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject)
{
  // TODO: Handle React Native URLs from camera roll.
  if (request.URL.isFileURL) {
    if (![[NSFileManager defaultManager] fileExistsAtPath:request.URL.path]) {
      reject(RN_IMGLY.kErrorUnableToLoad, @"File does not exist", nil);
      return;
    }
  }
  PESDKVideo *video = [[PESDKVideo alloc] initWithURL:request.URL];
  [self present:video withConfiguration:configuration andSerialization:state resolve:resolve reject:reject];
}

#pragma mark - Nixplay Customization

- (void)addTargetDiscard:(UIButton *)button {
    [button addTarget:self action:@selector(didSelectDiscard:) forControlEvents:UIControlEventTouchUpInside];
}

- (void)didSelectDiscard:(id)sender {
    self.mainController = nil;
}

- (void)didSubscribe:(id)sender {
    [self.banner removeFromSuperview];
    self.banner = nil;
    RCTPromiseResolveBlock resolve = self.resolve;
//    RCTPromiseRejectBlock reject = self.reject;
    [self.mainController dismissViewControllerAnimated:YES completion:^{
        resolve(@{ @"action": @"open-subscription" });
    }];
}

- (void)addBanner:(NSString *)nixTitle subtitle:(NSString *)nixSubtitle {
    dispatch_async(dispatch_get_main_queue(), ^{
        int additionalHeight = 0;
        if (@available( iOS 11.0, * )) {
            if ([[[UIApplication sharedApplication] keyWindow] safeAreaInsets].bottom > 0) {
              additionalHeight = 10;
            }
        }
        if (self.banner == nil) {
          CGRect frame = [UIScreen mainScreen].bounds;

          self.banner = [[UIView alloc] init];
          self.banner.backgroundColor = UIColorFromRGB(0x4A90E2);
          self.banner.frame = CGRectMake(0, 0, frame.size.width, (80 + additionalHeight));

          CAGradientLayer *gradient = [CAGradientLayer layer];
          gradient.frame = self.banner.bounds;
          gradient.colors = @[(id)UIColorFromRGB(0x80C3F3).CGColor, (id)UIColorFromRGB(0x4A90E2).CGColor, (id)UIColorFromRGB(0x4A90E2).CGColor];
          gradient.startPoint = CGPointMake(0.10, 0.10);
          gradient.endPoint = CGPointMake(1.0, 0.8);

          [self.banner.layer insertSublayer:gradient atIndex:0];

          // label top
          UILabel *title = [[UILabel alloc] initWithFrame:CGRectMake(20, (25 + additionalHeight), frame.size.width - 16, 25)];
          [title setFont:[UIFont fontWithName:@"Helvetica-Bold" size:16.0]];
          [title setTextColor:[UIColor whiteColor]];
          [title setText:nixTitle];
          [self.banner addSubview:title];

          // label bottom
          UILabel *subtitle = [[UILabel alloc] initWithFrame:CGRectMake(20, (42 + additionalHeight), frame.size.width - 16, 25)];
          [subtitle setFont:[UIFont fontWithName:@"Helvetica" size:13.0]];
          [subtitle setText:nixSubtitle];
          [subtitle setTextColor:[UIColor whiteColor]];
          [self.banner addSubview:subtitle];

          // chevron-forward icon
          UIImageView *imgView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"ic-arrow-right"]];
          imgView.frame = CGRectMake(frame.size.width - 40, (35 + additionalHeight), 20, 20);
          [self.banner addSubview:imgView];

          UIButton *btn = [UIButton buttonWithType:UIButtonTypeRoundedRect];
          [btn addTarget:self action:@selector(didSubscribe:) forControlEvents:UIControlEventTouchUpInside];
          [btn setFrame:CGRectMake(0, 0, frame.size.width, frame.size.height)];
          [self.banner addSubview:btn];
          [self.mainController.view addSubview:self.banner];
        }
    });
}

- (void)presentToolWithName:(NSString *)toolName icon:(NSString *)icon class:(Class)class {
    PESDKToolMenuItem *menuItem =  [[PESDKToolMenuItem alloc] initWithTitle:toolName icon:[UIImage imageNamed:icon] toolControllerClass:class supportsPhoto:YES supportsVideo:YES];
    [self.mainController presentToolFor:menuItem];
}

#pragma mark - PESDKVideoEditViewControllerDelegate

 - (void)videoEditViewController:(nonnull PESDKVideoEditViewController *)videoEditViewController didFinishWithVideoAtURL:(nullable NSURL *)url {

    NSError *error = nil;
    id serialization = nil;

    if (self.serializationEnabled)
    {
      NSData *serializationData = [videoEditViewController serializedSettings];
      if ([self.serializationType isEqualToString:RN_IMGLY.kExportTypeFileURL]) {
        if ([serializationData RN_IMGLY_writeToURL:self.serializationFile andCreateDirectoryIfNecessary:YES error:&error]) {
          serialization = self.serializationFile.absoluteString;
        }
      } else if ([self.serializationType isEqualToString:RN_IMGLY.kExportTypeObject]) {
        serialization = [NSJSONSerialization JSONObjectWithData:serializationData options:kNilOptions error:&error];
      }
    }

    RCTPromiseResolveBlock resolve = self.resolve;
    RCTPromiseRejectBlock reject = self.reject;

    // save to album photos
    __block NSURL *movieUrl = url;
    dispatch_semaphore_t sema = dispatch_semaphore_create(0);

    if ([PHObject class]) {
        __block PHAssetChangeRequest *assetRequest;
        __block PHObjectPlaceholder *placeholder;
        // Save to the album
        [PHPhotoLibrary requestAuthorization:^(PHAuthorizationStatus status) {

            [[PHPhotoLibrary sharedPhotoLibrary] performChanges:^{

                assetRequest = [PHAssetChangeRequest creationRequestForAssetFromVideoAtFileURL:movieUrl];
                placeholder = [assetRequest placeholderForCreatedAsset];
            } completionHandler:^(BOOL success, NSError *error) {
                if (success) {
                    [self dismiss:videoEditViewController animated:YES completion:^{
                      if (error == nil) {
                        resolve(@{ @"video": (url != nil) ? url.absoluteString : [NSNull null],
                                   @"hasChanges": @(videoEditViewController.hasChanges),
                                   @"serialization": (serialization != nil) ? serialization : [NSNull null],
                                   @"phasset": [NSString stringWithFormat:@"ph://%@", placeholder.localIdentifier],
                                });
                      } else {
                        reject(RN_IMGLY.kErrorUnableToExport, [NSString RN_IMGLY_string:@"Unable to export video or serialization." withError:error], error);
                      }
                    }];

                    dispatch_semaphore_signal(sema);
                }
                else {
                    NSLog(@"%@", error);
                    dispatch_semaphore_signal(sema);
                }
            }];
        }];
    }
 }

- (void)videoEditViewControllerDidCancel:(nonnull PESDKVideoEditViewController *)videoEditViewController {
  RCTPromiseResolveBlock resolve = self.resolve;
  [self dismiss:videoEditViewController animated:YES completion:^{
    resolve([NSNull null]);
  }];
}

- (void)videoEditViewControllerDidFailToGenerateVideo:(nonnull PESDKVideoEditViewController *)videoEditViewController {
  RCTPromiseRejectBlock reject = self.reject;
  [self dismiss:videoEditViewController animated:YES completion:^{
    reject(RN_IMGLY.kErrorUnableToExport, @"Unable to generate video", nil);
  }];
}

@end
