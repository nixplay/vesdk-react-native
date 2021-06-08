#import "RNImglyKit.h"
#import "RNVideoEditorSDK.h"
#import "RNImglyKitSubclass.h"

#define RN_IMGLY_DEBUG 0

NSString *const kNixVSubscriberState = @"isSubscriber";
NSString *const kNixVToolFilterDefault = @"filter.categories";
NSString *const kNixVToolFilter = @"nixFilter.list";
NSString *const kNixVToolAdjust = @"nixAdjust.list";
NSString *const kNixVToolFocus = @"nixFocus.list";
NSString *const kNixVToolSticker = @"nixSticker.list";
NSString *const kNixVToolText = @"nixText.list";
NSString *const kNixVToolTextDesign = @"nixTextDesign.list";
NSString *const kNixVToolOverlay = @"nixOverlay.list";
NSString *const kNixVToolFrame = @"nixFrame.list";
NSString *const kNixVTopTools = @"parentTools";

@implementation RN_IMGLY_ImglyKit

static IMGLYConfigurationBlock _configureWithBuilder = nil;

+ (IMGLYConfigurationBlock)configureWithBuilder {
  return _configureWithBuilder;
}

+ (void)setConfigureWithBuilder:(IMGLYConfigurationBlock)configurationBlock {
  _configureWithBuilder = configurationBlock;
}

const struct RN_IMGLY_Constants RN_IMGLY = {
  .kErrorUnableToUnlock = @"E_UNABLE_TO_UNLOCK",
  .kErrorUnableToLoad = @"E_UNABLE_TO_LOAD",
  .kErrorUnableToExport = @"E_UNABLE_TO_EXPORT",

  .kExportTypeFileURL = @"file-url",
  .kExportTypeDataURL = @"data-url",
  .kExportTypeObject = @"object"
};

- (void)dealloc {
  [self dismiss:self.mediaEditViewController animated:NO completion:NULL];
}

