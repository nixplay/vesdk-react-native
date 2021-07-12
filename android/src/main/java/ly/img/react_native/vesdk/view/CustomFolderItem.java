package ly.img.react_native.vesdk.view;

import android.os.Parcel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import ly.img.android.pesdk.backend.decoder.ImageSource;
import ly.img.android.pesdk.ui.adapter.DataSourceListAdapter;
import ly.img.android.pesdk.ui.panels.item.FolderItem;
import ly.img.react_native.vesdk.viewholder.CustomFolderViewHolder;
import ly.img.react_native.vesdk.R;

import static ly.img.react_native.vesdk.RNVideoEditorSDKModule.filterConfig;

public class CustomFolderItem extends FolderItem {

    public static final Creator<CustomFolderItem> CREATOR = new Creator<CustomFolderItem>() {
        public CustomFolderItem createFromParcel(Parcel source) {
            return new CustomFolderItem(source);
        }

        public CustomFolderItem[] newArray(int size) {
            return new CustomFolderItem[size];
        }
    };

    public CustomFolderItem(@Nullable String name, @Nullable ImageSource thumbnailSource, List itemList) {
        super(name, thumbnailSource, itemList);
    }

    public CustomFolderItem(int name, @Nullable ImageSource thumbnailSource, List itemList) {
        super(name, thumbnailSource, itemList);
    }

    public CustomFolderItem(String id, int name, @Nullable ImageSource thumbnailSource, List itemList) {
        super(id, name, thumbnailSource, itemList);
    }

    public CustomFolderItem(String id, @Nullable String name, @Nullable ImageSource thumbnailSource, List itemList) {
        super(id, name, thumbnailSource, itemList);
    }

    public boolean isEnabled() {
        return filterConfig.toArrayList().contains(getName());
    }

    protected CustomFolderItem(Parcel parcel) {
        super(parcel);
    }

    public int getLayout() {
        return R.layout.imgly_list_item_folder_plus;
    }

    @NonNull
    public Class<? extends DataSourceListAdapter.DataSourceViewHolder> getViewHolderClass() {
        return CustomFolderViewHolder.class;
    }

}
