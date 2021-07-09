package ly.img.react_native.vesdk.view;

import android.os.Parcel;

import androidx.annotation.NonNull;

import ly.img.android.pesdk.ui.adapter.DataSourceListAdapter;
import ly.img.android.pesdk.ui.panels.item.FilterItem;
import ly.img.react_native.vesdk.viewholder.CustomImageFilterViewHolder;
import ly.img.react_native.vesdk.R;

import static ly.img.react_native.vesdk.RNVideoEditorSDKModule.filterConfig;

public class CustomFilterItem extends FilterItem {
    public CustomFilterItem(String id, int name) {
        super(id, name);
    }

    public CustomFilterItem(String id, String name) {
        super(id, name);
    }

    public boolean isEnabled() {
        return filterConfig.toArrayList().contains(getName());
    }

    @Override
    public int getLayout() {
        return R.layout.imgly_list_item_filter;
    }

    @Override
    public int getLayout(String flavor) {
        return flavor.equals("FLAVOR_OPTION_LIST_FOLDER_SUBITEM") ? R.layout.imgly_list_item_filter_folder_subitem_plus : R.layout.imgly_list_item_filter;
    }

    @Override
    public boolean isSelectable() {
        return false;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public boolean isPurchased(){
        // Todo: Your code here!
        return false;
    }

    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }

    @NonNull
    @Override
    public Class<? extends DataSourceListAdapter.DataSourceViewHolder> getViewHolderClass() {
        return CustomImageFilterViewHolder.class;
    }

    protected CustomFilterItem(Parcel in) {
        super(in);
    }

    public static final Creator<CustomFilterItem> CREATOR = new Creator<CustomFilterItem>() {
        @Override
        public CustomFilterItem createFromParcel(Parcel source) {
            return new CustomFilterItem(source);
        }
        @Override
        public CustomFilterItem[] newArray(int size) {
            return new CustomFilterItem[size];
        }
    };
}