- (void)present:(nonnull IMGLYMediaEditViewControllerBlock)createMediaEditViewController withUTI:(nonnull IMGLYUTIBlock)getUTI
  configuration:(nullable NSDictionary *)dictionary serialization:(nullable NSDictionary *)state controller:(RNVideoEditorSDK *)controller
        resolve:(nonnull RCTPromiseResolveBlock)resolve reject:(nonnull RCTPromiseRejectBlock)reject
{
#if RN_IMGLY_DEBUG
  {
    // For release debugging
    NSURL *debugURL = [RCTConvert RN_IMGLY_ExportFileURL:@"imgly-debug" withExpectedUTI:kUTTypeJSON];
    if (debugURL) {
      NSError *error = nil;
      NSJSONWritingOptions debugOptions = NSJSONWritingPrettyPrinted;
      if (@available(iOS 11.0, *)) { debugOptions = debugOptions | NSJSONWritingSortedKeys; }
      NSData *debugData = [NSJSONSerialization dataWithJSONObject:dictionary options:debugOptions error:&error];
      [debugData RN_IMGLY_writeToURL:debugURL andCreateDirectoryIfNecessary:YES error:&error];
      if (error != nil) {
        NSLog(@"Could not write debug configuration: %@", error);
      } else {
        NSLog(@"Wrote debug configuration to URL: %@", debugURL);
      }
    }
  }
#endif

  __block NSError *error = nil;
  NSData *serializationData = nil;
  if (state != nil) {
    serializationData = [NSJSONSerialization dataWithJSONObject:state options:kNilOptions error:&error];
    if (error != nil) {
      reject(RN_IMGLY.kErrorUnableToLoad, [NSString RN_IMGLY_string:@"Invalid serialization." withError:error], error);
      return;
    }
  }

  dispatch_async(dispatch_get_main_queue(), ^{
    if (self.licenseError != nil) {
      reject(RN_IMGLY.kErrorUnableToUnlock, [NSString RN_IMGLY_string:@"Unable to unlock with license." withError:self.licenseError], self.licenseError);
      return;
    }

    PESDKAssetCatalog *assetCatalog = PESDKAssetCatalog.defaultItems;
    PESDKConfiguration *configuration = [[PESDKConfiguration alloc] initWithBuilder:^(PESDKConfigurationBuilder * _Nonnull builder) {
      builder.assetCatalog = assetCatalog;
      [builder configureFromDictionary:dictionary error:&error];
    }];
    if (error != nil) {
      RCTLogError(@"Error while decoding configuration: %@", error);
      reject(RN_IMGLY.kErrorUnableToLoad, [NSString RN_IMGLY_string:@"Unable to load configuration." withError:error], error);
      return;
    }

    // Set default values if necessary
    id valueExportType = [NSDictionary RN_IMGLY_dictionary:dictionary valueForKeyPath:@"export.type" default:RN_IMGLY.kExportTypeFileURL];
    id valueExportFile = [NSDictionary RN_IMGLY_dictionary:dictionary valueForKeyPath:@"export.filename" default:[NSString stringWithFormat:@"imgly-export/%@", [[NSUUID UUID] UUIDString]]];
    id valueSerializationEnabled = [NSDictionary RN_IMGLY_dictionary:dictionary valueForKeyPath:@"export.serialization.enabled" default:@(NO)];
    id valueSerializationType = [NSDictionary RN_IMGLY_dictionary:dictionary valueForKeyPath:@"export.serialization.exportType" default:RN_IMGLY.kExportTypeFileURL];
    id valueSerializationFile = [NSDictionary RN_IMGLY_dictionary:dictionary valueForKeyPath:@"export.serialization.filename" default:valueExportFile];
    id valueSerializationEmbedImage = [NSDictionary RN_IMGLY_dictionary:dictionary valueForKeyPath:@"export.serialization.embedSourceImage" default:@(NO)];

    NSString *exportType = [RCTConvert NSString:valueExportType];
    NSURL *exportFile = [RCTConvert RN_IMGLY_ExportFileURL:valueExportFile withExpectedUTI:getUTI(configuration)];
    BOOL serializationEnabled = [RCTConvert BOOL:valueSerializationEnabled];
    NSString *serializationType = [RCTConvert NSString:valueSerializationType];
    NSURL *serializationFile = [RCTConvert RN_IMGLY_ExportFileURL:valueSerializationFile withExpectedUTI:kUTTypeJSON];
    BOOL serializationEmbedImage = [RCTConvert BOOL:valueSerializationEmbedImage];

    // Make sure that the export settings are valid
    if ((exportType == nil) ||
        (exportFile == nil && [exportType isEqualToString:RN_IMGLY.kExportTypeFileURL]) ||
        (serializationFile == nil && [serializationType isEqualToString:RN_IMGLY.kExportTypeFileURL]))
    {
      RCTLogError(@"Invalid export configuration");
      reject(RN_IMGLY.kErrorUnableToLoad, @"Invalid export configuration", nil);
      return;
    }

    // Check if subscriber
    __block BOOL isSubscriber = NO;
    if ([[dictionary allKeys] containsObject:kNixVSubscriberState]) {
        isSubscriber = [[dictionary objectForKey:kNixVSubscriberState] boolValue];
    }

    NSArray *nixToolFilter = (isSubscriber == NO) ? (NSArray *)[dictionary valueForKeyPath:kNixVToolFilter] : nil;
    NSArray *nixToolFilterDefault = (isSubscriber == NO) ? (NSArray *)[dictionary valueForKeyPath:kNixVToolFilterDefault] : nil;
    NSArray *nixTFCDuotone = [(NSDictionary *)nixToolFilterDefault[0] objectForKey:@"items"];
    NSArray *nixToolAdjust = (isSubscriber == NO) ? (NSArray *)[dictionary valueForKeyPath:kNixVToolAdjust] : nil;
    NSArray *nixToolFocus = (isSubscriber == NO) ? (NSArray *)[dictionary valueForKeyPath:kNixVToolFocus] : nil;
    NSArray *nixToolSticker = (isSubscriber == NO) ? (NSArray *)[dictionary valueForKeyPath:kNixVToolSticker] : nil;
    NSArray *nixToolText = (isSubscriber == NO) ? (NSArray *)[dictionary valueForKeyPath:kNixVToolText] : nil;
    NSArray *nixToolTextDesign = (isSubscriber == NO) ? (NSArray *)[dictionary valueForKeyPath:kNixVToolTextDesign] : nil;
    NSArray *nixToolOverlay = (isSubscriber == NO) ? (NSArray *)[dictionary valueForKeyPath:kNixVToolOverlay] : nil;
    NSArray *nixToolFrame = (isSubscriber == NO) ? (NSArray *)[dictionary valueForKeyPath:kNixVToolFrame] : nil;

    // Update configuration
    NSMutableDictionary *updatedDictionary = [NSMutableDictionary dictionaryWithDictionary:dictionary];
    NSMutableDictionary *exportDictionary = [NSMutableDictionary dictionaryWithDictionary:[NSDictionary RN_IMGLY_dictionary:updatedDictionary valueForKeyPath:@"export" default:@{}]];
    [exportDictionary setValue:exportFile.absoluteString forKeyPath:@"filename"];
    [updatedDictionary setValue:exportDictionary forKeyPath:@"export"];

    __weak typeof(self)weakSelf = self;
    __block NSDictionary *rawDictionary = [dictionary copy];
    __weak typeof(RNVideoEditorSDK)* weakController = controller;
    configuration = [[PESDKConfiguration alloc] initWithBuilder:^(PESDKConfigurationBuilder * _Nonnull builder) {
      builder.assetCatalog = assetCatalog;
      [builder configureFromDictionary:updatedDictionary error:&error];
      IMGLYConfigurationBlock configureWithBuilder = RN_IMGLY_ImglyKit.configureWithBuilder;
      if (configureWithBuilder != nil) {
        configureWithBuilder(builder);
      }
#pragma mark - Setup Functions Config
        // FRAME CONFIG
        [builder configureFrameToolController:^(PESDKFrameToolControllerOptionsBuilder * _Nonnull options) {
            [options setCellConfigurationClosure:^(PESDKMenuCollectionViewCell * _Nonnull cell, PESDKFrame * _Nullable frame) {
                if (isSubscriber == NO) {
                    NSDictionary *d = (NSDictionary *)[rawDictionary valueForKeyPath:@"nixFrame"];
                    [weakController addBanner:[d objectForKey:@"title"] subtitle:[d objectForKey:@"subtitle"]];

                    // reset all plus icon
                    [weakSelf removePlusBanner:cell];
                    // enable and adding plus icon
                    if ([weakSelf isExistWithList:nixToolFrame predicate:[NSPredicate predicateWithFormat:@"SELF == %@", frame.identifier]]
                        || frame == nil) {
                        [cell setUserInteractionEnabled:YES];
                    } else {
                        [weakSelf addPlusBanner:cell];
                        [cell setUserInteractionEnabled:NO];
                    }
                }
            }];
            [options setWillLeaveToolClosure:^{
                NSLog(@"configureFrameToolController>Leave");
                [weakController.banner removeFromSuperview];
                weakController.banner = nil;
            }];
        }];

        // OVERLAY CONFIG
        [builder configureOverlayToolController:^(PESDKOverlayToolControllerOptionsBuilder * _Nonnull options) {
            [options setOverlayCellConfigurationClosure:^(PESDKMenuCollectionViewCell * _Nonnull cell, PESDKOverlay * _Nonnull overlay) {
                if (isSubscriber == NO) {
                    // reset all plus icon
                    [weakSelf removePlusBanner:cell];
                    // enable and adding plus icon
                    if ([weakSelf isExistWithList:nixToolOverlay predicate:[NSPredicate predicateWithFormat:@"SELF == %@", overlay.identifier]]) {
                        [cell setUserInteractionEnabled:YES];
                    } else {
                        [weakSelf addPlusBanner:cell];
                        [cell setUserInteractionEnabled:NO];
                    }
                }
            }];
            [weakSelf addSubscriptionBanner:rawDictionary controller:weakController key:@"nixOverlay" subscriber:isSubscriber opt:options];
        }];

        // TEXT DESIGN CONFIG
        [builder configureTextDesignOptionsToolController:^(PESDKTextDesignOptionsToolControllerOptionsBuilder * _Nonnull options) {
            [options setActionButtonConfigurationClosure:^(PESDKMenuCollectionViewCell * _Nonnull cell, PESDKTextDesign * _Nonnull design) {
                if (isSubscriber == NO) {
                    // reset all plus icon
                    [weakSelf removePlusBanner:cell];
                    // enable and adding plus icon
                    if ([weakSelf isExistWithList:nixToolTextDesign predicate:[NSPredicate predicateWithFormat:@"SELF == %@", design.identifier]]) {
                        [cell setUserInteractionEnabled:YES];
                    } else {
                        [weakSelf addPlusBanner:cell];
                        [cell setUserInteractionEnabled:NO];
                    }
                }
            }];
            [weakSelf addSubscriptionBanner:rawDictionary controller:weakController key:@"nixTextDesign" subscriber:isSubscriber opt:options];
        }];

        // TEXT CONFIG
        [builder configureTextFontToolController:^(PESDKTextFontToolControllerOptionsBuilder * _Nonnull options) {
            [options setActionButtonConfigurationClosure:^(PESDKMenuCollectionViewCell * _Nonnull cell, PESDKFont * _Nonnull font) {
                if (isSubscriber == NO) {
                    // reset all plus icon
                    [weakSelf removePlusBanner:cell];
                    // enable and adding plus icon
                    if ([weakSelf isExistWithList:nixToolText predicate:[NSPredicate predicateWithFormat:@"SELF == %@", cell.captionTextLabel.text]]) {
                        [cell setUserInteractionEnabled:YES];
                    } else {
                        [weakSelf addPlusBanner:cell];
                        [cell setUserInteractionEnabled:NO];
                    }
                }
            }];
            [weakSelf addSubscriptionBanner:rawDictionary controller:weakController key:@"nixText" subscriber:isSubscriber opt:options];
        }];

        // STICKER CONFIG
        [builder configureStickerToolController:^(PESDKStickerToolControllerOptionsBuilder * _Nonnull options) {
            [options setStickerButtonConfigurationClosure:^(PESDKIconCollectionViewCell * _Nonnull cell, PESDKSticker * _Nonnull sticker) {
                if (isSubscriber == NO) {
                    [weakSelf removePlusBanner:cell];
                    if ([weakSelf isExistWithList:nixToolSticker predicate:[NSPredicate predicateWithFormat:@"SELF == %@", sticker.identifier]]) {
                        [cell setUserInteractionEnabled:YES];
                    } else {
                        [weakSelf addPlusBanner:cell];
                        [cell setUserInteractionEnabled:NO];
                    }
                }
            }];
            [weakSelf addSubscriptionBanner:rawDictionary controller:weakController key:@"nixSticker" subscriber:isSubscriber opt:options];
        }];

        // FOCUS CONFIG
        [builder configureFocusToolController:^(PESDKFocusToolControllerOptionsBuilder * _Nonnull options) {
            [options setFocusModeButtonConfigurationClosure:^(PESDKMenuCollectionViewCell * _Nonnull cell, enum PESDKFocusMode mode) {
                if (isSubscriber == NO) {
                    [weakSelf removePlusBanner:cell];
                    if ([weakSelf isExistWithList:nixToolFocus predicate:[NSPredicate predicateWithFormat:@"SELF == %@", cell.captionTextLabel.text]]) {
                        [cell setUserInteractionEnabled:YES];
                    } else {
                        [weakSelf addPlusBanner:cell];
                        [cell setUserInteractionEnabled:NO];
                    }
                }
            }];
            [weakSelf addSubscriptionBanner:rawDictionary controller:weakController key:@"nixFocus" subscriber:isSubscriber opt:options];
        }];

        // ADJUSTMENT CONFIG
        [builder configureAdjustToolController:^(PESDKAdjustToolControllerOptionsBuilder * _Nonnull options) {
            [options setAdjustToolButtonConfigurationBlock:^(PESDKMenuCollectionViewCell * _Nonnull cell, NSNumber * _Nullable index) {
                if (isSubscriber == NO) {
                    [weakSelf removePlusBanner:cell];
                    if ([weakSelf isExistWithList:nixToolAdjust predicate:[NSPredicate predicateWithFormat:@"SELF == %@", cell.captionTextLabel.text]]) {
                        [cell setUserInteractionEnabled:YES];
                    } else {
                        [weakSelf addPlusBanner:cell];
                        [cell setUserInteractionEnabled:NO];
                    }
                }
            }];
            [weakSelf addSubscriptionBanner:rawDictionary controller:weakController key:@"nixAdjust" subscriber:isSubscriber opt:options];
        }];

        // FILTER CONFIG
        [builder configureFilterToolController:^(PESDKFilterToolControllerOptionsBuilder * _Nonnull options) {
          // visible on the screen
          [options setFilterCellConfigurationClosure:^(PESDKMenuCollectionViewCell * _Nonnull cell, PESDKEffect * _Nonnull effect) {
              if (isSubscriber == NO) {
                  // safe remove plus banner
                  [weakSelf removePlusBanner:cell];
                  // will change this if plus user
                  if ([weakSelf isExistWithList:nixTFCDuotone predicate:[NSPredicate predicateWithFormat:@"SELF.identifier == %@", effect.identifier]]) {
                      [cell setUserInteractionEnabled:YES];
                  } else if ([cell.captionTextLabel.text isEqualToString:@"None"]){
                      [cell setUserInteractionEnabled:YES];
                  } else {
                      [weakSelf addPlusBanner:cell];
                      [cell setUserInteractionEnabled:NO];
                  }
              }
          }];
          [options setFolderCellConfigurationClosure:^(PESDKMenuCollectionViewCell * _Nonnull cell) {
              if (isSubscriber == NO) {
                  [cell setUserInteractionEnabled:YES];
                  [weakSelf removePlusBanner:cell];
                  if (![weakSelf isExistWithList:nixToolFilter predicate:[NSPredicate predicateWithFormat:@"SELF == %@", cell.captionTextLabel.text]]) {
                      [weakSelf addPlusBanner:cell];
                  }
              }
          }];
          [options setApplyButtonConfigurationClosure:^(PESDKButton * _Nonnull btn) {
              // track
          }];
          [weakSelf addSubscriptionBanner:rawDictionary controller:weakController key:@"nixFilter" subscriber:isSubscriber opt:options];
        }];

#pragma mark - Setup Top Level Menu
        // MAIN MENU
        [builder configureVideoEditViewController:^(PESDKVideoEditViewControllerOptionsBuilder * _Nonnull options) {
            // fix video playback dismissal
            [options setDiscardButtonConfigurationClosure:^(PESDKButton * _Nonnull button) {
                [controller addTargetDiscard:button];
            }];

            NSMutableArray<PESDKPhotoEditMenuItem *> *menuItems = [[PESDKPhotoEditMenuItem defaultItems] mutableCopy];
            [menuItems removeObjectAtIndex:0]; // remove video/trim
            if (isSubscriber == NO) {
                [options setActionButtonConfigurationBlock:^(PESDKMenuCollectionViewCell * _Nonnull cell, PESDKPhotoEditMenuItem * _Nonnull menuItem) {
                    // remove subview for transform
                    [weakSelf removePlusBanner:cell];
                    // add plus icon
                    if (![cell.captionTextLabel.text isEqualToString:@"Transform"] && ![cell.captionTextLabel.text isEqualToString:@"Brush"]) {
                        [weakSelf addPlusBanner:cell];
                    }
                }];
                // remove for brush
                [menuItems removeLastObject];
                // custom action button for brush that can go inside
                [menuItems addObject:[self createMenuitemWithTitle:@"Brush" icon:@"ic_brush_plus" vc:controller isEnable:NO]];
            }
            options.menuItems = menuItems;
        }];
    }];

    if (error != nil) {
      RCTLogError(@"Error while updating configuration: %@", error);
      reject(RN_IMGLY.kErrorUnableToLoad, [NSString RN_IMGLY_string:@"Unable to update configuration." withError:error], error);
      return;
    }

    PESDKMediaEditViewController *mediaEditViewController = createMediaEditViewController(configuration, serializationData);
    if (mediaEditViewController == nil) {
      return;
    }

    self.exportType = exportType;
    self.exportFile = exportFile;
    self.serializationEnabled = serializationEnabled;
    self.serializationType = serializationType;
    self.serializationFile = serializationFile;
    self.serializationEmbedImage = serializationEmbedImage;
    self.resolve = resolve;
    self.reject = reject;
    self.mediaEditViewController = mediaEditViewController;

    UIViewController *currentViewController = RCTPresentedViewController();
    [currentViewController presentViewController:self.mediaEditViewController animated:YES completion:NULL];
  });
}

