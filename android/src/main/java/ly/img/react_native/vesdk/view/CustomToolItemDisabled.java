package ly.img.react_native.vesdk.view;

import android.os.Parcel;

import androidx.annotation.NonNull;

import ly.img.android.pesdk.backend.decoder.ImageSource;
import ly.img.android.pesdk.ui.adapter.DataSourceListAdapter;
import ly.img.android.pesdk.ui.panels.item.ToolItem;
import ly.img.react_native.vesdk.R;
import ly.img.react_native.vesdk.RNVideoEditorSDKModule;
import ly.img.react_native.vesdk.viewholder.CustomToolItemDisabledViewHolder;

public class CustomToolItemDisabled extends ToolItem {
    public static final Creator<CustomToolItemDisabled> CREATOR = new Creator<CustomToolItemDisabled>() {
        public CustomToolItemDisabled createFromParcel(Parcel source) {
            return new CustomToolItemDisabled(source);
        }

        public CustomToolItemDisabled[] newArray(int size) {
            return new CustomToolItemDisabled[size];
        }
    };
    public CustomToolItemDisabled(String id, int name, ImageSource iconSource) {
        super(id, name, iconSource);
    }


    public CustomToolItemDisabled(String id, String name, ImageSource iconSource) {
        super(id, name, iconSource);
    }

    protected CustomToolItemDisabled(Parcel in) {
        super(in);
    }

    @Override
    public int getLayout() {
        return RNVideoEditorSDKModule.getTrialFlag() ? R.layout.imgly_list_item_tool_plus_trial : R.layout.imgly_list_item_tool_plus;
    }

    @NonNull
    public Class<? extends DataSourceListAdapter.DataSourceViewHolder> getViewHolderClass() {
        return CustomToolItemDisabledViewHolder.class;
    }
}
