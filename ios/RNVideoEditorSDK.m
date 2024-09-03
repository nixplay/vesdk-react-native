#import "RNVideoEditorSDK.h"
#import "RNImglyKit.h"
#import "RNImglyKitSubclass.h"
#import <AVFoundation/AVFoundation.h>

#import <Photos/Photos.h>

#import <FirebaseAnalytics/FirebaseAnalytics.h>

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

- (void)handleError:(nonnull PESDKVideoEditViewController *)videoEditViewController code:(nullable NSString *)code message:(nullable NSString *)message error:(nullable NSError *)error {
  RCTPromiseRejectBlock reject = self.reject;
  [self dismiss:videoEditViewController animated:YES completion:^{
    reject(code, message, error);
  }];
}

- (void)present:(nonnull PESDKVideo *)video withConfiguration:(nullable NSDictionary *)dictionary andSerialization:(nullable NSDictionary *)state
        resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject
{
    // Check if enable free trial
    if ([[dictionary allKeys] containsObject:@"freeTrial"]) {
        self.flagTrial = [[dictionary objectForKey:@"freeTrial"] boolValue];
    }

    if ([[dictionary allKeys] containsObject:@"tooltip"]) {
        self.flagTooltip = [[dictionary objectForKey:@"tooltip"] boolValue];
        self.tooltipShown = NO;
    }
    // alert prompt dictionary
    if ([[dictionary allKeys] containsObject:@"alertPromptInfo"]) {
        self.alertPromptInfo = (NSDictionary *)[dictionary objectForKey:@"alertPromptInfo"];
    }

    // Check if subscriber
    __block BOOL isSubscriber = NO;
    if ([[dictionary allKeys] containsObject:@"isSubscriber"]) {
        isSubscriber = [[dictionary objectForKey:@"isSubscriber"] boolValue];
    }

    // Check if came on subscription
    __block BOOL isCameOnSubscription = NO;
    if ([[dictionary allKeys] containsObject:@"isCameOnSubscription"]) {
        isCameOnSubscription = [[dictionary objectForKey:@"isCameOnSubscription"] boolValue];
    }

    // content width and height
    __block int contentWidth = 0;
    if ([[dictionary allKeys] containsObject:@"width"]) {
        contentWidth = [[dictionary objectForKey:@"width"] intValue];
    }
    __block int contentHeight = 0;
    if ([[dictionary allKeys] containsObject:@"height"]) {
        contentHeight = [[dictionary objectForKey:@"height"] intValue];
    }

    [self present:^PESDKMediaEditViewController * _Nullable(PESDKConfiguration * _Nonnull configuration, NSData * _Nullable serializationData) {
        self.uuid = [[NSUUID new] UUIDString];

        PESDKPhotoEditModel *photoEditModel = [[PESDKPhotoEditModel alloc] init];

        if (serializationData != nil) {
          PESDKDeserializationResult *deserializationResult = [PESDKDeserializer deserializeWithData:serializationData imageDimensions:video.size assetCatalog:configuration.assetCatalog];
          photoEditModel = deserializationResult.model ?: photoEditModel;
        }

        if (isSubscriber && isCameOnSubscription) {
            // Only apply previous changes if user successfuly
            NSDictionary *stateStore = [[NSKeyedUnarchiver unarchiveObjectWithData:
                                       [self.sharedDefaults objectForKey:@"lastChanges"]] copy];
            NSString *effectsUsed = [stateStore objectForKey:@"effects"];
            if (effectsUsed != nil && ![effectsUsed isEqualToString:@""]) {
                [FIRAnalytics logEventWithName:[NSString stringWithFormat:@"unblock_feat_v_%@_buy", effectsUsed] parameters:@{}];
            }
            if ([[stateStore allKeys] count] && ![[stateStore objectForKey:@"data"] isEqual:[NSNull null]]) {
                photoEditModel = [[PESDKPhotoEditModel alloc] initWithSerializedData:[NSData dataWithData:[stateStore objectForKey:@"data"]] referenceSize:CGSizeMake(contentWidth, contentHeight)];
            }
        } else if(isCameOnSubscription) {
            // Only apply previous changes if user successfuly
            NSDictionary *stateStore = [[NSKeyedUnarchiver unarchiveObjectWithData:
                                       [self.sharedDefaults objectForKey:@"nonPlusActivity"]] copy];

            if ([[stateStore allKeys] count] && ![[stateStore objectForKey:@"data"] isEqual:[NSNull null]]) {
                photoEditModel = [[PESDKPhotoEditModel alloc] initWithSerializedData:[NSData dataWithData:[stateStore objectForKey:@"data"]] referenceSize:CGSizeMake(contentWidth, contentHeight)];
            }
        }

	// commented out. this will be automatically replaced on update of the dictionary
		/*
        [VESDK setLocalizationDictionary:@{
          @"en": @{
                  @"pesdk_editor_title_discardChangesAlert": @"Discard Changes?",
                  @"pesdk_common_button_cancel": @"Cancel",
                  @"pesdk_editor_button_discardChanges": @"Discard",
                  @"pesdk_editor_text_discardChangesAlert": @"If you go back now, your edits will be discarded.",
          }
        }];
		*/

        [VESDK setBundleImageBlock:^UIImage * _Nullable(NSString * _Nonnull imageName) {
            if ([imageName isEqualToString:@"imgly_icon_save"]) {
                return [UIImage imageNamed:@"ic_approve"];
            }
            return nil;
        }];

        self.currentEffects = [[NSString alloc] init];
        self.mainController = [PESDKVideoEditViewController videoEditViewControllerWithVideoAsset:video configuration:configuration photoEditModel:photoEditModel];
        self.mainController.modalPresentationStyle = UIModalPresentationFullScreen;
        self.mainController.delegate = self;
        RNVESDKWillPresentBlock willPresentVideoEditViewController = RNVideoEditorSDK.willPresentVideoEditViewController;
        if (willPresentVideoEditViewController != nil) {
          willPresentVideoEditViewController(self.mainController);
        }
        if (self.flagTooltip && self.freeTrial) {
            CGRect mRect = [UIScreen mainScreen].bounds;
            UIButton *button = [UIButton buttonWithType:UIButtonTypeCustom];
            button.frame = CGRectMake(0, 0, mRect.size.width, mRect.size.height);
            [button addTarget:self action:@selector(hideTooltip:) forControlEvents:UIControlEventTouchUpInside];
            [self.mainController.view addSubview:button];
            [self.mainController.view bringSubviewToFront:button];
        }
        return self.mainController;

        } withUTI:^CFStringRef _Nonnull(PESDKConfiguration * _Nonnull configuration) {

        return configuration.videoEditViewControllerOptions.videoContainerFormatUTI;

    } configuration:dictionary serialization:state controller:self resolve:resolve reject:reject];
}

