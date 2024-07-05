package ly.img.react_native.vesdk.viewholder;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Keep;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import ly.img.android.PESDK;
import ly.img.android.pesdk.backend.model.state.OverlaySettings;
import ly.img.android.pesdk.ui.adapter.DataSourceListAdapter;
import ly.img.android.pesdk.ui.panels.item.OverlayItem;
import ly.img.android.pesdk.ui.widgets.ImageSourceView;
import ly.img.react_native.vesdk.R;
import ly.img.react_native.vesdk.view.CustomOverlayItem;
import ly.img.react_native.vesdk.view.CustomVideoEditorActivity;

public class CustomOverlayViewHolder extends DataSourceListAdapter.DataSourceViewHolder<CustomOverlayItem, Bitmap> implements View.OnClickListener {
    public static boolean SHOW_PREVIEW_IMAGE_ON_NONE_OVERLAY_ITEM = true;
    @NonNull
    public final View contentHolder;
    @Nullable
    protected final TextView labelTextView;
    @Nullable
    protected final ImageSourceView imageView;
    @Nullable
    protected final ImageSourceView selectedOverlay;
    @Nullable
    protected final ImageSourceView selectedShuffle;
    private final AppCompatImageView nixPlusIcon;
    private boolean isEnabled;
    private boolean isNoneOverlayItem = false;

    @Keep
    public CustomOverlayViewHolder(@NonNull View v) {
        super(v);
        this.imageView = v.findViewById(R.id.image);
        this.labelTextView = v.findViewById(R.id.label);
        this.contentHolder = v.findViewById(R.id.contentHolder);
        this.selectedOverlay = v.findViewById(R.id.selected_overlay);
        this.selectedShuffle = v.findViewById(R.id.selected_shuffle);
        this.nixPlusIcon = v.findViewById(R.id.nixPlusText);
        this.contentHolder.setOnClickListener(this);
    }

    public void setSelectedState(boolean selected) {

    }

    public void onClick(View v) {
        CustomVideoEditorActivity.closeTooltip();
        this.dispatchSelection();
        this.dispatchOnItemClick();
    }

    protected Bitmap createAsyncData(CustomOverlayItem abstractItem) {
        return abstractItem.hasStaticThumbnail() ? null : abstractItem.getThumbnailBitmap(Math.round(64.0F * this.uiDensity));
    }

    protected void bindData(CustomOverlayItem data) {
        if (this.labelTextView != null) {
            this.labelTextView.setText(data.getName());
        }

        this.isNoneOverlayItem = "imgly_overlay_none".equals(data.getId());
        if (this.selectedOverlay != null) {
            this.selectedOverlay.setVisibility(this.isNoneOverlayItem ? View.INVISIBLE : View.VISIBLE);
        }

        if (this.selectedShuffle != null) {
            this.selectedShuffle.setVisibility(this.isNoneOverlayItem ? View.INVISIBLE : View.VISIBLE);
        }

        if (!isNoneOverlayItem) {
            isEnabled = data.isEnabled();
            nixPlusIcon.setVisibility(isEnabled ? View.GONE : View.VISIBLE);
        }

        if (this.imageView != null) {
            if (data.hasStaticThumbnail()) {
                this.imageView.setAlpha(1.0F);
                this.imageView.setImageSource(data.getThumbnailSource());
            } else {
                this.imageView.setAlpha(0.0F);
            }
        }

    }

    protected void bindData(OverlayItem data, Bitmap bitmap) {
        if (this.imageView != null) {
            this.imageView.setAlpha(1.0F);
            this.imageView.setImageBitmap(bitmap);
        }

    }
}
