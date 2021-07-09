package ly.img.react_native.vesdk.view;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.facebook.react.bridge.ReadableMap;

import java.io.File;
import java.io.IOException;

import ly.img.android.pesdk.backend.model.state.AbsLayerSettings;
import ly.img.android.pesdk.backend.model.state.ColorAdjustmentSettings;
import ly.img.android.pesdk.backend.model.state.FilterSettings;
import ly.img.android.pesdk.backend.model.state.FocusSettings;
import ly.img.android.pesdk.backend.model.state.FrameSettings;
import ly.img.android.pesdk.backend.model.state.LayerListSettings;
import ly.img.android.pesdk.backend.model.state.OverlaySettings;
import ly.img.android.pesdk.backend.model.state.layer.ImageStickerLayerSettings;
import ly.img.android.pesdk.backend.model.state.layer.TextDesignLayerSettings;
import ly.img.android.pesdk.backend.model.state.layer.TextLayerSettings;
import ly.img.android.pesdk.backend.model.state.manager.StateHandler;
import ly.img.android.pesdk.ui.activity.VideoEditorActivity;

import ly.img.android.pesdk.ui.model.state.UiStateMenu;
import ly.img.android.serializer._3.IMGLYFileWriter;
import ly.img.react_native.pesdk.SubscriptionOverlay;
import ly.img.react_native.vesdk.R;
import ly.img.react_native.vesdk.RNVideoEditorSDKModule;

import static ly.img.react_native.vesdk.RNVideoEditorSDKModule._isSubscriber;
import static ly.img.react_native.vesdk.RNVideoEditorSDKModule.stickerConfig;
import static ly.img.react_native.vesdk.RNVideoEditorSDKModule.textConfig;
import static ly.img.react_native.vesdk.RNVideoEditorSDKModule.textDesignConfig;
import static ly.img.react_native.vesdk.RNVideoEditorSDKModule.RESULT_SUBSCRIBE;

public class CustomVideoEditorActivity extends VideoEditorActivity {
    private SubscriptionOverlay videoSubscriptionOverlayLayout;
    private TextView tv_subtitle;
    private UiStateMenu uiStateMenu;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uiStateMenu = getStateHandler().getStateModel(UiStateMenu.class);
        videoSubscriptionOverlayLayout = findViewById(R.id.subscription_overlay);
        tv_subtitle = findViewById(R.id.tv_features);
        videoSubscriptionOverlayLayout.setConfigs(RNVideoEditorSDKModule.configMap, tv_subtitle);
        videoSubscriptionOverlayLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                setResult(RESULT_SUBSCRIBE, intent);
                finish();
            }
        });
    }

    @Override
    protected void onAcceptClicked() {
        if (!_isSubscriber && !uiStateMenu.getCurrentPanelData().getId().equals("imgly_tool_transform")) {
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

            if (!filterSettings.getFilter().getId().equals("imgly_filter_none")) {
                showUpgradeDialog();
            } else if (adjustmentSettings.getBrightness() != 0.0 || adjustmentSettings.getContrast() != 0.0 ||
                    adjustmentSettings.getSaturation() != 0.0 || adjustmentSettings.getClarity() != 0.0 || adjustmentSettings.getShadow() != 0.0 ||
                    adjustmentSettings.getHighlight() != 0.0 || adjustmentSettings.getExposure() != 0.0 || adjustmentSettings.getGamma() != 1.0 ||
                    adjustmentSettings.getBlacks() != 0.0 || adjustmentSettings.getWhites() != 0.0 || adjustmentSettings.getTemperature() != 0.0 ||
                    adjustmentSettings.getSharpness() != 0.0) {
                showUpgradeDialog();
            } else if (!focusSettings.getFocusMode().equals(FocusSettings.MODE.NO_FOCUS)) {
                showUpgradeDialog();
            } else if (hasPlusSticker || hasPlusTextLayer || hasPlusTextDesignLayer) {
                if (uiStateMenu.getCurrentTool().toString().equals("ly.img.android.pesdk.ui.panels.TextOptionToolPanel")
                        || uiStateMenu.getCurrentTool().toString().equals("ly.img.android.pesdk.ui.panels.TextDesignOptionToolPanel")
                        || uiStateMenu.getCurrentTool().toString().equals("ly.img.android.pesdk.ui.panels.StickerOptionToolPanel")) {
                    showUpgradeDialog();
                } else {
                    super.onAcceptClicked();
                }
            } else if (!frameSettings.getFrameConfig().getId().equals("imgly_frame_none")) {
                showUpgradeDialog();
            } else if (!overlaySettings.getOverlayAsset().getId().equals("imgly_overlay_none")) {
                showUpgradeDialog();
            } else {
                super.onAcceptClicked();
            }
        } else {
            super.onAcceptClicked();
        }
    }

    private void showUpgradeDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Unblock this feature?")
                .setMessage("Upgrade to Nixplay Plus now to unblock this feature and enjoy more advance editing tools.")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        saveSerialization();
                        goToSubscriptionScreen(0);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        uiStateMenu.openMainMenu();
                    }
                })
                .show();
    }

    private void goToSubscriptionScreen(int target) {
        Intent intent = new Intent();
        intent.putExtra("targetScreen", target);
        setResult(RESULT_SUBSCRIBE, intent);
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

}