RCT_EXPORT_METHOD(unlockWithLicenseURL:(nonnull NSURL *)url resolve:(nonnull RCTPromiseResolveBlock)resolve reject:(nonnull RCTPromiseRejectBlock)reject)
{
  dispatch_async(dispatch_get_main_queue(), ^{
    NSError *error = nil;
    [VESDK unlockWithLicenseFromURL:url error:&error];
    [self handleLicenseError:error resolve:resolve reject:reject];
  });
}

RCT_EXPORT_METHOD(unlockWithLicenseString:(nonnull NSString *)string resolve:(nonnull RCTPromiseResolveBlock)resolve reject:(nonnull RCTPromiseRejectBlock)reject)
{
  dispatch_async(dispatch_get_main_queue(), ^{
    NSError *error = nil;
    [VESDK unlockWithLicenseFromString:string error:&error];
    [self handleLicenseError:error resolve:resolve reject:reject];
  });
}

RCT_EXPORT_METHOD(unlockWithLicenseObject:(nonnull NSDictionary *)dictionary resolve:(nonnull RCTPromiseResolveBlock)resolve reject:(nonnull RCTPromiseRejectBlock)reject)
{
  dispatch_async(dispatch_get_main_queue(), ^{
    NSError *error = nil;
    [VESDK unlockWithLicenseFromDictionary:dictionary error:&error];
    [self handleLicenseError:error resolve:resolve reject:reject];
  });
}

RCT_EXPORT_METHOD(unlockWithLicense:(nonnull id)json resolve:(nonnull RCTPromiseResolveBlock)resolve reject:(nonnull RCTPromiseRejectBlock)reject)
{
  [super unlockWithLicense:json resolve:resolve reject:reject];
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
  if (![self isValidURL:request.URL]) {
    reject(RN_IMGLY.kErrorUnableToLoad, @"File does not exist", nil);
    return;
  }
  PESDKVideo *video = [[PESDKVideo alloc] initWithURL:request.URL];
  [self present:video withConfiguration:configuration andSerialization:state resolve:resolve reject:reject];
}

