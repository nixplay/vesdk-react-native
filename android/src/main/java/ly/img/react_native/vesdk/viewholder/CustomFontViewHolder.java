package ly.img.react_native.vesdk.viewholder;

import android.graphics.Typeface;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import ly.img.android.pesdk.backend.model.config.FontAsset;
import ly.img.android.pesdk.backend.model.state.AssetConfig;
import ly.img.android.pesdk.ui.adapter.DataSourceListAdapter;
import ly.img.android.pesdk.ui.panels.item.FontItem;
import ly.img.react_native.vesdk.R;
import ly.img.react_native.vesdk.view.CustomFontItem;

public class CustomFontViewHolder extends DataSourceListAdapter.DataSourceViewHolder<CustomFontItem, Typeface> implements View.OnClickListener {
    private final View contentHolder;
    @NonNull
    private final TextView textView;
    @NonNull
    private final TextView labelView;
    private AssetConfig assetConfig;

    @Keep
    public CustomFontViewHolder(@NonNull View v) {
        super(v);
        this.textView = v.findViewById(R.id.text);
        this.labelView = v.findViewById(R.id.label);
        this.contentHolder = v.findViewById(R.id.contentHolder);
        this.contentHolder.setOnClickListener(this);
        this.assetConfig = this.stateHandler.getSettingsModel(AssetConfig.class);
    }

    protected Typeface createAsyncData(FontItem data) {
        FontAsset asset = data.getData(this.assetConfig.getAssetMap(FontAsset.class));
        return asset.getTypeface();
    }

    protected void bindData(CustomFontItem data) {
        FontAsset asset = data.getData(this.assetConfig.getAssetMap(FontAsset.class));
        if (asset.isLocalAsset()) {
            this.textView.setTypeface(asset.getTypeface());
        }

        this.textView.setText(R.string.pesdk_text_button_fontPreview);
        this.labelView.setText(data.getName());
    }

    protected void bindData(CustomFontItem data, Typeface typeface) {
        super.bindData(data, typeface);
        this.textView.setTypeface(data.getData(this.assetConfig.getAssetMap(FontAsset.class)).getTypeface());
    }

    public void setSelectedState(boolean selected) {
        this.contentHolder.setSelected(selected);
    }

    public void onClick(View v) {
//        this.dispatchOnItemClick();
//        this.dispatchSelection();
    }
}
