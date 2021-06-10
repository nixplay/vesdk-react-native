package ly.img.react_native.vesdk.viewholder;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import ly.img.android.pesdk.ui.adapter.DataSourceListAdapter;
import ly.img.android.pesdk.ui.widgets.ImageSourceView;
import ly.img.react_native.vesdk.R;
import ly.img.react_native.vesdk.view.CustomFrameItem;

public class CustomFrameViewHolder extends DataSourceListAdapter.DataSourceViewHolder<CustomFrameItem, Bitmap> implements View.OnClickListener {
    @NonNull
    public final View contentHolder;
    @Nullable
    protected final TextView labelTextView;
    @Nullable
    protected final ImageSourceView imageView;
    private boolean isNoneFrameItem = false;

    @Keep
    public CustomFrameViewHolder(@NonNull View v) {
        super(v);
        this.imageView = v.findViewById(R.id.image);
        this.labelTextView = v.findViewById(R.id.label);
        this.contentHolder = v.findViewById(R.id.contentHolder);
        this.contentHolder.setOnClickListener(this);
    }

    public void onClick(View v) {
//        this.dispatchSelection();
//        this.dispatchOnItemClick();
    }

    protected Bitmap createAsyncData(CustomFrameItem abstractItem) {
        return abstractItem.hasStaticThumbnail() ? null : abstractItem.getThumbnailBitmap(Math.round(64.0F * this.uiDensity));
    }

    protected void bindData(CustomFrameItem data) {
        this.isNoneFrameItem = "imgly_frame_none".equals(data.getId());
        if (this.labelTextView != null) {
            this.labelTextView.setText(data.getName());
            this.labelTextView.setVisibility(this.isNoneFrameItem ? View.VISIBLE : View.INVISIBLE);
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

    protected void bindData(CustomFrameItem data, Bitmap bitmap) {
        super.bindData(data, bitmap);
        if (this.imageView != null) {
            this.imageView.setAlpha(1.0F);
            this.imageView.setImageBitmap(bitmap);
            this.imageView.setVisibility(this.isNoneFrameItem ? View.INVISIBLE : View.VISIBLE);
        }

    }

    public void setSelectedState(boolean selected) {
        this.contentHolder.setSelected(selected);
    }
}
