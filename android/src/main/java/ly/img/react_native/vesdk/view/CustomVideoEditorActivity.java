package ly.img.react_native.vesdk.view;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

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

import static ly.img.react_native.vesdk.RNVideoEditorSDKModule.RESULT_DISCARD;
import static ly.img.react_native.vesdk.RNVideoEditorSDKModule.VESDK_RESULT;
import static ly.img.react_native.vesdk.RNVideoEditorSDKModule._isCameOnSubscription;
import static ly.img.react_native.vesdk.RNVideoEditorSDKModule._isSubscriber;
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
    }

    @Override
    protected void onAcceptClicked() {
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
                videoSubscriptionOverlayLayout.setHasChanges(false);
                super.onAcceptClicked();
            }
        } else {
            videoSubscriptionOverlayLayout.setHasChanges(false);
            super.onAcceptClicked();
        }
    }

    private void showUpgradeDialog() {
        final String effects = getCurrentEffects();
        mFirebaseAnalytics.logEvent("unblock_feat_v_" + effects + "_show", null);
        new AlertDialog.Builder(this)
                .setTitle(R.string.nixplay_upgradeEditorAlert)
                .setMessage(R.string.nixplay_text_upgradeEditorAlert)
                .setPositiveButton(R.string.nixplay_button_upgradeEditorAlertConfirmation, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
//                        saveSerialization();
                        mFirebaseAnalytics.logEvent("unblock_feat_v_" + effects + "_upgrade", null);
                        goToSubscriptionScreen(0);
                    }
                })
                .setNegativeButton(R.string.nixplay_button_upgradeEditorAlertCancelation, new DialogInterface.OnClickListener() {
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

