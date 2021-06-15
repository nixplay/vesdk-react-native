package ly.img.react_native.vesdk.view;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.facebook.react.bridge.ReadableMap;

import ly.img.android.pesdk.ui.activity.VideoEditorActivity;

import ly.img.react_native.pesdk.SubscriptionOverlay;
import ly.img.react_native.vesdk.R;
import ly.img.react_native.vesdk.RNVideoEditorSDKModule;

import static ly.img.react_native.vesdk.RNVideoEditorSDKModule.RESULT_SUBSCRIBE;

public class CustomVideoEditorActivity extends VideoEditorActivity {
    private SubscriptionOverlay videoSubscriptionOverlayLayout;
    private TextView tv_subtitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
}