RCT_EXPORT_METHOD(presentVideoSegments:(nonnull NSArray<NSDictionary *> *)requests
                  configuration:(nullable NSDictionary *)configuration
                  serialization:(nullable NSDictionary *)state
                  videoSize:(CGSize)videoSize
                  resolve:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject)
{
  [self openEditorFromVideos:requests videoSize:videoSize configuration:configuration serialization:state resolve:resolve reject:reject];
}

RCT_EXPORT_METHOD(presentComposition:(nonnull RN_IMGLY_URLRequestArray *)requests
                  configuration:(nullable NSDictionary *)configuration
                  serialization:(nullable NSDictionary *)state
                  videoSize:(CGSize)videoSize
                  resolve:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject)
{
  [self openEditorFromVideos:requests videoSize:videoSize configuration:configuration serialization:state resolve:resolve reject:reject];
}

- (void)openEditorFromVideos:(nonnull NSArray *)videos videoSize:(CGSize)videoSize configuration:(nullable NSDictionary *)configuration serialization:(nullable NSDictionary *)state resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock) reject
{
  NSMutableArray<PESDKVideoSegment *> *segments = [NSMutableArray<PESDKVideoSegment *> new];

  for (id video in videos) {
    if ([video isKindOfClass:[NSURLRequest class]]) {
      NSURLRequest *request = video;
      if (![self isValidURL:request.URL]) {
        reject(RN_IMGLY.kErrorUnableToLoad, @"File does not exist", nil);
        return;
      }
      PESDKVideoSegment *videoSegment = [[PESDKVideoSegment alloc] initWithURL:request.URL];
      [segments addObject: videoSegment];
    } else {
      NSURLRequest *request = [RCTConvert NSURLRequest:video];
      if (request == nil) {
        // Segment.
        NSURLRequest *segmentRequest = [RCTConvert NSURLRequest:[video valueForKey:@"videoURI"]];
        NSNumber *startTime;
        NSNumber *endTime;

        id start = [video valueForKey:@"startTime"];
        id end = [video valueForKey:@"endTime"];

        if (![start isKindOfClass:[NSNull class]]) {
          startTime = [RCTConvert NSNumber:start];
        }
        if (![end isKindOfClass:[NSNull class]]) {
          endTime = [RCTConvert NSNumber:end];
        }

        if (![self isValidURL:segmentRequest.URL]) {
          reject(RN_IMGLY.kErrorUnableToLoad, @"File does not exist", nil);
          return;
        }
        PESDKVideoSegment *videoSegment = [[PESDKVideoSegment alloc] initWithURL:segmentRequest.URL startTime:startTime endTime:endTime];
        [segments addObject: videoSegment];
      } else {
        if (![self isValidURL:request.URL]) {
          reject(RN_IMGLY.kErrorUnableToLoad, @"File does not exist", nil);
          return;
        }
        PESDKVideoSegment *videoSegment = [[PESDKVideoSegment alloc] initWithURL:request.URL];
        [segments addObject: videoSegment];
      }
    }
  }

  PESDKVideo *video;

  if (CGSizeEqualToSize(videoSize, CGSizeZero)) {
    if (segments.count == 0) {
      RCTLogError(@"A video without assets must have a specific size.");
      reject(RN_IMGLY.kErrorUnableToLoad, @"The editor requires a valid size when initialized without a video.", nil);
      return;
    }
    video = [[PESDKVideo alloc] initWithSegments:segments];
  } else {
    if (videoSize.height <= 0 || videoSize.width <= 0) {
      RCTLogError(@"Invalid video size: width and height must be greater than zero");
      reject(RN_IMGLY.kErrorUnableToLoad, @"Invalid video size: width and height must be greater than zero", nil);
      return;
    }
    if (segments.count == 0) {
      video = [[PESDKVideo alloc] initWithSize:videoSize];
    }
    video = [[PESDKVideo alloc] initWithSegments:segments size:videoSize];
  }

  [self present:video withConfiguration:configuration andSerialization:state resolve:resolve reject:reject];
}

- (BOOL)isValidURL:(nonnull NSURL*)url {
  if (url.isFileURL) {
    if (![[NSFileManager defaultManager] fileExistsAtPath:url.path]) {
      return false;
    }
  }
  return true;
}

