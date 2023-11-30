#import <React/RCTBridgeModule.h>
#import <React/RCTViewManager.h>
#import "RNImglyKit.h"

#import <NMEasyTipView/RCEasyTipView.h>

#import <ImglyKit/ImglyKit-Swift.h>

/// The React Native module for VideoEditor SDK
@interface RNVideoEditorSDK : RNVESDKImglyKit <RCTBridgeModule>

@property (nonatomic, retain) PESDKVideoEditViewController * _Nullable mainController;
@property (nonatomic, strong) UIView * _Nullable banner;
@property (nonatomic, strong) NSUserDefaults * _Nonnull sharedDefaults;
@property (nonatomic, strong) NSMutableDictionary * _Nullable userActivity;
@property (nonatomic, strong) NSDictionary * _Nonnull alertPromptInfo;
@property (nonatomic, strong) NSString * _Nonnull currentEffects;
@property (nonatomic, strong) NSString * _Nonnull clampTooltip;
@property (nonatomic, strong) RCEasyTipView * _Nonnull tooltipView;
@property int needToUpgrade;
@property int textAdded;
@property int enableToValidate;
@property BOOL hasBegan;
@property BOOL flagTrial;
@property BOOL flagTooltip;
@property BOOL tooltipShown;
@property NSString * _Nonnull languageCode;

typedef void (^RNVESDKConfigurationBlock)(PESDKConfigurationBuilder * _Nonnull builder);
typedef void (^RNVESDKWillPresentBlock)(PESDKVideoEditViewController * _Nonnull videoEditViewController);

/// Set this closure to modify the @c Configuration before it is used to initialize a new @c VideoEditViewController instance.
/// The configuration defined in JavaScript and passed to @c VESDK.openEditor() is already applied to the provided @c ConfigurationBuilder object.
@property (class, strong, atomic, nullable) RNVESDKConfigurationBlock configureWithBuilder;

/// Set this closure to modify a new @c VideoEditViewController before it is presented on screen.
@property (class, strong, atomic, nullable) RNVESDKWillPresentBlock willPresentVideoEditViewController;

// generic method to present tool bar of editor sub menus
- (void)presentToolWithName:(NSString *_Nullable)toolName icon:(NSString *_Nullable)icon class:(Class _Nonnull )class;
- (void)addBanner:(NSString *_Nullable)nixTitle subtitle:(NSString *_Nullable)nixSubtitle;
- (void)addTargetDiscard:(UIButton * _Nonnull)button;
- (void)addButtonTrigger:(UIButton* _Nonnull)button usage:(NSString * _Nonnull)usage;
- (void)showPromptUpgrade:(NSString * _Nonnull)key;
- (void)resetEffectsOnExit:(NSString * _Nonnull)key;
- (void)addButtonApply:(UIButton* _Nonnull)button;
- (void)addButtonDiscard:(UIButton* _Nonnull)button;
- (void)saveSerialDataWithKey:(NSString * _Nonnull)key;
- (void)trackEvent:(NSString * _Nonnull)key;
- (void)removeBanner;
- (void)addTooltip:(UIView * _Nonnull)target;
- (void)hideTooltip;

@end
