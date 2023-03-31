package ly.img.react_native.vesdk.viewholder;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import ly.img.android.pesdk.ui.adapter.DataSourceListAdapter;
import ly.img.android.pesdk.ui.widgets.ImageSourceView;
import ly.img.react_native.vesdk.R;
import ly.img.react_native.vesdk.view.CustomFolderItem;
import ly.img.react_native.vesdk.view.CustomVideoEditorActivity;

public class CustomFolderViewHolder extends DataSourceListAdapter.DataSourceViewHolder<CustomFolderItem, Bitmap> implements View.OnClickListener {
    public final View contentHolder;
    @Nullable
    private final TextView textView;
    @Nullable
    private final ImageSourceView imageView;
    private final AppCompatImageView nixPlusIcon;
    private boolean isEnabled;

    public CustomFolderViewHolder(@NonNull View itemView) {
        super(itemView);
        this.textView = itemView.findViewById(R.id.label);
        this.imageView = itemView.findViewById(R.id.image);
        this.contentHolder = itemView.findViewById(R.id.contentHolder);
        this.nixPlusIcon = itemView.findViewById(R.id.nixPlusText);
        this.contentHolder.setOnClickListener(this);
        this.receiveTouches = true;
    }

    @Override
    public void onClick(View view) {
        CustomVideoEditorActivity.closeTooltip();
        this.dispatchOnItemClick();
    }

    @Override
    protected void bindData(CustomFolderItem customFolderItem) {
        isEnabled = customFolderItem.isEnabled();
        nixPlusIcon.setVisibility(isEnabled ? View.GONE : View.VISIBLE);
        this.itemView.setContentDescription(customFolderItem.getName());
        this.itemView.setSelected(customFolderItem.isOpen());
        if (this.textView != null) {
            this.textView.setText(customFolderItem.getName());
        }

        if (this.imageView != null) {
            if (customFolderItem.hasStaticThumbnail()) {
                this.imageView.setAlpha(1.0F);
                this.imageView.setImageSource(customFolderItem.getThumbnailSource());
            } else {
                this.imageView.setAlpha(0.0F);
            }
        }
    }

    @Override
    protected void bindData(CustomFolderItem data, Bitmap bitmap) {
        if (this.imageView != null) {
            this.imageView.setAlpha(1.0F);
            this.imageView.setImageBitmap(bitmap);
        }
    }

    @Override
    public void setSelectedState(boolean b) {}
}
