package ly.img.react_native.vesdk.view;

import android.os.Parcel;

import androidx.annotation.NonNull;

import ly.img.android.pesdk.backend.decoder.ImageSource;
import ly.img.android.pesdk.ui.adapter.DataSourceListAdapter;
import ly.img.android.pesdk.ui.panels.item.OverlayItem;
import ly.img.android.pesdk.ui.viewholder.OverlayViewHolder;
import ly.img.react_native.vesdk.viewholder.CustomOverlayViewHolder;
import ly.img.react_native.vesdk.R;

import static ly.img.react_native.vesdk.RNVideoEditorSDKModule.overlayConfig;

public class CustomOverlayItem extends OverlayItem {

    public static final Creator<CustomOverlayItem> CREATOR = new Creator<CustomOverlayItem>() {
        public CustomOverlayItem createFromParcel(Parcel source) {
            return new CustomOverlayItem(source);
        }

        public CustomOverlayItem[] newArray(int size) {
            return new CustomOverlayItem[size];
        }
    };

    public CustomOverlayItem(String id, int name, ImageSource previewSource) {
        super(id, name, previewSource);
    }

    public CustomOverlayItem(String id, String name, ImageSource previewSource) {
        super(id, name, previewSource);
    }

    public boolean isEnabled() {
        return overlayConfig.toArrayList().contains(getName());
    }

    protected CustomOverlayItem(Parcel in) {
        super(in);
    }

    public int getLayout() {
        return this.id.equals("imgly_overlay_none") && OverlayViewHolder.SHOW_PREVIEW_IMAGE_ON_NONE_OVERLAY_ITEM ? R.layout.imgly_list_item_overlay_thumbnail : R.layout.imgly_list_item_overlay_plus;
    }

    @NonNull
    public Class<? extends DataSourceListAdapter.DataSourceViewHolder> getViewHolderClass() {
        return CustomOverlayViewHolder.class;
    }
}
