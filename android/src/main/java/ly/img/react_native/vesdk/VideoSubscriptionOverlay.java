package ly.img.react_native.vesdk;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.MainThread;
import androidx.constraintlayout.widget.ConstraintLayout;

import ly.img.android.pesdk.annotations.OnEvent;
import ly.img.android.pesdk.backend.model.state.manager.StateHandler;
import ly.img.android.pesdk.ui.model.state.UiStateMenu;
import ly.img.android.pesdk.ui.panels.AbstractToolPanel;

import static ly.img.react_native.vesdk.RNVideoEditorSDKModule._isSubscriber;


public class VideoSubscriptionOverlay extends ConstraintLayout {
    private UiStateMenu settings;

    public VideoSubscriptionOverlay(Context context) {
        super(context);
    }

    public VideoSubscriptionOverlay(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VideoSubscriptionOverlay(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @MainThread
    @OnEvent(value = {
            UiStateMenu.Event.ENTER_TOOL,
    }, triggerDelay = 30)
    protected void onToolChanged() {
        AbstractToolPanel currentTool = settings != null ? settings.getCurrentTool() : null;
        if (currentTool != null && currentTool.isAttached() && !_isSubscriber) {
            if (!UiStateMenu.MAIN_TOOL_ID.equals(settings.getCurrentPanelData().getId()) && !settings.getCurrentPanelData().getId().equals("imgly_tool_transform")
                    && !settings.getCurrentPanelData().getId().equals("imgly_tool_trim")) {
                Window window = ((Activity) getContext()).getWindow();
                Drawable background = this.getResources().getDrawable(R.drawable.subscription_gradient_bg);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(this.getResources().getColor(android.R.color.transparent));
                window.setNavigationBarColor(this.getResources().getColor(android.R.color.transparent));
                window.setBackgroundDrawable(background);
                setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        try {
            StateHandler stateHandler = StateHandler.findInViewContext(getContext());
            stateHandler.registerSettingsEventListener(this);
            settings = stateHandler.getStateModel(UiStateMenu.class);
        } catch (StateHandler.StateHandlerNotFoundException ignored) {
            ignored.printStackTrace();
        }
    }

}