- (BOOL)isExistWithList:(NSArray *)list predicate:(NSPredicate*)predicate {
    return ([list filteredArrayUsingPredicate:predicate].count > 0);
}

- (void)addPlusBanner:(UICollectionViewCell *)cell {
    dispatch_async(dispatch_get_main_queue(), ^{
        CGRect rect = cell.bounds;
        UIImageView *imageView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"ic_plus"]];
        imageView.frame = CGRectMake(rect.size.width - 24, 4, 20, 10);
        imageView.tag = 100;
        [cell addSubview:imageView];
        [cell setNeedsDisplay];
        [cell setNeedsLayout];
    });
}

- (void)removePlusBanner:(UICollectionViewCell *)cell {
    for (UIView *view in cell.subviews) {
      if (view.tag == 100) {
          [view removeFromSuperview];
      }
    }
}

- (void)addSubscriptionBanner:(NSDictionary *)rawDictionary
                   controller:(RNVideoEditorSDK *)controller
                          key:(NSString *)key
                   subscriber:(BOOL)isSubscriber
                          opt:(PESDKToolControllerOptionsBuilder *)options
{
    if (isSubscriber == NO) {
        [options setWillLeaveToolClosure:^{
            [controller.banner removeFromSuperview];
            controller.banner = nil;
        }];
        [options setDidEnterToolClosure:^{
            NSDictionary *d = (NSDictionary *)[rawDictionary valueForKeyPath:key];
            [controller addBanner:[d objectForKey:@"title"] subtitle:[d objectForKey:@"subtitle"]];
        }];
    }
}

