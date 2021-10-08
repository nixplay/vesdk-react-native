package ly.img.react_native.vesdk.view;

import android.os.Parcel;

import com.google.firebase.analytics.FirebaseAnalytics;

import ly.img.android.PESDK;
import ly.img.android.pesdk.annotations.OnEvent;
import ly.img.android.pesdk.backend.model.state.ColorAdjustmentSettings;
import ly.img.android.pesdk.backend.model.state.FilterSettings;
import ly.img.android.pesdk.backend.model.state.FocusSettings;
import ly.img.android.pesdk.backend.model.state.FrameSettings;
import ly.img.android.pesdk.backend.model.state.OverlaySettings;
import ly.img.android.pesdk.backend.model.state.TransformSettings;
import ly.img.android.pesdk.backend.model.state.manager.EventTracker;

public class CustomEventVideoTracker extends EventTracker {

    private FirebaseAnalytics mFirebaseAnalytics;

    public static final Creator<CustomEventVideoTracker> CREATOR = new Creator<CustomEventVideoTracker>() {
        @Override
        public CustomEventVideoTracker createFromParcel(Parcel source) {
            return new CustomEventVideoTracker(source);
        }

        @Override
        public CustomEventVideoTracker[] newArray(int size) {
            return new CustomEventVideoTracker[size];
        }
    };

    public CustomEventVideoTracker(String trackerId) {
        init(trackerId);
    }

    protected CustomEventVideoTracker(Parcel in) {
        super(in);
        init(in.readString());
    }

    private void init(String trackerId) {
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(PESDK.getAppContext());
    }

    /*
     * This annotated method tracks the following
     * transform, filter, adjustment, focus, overlay and frame
     * text, text design and sticker are track once it is being use
     */
    @OnEvent(value = TransformSettings.Event.ASPECT, ignoreReverts = true, triggerDelay = 1000)
    protected void onTransformAspect(ColorAdjustmentSettings colorAdjustmentSettings) {
        mFirebaseAnalytics.logEvent("vesdk_transform_tap", null);
    }

    @OnEvent(FilterSettings.Event.FILTER)
    protected void onFilterEvent(FilterSettings filterSettings) {
        mFirebaseAnalytics.logEvent("vesdk_filter_tap", null);
    }

    @OnEvent(value = ColorAdjustmentSettings.Event.BRIGHTNESS, ignoreReverts = true, triggerDelay = 1000)
    protected void onColorAdjustmentChangeBrightness(ColorAdjustmentSettings colorAdjustmentSettings) {
        mFirebaseAnalytics.logEvent("vesdk_adjust_tap", null);
    }

    @OnEvent(value = ColorAdjustmentSettings.Event.CONTRAST, ignoreReverts = true, triggerDelay = 1000)
    protected void onColorAdjustmentChangeContrast(ColorAdjustmentSettings colorAdjustmentSettings) {
        mFirebaseAnalytics.logEvent("vesdk_adjust_tap", null);
    }

    @OnEvent(value = ColorAdjustmentSettings.Event.SATURATION, ignoreReverts = true, triggerDelay = 1000)
    protected void onColorAdjustmentChangeSaturation(ColorAdjustmentSettings colorAdjustmentSettings) {
        mFirebaseAnalytics.logEvent("vesdk_adjust_tap", null);
    }

    @OnEvent(value = ColorAdjustmentSettings.Event.CLARITY, ignoreReverts = true, triggerDelay = 1000)
    protected void onColorAdjustmentChangeClarity(ColorAdjustmentSettings colorAdjustmentSettings) {
        mFirebaseAnalytics.logEvent("vesdk_adjust_tap", null);
    }

    @OnEvent(value = ColorAdjustmentSettings.Event.SHADOW, ignoreReverts = true, triggerDelay = 1000)
    protected void onColorAdjustmentChangeShadow(ColorAdjustmentSettings colorAdjustmentSettings) {
        mFirebaseAnalytics.logEvent("vesdk_adjust_tap", null);
    }

    @OnEvent(value = ColorAdjustmentSettings.Event.HIGHLIGHT, ignoreReverts = true, triggerDelay = 1000)
    protected void onColorAdjustmentChangeHightlight(ColorAdjustmentSettings colorAdjustmentSettings) {
        mFirebaseAnalytics.logEvent("vesdk_adjust_tap", null);
    }

    @OnEvent(value = ColorAdjustmentSettings.Event.EXPOSURE, ignoreReverts = true, triggerDelay = 1000)
    protected void onColorAdjustmentChangeExpose(ColorAdjustmentSettings colorAdjustmentSettings) {
        mFirebaseAnalytics.logEvent("vesdk_adjust_tap", null);
    }

    @OnEvent(value = ColorAdjustmentSettings.Event.GAMMA, ignoreReverts = true, triggerDelay = 1000)
    protected void onColorAdjustmentChangeGamma(ColorAdjustmentSettings colorAdjustmentSettings) {
        mFirebaseAnalytics.logEvent("vesdk_adjust_tap", null);
    }

    @OnEvent(value = ColorAdjustmentSettings.Event.BLACKS, ignoreReverts = true, triggerDelay = 1000)
    protected void onColorAdjustmentChangeBlacks(ColorAdjustmentSettings colorAdjustmentSettings) {
        mFirebaseAnalytics.logEvent("vesdk_adjust_tap", null);
    }

    @OnEvent(value = ColorAdjustmentSettings.Event.WHITES, ignoreReverts = true, triggerDelay = 1000)
    protected void onColorAdjustmentChangeWhites(ColorAdjustmentSettings colorAdjustmentSettings) {
        mFirebaseAnalytics.logEvent("vesdk_adjust_tap", null);
    }

    @OnEvent(value = ColorAdjustmentSettings.Event.TEMPERATURE, ignoreReverts = true, triggerDelay = 1000)
    protected void onColorAdjustmentChangeTemperature(ColorAdjustmentSettings colorAdjustmentSettings) {
        mFirebaseAnalytics.logEvent("vesdk_adjust_tap", null);
    }

    @OnEvent(value = ColorAdjustmentSettings.Event.SHARPNESS, ignoreReverts = true, triggerDelay = 1000)
    protected void onColorAdjustmentChangeSharpness(ColorAdjustmentSettings colorAdjustmentSettings) {
        mFirebaseAnalytics.logEvent("vesdk_adjust_tap", null);
    }

    @OnEvent(FocusSettings.Event.MODE)
    protected void onTransformEvent(FocusSettings focusSettings) {
        mFirebaseAnalytics.logEvent("vesdk_focus_tap", null);
    }

    @OnEvent(value = OverlaySettings.Event.BLEND_MODE, ignoreReverts = true, triggerDelay = 1000)
    protected void onOverlayEvent(OverlaySettings overlaySettings) {
        mFirebaseAnalytics.logEvent("vesdk_overlay_tap", null);
    }

    @OnEvent(FrameSettings.Event.FRAME_CONFIG)
    protected void onOverlayEvent(FrameSettings frameSettings) {
        mFirebaseAnalytics.logEvent("vesdk_frame_tap", null);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
