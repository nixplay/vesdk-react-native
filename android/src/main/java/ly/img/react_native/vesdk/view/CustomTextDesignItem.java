package ly.img.react_native.vesdk.view;

import android.os.Parcel;

import androidx.annotation.NonNull;

import ly.img.android.pesdk.backend.decoder.ImageSource;
import ly.img.android.pesdk.ui.adapter.DataSourceListAdapter;
import ly.img.android.pesdk.ui.panels.item.TextDesignItem;
import ly.img.react_native.vesdk.viewholder.CustomTextDesignViewHolder;
import ly.img.react_native.vesdk.R;

import static ly.img.react_native.vesdk.RNVideoEditorSDKModule.textDesignConfig;

public class CustomTextDesignItem extends TextDesignItem {

    public static final Creator<CustomTextDesignItem> CREATOR = new Creator<CustomTextDesignItem>() {
        public CustomTextDesignItem createFromParcel(Parcel source) {
            return new CustomTextDesignItem(source);
        }

        public CustomTextDesignItem[] newArray(int size) {
            return new CustomTextDesignItem[size];
        }
    };


    public CustomTextDesignItem(String id, int name, ImageSource drawableId) {
        super(id, name, drawableId);
    }

    public CustomTextDesignItem(String id, String name, ImageSource drawableId) {
        super(id, name, drawableId);
    }

    protected CustomTextDesignItem(Parcel in) {
        super(in);
    }

    public boolean isEnabled() {
        return textDesignConfig.toArrayList().contains(getName());
    }

    public int getLayout() {
        return R.layout.imgly_list_item_text_design_plus;
    }

    @NonNull
    public Class<? extends DataSourceListAdapter.DataSourceViewHolder> getViewHolderClass() {
        return CustomTextDesignViewHolder.class;
    }
}
