package ly.img.react_native.vesdk.view;

import android.os.Parcel;

import androidx.annotation.NonNull;

import ly.img.android.pesdk.backend.decoder.ImageSource;
import ly.img.android.pesdk.ui.adapter.DataSourceListAdapter;
import ly.img.android.pesdk.ui.panels.item.ToolItem;
import ly.img.react_native.vesdk.RNVideoEditorSDKModule;
import ly.img.react_native.vesdk.viewholder.CustomDefaultViewHolder;
import ly.img.react_native.vesdk.R;

public class CustomToolItem extends ToolItem {
    public static final Creator<CustomToolItem> CREATOR = new Creator<CustomToolItem>() {
        public CustomToolItem createFromParcel(Parcel source) {
            return new CustomToolItem(source);
        }

        public CustomToolItem[] newArray(int size) {
            return new CustomToolItem[size];
        }
    };
    public CustomToolItem(String id, int name, ImageSource iconSource) {
        super(id, name, iconSource);
    }

    public CustomToolItem(String id, String name, ImageSource iconSource) {
        super(id, name, iconSource);
    }

    protected CustomToolItem(Parcel in) {
        super(in);
    }

    @Override
    public int getLayout() {
        return RNVideoEditorSDKModule.getTrialFlag() ? R.layout.imgly_list_item_tool_plus_trial : R.layout.imgly_list_item_tool_plus;
    }

    @NonNull
    public Class<? extends DataSourceListAdapter.DataSourceViewHolder> getViewHolderClass() {
        return CustomDefaultViewHolder.class;
    }
}
