package ly.img.react_native.vesdk.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.MainThread;

import ly.img.android.pesdk.backend.decoder.ImageSource;
import ly.img.android.pesdk.backend.model.state.LoadState;
import ly.img.android.pesdk.backend.model.state.manager.StateHandler;
import ly.img.android.pesdk.ui.model.state.UiStateMenu;
import ly.img.android.pesdk.ui.panels.AbstractToolPanel;
import ly.img.android.pesdk.ui.widgets.AutoRotateImageSource;
import ly.img.react_native.vesdk.R;

public class CustomAcceptButton extends AutoRotateImageSource implements View.OnClickListener {
    private final ImageSource confirmIcon;
    private UiStateMenu settings;

    public CustomAcceptButton(Context context) {
        super(context);
        this.confirmIcon = ImageSource.create(R.drawable.imgly_icon_confirm);
        this.init();
    }

    public CustomAcceptButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.confirmIcon = ImageSource.create(R.drawable.imgly_icon_confirm);
        this.init();
    }

    public CustomAcceptButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.confirmIcon = ImageSource.create(R.drawable.imgly_icon_confirm);
        this.init();
    }

    private void init() {
        this.setImageSource(this.confirmIcon);
        this.setOnClickListener(this);
    }

    @MainThread
    protected void onImageBroken(LoadState loadState) {
        this.setVisibility(loadState.isSourceBroken() ? INVISIBLE : VISIBLE);
    }

    @MainThread
    protected void onToolChanged() {
        AbstractToolPanel currentTool = this.settings != null ? this.settings.getCurrentTool() : null;
        if (currentTool != null && currentTool.isAttached()) {
            this.setVisibility(currentTool.isAcceptable() ? VISIBLE : GONE);
        }

    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        try {
            StateHandler stateHandler = StateHandler.findInViewContext(this.getContext());
            stateHandler.registerSettingsEventListener(this);
            this.settings = (UiStateMenu)stateHandler.getStateModel(UiStateMenu.class);
        } catch (StateHandler.StateHandlerNotFoundException var2) {
            var2.printStackTrace();
        }

    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        try {
            StateHandler.findInViewContext(this.getContext()).unregisterSettingsEventListener(this);
        } catch (StateHandler.StateHandlerNotFoundException var2) {
            var2.printStackTrace();
        }

        this.settings = null;
    }

    public void onClick(View v) {
        if (this.settings != null) {
            if ("imgly_tool_mainmenu".equals(this.settings.getCurrentPanelData().getId())) {
                this.settings.notifySaveClicked();
            } else {
                this.settings.notifyAcceptClicked();
            }
        }

    }
}