- (PESDKPhotoEditMenuItem *)createMenuitemWithTitle:(NSString *)title icon:(NSString *)icon vc:(RNVideoEditorSDK *)vc isEnable:(BOOL)isEnable {
    PESDKActionMenuItem *actionItem =
        [[PESDKActionMenuItem alloc] initWithTitle:title
                                              icon:[UIImage imageNamed:icon]
                                 objcActionClosure:^(PESDKPhotoEditModel * _Nonnull editModel) {
    } objcSelectedClosure:^BOOL(PESDKPhotoEditModel * _Nonnull editModel) {
      return NO;
    }];
    return [[PESDKPhotoEditMenuItem alloc] initWithActionMenuItem:actionItem];
}

- (void)dismiss:(nullable PESDKMediaEditViewController *)mediaEditViewController animated:(BOOL)animated completion:(nullable IMGLYCompletionBlock)completion
{
  if (mediaEditViewController != self.mediaEditViewController) {
    RCTLogError(@"Unregistered %@", NSStringFromClass(mediaEditViewController.class));
  }

  self.exportType = nil;
  self.exportFile = nil;
  self.serializationEnabled = NO;
  self.serializationType = nil;
  self.serializationFile = nil;
  self.serializationEmbedImage = NO;
  self.resolve = nil;
  self.reject = nil;
  self.mediaEditViewController = nil;

  dispatch_async(dispatch_get_main_queue(), ^{
    [mediaEditViewController.presentingViewController dismissViewControllerAnimated:animated completion:completion];
  });
}

