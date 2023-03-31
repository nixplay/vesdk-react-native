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

//    NSArray *nixToolFilter = (isSubscriber == NO) ? (NSArray *)[dictionary valueForKeyPath:kNixVToolFilter] : nil;
//    NSArray *nixToolFilterDefault = (isSubscriber == NO) ? (NSArray *)[dictionary valueForKeyPath:kNixVToolFilterDefault] : nil;
//    NSArray *nixTFCDuotone = [(NSDictionary *)nixToolFilterDefault[0] objectForKey:@"items"];
    // NSArray *nixToolAdjust = (isSubscriber == NO) ? (NSArray *)[dictionary valueForKeyPath:kNixVToolAdjust] : nil;
    // NSArray *nixToolFocus = (isSubscriber == NO) ? (NSArray *)[dictionary valueForKeyPath:kNixVToolFocus] : nil;
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
    self.freeTrial = controller.freeTrial;
    configuration = [[PESDKConfiguration alloc] initWithBuilder:^(PESDKConfigurationBuilder * _Nonnull builder) {
      builder.assetCatalog = assetCatalog;
      [builder configureFromDictionary:updatedDictionary error:&error];
      IMGLYConfigurationBlock configureWithBuilder = RN_IMGLY_ImglyKit.configureWithBuilder;
      if (configureWithBuilder != nil) {
        configureWithBuilder(builder);
      }
#pragma mark - Setup Functions Config
        if (isSubscriber == NO) {
			// "Reset" and "None" strings for all languages
            NSBundle *bundle = [NSBundle mainBundle];
            NSArray *resourceNameList = @[@"ImglyEN", @"ImglyDE", @"ImglyES",
                                          @"ImglyFR", @"ImglyIT", @"ImglyJA"];
            NSArray *identifiersList = @[@"pesdk_transform_button_reset",
                                         @"pesdk_adjustments_button_reset",
                                         @"pesdk_frame_button_none",
                                         @"pesdk_filter_button_none",
                                         @"pesdk_overlay_button_none",
                                         @"pesdk_focus_button_disabled"];
            NSMutableArray *noneAndResetListRaw = [NSMutableArray new];
            for (NSString *r in resourceNameList) {
                NSString* path = [bundle pathForResource:r ofType:@"plist"];
                NSDictionary *d = [[NSDictionary alloc] initWithContentsOfFile:path];
                for(NSString *t in identifiersList) {
                    [noneAndResetListRaw addObject:[d objectForKey:t]];
                }
            }
            NSOrderedSet *orderedSet = [NSOrderedSet orderedSetWithArray:noneAndResetListRaw];
            NSArray *noneAndResetList = [orderedSet array]; //no duplicates

            // FRAME CONFIG
            [builder configureFrameToolController:^(PESDKFrameToolControllerOptionsBuilder * _Nonnull options) {
          //            __block PESDKToolControllerOptionsBuilder * _options = options;
                [options setCellConfigurationClosure:^(PESDKMenuCollectionViewCell * _Nonnull cell, PESDKFrame * _Nullable frame) {
                    // reset all plus icon
                    [weakSelf removePlusBanner:cell];
					// enable and adding plus icon
					 if ([noneAndResetList containsObject:cell.captionTextLabel.text]) {
                        // do nothing
                    } else {
                        [weakSelf addPlusBanner:cell];
                    }
					/*
                    if (![weakSelf isExistWithList:nixToolFrame predicate:[NSPredicate predicateWithFormat:@"SELF == %@", frame.identifier]]
                        || frame != nil) {
                        [weakSelf addPlusBanner:cell];
                    }
					*/
                }];
                [options setSelectedFrameClosure:^(PESDKFrame * _Nullable frame) {
                    if (weakController.hasBegan == NO) {
                        [weakController.mainController.undoController beginUndoGrouping];
                    }
                    if (frame.identifier) {
                        weakController.needToUpgrade = 1;
                        NSDictionary *d = (NSDictionary *)[rawDictionary valueForKeyPath:@"nixFrame"];
                        [weakController addBanner:[d objectForKey:@"title"] subtitle:[d objectForKey:@"subtitle"]];
                    } else {
                        [weakController removeBanner];
                        weakController.needToUpgrade = 0;
                    }
                }];
                [options setDidEnterToolClosure:^{
                    weakController.currentEffects = @"frame";
                    weakController.userActivity = nil;
                    weakController.userActivity = [[NSMutableDictionary alloc] init];
                    // save last changes
                    [weakController saveSerialDataWithKey:@"nonPlusActivity"];
                    [weakController trackEvent:@"frame_tap"];
                }];
            }];
            [builder configureFrameOptionsToolController:^(PESDKFrameOptionsToolControllerOptionsBuilder * _Nonnull options) {
                [options setWillLeaveToolClosure:^{
                    [weakController removeBanner];
                }];
                [options setApplyButtonConfigurationClosure:^(PESDKButton * _Nonnull button) {
                    [weakController trackEvent:@"frame_apply"];
                    [weakController addButtonApply:button];
                }];
                [options setDiscardButtonConfigurationClosure:^(PESDKButton * _Nonnull button) {
                    [weakController addButtonDiscard:button];
                }];
                [options setDidEnterToolClosure:^{
                    weakController.currentEffects = @"frame";
                }];
            }];

            // OVERLAY CONFIG
            [builder configureOverlayToolController:^(PESDKOverlayToolControllerOptionsBuilder * _Nonnull options) {
                [options setOverlayCellConfigurationClosure:^(PESDKMenuCollectionViewCell * _Nonnull cell, PESDKOverlay * _Nonnull overlay) {
                    // reset all plus icon
                    [weakSelf removePlusBanner:cell];
                    // enable and adding plus icon
                    if (![weakSelf isExistWithList:nixToolOverlay predicate:[NSPredicate predicateWithFormat:@"SELF == %@", overlay.identifier]]) {
                        [weakSelf addPlusBanner:cell];
                    }
                }];
                // handling of upgrade new upgrade path
                [options setOverlaySelectedClosure:^(PESDKOverlay * _Nonnull overlay) {
                    [weakController.userActivity setObject:overlay.identifier forKey:@"nixOverlay"];
                    if ([weakSelf isExistWithList:nixToolOverlay predicate:[NSPredicate predicateWithFormat:@"SELF == %@", overlay.identifier]]) {
                        [weakController removeBanner];
                        weakController.needToUpgrade = 0;
                    } else {
                        weakController.needToUpgrade = 1;
                        [weakController removeBanner];
                        NSDictionary *d = (NSDictionary *)[rawDictionary valueForKeyPath:@"nixOverlay"];
                        [weakController addBanner:[d objectForKey:@"title"] subtitle:[d objectForKey:@"subtitle"]];
                    }
                }];
                [weakSelf handleApply:weakController opt:options key:@"overlay"];
            }];

            // TEXT DESIGN CONFIG
            [builder configureTextDesignOptionsToolController:^(PESDKTextDesignOptionsToolControllerOptionsBuilder * _Nonnull options) {
                [options setActionButtonConfigurationClosure:^(PESDKMenuCollectionViewCell * _Nonnull cell, PESDKTextDesign * _Nonnull design) {
                    // reset all plus icon
                    [weakSelf removePlusBanner:cell];
                    // enable and adding plus icon
                    if (![weakSelf isExistWithList:nixToolTextDesign predicate:[NSPredicate predicateWithFormat:@"SELF == %@", design.identifier]]) {
                        [weakSelf addPlusBanner:cell];
                    }
                }];
                // handling of upgrade new upgrade path
                [options setTextDesignActionSelectedClosure:^(PESDKTextDesign * _Nonnull design) {
                    [weakController.userActivity setObject:design.identifier forKey:@"nixTextDesign"];
                    weakController.needToUpgrade = 1;
                }];
                [options setWillLeaveToolClosure:^{
                    [weakController removeBanner];
                    weakController.enableToValidate = 0;
                }];
                [options setDidEnterToolClosure:^{
                    weakController.currentEffects = @"textdesign";
                    [weakController trackEvent:@"textdesign_tap"];
                }];
                [options setOverlayButtonConfigurationClosure:^(PESDKOverlayButton * _Nonnull button, enum TextDesignOverlayAction action) {
                    if ((long)action == 4) {
                        // undo
                        [weakController addButtonTrigger:button usage:@"undo"];
                    } else if ((long)action == 0 || (long)action == 5) {
                        // add, redo
                        [weakController addButtonTrigger:button usage:@"redo"];
                    } else if ((long)action == 1) {
                        // delete
                        [weakController addButtonTrigger:button usage:@"delete"];
                    }
                }];
                [options setApplyButtonConfigurationClosure:^(PESDKButton * _Nonnull button) {
                    [weakController addButtonApply:button];
                    [weakController trackEvent:@"textdesign_apply"];
                }];
                [options setDiscardButtonConfigurationClosure:^(PESDKButton * _Nonnull button) {
                    [weakController addButtonDiscard:button];
                }];
            }];
            [builder configureTextDesignToolController:^(PESDKTextDesignToolControllerOptionsBuilder * _Nonnull options) {
                [options setWillLeaveToolClosure:^{
                    weakController.needToUpgrade = 1;
                    NSDictionary *d = (NSDictionary *)[rawDictionary valueForKeyPath:@"nixTextDesign"];
                    [weakController addBanner:[d objectForKey:@"title"] subtitle:[d objectForKey:@"subtitle"]];
                    if (weakController.hasBegan == NO) {
                        [weakController.mainController.undoController beginUndoGrouping];
                    }
                }];
                [options setDidEnterToolClosure:^{
                    weakController.currentEffects = @"textdesign";
                    [weakController removeBanner];
                }];
            }];

            // TEXT CONFIG
            [builder configureTextFontToolController:^(PESDKTextFontToolControllerOptionsBuilder * _Nonnull options) {
                [options setActionButtonConfigurationClosure:^(PESDKMenuCollectionViewCell * _Nonnull cell, PESDKFont * _Nonnull font) {
                    // reset all plus icon
                    [weakSelf removePlusBanner:cell];
                    // enable and adding plus icon
                    if (![weakSelf isExistWithList:nixToolText predicate:[NSPredicate predicateWithFormat:@"SELF == %@", cell.captionTextLabel.text]]) {
                        [weakSelf addPlusBanner:cell];
                    }
                }];
                // handling of upgrade new upgrade path
                [options setTextFontActionSelectedClosure:^(NSString * _Nonnull font) {
                    weakController.needToUpgrade = 1;
                }];
                [options setDidEnterToolClosure:^{
                    weakController.currentEffects = @"text";
                }];
            }];
            [builder configureTextToolController:^(PESDKTextToolControllerOptionsBuilder * _Nonnull options) {
                [options setWillLeaveToolClosure:^{
                    NSDictionary *d = (NSDictionary *)[rawDictionary valueForKeyPath:@"nixText"];
                    [weakController addBanner:[d objectForKey:@"title"] subtitle:[d objectForKey:@"subtitle"]];
                    weakController.needToUpgrade = 1;
                    if (weakController.hasBegan == NO) {
                        [weakController.mainController.undoController beginUndoGrouping];
                    }
                }];
                [options setDidEnterToolClosure:^{
                    weakController.currentEffects = @"text";
                    [weakController removeBanner];
                    [weakController trackEvent:@"text_tap"];
                }];
            }];
            [builder configureTextOptionsToolController:^(PESDKTextOptionsToolControllerOptionsBuilder * _Nonnull options) {
                [options setOverlayButtonConfigurationClosure:^(PESDKOverlayButton * _Nonnull button, enum TextOverlayAction action) {
                    if ((long)action == 4) {
                        // undo
                        [weakController addButtonTrigger:button usage:@"undo"];
                    } else if ((long)action == 0 || (long)action == 5) {
                        // add, redo
                        [weakController addButtonTrigger:button usage:@"redo"];
                    } else if ((long)action == 1) {
                        // delete
                        [weakController addButtonTrigger:button usage:@"delete"];
                    }
                }];
                [options setApplyButtonConfigurationClosure:^(PESDKButton * _Nonnull button) {
                    [weakController addButtonApply:button];
                    [weakController trackEvent:@"text_apply"];
                }];
                [options setDiscardButtonConfigurationClosure:^(PESDKButton * _Nonnull button) {
                    [weakController addButtonDiscard:button];
                }];
                [options setWillLeaveToolClosure:^{
                    [weakController removeBanner];
                    weakController.enableToValidate = 0;
                }];
                [options setDidEnterToolClosure:^{
                    weakController.currentEffects = @"text";
                }];
            }];

            // STICKER CONFIG
            [builder configureStickerToolController:^(PESDKStickerToolControllerOptionsBuilder * _Nonnull options) {
                [options setStickerButtonConfigurationClosure:^(PESDKIconCollectionViewCell * _Nonnull cell, PESDKSticker * _Nonnull sticker) {
                    [weakSelf removePlusBanner:cell];
                    if (![weakSelf isExistWithList:nixToolSticker predicate:[NSPredicate predicateWithFormat:@"SELF == %@", sticker.identifier]]) {
                        [weakSelf addPlusBanner:cell];
                    }
                }];
                // handling of upgrade new upgrade path
                [options setAddedStickerClosure:^(PESDKSticker * _Nonnull sticker) {
                    // need to check if came from replace, if yes need to remove previous added sticker
                    weakController.needToUpgrade++;
                    [weakController.userActivity setObject:sticker.identifier
                                                    forKey:[NSString stringWithFormat:@"sticker-%@", sticker.identifier]];
                    if (weakController.hasBegan == NO) {
                        [weakController.mainController.undoController beginUndoGrouping];
                    }
                }];
                [options setDidEnterToolClosure:^{
                    weakController.currentEffects = @"sticker";
                    [weakController removeBanner];
                    weakController.enableToValidate++;
                    [weakController trackEvent:@"sticker_tap"];
                }];
                [options setWillLeaveToolClosure:^{
                    NSDictionary *d = (NSDictionary *)[rawDictionary valueForKeyPath:@"nixSticker"];
                    [weakController addBanner:[d objectForKey:@"title"] subtitle:[d objectForKey:@"subtitle"]];
                }];
                [options setApplyButtonConfigurationClosure:^(PESDKButton * _Nonnull button) {
                    [weakController addButtonApply:button];
                    [weakController trackEvent:@"sticker_apply"];
                }];
                [options setDiscardButtonConfigurationClosure:^(PESDKButton * _Nonnull button) {
                    [weakController addButtonDiscard:button];
                }];
            }];
            [builder configureStickerOptionsToolController:^(PESDKStickerOptionsToolControllerOptionsBuilder * _Nonnull options) {
                // add, delete, flip-up, flip-down, undo, redo
                [options setOverlayButtonConfigurationClosure:^(PESDKOverlayButton * _Nonnull button, enum StickerOverlayAction action) {
                    if ((long)action == 4) {
                        // undo
                        [weakController addButtonTrigger:button usage:@"undo"];
                    } else if ((long)action == 0 || (long)action == 5) {
                        // add, redo
                        [weakController addButtonTrigger:button usage:@"redo"];
                    } else if ((long)action == 1) {
                        // delete
                        [weakController addButtonTrigger:button usage:@"delete"];
                    }
                }];
                // replace, opacity
                [options setActionButtonConfigurationClosure:^(UICollectionViewCell * _Nonnull cell, enum StickerAction action) {}];
                [options setDidEnterToolClosure:^{
                    weakController.currentEffects = @"sticker";
                    weakController.enableToValidate++;
                }];
                [options setWillLeaveToolClosure:^{
                    dispatch_async(dispatch_get_main_queue(), ^{
                        [weakController removeBanner];
                    });
                    [weakController.mainController.undoController endUndoGrouping];
                    weakController.enableToValidate = 0;
                }];
                [options setApplyButtonConfigurationClosure:^(PESDKButton * _Nonnull button) {
                    [weakController addButtonApply:button];
                }];
                [options setDiscardButtonConfigurationClosure:^(PESDKButton * _Nonnull button) {
                    [weakController addButtonDiscard:button];
                }];
            }];
            [builder configureStickerColorToolController:^(PESDKColorToolControllerOptionsBuilder * _Nonnull options) {
                // enter, leave
            }];

            // FOCUS CONFIG
            [builder configureFocusToolController:^(PESDKFocusToolControllerOptionsBuilder * _Nonnull options) {
                [options setFocusModeButtonConfigurationClosure:^(PESDKMenuCollectionViewCell * _Nonnull cell, enum PESDKFocusMode mode) {
                    [weakSelf removePlusBanner:cell];
					if ([noneAndResetList containsObject:cell.captionTextLabel.text]) {
                        // do nothing
                    } else {
                        [weakSelf addPlusBanner:cell];
                    }
					/*
                    if (![weakSelf isExistWithList:nixToolFocus predicate:[NSPredicate predicateWithFormat:@"SELF == %@", cell.captionTextLabel.text]]) {
                        [weakSelf addPlusBanner:cell];
                    }
					*/
                }];
                // handling of upgrade new upgrade path
                [options setFocusModeSelectedClosure:^(enum PESDKFocusMode mode) {
                    [weakController removeBanner];
                    [weakController.userActivity setObject:[NSNumber numberWithLong:mode]
                                                    forKey:@"nixFocus"];
                    if ((long)mode > 0) {
                        NSDictionary *d = (NSDictionary *)[rawDictionary valueForKeyPath:@"nixFocus"];
                        [weakController addBanner:[d objectForKey:@"title"] subtitle:[d objectForKey:@"subtitle"]];
                        weakController.needToUpgrade = 1;
                    } else {
                        weakController.needToUpgrade = 0;
                    }
                }];
                // enter, leave
                [weakSelf handleApply:weakController opt:options key:@"focus"];
            }];

            // ADJUSTMENT CONFIG
            [builder configureAdjustToolController:^(PESDKAdjustToolControllerOptionsBuilder * _Nonnull options) {
                [options setAdjustToolButtonConfigurationBlock:^(PESDKMenuCollectionViewCell * _Nonnull cell, NSNumber * _Nullable index) {
                    [weakSelf removePlusBanner:cell];
					 if ([noneAndResetList containsObject:cell.captionTextLabel.text]) {
                        // do nothing
                    } else {
                        [weakSelf addPlusBanner:cell];
                    }
					/*
                    if (![weakSelf isExistWithList:nixToolAdjust predicate:[NSPredicate predicateWithFormat:@"SELF == %@", cell.captionTextLabel.text]]) {
                        [weakSelf addPlusBanner:cell];
                    }
					*/
                }];
                [options setSliderChangedValueClosure:^(PESDKSlider * _Nonnull slider, enum AdjustTool tool) {
                    if (weakController.hasBegan == NO) {
                        [weakController.mainController.undoController beginUndoGrouping];
                    }
                    float sliderValue = slider.value * 100;
                    if (fabsf(sliderValue)) {
                        weakController.needToUpgrade++;
                    }
                    [weakController.userActivity setObject:[NSNumber numberWithFloat:sliderValue]
                                                    forKey:[NSString stringWithFormat:@"%ld", (long)tool]];
                }];
                // reset invoke
                [options setAdjustToolSelectedBlock:^(NSNumber * _Nullable adjust) {
					 if (adjust != nil) {
                        [weakController removeBanner];
                        NSDictionary *d = (NSDictionary *)[rawDictionary valueForKeyPath:@"nixAdjust"];
                        [weakController addBanner:[d objectForKey:@"title"] subtitle:[d objectForKey:@"subtitle"]];
                    } else {
                        [weakController removeBanner];
                    }
                    if ([adjust intValue] == 0 && weakController.needToUpgrade) {
                        [weakController.mainController.undoController undo];
                        [weakController.mainController.undoController undoStep];
                        [weakController.mainController.undoController undoStepInCurrentGroup];
                        [weakController.mainController.undoController removeAllActionsInCurrentGroup];
                        weakController.needToUpgrade = 0;
                        weakController.hasBegan = NO;
                    }
                }];
                // enter, leave
                [weakSelf handleApply:weakController opt:options key:@"adjust"];
                // undo, redo
                [options setOverlayButtonConfigurationClosure:^(PESDKOverlayButton * _Nonnull button, enum AdjustOverlayAction action) {
                    if ((long)action == 0) {
                        [weakController addButtonTrigger:button usage:@"undo"];
                    } else if ((long)action == 1) {
                        // redo
                        [weakController addButtonTrigger:button usage:@"redo"];
                    }
                }];
            }];

            // FILTER CONFIG
            [builder configureFilterToolController:^(PESDKFilterToolControllerOptionsBuilder * _Nonnull options) {
              // visible on the screen
              [options setFilterCellConfigurationClosure:^(PESDKMenuCollectionViewCell * _Nonnull cell, PESDKEffect * _Nonnull effect) {
                  // safe remove plus banner
                  [weakSelf removePlusBanner:cell];
				    if ([noneAndResetList containsObject:cell.captionTextLabel.text]) {
                      // do nothing
                  } else {
                      [weakSelf addPlusBanner:cell];
                  }
				  /*
                  if ([cell.captionTextLabel.text isEqualToString:@"None"]){
                      // do nothing
                  } else {
                      [weakSelf addPlusBanner:cell];
                  }
				  */
              }];
              // handling of upgrade new upgrade path
                [options setFilterSelectedClosure:^(PESDKEffect * _Nonnull effect) {
                    [weakController.userActivity setObject:effect.identifier
                                                    forKey:@"nixFilter"];
                    [weakController removeBanner];
                    if ([effect.identifier isEqualToString:@"None"]){
                        // do nothing
                        weakController.needToUpgrade = 0;
                    } else {
                        weakController.needToUpgrade = 1;
                        NSDictionary *d = (NSDictionary *)[rawDictionary valueForKeyPath:@"nixFilter"];
                        [weakController addBanner:[d objectForKey:@"title"] subtitle:[d objectForKey:@"subtitle"]];
                    }
                }];
                [options setFolderCellConfigurationClosure:^(PESDKMenuCollectionViewCell * _Nonnull cell) {
                    [cell setUserInteractionEnabled:YES];
                    [weakSelf removePlusBanner:cell];
                    [weakSelf addPlusBanner:cell];
                }];
                [weakSelf handleApply:weakController opt:options key:@"filter"];
            }];

            // TRANSFORM
            [builder configureTransformToolController:^(PESDKTransformToolControllerOptionsBuilder * _Nonnull options) {
                [options setWillLeaveToolClosure:^{
                    [weakController saveSerialDataWithKey:@"nonPlusActivity"];
                    weakController.hasBegan = NO;
                }];
                [options setDidEnterToolClosure:^{
                    weakController.currentEffects = @"filter";
                    [weakController trackEvent:@"transform_tap"];
                }];
                [options setApplyButtonConfigurationClosure:^(PESDKButton * _Nonnull button) {
                    [weakController trackEvent:@"transform_apply"];
                }];
            }];
        }
#pragma mark - Setup Top Level Menu
        // MAIN MENU
        [builder configureVideoEditViewController:^(PESDKVideoEditViewControllerOptionsBuilder * _Nonnull options) {
            [options setDiscardButtonConfigurationClosure:^(PESDKButton * _Nonnull button) {
                [controller addTargetDiscard:button];
            }];
            NSMutableArray<PESDKPhotoEditMenuItem *> *menuItems = [[PESDKPhotoEditMenuItem defaultItems] mutableCopy];
            [menuItems removeObjectAtIndex:0]; // remove video/trim
            if (isSubscriber == NO) {
                [menuItems removeLastObject]; // brush
                [options setActionButtonConfigurationBlock:^(PESDKMenuCollectionViewCell * _Nonnull cell, PESDKPhotoEditMenuItem * _Nonnull menuItem) {
                    // remove subview for transform
                    [weakSelf removePlusBanner:cell];

					// add plus icon
					//look for "Transform" localization in all languages to hide plus icon on Transform
                    NSBundle *bundle = [NSBundle mainBundle];
                    NSArray *resourceNameList = @[@"ImglyEN", @"ImglyDE", @"ImglyES", @"ImglyFR", @"ImglyIT", @"ImglyJA"];
                    NSMutableArray *transformStrings = [NSMutableArray new];
                    for (NSString *r in resourceNameList) {
                        NSString* path = [bundle pathForResource:r ofType:@"plist"];
                        NSDictionary *d = [[NSDictionary alloc] initWithContentsOfFile:path];
                        [transformStrings addObject:[d objectForKey:@"pesdk_transform_title_name"]]; //key for Transform
                    }
                      
                    BOOL isTransform = NO;
                    for (NSString *t in transformStrings) {
                        if ([cell.captionTextLabel.text isEqualToString:t]){
                            isTransform = YES;
                        }
                    }
                    if (!isTransform) {
                        [weakSelf addPlusBanner:cell];
                    }
                    //   if (![cell.captionTextLabel.text isEqualToString:@"Transform"]) {
                    //       [weakSelf addPlusBanner:cell];
                    //   }
                }];
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

- (void)handleApply:(RNVideoEditorSDK *)controller opt:(PESDKToolControllerOptionsBuilder *)options key:(NSString*)key {
    [options setDidEnterToolClosure:^{
        controller.currentEffects = key;
        controller.needToUpgrade = 0;
        controller.userActivity = nil;
        controller.userActivity = [[NSMutableDictionary alloc] init];
        // save last changes
        [controller saveSerialDataWithKey:@"nonPlusActivity"];
        [controller trackEvent:[NSString stringWithFormat:@"%@_tap", key]];
    }];
    [options setWillLeaveToolClosure:^{
        if (controller.hasBegan) {
            [controller.mainController.undoController endUndoGrouping];
        }
        if (controller.banner != nil) {
            [controller removeBanner];
        }
        controller.hasBegan = NO;
    }];
    [options setApplyButtonConfigurationClosure:^(PESDKButton * _Nonnull button) {
        [controller trackEvent:[NSString stringWithFormat:@"%@_apply", key]];
        [controller addButtonApply:button];
    }];
}

- (BOOL)hasUsedPlusAdjust:(NSDictionary *)activity {
    BOOL hasUsedAdvance = NO;
    for (NSString *key in [activity allKeys]) {
        if ([key integerValue] >= 0 && [[activity objectForKey:key] intValue] > 0) {
            hasUsedAdvance = YES;
        }
    }
    return hasUsedAdvance;
}

- (BOOL)isExistWithList:(NSArray *)list predicate:(NSPredicate*)predicate {
    return ([list filteredArrayUsingPredicate:predicate].count > 0);
}

- (void)addPlusBanner:(UICollectionViewCell *)cell {
    NSString *iconName = self.freeTrial ? @"ic_plus_trial" : @"ic_plus";
    dispatch_async(dispatch_get_main_queue(), ^{
        CGRect rect = cell.bounds;
        UIImageView *imageView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:iconName]];
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

- (void)saveSerialDataWithKey:(NSString *)key controller:(RNVideoEditorSDK *)controller {
    NSDictionary *state = @{ @"data" : controller.mainController.serializedSettings };
    NSMutableDictionary *md = [[NSMutableDictionary alloc] initWithDictionary:state];
    [controller.sharedDefaults setObject:[NSKeyedArchiver archivedDataWithRootObject:md]
                                forKey:key];
    [controller.sharedDefaults synchronize];
}

//- (void)showAlertPromptUpgrade:(RNVideoEditorSDK *)controller {
//    dispatch_async(dispatch_get_main_queue(), ^{
//        // @"Upgrade to Nixplay Plus now to apply these changes and enjoy more advanced features."
//      UIAlertController *alert = [UIAlertController alertControllerWithTitle:@"Unblock this feature?"
//                                                                     message:[NSString stringWithFormat:@"%d", controller.needToUpgrade]
//                                                              preferredStyle:UIAlertControllerStyleAlert];
//
//      UIAlertAction * action = [UIAlertAction actionWithTitle:@"Upgrade"
//                                                              style:UIAlertActionStyleDefault
//                                                            handler:^(UIAlertAction * _Nonnull action) {
//          // save last changes to user defaults for advance usage
//          [self saveSerialDataWithKey:@"lastChanges" controller:controller];
//
//          [controller.mainController play];
//          RCTPromiseResolveBlock resolve = controller.resolve;
//          [controller.mainController dismissViewControllerAnimated:YES completion:^{
//              resolve(@{ @"action": @"open-subscription", @"path": @2 });
//          }];
//                                                            }];
//      UIAlertAction * cancelAction = [UIAlertAction actionWithTitle:@"Discard"
//                                                              style:UIAlertActionStyleDefault
//                                                            handler:^(UIAlertAction * _Nonnull action) {
//          [controller.mainController.undoController undoStep];
//          [controller.mainController.undoController removeAllActionsInCurrentGroup];
//          [controller.mainController play];
//          [alert dismissViewControllerAnimated:YES completion:nil];
//                                                            }];
//      [alert addAction:cancelAction];
//      [alert addAction:action];
//
//      [controller.mainController presentViewController:alert animated:YES completion:nil];
//    });
//}

//- (BOOL)hasUsedAdvanceFeatures:(RNVideoEditorSDK *)controller {
//    BOOL hasUsedAdvance = NO;
//    if (controller.userActivity != nil) {
//        int entries = (int)[[controller.userActivity allKeys] count];
//        if (entries > 1) {
//            // adjustment
//            for (NSString *key in [controller.userActivity allKeys]) {
//                NSDictionary *effects = [controller.userActivity objectForKey:key];
//                if ([[effects objectForKey:@"value"] intValue] > 0 && !(BOOL)[effects objectForKey:@"isUsed"]) {
//                    hasUsedAdvance = YES;
//                }
//            }
//        } else if (entries == 1){
//            // filter, brush, frame, focus, text, text design, sticker
//            hasUsedAdvance = YES;
//        }
//    }
//    return hasUsedAdvance;
//}

- (void)addSubscriptionBanner:(NSDictionary *)rawDictionary
                   controller:(RNVideoEditorSDK *)controller
                          key:(NSString *)key
                   subscriber:(BOOL)isSubscriber
                          opt:(PESDKToolControllerOptionsBuilder *)options
{
    if (isSubscriber == NO) {
        [options setWillLeaveToolClosure:^{
            // reset
            [controller removeBanner];
            if (controller.userActivity != nil && controller.needToUpgrade) {
                [controller showPromptUpgrade:key];
            }
        }];
        [options setDidEnterToolClosure:^{
            // clearing user activity on enter
            controller.needToUpgrade = 0;
            controller.userActivity = nil;
            controller.userActivity = [[NSMutableDictionary alloc] init];
            // save last changes
            [self saveSerialDataWithKey:@"nonPlusActivity" controller:controller];
//            if ([key isEqual:@"nixAdjust"]) {
//                [controller.mainController.undoController setIsEnabled:NO];
//            } else {
//                [controller.mainController.undoController setIsEnabled:YES];
//            }
        }];
    }
}

- (void)resetEffectValidator:(RNVideoEditorSDK*)controller {
    if (controller.userActivity != nil && controller.needToUpgrade) {
        [controller resetEffectsOnExit:@""];
        controller.needToUpgrade = 0;
        controller.userActivity = nil;
        controller.userActivity = [[NSMutableDictionary alloc] init];
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