- (NSArray<NSDictionary *> *)serializeVideoSegments:(nonnull NSArray<PESDKVideoSegment *> *)segments {
  NSMutableArray<NSDictionary *> *videoSegments = [NSMutableArray<NSDictionary *> new];
  for (PESDKVideoSegment *segment in segments) {
    NSDictionary *videoSegment = @{
      @"videoURI": segment.url.absoluteString,
      @"startTime": (segment.startTime != nil) ? segment.startTime : [NSNull null],
      @"endTime": (segment.endTime != nil) ? segment.endTime : [NSNull null]
    };
    [videoSegments addObject: videoSegment];
  }
  return [videoSegments copy];
}

RCT_EXPORT_METHOD(updateLanguage:(NSString*)languageCode)
{
    NSString *needle = [languageCode lowercaseString];
	self.languageCode = needle;
    NSString *resourceName = @"ImglyEN";
    if ([needle isEqualToString:@"de"]) {
        resourceName=@"ImglyDE";
    } else if ([needle isEqualToString:@"es"]) {
        resourceName=@"ImglyES";
    } else if ([needle isEqualToString:@"fr"]) {
        resourceName=@"ImglyFR";
    } else if ([needle isEqualToString:@"it"]) {
        resourceName=@"ImglyIT";
    } else if ([needle isEqualToString:@"pt"]) {
        resourceName=@"ImglyPT";
    } else if ([needle isEqualToString:@"ja"]) {
        resourceName=@"ImglyJA";
    } else {
        //always default to english
        resourceName=@"ImglyEN";
    }
                
	NSBundle *bundle = [NSBundle mainBundle]; //query mainbundle
    NSString* path = [bundle pathForResource:resourceName ofType:@"plist"];
    NSDictionary *d = [[NSDictionary alloc] initWithContentsOfFile:path];
    
    // set clamp tooltip name for adjust
    self.clampTooltip = [NSString stringWithFormat:@"%@", [d objectForKey:@"pesdk_adjustments_title_name"]];

    // sometimes languagePreferred is not the phone language, use languageCurrent
    NSString *languagePreferred = [[NSLocale preferredLanguages] objectAtIndex:0];
    NSString *languagePreferredCode = [[NSLocale componentsFromLocaleIdentifier:languagePreferred] objectForKey:NSLocaleLanguageCode];
    NSString *languageCurrentCode = @"en";
    if (@available(iOS 10.0, *)) {
        languageCurrentCode = [[NSLocale currentLocale] languageCode];
    }

    if (d) {
        if ([languagePreferredCode isEqualToString:languageCurrentCode]) {
            @try {
                [PESDK setLocalizationDictionary: @{
                    languagePreferredCode: d
                }];
                
            } @catch (NSException *exception) {
                NSLog(@"Error setting localization dictionary");
            };
        } else {
            @try {
                [PESDK setLocalizationDictionary: @{
                    languagePreferredCode: d
                }];
                
                [PESDK setLocalizationDictionary: @{
                    languageCurrentCode: d
                }];
                
            } @catch (NSException *exception) {
                NSLog(@"Error setting localization dictionary");
            };
        }
    }
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
    [self resetEffectsOnExit:@"discard"];
    self.enableToValidate = 0;
}

- (void)applyTrigger:(id)button {
    if (self.needToUpgrade) {
        [self showPromptUpgrade:@"apply"];
    } else {
        [self resetEffectsOnExit:@"discard"];
    }
}

- (void)discardTrigger:(id)button {
    [self resetEffectsOnExit:@"discard"];
}

- (void)addButtonApply:(UIButton* _Nonnull)button {
    [button addTarget:self action:@selector(applyTrigger:) forControlEvents:UIControlEventTouchUpInside];
}

- (void)addButtonDiscard:(UIButton* _Nonnull)button {
    [button addTarget:self action:@selector(discardTrigger:) forControlEvents:UIControlEventTouchUpInside];
}

