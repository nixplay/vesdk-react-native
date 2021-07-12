package ly.img.react_native.vesdk.viewholder;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;

import ly.img.android.pesdk.backend.filter.FilterAsset;
import ly.img.android.pesdk.backend.model.state.AssetConfig;
import ly.img.android.pesdk.ui.adapter.DataSourceListAdapter;
import ly.img.android.pesdk.ui.panels.FilterPreviewView;
import ly.img.react_native.vesdk.R;
import ly.img.react_native.vesdk.view.CustomFilterItem;

public class CustomImageFilterViewHolder extends DataSourceListAdapter.DataSourceViewHolder<CustomFilterItem, Bitmap> implements View.OnClickListener {
    private final View contentHolder;
    private final TextView labelTextView;
    private final FilterPreviewView filterPreviewView;
    private final AppCompatImageView nixPlusIcon;
    private final AssetConfig assetConfig;
    private boolean isEnabled;
    private boolean isNoneFilterItem = false;

    public CustomImageFilterViewHolder(@NonNull View v) {
        super(v);
        contentHolder = v.findViewById(R.id.contentHolder);
        labelTextView = v.findViewById(R.id.label);
        filterPreviewView = v.findViewById(R.id.filterPreview);
        nixPlusIcon = v.findViewById(R.id.nixPlusText);
        assetConfig = this.stateHandler.getSettingsModel(AssetConfig.class);
        contentHolder.setOnClickListener(this);
    }

    @Override
    protected void bindData(CustomFilterItem data) {
        this.isNoneFilterItem = "imgly_filter_none".equals(data.getId());
        if (this.labelTextView != null) {
            this.labelTextView.setText(data.getName());
        }

        if (!isNoneFilterItem) {
            isEnabled = data.isEnabled();
            nixPlusIcon.setVisibility(isEnabled ? View.GONE : View.VISIBLE);
        }

        FilterAsset filterAsset = data.getData(this.assetConfig.getAssetMap(FilterAsset.class));
        if (filterAsset != null) {
            FilterPreviewView var10000 = this.filterPreviewView;
            if (var10000 != null) {
                FilterPreviewView var3 = var10000;
                var3.setFilter(filterAsset);
                var3.render();
            }
        }

    }

    @Override
    public void setSelectedState(boolean selected) {
        contentHolder.setSelected(selected);
    }
    public void onClick(View v) {
        dispatchSelection();
        dispatchOnItemClick();
    }
}

