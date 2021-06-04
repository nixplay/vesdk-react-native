package ly.img.react_native.vesdk.view;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import ly.img.android.pesdk.ui.activity.VideoEditorActivity;
import ly.img.react_native.vesdk.R;
import ly.img.react_native.vesdk.VideoSubscriptionOverlay;

import static ly.img.react_native.vesdk.RNVideoEditorSDKModule.RESULT_SUBSCRIBE;

public class CustomVideoEditorActivity extends VideoEditorActivity {
    private String TAG = "EditorActivity";
    private VideoSubscriptionOverlay videoSubscriptionOverlayLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        videoSubscriptionOverlayLayout = findViewById(R.id.video_subscription_overlay);
        videoSubscriptionOverlayLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                setResult(RESULT_SUBSCRIBE, intent);
                finish();
            }
        });
    }
}