- (void)handleLicenseError:(nullable NSError *)error
{
  self.licenseError = nil;
  if (error != nil) {
    if ([error.domain isEqualToString:@"ImglyKit.IMGLY.Error"]) {
      switch (error.code) {
        case 3:
          RCTLogWarn(@"%@: %@", NSStringFromClass(self.class), error.localizedDescription);
          break;
        default:
          self.licenseError = error;
          RCTLogError(@"%@: %@", NSStringFromClass(self.class), error.localizedDescription);
          break;
      }
    } else {
      self.licenseError = error;
      RCTLogError(@"Error while unlocking with license: %@", error);
    }
  }
}

- (void)unlockWithLicenseURL:(nonnull NSURL *)url {}

- (void)unlockWithLicenseString:(nonnull NSString *)string {}

- (void)unlockWithLicenseObject:(nonnull NSDictionary *)dictionary {}

- (void)unlockWithLicense:(nonnull id)json
{
  NSString *string = nil;
  NSURL *url = nil;
  BOOL isString = [json isKindOfClass:[NSString class]];
  if (isString) {
    string = json;
    @try { // NSURL has a history of crashing with bad input, so let's be safe
      url = [NSURL URLWithString:string];
    }
    @catch (__unused NSException *e) {}
  }

  // If the user specifies a file URL we do not use the converter and use the URL without any checks
  if (url == nil || !url.isFileURL) {
    // `RCTConvert` changed the conversion for json to URL and it throws now an error if it is not a string
    if (isString) {
      url = [RCTConvert NSURL:json];
      // Test if the resulting URL is an existing local file otherwise we try to read the license from a string or a dictionary
      if (![[NSFileManager defaultManager] fileExistsAtPath:url.path]) {
        url = nil;
      }
    }
  }

  if (url != nil) {
    [self unlockWithLicenseURL:url];
  }
  else if (string != nil) {
    [self unlockWithLicenseString:string];
  }
  else if ([json isKindOfClass:[NSDictionary class]]) {
    NSDictionary *dictionary = json;
    [self unlockWithLicenseObject:dictionary];
  }
  else if (json) {
    RCTLogConvertError(json, @"a valid license format");
  }
}

