package ly.img.react_native.vesdk.view;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.facebook.react.views.text.ReactFontManager;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.File;
import java.io.IOException;

import ly.img.android.pesdk.backend.model.state.AbsLayerSettings;
import ly.img.android.pesdk.backend.model.state.ColorAdjustmentSettings;
import ly.img.android.pesdk.backend.model.state.FilterSettings;
import ly.img.android.pesdk.backend.model.state.FocusSettings;
import ly.img.android.pesdk.backend.model.state.FrameSettings;
import ly.img.android.pesdk.backend.model.state.HistoryState;
import ly.img.android.pesdk.backend.model.state.LayerListSettings;
import ly.img.android.pesdk.backend.model.state.OverlaySettings;
import ly.img.android.pesdk.backend.model.state.layer.ImageStickerLayerSettings;
import ly.img.android.pesdk.backend.model.state.layer.TextDesignLayerSettings;
import ly.img.android.pesdk.backend.model.state.layer.TextLayerSettings;
import ly.img.android.pesdk.backend.model.state.manager.StateHandler;
import ly.img.android.pesdk.ui.activity.VideoEditorActivity;

import ly.img.android.pesdk.ui.model.state.UiStateMenu;
import ly.img.android.serializer._3.IMGLYFileWriter;
import ly.img.react_native.vesdk.SubscriptionOverlayVideo;
import ly.img.react_native.vesdk.R;
import ly.img.react_native.vesdk.RNVideoEditorSDKModule;
import ly.img.react_native.vesdk.ViewTooltip;

import static ly.img.react_native.vesdk.RNVideoEditorSDKModule.RESULT_DISCARD;
import static ly.img.react_native.vesdk.RNVideoEditorSDKModule.VESDK_RESULT;
import static ly.img.react_native.vesdk.RNVideoEditorSDKModule._freeTrial;
import static ly.img.react_native.vesdk.RNVideoEditorSDKModule._isCameOnSubscription;
import static ly.img.react_native.vesdk.RNVideoEditorSDKModule._isSubscriber;
import static ly.img.react_native.vesdk.RNVideoEditorSDKModule.alertPromptInfo;
import static ly.img.react_native.vesdk.RNVideoEditorSDKModule.stickerConfig;
import static ly.img.react_native.vesdk.RNVideoEditorSDKModule.textConfig;
import static ly.img.react_native.vesdk.RNVideoEditorSDKModule.textDesignConfig;
import static ly.img.react_native.vesdk.RNVideoEditorSDKModule.RESULT_SUBSCRIBE;

public class CustomVideoEditorActivity extends VideoEditorActivity {
    private SubscriptionOverlayVideo videoSubscriptionOverlayLayout;
    private TextView tv_title;
    private TextView tv_subtitle;
    private UiStateMenu uiStateMenu;
    private FirebaseAnalytics mFirebaseAnalytics;
    public static ViewTooltip.TooltipView tooltip_view;

