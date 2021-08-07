package ly.img.react_native.vesdk.view;

import android.os.Parcel;

import androidx.annotation.NonNull;

import ly.img.android.pesdk.backend.decoder.ImageSource;
import ly.img.android.pesdk.ui.adapter.DataSourceListAdapter;
import ly.img.android.pesdk.ui.panels.item.FrameItem;
import ly.img.react_native.vesdk.viewholder.CustomFrameViewHolder;
import ly.img.react_native.vesdk.R;

import static ly.img.react_native.vesdk.RNVideoEditorSDKModule.frameConfig;

public class CustomFrameItem extends FrameItem {
    public static final Creator<CustomFrameItem> CREATOR = new Creator<CustomFrameItem>() {
        public CustomFrameItem createFromParcel(Parcel source) {
            return new CustomFrameItem(source);
        }

        public CustomFrameItem[] newArray(int size) {
            return new CustomFrameItem[size];
        }
    };


    public CustomFrameItem(String id, int name, ImageSource drawableId) {
        super(id, name, drawableId);
    }

    public CustomFrameItem(String id, String name, ImageSource drawableId) {
        super(id, name, drawableId);
    }

    public boolean isEnabled() {
        return frameConfig.toArrayList().contains(getName());
    }

    protected CustomFrameItem(Parcel in) {
        super(in);
    }

    public int getLayout() {
        return "imgly_frame_none".equals(this.getId()) ? R.layout.imgly_list_item_none_frame : ly.img.react_native.vesdk.R.layout.imgly_list_item_frame_plus;
    }

    @NonNull
    public Class<? extends DataSourceListAdapter.DataSourceViewHolder> getViewHolderClass() {
        return CustomFrameViewHolder.class;
    }
}