@end

@implementation NSString (RN_IMGLY_Category)

+ (nonnull NSString *)RN_IMGLY_string:(nonnull NSString *)message withError:(nullable NSError *)error
{
  NSString *description = error.localizedDescription;
  if (description != nil) {
    return [NSString stringWithFormat:@"%@ %@", message, description];
  } else {
    return message;
  }
}

@end

@implementation NSData (RN_IMGLY_Category)

- (BOOL)RN_IMGLY_writeToURL:(nonnull NSURL *)fileURL andCreateDirectoryIfNecessary:(BOOL)createDirectory error:(NSError *_Nullable*_Nullable)error
{
  if (createDirectory) {
    if (![[NSFileManager defaultManager] createDirectoryAtURL:fileURL.URLByDeletingLastPathComponent withIntermediateDirectories:YES attributes:nil error:error]) {
      return NO;
    }
  }
  return [self writeToURL:fileURL options:NSDataWritingAtomic error:error];
}

@end

@implementation RCTConvert (RN_IMGLY_Category)

+ (nullable RN_IMGLY_ExportURL *)RN_IMGLY_ExportURL:(nullable id)json
{
  // This code is identical to the implementation of
  // `+ (NSURL *)NSURL:(id)json`
  // except that it creates a path to a temporary file instead of assuming a resource path as last resort.

  NSString *path = [self NSString:json];
  if (!path) {
    return nil;
  }

  @try { // NSURL has a history of crashing with bad input, so let's be safe

    NSURL *URL = [NSURL URLWithString:path];
    if (URL.scheme) { // Was a well-formed absolute URL
      return URL;
    }

    // Check if it has a scheme
    if ([path rangeOfString:@":"].location != NSNotFound) {
      NSMutableCharacterSet *urlAllowedCharacterSet = [NSMutableCharacterSet new];
      [urlAllowedCharacterSet formUnionWithCharacterSet:[NSCharacterSet URLUserAllowedCharacterSet]];
      [urlAllowedCharacterSet formUnionWithCharacterSet:[NSCharacterSet URLPasswordAllowedCharacterSet]];
      [urlAllowedCharacterSet formUnionWithCharacterSet:[NSCharacterSet URLHostAllowedCharacterSet]];
      [urlAllowedCharacterSet formUnionWithCharacterSet:[NSCharacterSet URLPathAllowedCharacterSet]];
      [urlAllowedCharacterSet formUnionWithCharacterSet:[NSCharacterSet URLQueryAllowedCharacterSet]];
      [urlAllowedCharacterSet formUnionWithCharacterSet:[NSCharacterSet URLFragmentAllowedCharacterSet]];
      path = [path stringByAddingPercentEncodingWithAllowedCharacters:urlAllowedCharacterSet];
      URL = [NSURL URLWithString:path];
      if (URL) {
        return URL;
      }
    }

    // Assume that it's a local path
    path = path.stringByRemovingPercentEncoding;
    if ([path hasPrefix:@"~"]) {
      // Path is inside user directory
      path = path.stringByExpandingTildeInPath;
    } else if (!path.absolutePath) {
      // Create a path to a temporary file
      path = [NSTemporaryDirectory() stringByAppendingPathComponent:path];
    }
    if (!(URL = [NSURL fileURLWithPath:path isDirectory:NO])) {
      RCTLogConvertError(json, @"a valid URL");
    }
    return URL;
  }
  @catch (__unused NSException *e) {
    RCTLogConvertError(json, @"a valid URL");
    return nil;
  }
}

