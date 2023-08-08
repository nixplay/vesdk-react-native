package ly.img.react_native.vesdk.viewholder;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import org.jetbrains.annotations.NotNull;

import ly.img.android.pesdk.ui.adapter.DataSourceListAdapter;
import ly.img.android.pesdk.ui.widgets.ImageSourceView;
import ly.img.react_native.vesdk.R;
import ly.img.react_native.vesdk.view.CustomFocusOption;
import ly.img.react_native.vesdk.view.CustomVideoEditorActivity;

public class CustomFocusOptionViewHolder extends DataSourceListAdapter.DataSourceViewHolder<CustomFocusOption, CustomFocusOptionViewHolder.ThumbnailResult> implements View.OnClickListener {
    public final View contentHolder;
    @Nullable
    private final TextView textView;
    @Nullable
    private final ImageSourceView imageView;
    private final AppCompatImageView nixPlusIcon;
    private final Context context;
    private boolean isEnabled;

    public CustomFocusOptionViewHolder(@NonNull @NotNull View v) {
        super(v);
        this.textView = v.findViewById(R.id.label);
        this.imageView = v.findViewById(R.id.image);
        this.contentHolder = v.findViewById(R.id.contentHolder);
        this.nixPlusIcon = v.findViewById(R.id.nixPlusText);
        this.contentHolder.setOnClickListener(this);
        this.context = v.getContext();
    }

    @Override
    public void onClick(View view) {
        CustomVideoEditorActivity.closeTooltip();
        this.dispatchSelection();
        this.dispatchOnItemClick();
    }

    @Override
    protected void bindData(CustomFocusOption customFocusOption) {
        isEnabled = customFocusOption.isEnabled();
        nixPlusIcon.setVisibility(isEnabled ? View.GONE : View.VISIBLE);
        this.itemView.setContentDescription(customFocusOption.getName());
        if (this.textView != null) {
            this.textView.setText(customFocusOption.getName());
        }

        if (this.imageView != null) {
            if (customFocusOption.hasStaticThumbnail()) {
                this.imageView.setImageSource(customFocusOption.getThumbnailSource());
            } else {
                this.imageView.setImageBitmap(null);
            }
        }
    }

    @Override
    protected void bindData(CustomFocusOption data, CustomFocusOptionViewHolder.ThumbnailResult thumbnailResult) {
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
