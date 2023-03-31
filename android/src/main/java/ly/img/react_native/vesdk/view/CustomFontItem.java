package ly.img.react_native.vesdk.view;

import android.os.Parcel;

import androidx.annotation.NonNull;

import ly.img.android.pesdk.ui.adapter.DataSourceListAdapter;
import ly.img.android.pesdk.ui.panels.item.FontItem;
import ly.img.react_native.vesdk.viewholder.CustomFontViewHolder;
import ly.img.react_native.vesdk.R;

import static ly.img.react_native.vesdk.RNVideoEditorSDKModule._freeTrial;
import static ly.img.react_native.vesdk.RNVideoEditorSDKModule.textConfig;

public class CustomFontItem extends FontItem {
    public static final Creator<CustomFontItem> CREATOR = new Creator<CustomFontItem>() {
        public CustomFontItem createFromParcel(Parcel source) {
            return new CustomFontItem(source);
        }

        public CustomFontItem[] newArray(int size) {
            return new CustomFontItem[size];
        }
    };

    public CustomFontItem(String id, String name) {
        super(id, name);
    }

    public boolean isEnabled() {
        return textConfig.toArrayList().contains(getName());
    }

    protected CustomFontItem(Parcel in) {
        super(in);
    }

    public int getLayout() {
        return _freeTrial ? R.layout.imgly_list_item_font_plus_trial : R.layout.imgly_list_item_font_plus;
    }

    @NonNull
    public Class<? extends DataSourceListAdapter.DataSourceViewHolder> getViewHolderClass() {
        return CustomFontViewHolder.class;
    }
}