+ (nullable RN_IMGLY_ExportFileURL *)RN_IMGLY_ExportFileURL:(nullable id)json withExpectedUTI:(nonnull CFStringRef)expectedUTI
{
  // This code is similar to the implementation of
  // `+ (RCTFileURL *)RCTFileURL:(id)json`.

  NSURL *fileURL = [self RN_IMGLY_ExportURL:json];
  if (!fileURL.fileURL) {
    RCTLogError(@"URI must be a local file, '%@' isn't.", fileURL);
    return nil;
  }

  // Append correct file extension if necessary
  NSString *fileUTI = CFBridgingRelease(UTTypeCreatePreferredIdentifierForTag(kUTTagClassFilenameExtension, (__bridge CFStringRef)(fileURL.pathExtension.lowercaseString), nil));
  if (fileUTI == nil || !UTTypeEqual((__bridge CFStringRef)(fileUTI), expectedUTI)) {
    NSString *extension = CFBridgingRelease(UTTypeCopyPreferredTagWithClass(expectedUTI, kUTTagClassFilenameExtension));
    if (extension != nil) {
      fileURL = [fileURL URLByAppendingPathExtension:extension];
    }
  }

  BOOL isDirectory = false;
  if ([[NSFileManager defaultManager] fileExistsAtPath:fileURL.path isDirectory:&isDirectory]) {
    if (isDirectory) {
      RCTLogError(@"File '%@' must not be a directory.", fileURL);
    } else {
      RCTLogWarn(@"File '%@' will be overwritten on export.", fileURL);
    }
  }
  return fileURL;
}

@end

@implementation NSDictionary (RN_IMGLY_Category)

- (nullable id)RN_IMGLY_valueForKeyPath:(nonnull NSString *)keyPath default:(nullable id)defaultValue
{
  id value = [self valueForKeyPath:keyPath];

  if (value == nil || value == [NSNull null]) {
    return defaultValue;
  } else {
    return value;
  }
}

+ (nullable id)RN_IMGLY_dictionary:(nullable NSDictionary *)dictionary valueForKeyPath:(nonnull NSString *)keyPath default:(nullable id)defaultValue
{
  if (dictionary == nil) {
    return defaultValue;
  }
  return [dictionary RN_IMGLY_valueForKeyPath:keyPath default:defaultValue];
}

@end
