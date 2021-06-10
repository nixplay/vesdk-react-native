package ly.img.react_native.vesdk.view;

import android.os.Parcel;

import org.jetbrains.annotations.NotNull;

import ly.img.android.pesdk.backend.decoder.ImageSource;
import ly.img.android.pesdk.ui.adapter.DataSourceListAdapter;
import ly.img.android.pesdk.ui.panels.item.ImageStickerItem;
import ly.img.react_native.vesdk.R;
import ly.img.react_native.vesdk.viewholder.CustomImageStickerViewHolder;

public class CustomImageStickerItem extends ImageStickerItem {

    public static final Creator<CustomImageStickerItem> CREATOR = new Creator<CustomImageStickerItem>() {
        public CustomImageStickerItem createFromParcel(Parcel source) {
            return new CustomImageStickerItem(source);
        }

        public CustomImageStickerItem[] newArray(int size) {
            return new CustomImageStickerItem[size];
        }
    };

    public CustomImageStickerItem(String id, int name, ImageSource previewSource) {
        super(id, name, previewSource);
    }

    public CustomImageStickerItem(String id, String name, ImageSource previewSource) {
        super(id, name, previewSource);
    }

    protected CustomImageStickerItem(Parcel in) {
        super(in);
    }

    public int getLayout() {
        return R.layout.imgly_list_item_sticker_plus;
    }

    @NotNull
    public Class<? extends DataSourceListAdapter.DataSourceViewHolder> getViewHolderClass() {
        return CustomImageStickerViewHolder.class;
    }
}
