package ly.img.react_native.vesdk.viewholder;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import ly.img.android.pesdk.ui.adapter.DataSourceListAdapter;
import ly.img.android.pesdk.ui.panels.item.AbstractItem;
import ly.img.android.pesdk.ui.widgets.ImageSourceView;
import ly.img.react_native.vesdk.R;

public class CustomImageStickerViewHolder extends DataSourceListAdapter.DataSourceViewHolder<AbstractItem, CustomImageStickerViewHolder.ThumbnailResult> implements View.OnClickListener {
    public final View contentHolder;
    @Nullable
    private final TextView textView;
    @Nullable
    private final ImageSourceView imageView;
    private final Context context;

    public CustomImageStickerViewHolder(@NonNull View v) {
        super(v);
        this.textView = v.findViewById(R.id.label);
        this.imageView = v.findViewById(R.id.image);
        this.contentHolder = v.findViewById(R.id.contentHolder);
        this.contentHolder.setOnClickListener(this);
        this.context = v.getContext();
    }

    @Override
    public void onClick(View view) {
//        this.dispatchSelection();
//        this.dispatchOnItemClick();
    }

    @Override
    protected void bindData(AbstractItem abstractItem) {
        this.itemView.setContentDescription(abstractItem.getName());
        if (this.textView != null) {
            this.textView.setText(abstractItem.getName());
        }

        if (this.imageView != null) {
            if (abstractItem.hasStaticThumbnail()) {
                this.imageView.setImageSource(abstractItem.getThumbnailSource());
            } else {
                this.imageView.setImageBitmap(null);
            }
        }
    }

    @Override
    protected void bindData(AbstractItem data, CustomImageStickerViewHolder.ThumbnailResult thumbnailResult) {
        if (this.imageView != null && thumbnailResult != null) {
            if (thumbnailResult.bitmap != null) {
                this.imageView.setImageBitmap(thumbnailResult.bitmap);
            } else {
                this.imageView.setImageDrawable(thumbnailResult.drawable);
            }
        }
    }

    @Override
    public void setSelectedState(boolean b) {
        this.contentHolder.setSelected(b);
    }

    public class ThumbnailResult {
        private Bitmap bitmap;
        private Drawable drawable;

        ThumbnailResult(Bitmap bitmap) {
            this.bitmap = bitmap;
        }

        ThumbnailResult(Drawable drawable) {
            this.drawable = drawable;
        }
    }
}
