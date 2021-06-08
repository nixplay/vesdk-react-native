#import <React/RCTBridgeModule.h>
#import "RNImglyKit.h"

@import VideoEditorSDK;

/// The React Native module for VideoEditor SDK
@interface RNVideoEditorSDK : RNVESDKImglyKit <RCTBridgeModule>

@property (nonatomic, retain) PESDKVideoEditViewController * _Nullable mainController;
@property (nonatomic, strong) UIView * _Nullable banner;

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

@end
