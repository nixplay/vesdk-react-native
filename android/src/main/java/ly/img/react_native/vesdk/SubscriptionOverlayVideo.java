package ly.img.react_native.vesdk;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.facebook.react.bridge.ReadableMap;

import ly.img.android.pesdk.annotations.OnEvent;
import ly.img.android.pesdk.backend.model.state.HistoryState;
import ly.img.android.pesdk.backend.model.state.LayerListSettings;
import ly.img.android.pesdk.backend.model.state.manager.StateHandler;
import ly.img.android.pesdk.ui.model.state.UiStateMenu;
import ly.img.android.pesdk.ui.panels.AbstractToolPanel;

import static ly.img.react_native.vesdk.RNVideoEditorSDKModule._isSubscriber;

public class SubscriptionOverlayVideo extends ConstraintLayout {
    private UiStateMenu settings;
    private TextView tv_subtitle;
    public ReadableMap configMap;
    private StateHandler stateHandler;
    private boolean hasChanges;

    public SubscriptionOverlayVideo(Context context) {
        super(context);
    }

    public SubscriptionOverlayVideo(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SubscriptionOverlayVideo(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setConfigs(ReadableMap configMap, TextView tv_subtitle) {
        this.configMap = configMap;
        this.tv_subtitle = tv_subtitle;
    }

    public void setHasChanges(boolean hasChanges) {
        this.hasChanges = hasChanges;
    }

    @OnEvent(value = {
            UiStateMenu.Event.ENTER_GROUND,
    }, triggerDelay = 50)
    public void onEnterGround(UiStateMenu uiStateMenu) {
        if (uiStateMenu.getCurrentTool().toString().equals("ly.img.android.pesdk.ui.panels.TextToolPanel")) {
            return;
        }
        if (!_isSubscriber && hasChanges && settings.getCurrentTool().isAttached()) {
            stateHandler.getStateModel(HistoryState.class).revertToInitial(0);
            stateHandler.getStateModel(HistoryState.class).removeAll(0);
        }
    }

    @OnEvent(LayerListSettings.Event.REMOVE_LAYER)
    public void onRemoveLayer() {
        hasChanges = true;
    }

    @OnEvent(LayerListSettings.Event.ADD_LAYER)
    public void onAddLayer() {
        hasChanges = true;
    }

    @OnEvent(value = {
            UiStateMenu.Event.ENTER_TOOL,
    }, triggerDelay = 50)
    public void onToolChanged() {
        AbstractToolPanel currentTool = settings != null ? settings.getCurrentTool() : null;
        if (!_isSubscriber) {
            if (currentTool != null && currentTool.isAttached() && !UiStateMenu.MAIN_TOOL_ID.equals(settings.getCurrentPanelData().getId())
                    && !settings.getCurrentPanelData().getId().equals("imgly_tool_transform")) {
                Window window = ((Activity) getContext()).getWindow();
                Drawable background = this.getResources().getDrawable(R.drawable.subscription_gradient_bg);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(this.getResources().getColor(android.R.color.transparent));
                window.setNavigationBarColor(this.getResources().getColor(android.R.color.transparent));
                window.setBackgroundDrawable(background);
                setVisibility(View.VISIBLE);
                if (settings.getCurrentPanelData().getId().equals("imgly_tool_filter")) {
                    tv_subtitle.setText(configMap.getMap("nixFilter").getString("subtitle"));
                    saveSerialization();
                } else if (settings.getCurrentPanelData().getId().equals("imgly_tool_adjustment")) {
                    tv_subtitle.setText(configMap.getMap("nixAdjust").getString("subtitle"));
                    saveSerialization();
                } else if (settings.getCurrentPanelData().getId().equals("imgly_tool_focus")) {
                    tv_subtitle.setText(configMap.getMap("nixFocus").getString("subtitle"));
                    saveSerialization();
                } else if (settings.getCurrentPanelData().getId().equals("imgly_tool_sticker_selection")) {
                    tv_subtitle.setText(configMap.getMap("nixSticker").getString("subtitle"));
                    saveSerialization();
                } else if (settings.getCurrentPanelData().getId().equals("imgly_tool_text")) {
                    tv_subtitle.setText(configMap.getMap("nixText").getString("subtitle"));
                    saveSerialization();
                } else if (settings.getCurrentPanelData().getId().equals("imgly_tool_text_design")) {
                    tv_subtitle.setText(configMap.getMap("nixTextDesign").getString("subtitle"));
                    saveSerialization();
                } else if (settings.getCurrentPanelData().getId().equals("imgly_tool_overlay")) {
                    tv_subtitle.setText(configMap.getMap("nixOverlay").getString("subtitle"));
                    saveSerialization();
                } else if (settings.getCurrentPanelData().getId().equals("imgly_tool_frame_replacement")) {
                    tv_subtitle.setText(configMap.getMap("nixFrame").getString("subtitle"));
                    saveSerialization();
                }
            }
        }
    }

    private void saveSerialization() {
//        IMGLYFileWriter writer = new IMGLYFileWriter(stateHandler.createSettingsListDump());
//
//        File file = new File(Environment.getExternalStorageDirectory(), "staveState.pesdk");
//        try {
//            if (file.exists()) {
//                file.delete();
//            }
//            file.createNewFile();
//            writer.writeJson(file);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        try {
            stateHandler = StateHandler.findInViewContext(getContext());
            stateHandler.registerSettingsEventListener(this);
            settings = stateHandler.getStateModel(UiStateMenu.class);
        } catch (StateHandler.StateHandlerNotFoundException ignored) {
            ignored.printStackTrace();
        }
    }

}
