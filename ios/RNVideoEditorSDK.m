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
    // Check if subscriber
    __block BOOL isSubscriber = NO;
    if ([[dictionary allKeys] containsObject:@"isSubscriber"]) {
        isSubscriber = [[dictionary objectForKey:@"isSubscriber"] boolValue];
    }

    // Check if came on subscription
    __block BOOL isCameOnSubscription = NO;
    if ([[dictionary allKeys] containsObject:@"isCameOnSubscription"]) {
        isSubscriber = [[dictionary objectForKey:@"isCameOnSubscription"] boolValue];
    }

    [self present:^PESDKMediaEditViewController * _Nullable(PESDKConfiguration * _Nonnull configuration, NSData * _Nullable serializationData) {

        PESDKPhotoEditModel *photoEditModel = [[PESDKPhotoEditModel alloc] init];

        if (isSubscriber && isCameOnSubscription) {
            // Only apply previous changes if user successfuly
            NSDictionary *stateStore = [[NSKeyedUnarchiver unarchiveObjectWithData:
                                       [self.sharedDefaults objectForKey:@"lastChanges"]] copy];

            if ([[stateStore allKeys] count] && ![[stateStore objectForKey:@"data"] isEqual:[NSNull null]]) {
                photoEditModel = [[PESDKPhotoEditModel alloc] initWithSerializedData:[NSData dataWithData:[stateStore objectForKey:@"data"]] referenceSize:CGSizeMake(1280, 720)];
            }
        } else if(isCameOnSubscription) {
            // Only apply previous changes if user successfuly
            NSDictionary *stateStore = [[NSKeyedUnarchiver unarchiveObjectWithData:
                                       [self.sharedDefaults objectForKey:@"nonPlusActivity"]] copy];

            if ([[stateStore allKeys] count] && ![[stateStore objectForKey:@"data"] isEqual:[NSNull null]]) {
                photoEditModel = [[PESDKPhotoEditModel alloc] initWithSerializedData:[NSData dataWithData:[stateStore objectForKey:@"data"]] referenceSize:CGSizeMake(1280, 720)];
            }
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
  // initialize user defaults
  self.sharedDefaults = [[NSUserDefaults alloc] initWithSuiteName:[self appGroup]];

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

-(NSString*) appGroup {
    return [NSString stringWithFormat:@"group.%@", [self appID]];
}
-(NSString*) appID {
    NSString *appID = [[[[NSBundle mainBundle] infoDictionary] objectForKey:@"CFBundleIdentifier"] stringByReplacingOccurrencesOfString:@".EditorSDKVideoExtension" withString:@""];
    NSLog(@"appID %@", appID);
    return appID != nil ? appID : @"group.com.creedon.Nixplay";
}

- (void)addTargetDiscard:(UIButton *)button {
    [button addTarget:self action:@selector(didSelectDiscard:) forControlEvents:UIControlEventTouchUpInside];
}

- (void)didSelectDiscard:(id)sender {
    self.mainController = nil;
}

- (void)undoTrigger:(id)button {
    self.needToUpgrade -= 1;
}

- (void)redoTrigger:(id)button {
    self.needToUpgrade += 1;
}

- (void)deleteTrigger:(id)button {
    // sticker: ask confirm, check if need to upgrade is 1
    if (self.needToUpgrade == 1) {
        [self resetEffectsOnExit];
        self.enableToValidate = 0;
    }
}

- (void)applyTrigger:(id)button {
    if (self.needToUpgrade) {
        [self showPromptUpgrade];
    }
}

- (void)addButtonApply:(UIButton* _Nonnull)button {
    [button addTarget:self action:@selector(applyTrigger:) forControlEvents:UIControlEventTouchUpInside];
}

- (void)showPromptUpgrade {
    dispatch_async(dispatch_get_main_queue(), ^{
      UIAlertController *alert = [UIAlertController alertControllerWithTitle:@"Unblock this feature?"
                                                                     message:@"Upgrade to Nixplay Plus now to unblock this feature and enjoy more advanced editing tools."
                                                              preferredStyle:UIAlertControllerStyleAlert];

      UIAlertAction * action = [UIAlertAction actionWithTitle:@"Upgrade"
                                                              style:UIAlertActionStyleDefault
                                                            handler:^(UIAlertAction * _Nonnull action) {
          // save last changes to user defaults for advance usage
          [self saveSerialDataWithKey:@"lastChanges"];
          [self.mainController play];
          RCTPromiseResolveBlock resolve = self.resolve;
          [self.mainController dismissViewControllerAnimated:YES completion:^{
              resolve(@{ @"action": @"open-subscription", @"path": @2 });
          }];
                                                            }];
      UIAlertAction * cancelAction = [UIAlertAction actionWithTitle:@"Cancel"
                                                              style:UIAlertActionStyleDefault
                                                            handler:^(UIAlertAction * _Nonnull action) {
          [self resetEffectsOnExit];
          [self.mainController play];
          [alert dismissViewControllerAnimated:YES completion:nil];
                                                            }];
      [alert addAction:cancelAction];
      [alert addAction:action];

      [self.mainController presentViewController:alert animated:YES completion:nil];
    });
}

- (void)resetEffectsOnExit {
    [self.mainController.undoController undo];
    [self.mainController.undoController undoStep];
    [self.mainController.undoController undoStepInCurrentGroup];
    [self.mainController.undoController removeAllActionsInCurrentGroup];
}

- (void)saveSerialDataWithKey:(NSString *)key {
    NSDictionary *state = @{ @"data" : self.mainController.serializedSettings };
    NSMutableDictionary *md = [[NSMutableDictionary alloc] initWithDictionary:state];
    [self.sharedDefaults setObject:[NSKeyedArchiver archivedDataWithRootObject:md]
                                forKey:key];
    [self.sharedDefaults synchronize];
}


- (void)addButtonTrigger:(UIButton* _Nonnull)button usage:(NSString * _Nonnull)usage {
    if ([usage isEqualToString:@"undo"]) {
        [button addTarget:self action:@selector(undoTrigger:) forControlEvents:UIControlEventTouchUpInside];
    } else if ([usage isEqualToString:@"delete"]){
        [button addTarget:self action:@selector(deleteTrigger:) forControlEvents:UIControlEventTouchUpInside];
    } else {
        [button addTarget:self action:@selector(redoTrigger:) forControlEvents:UIControlEventTouchUpInside];
    }
}

- (void)didSubscribe:(id)sender {
    [self.banner removeFromSuperview];
    self.banner = nil;
    RCTPromiseResolveBlock resolve = self.resolve;
//    RCTPromiseRejectBlock reject = self.reject;
    [self.mainController dismissViewControllerAnimated:YES completion:^{
        resolve(@{ @"action": @"open-subscription", @"path": @1 });
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