- (void)showPromptUpgrade:(NSString * _Nonnull)key {
    [self.mainController pause];
    if (self.currentEffects != nil && ![self.currentEffects isEqualToString:@""]) {
        [FIRAnalytics logEventWithName:[NSString stringWithFormat:@"unblock_feat_v_%@_show", self.currentEffects] parameters:@{}];
    }
    dispatch_async(dispatch_get_main_queue(), ^{
      UIAlertController *alert = [UIAlertController alertControllerWithTitle:[self.alertPromptInfo objectForKey:@"title"]
                                                                     message:[self.alertPromptInfo objectForKey:@"message"]
                                                              preferredStyle:UIAlertControllerStyleAlert];

      UIAlertAction * action = [UIAlertAction actionWithTitle:[self.alertPromptInfo objectForKey:@"upgrade"]
                                                              style:UIAlertActionStyleDefault
                                                            handler:^(UIAlertAction * _Nonnull action) {
          if (self.currentEffects != nil && ![self.currentEffects isEqualToString:@""]) {
              [FIRAnalytics logEventWithName:[NSString stringWithFormat:@"unblock_feat_v_%@_upgrade", self.currentEffects] parameters:@{}];
          }
          // save last changes to user defaults for advance usage
          [self saveSerialDataWithKey:@"lastChanges"];
          RCTPromiseResolveBlock resolve = self.resolve;
          [self.mainController dismissViewControllerAnimated:YES completion:^{
              resolve(@{ @"action": @"open-subscription", @"path": @2 });
          }];
                                                            }];
      [alert addAction:action];

      UIAlertAction * cancelAction = [UIAlertAction actionWithTitle:[self.alertPromptInfo objectForKey:@"cancel"]
                                                              style:UIAlertActionStyleDefault
                                                            handler:^(UIAlertAction * _Nonnull action) {
          [self resetEffectsOnExit:key];
          [self.mainController play];
          [alert dismissViewControllerAnimated:YES completion:nil];          
                                                            }];

      [alert addAction:cancelAction];

      [self.mainController presentViewController:alert animated:YES completion:nil];
    });
}

- (void)resetEffectsOnExit:(NSString * _Nonnull)key {
    if ([key isEqualToString:@"apply"] ||
        [key isEqualToString:@"discard"] ||
        [key isEqualToString:@"nixText"] ||
        [key isEqualToString:@"nixFrame"] ||
        [key isEqualToString:@"nixFrame"] ||
        [key isEqualToString:@"nixTextDesign"]) {
        for (int i = 0; i < 20; i++) {
            [self.mainController.undoController undoStep];
        }
        [self.mainController.undoController undoGroup];
        [self.mainController.undoController undoStepInCurrentGroup];
        [self.mainController.undoController undoAllInCurrentGroup];
        [self.mainController.undoController removeAllActionsInCurrentGroup];
        [self.mainController.undoController removeAllActions];
    } else {
        [self.mainController.undoController undoStep];
        [self.mainController.undoController removeAllActionsInCurrentGroup];
    }
    self.needToUpgrade = 0;
    self.textAdded = 0;
    self.hasBegan = NO;
    if (self.banner != nil) {
        [self.banner removeFromSuperview];
        self.banner = nil;
    }
}

- (void)saveSerialDataWithKey:(NSString *)key {
    NSDictionary *state = @{ @"data" : self.mainController.serializedSettings, @"effects": self.currentEffects };
    NSMutableDictionary *md = [[NSMutableDictionary alloc] initWithDictionary:state];
	if(md) {
		[self.sharedDefaults setObject:[NSKeyedArchiver archivedDataWithRootObject:md]
									forKey:key];
		[self.sharedDefaults synchronize];
	}
}