    public static void closeTooltip() {
        if (_freeTrial && tooltip_view != null) {
            tooltip_view.close();
            tooltip_view = null;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.imgly_activity_video_editor);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        uiStateMenu = getStateHandler().getStateModel(UiStateMenu.class);
        if (!_isSubscriber) {
            videoSubscriptionOverlayLayout = findViewById(R.id.subscription_overlay_video);
            videoSubscriptionOverlayLayout.configMap = RNVideoEditorSDKModule.configMap;
            tv_title = findViewById(R.id.tv_add_more_features_video);
            tv_subtitle = findViewById(R.id.tv_features_video);
            videoSubscriptionOverlayLayout.setConfigs(RNVideoEditorSDKModule.configMap, tv_subtitle, tv_title);
            videoSubscriptionOverlayLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    setResult(RESULT_SUBSCRIBE, intent);
                    finishActivity(VESDK_RESULT);
                    finish();
                }
            });
        }

        // insert tooltip for free trial subscription
        if (_freeTrial) {
            Typeface typeface = ReactFontManager.getInstance().getTypeface("NotoSans-SemiBold", 0, getApplicationContext().getAssets());
             this.tooltip_view = ViewTooltip
                    .on(this, findViewById(R.id.imglyActionBar))
                    .autoHide(false, 0)
                    .corner(30)
                    .distanceWithView(220)
                    .arrowHeight(20)
                    .clickToHide(true)
                    .margin(180,30,180,30)
                    .position(ViewTooltip.Position.TOP)
                    .textTypeFace(typeface)
                    .textSize(1, 16)
                    .text(alertPromptInfo.getString("tooltip"))
                    .align(ViewTooltip.ALIGN.CENTER)
                    .show();
        }
    }

    @Override
    protected void onAcceptClicked() {
        this.closeTooltip();
        String rawToolId = uiStateMenu.getCurrentPanelData().getId();
        String toolEffect = formatEffectsKey(rawToolId);
        // avoid double record for imgly_tool_text and imgly_tool_text_design
        if (rawToolId.equals("imgly_tool_text") || rawToolId.equals("imgly_tool_text_design")) {
            // do nothing
        } else {
            mFirebaseAnalytics.logEvent(toolEffect, null);
        }
        if (!_isSubscriber && !uiStateMenu.getCurrentPanelData().getId().equals("imgly_tool_transform") && uiStateMenu.getCurrentTool().isAttached()) {
            boolean hasPlusSticker = false;
            boolean hasPlusTextLayer = false;
            boolean hasPlusTextDesignLayer = false;
            StateHandler stateHandler = getStateHandler();
            LayerListSettings layerListSettings = stateHandler.get(LayerListSettings.class);
            layerListSettings.acquireLayerReadLock();
            for (AbsLayerSettings layer : layerListSettings.getLayerSettingsList()) {
                if (layer instanceof ImageStickerLayerSettings) {
                    ImageStickerLayerSettings sticker = (ImageStickerLayerSettings) layer;
                    String stickerId = sticker.getStickerConfig().getId();
                    hasPlusSticker = !stickerConfig.toArrayList().contains(stickerId);
                    if (hasPlusSticker) {
                        break;
                    }
                } else if (layer instanceof TextLayerSettings) {
                    TextLayerSettings textLayerSettings = (TextLayerSettings) layer;
                    String textLayerId = textLayerSettings.getStickerConfig().getFont().getId();
                    hasPlusTextLayer = !textConfig.toArrayList().contains(textLayerId);
                    if (hasPlusTextLayer) {
                        break;
                    }
                } else if (layer instanceof TextDesignLayerSettings) {
                    TextDesignLayerSettings textDesignLayerSettings = (TextDesignLayerSettings) layer;
                    String textDesignLayerId = textDesignLayerSettings.getStickerConfig().getId();
                    hasPlusTextDesignLayer = !textDesignConfig.toArrayList().contains(textDesignLayerId);
                    if (hasPlusTextDesignLayer) {
                        break;
                    }
                }
            }
            layerListSettings.releaseLayerReadLock();
            FilterSettings filterSettings = stateHandler.get(FilterSettings.class);
            ColorAdjustmentSettings adjustmentSettings = stateHandler.get(ColorAdjustmentSettings.class);
            FocusSettings focusSettings = stateHandler.get(FocusSettings.class);
            FrameSettings frameSettings = stateHandler.get(FrameSettings.class);
            OverlaySettings overlaySettings = stateHandler.get(OverlaySettings.class);
            videoSubscriptionOverlayLayout.setHasChanges(true);
            if (filterSettings.hasChanges()) {
                showUpgradeDialog();
            } else if (adjustmentSettings.hasChanges()) {
                showUpgradeDialog();
            } else if (focusSettings.hasChanges()) {
                showUpgradeDialog();
            } else if (hasPlusSticker || hasPlusTextLayer || hasPlusTextDesignLayer) {
                if (uiStateMenu.getCurrentTool().toString().equals("ly.img.android.pesdk.ui.panels.TextOptionToolPanel")
                        || uiStateMenu.getCurrentTool().toString().equals("ly.img.android.pesdk.ui.panels.TextDesignOptionToolPanel")
                        || uiStateMenu.getCurrentTool().toString().equals("ly.img.android.pesdk.ui.panels.StickerOptionToolPanel")) {
                    showUpgradeDialog();
                } else {
                    super.onAcceptClicked();
                }
            } else if (frameSettings.hasChanges()) {
                showUpgradeDialog();
            } else if (overlaySettings.hasChanges()) {
                showUpgradeDialog();
            } else {
                if (!_isSubscriber) { videoSubscriptionOverlayLayout.setHasChanges(false); };
                super.onAcceptClicked();
            }
        } else {
            if (!_isSubscriber) { videoSubscriptionOverlayLayout.setHasChanges(false); };
            super.onAcceptClicked();
        }
    }

    private void showUpgradeDialog() {
        final String effects = getCurrentEffects();
        mFirebaseAnalytics.logEvent("unblock_feat_v_show", null);
        // mFirebaseAnalytics.logEvent("unblock_feat_v_" + effects + "_show", null);
        new AlertDialog.Builder(this)
                .setTitle(alertPromptInfo.getString("title"))
                .setMessage(alertPromptInfo.getString("message"))
                .setPositiveButton(alertPromptInfo.getString("upgrade"), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
//                        saveSerialization();
                        // mFirebaseAnalytics.logEvent("unblock_feat_v_" + effects + "_upgrade", null);
                        goToSubscriptionScreen(2);
                    }
                })
                .setNegativeButton(alertPromptInfo.getString("cancel"), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                       revertToInitial();
                    }
                })
                .setCancelable(false)
                .show();
    }

    private void showDismissDialog() {
        final String effects = getCurrentEffects();
        mFirebaseAnalytics.logEvent("unblock_feat_v_" + effects + "_show", null);
        new AlertDialog.Builder(this)
                .setTitle(R.string.pesdk_editor_title_closeEditorAlert)
                .setMessage(R.string.pesdk_editor_text_closeEditorAlert)
                .setPositiveButton(R.string.pesdk_editor_button_closeEditorAlertConfirmation, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
//                        saveSerialization();
                        discardChanges();
                    }
                })
                .setNegativeButton(R.string.pesdk_editor_button_closeEditorAlertCancelation, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setCancelable(false)
                .show();
    }

    private String formatEffectsKey(String raw) {
        String currentEffects = raw.replace("imgly_tool_", "vesdk_");
        currentEffects = currentEffects.replace("_options", "");
        currentEffects = currentEffects.replace("_replacement", "");
        if (currentEffects.equals("text_design")) {
            currentEffects = currentEffects.replace("text_design", "textdesign");
        }
        currentEffects = currentEffects.concat("_apply");
        return currentEffects;
    }

    @Override
    protected void onCancelClicked() {
        getStateHandler().getStateModel(HistoryState.class).revertToInitial(0);
        getStateHandler().getStateModel(HistoryState.class).removeAll(0);
        super.onCancelClicked();
    }

    public void revertToInitial() {
        getStateHandler().getStateModel(HistoryState.class).revertToInitial(0);
        getStateHandler().getStateModel(HistoryState.class).removeAll(0);
        onCancelClicked();
    }

    private void discardChanges() {
        Intent intent = new Intent();
        setResult(RESULT_DISCARD, intent);
        finishActivity(VESDK_RESULT);
        finish();
    }

    private void goToSubscriptionScreen(int target) {
        Intent intent = new Intent();
        intent.putExtra("targetScreen", target);
        setResult(RESULT_SUBSCRIBE, intent);
        finishActivity(VESDK_RESULT);
        finish();
    }

    private void saveSerialization() {
        IMGLYFileWriter writer = new IMGLYFileWriter(getStateHandler().createSettingsListDump());

        File file = new File(Environment.getExternalStorageDirectory(), "staveState.pesdk.plus");
        try {
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
            writer.writeJson(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getCurrentEffects () {
        String effects = uiStateMenu.getCurrentPanelData().getId().toString().replaceAll("imgly_tool_","");
        effects = effects.replaceAll("_options", "");
        effects = effects.replaceAll("_replacement", "");
        if (effects.contains("adjustment")) {
            effects = "adjust";
        }
        return effects;
    }

    @Override
    protected void onCloseClicked() {
//        deleteSerialization();
        super.onCloseClicked();
        // verify if there has changes
        StateHandler stateHandler = getStateHandler();
        if (stateHandler.hasChanges()) {
            showDismissDialog();
        }
    }

    public void deleteSerialization() {
        File file = new File(Environment.getExternalStorageDirectory(), "staveState.pesdk");
        try {
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

