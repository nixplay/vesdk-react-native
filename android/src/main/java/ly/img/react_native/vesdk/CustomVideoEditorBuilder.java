package ly.img.react_native.vesdk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.jetbrains.annotations.Nullable;

import ly.img.android.pesdk.ui.activity.EditorBuilder;
import ly.img.react_native.vesdk.view.CustomVideoEditorActivity;

public class CustomVideoEditorBuilder extends EditorBuilder {
    private static final Class activityClass = CustomVideoEditorActivity.class;

    public CustomVideoEditorBuilder(@Nullable Intent intent) {
        super(intent, activityClass);
    }

    public CustomVideoEditorBuilder(@Nullable Activity activity) {
        super(activity, activityClass);
    }

    public CustomVideoEditorBuilder(@Nullable Activity activity, @Nullable Class<? extends Activity> activityClass) {
        super(activity, activityClass);
    }
}