- (void)trackEvent:(NSString *)key {
    [FIRAnalytics logEventWithName:[NSString stringWithFormat:@"vesdk_%@", key] parameters:@{}];
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

- (void)addTooltip:(UIView*)target {
    if (self.flagTrial && self.flagTooltip && !self.tooltipShown) {
        self.tooltipShown = YES;
        RCEasyTipPreferences *preferences = [[RCEasyTipPreferences alloc] initWithDefaultPreferences];
            preferences.drawing.backgroundColor = UIColorFromRGB(0x08829D);
            preferences.drawing.arrowPostion = Top;
            preferences.drawing.font = [UIFont fontWithName:@"NotoSans-SemiBold" size:16.0f];
            preferences.drawing.textAlignment = NSTextAlignmentLeft;
            preferences.animating.showDuration = 1.5;
            preferences.animating.dismissDuration = 0;
            preferences.animating.dismissTransform = CGAffineTransformMakeTranslation(0, -15);
            preferences.animating.showInitialTransform = CGAffineTransformMakeTranslation(0, -15);
            preferences.drawing.cornerRadius = 8.0f;
            preferences.positioning.maxWidth = 265;

        self.tooltipView = [[RCEasyTipView alloc] initWithPreferences:preferences];
        self.tooltipView.frame = CGRectMake(0, 0, 265, 70);
        self.tooltipView.text = [NSString stringWithFormat:@"%@", [self.alertPromptInfo objectForKey:@"tooltip"]];
        [self.tooltipView showAnimated:YES forView:target withinSuperView:[self.mainController view]];
    }
}

- (void)hideTooltip:(UIButton *)button {
    if (self.tooltipShown) {
        [button removeFromSuperview];
        [self.tooltipView dismissWithCompletion:^{}];
    }
}

- (void)addBanner:(NSString *)nixTitle subtitle:(NSString *)nixSubtitle {
    if (!self.flagTrial) {
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
              self.banner.backgroundColor = UIColorFromRGB(0x08829D);
              self.banner.frame = CGRectMake(0, 0, frame.size.width, (80 + additionalHeight));

              CAGradientLayer *gradient = [CAGradientLayer layer];
              gradient.frame = self.banner.bounds;
              gradient.colors = @[(id)UIColorFromRGB(0x08829D).CGColor, (id)UIColorFromRGB(0x08829D).CGColor, (id)UIColorFromRGB(0x08829D).CGColor];
              gradient.startPoint = CGPointMake(0.10, 0.10);
              gradient.endPoint = CGPointMake(1.0, 0.8);

              [self.banner.layer insertSublayer:gradient atIndex:0];

              // label top
              UILabel *title = [[UILabel alloc] initWithFrame:CGRectMake(20, (25 + additionalHeight), frame.size.width - 60, 25)];
              [title setAdjustsFontSizeToFitWidth:TRUE];
              [title setBaselineAdjustment:UIBaselineAdjustmentNone];
              [title setMinimumScaleFactor:0.7];
              [title setFont:[UIFont fontWithName:@"Helvetica-Bold" size:16.0]];
              [title setTextColor:[UIColor whiteColor]];
              [title setText:nixTitle];
              [self.banner addSubview:title];

              // label bottom
               UILabel *subtitle = [[UILabel alloc] initWithFrame:CGRectMake(20, (42 + additionalHeight), frame.size.width - 60, 25)];
              [subtitle setAdjustsFontSizeToFitWidth:TRUE];
              [subtitle setBaselineAdjustment:UIBaselineAdjustmentNone];
              [subtitle setMinimumScaleFactor:0.6];
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
}

- (void)removeBanner {
    if (!self.freeTrial) {
        [self.banner removeFromSuperview];
        self.banner = nil;
    }
}

- (void)presentToolWithName:(NSString *)toolName icon:(NSString *)icon class:(Class)class {
    PESDKToolMenuItem *menuItem =  [[PESDKToolMenuItem alloc] initWithTitle:toolName icon:[UIImage imageNamed:icon] toolControllerClass:class supportsPhoto:YES supportsVideo:YES supportsSingleToolUsage:YES];
    [self.mainController presentToolFor:menuItem];
}

#pragma mark - PESDKVideoEditViewControllerDelegate

- (void)videoEditViewControllerDidFinish:(nonnull PESDKVideoEditViewController *)videoEditViewController result:(nonnull PESDKVideoEditorResult *)result {
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

    // save to album photos
    __block NSURL *movieUrl = result.output.url;
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
                        NSArray<NSDictionary *> *segments;

                        if (self.exportVideoSegments) {
                            segments = [self serializeVideoSegments:result.task.video.segments];
                        }
                        resolve(@{ @"video": (result.output.url != nil) ? result.output.url.absoluteString : [NSNull null],
                                   @"hasChanges": @(result.status == VESDKVideoEditorStatusRenderedWithChanges),
                                   @"serialization": (serialization != nil) ? serialization : [NSNull null],
                                   @"phasset": [NSString stringWithFormat:@"ph://%@", placeholder.localIdentifier],
                                   @"segments": (segments != nil) ? segments : [NSNull null],
                                   @"videoSize": @{@"height": @(result.task.video.size.height), @"width": @(result.task.video.size.height)},
                                   @"identifier": self.uuid
                                });
                      } else {
                        [self handleError:videoEditViewController code:RN_IMGLY.kErrorUnableToExport message:[NSString RN_IMGLY_string:@"Unable to export video or serialization." withError:error] error:error];
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

- (void)videoEditViewControllerDidFail:(nonnull PESDKVideoEditViewController *)videoEditViewController error:(PESDKVideoEditorError *)error {
  [self handleError:videoEditViewController code:RN_IMGLY.kErrorUnableToExport message:@"Unable to generate video" error:error];
}

@end
