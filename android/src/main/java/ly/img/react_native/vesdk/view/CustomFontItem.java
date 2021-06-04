package ly.img.react_native.vesdk.view;

import android.os.Parcel;

import androidx.annotation.NonNull;

import ly.img.android.pesdk.ui.adapter.DataSourceListAdapter;
import ly.img.android.pesdk.ui.panels.item.FontItem;
import ly.img.react_native.vesdk.R;
import ly.img.react_native.vesdk.viewholder.CustomFontViewHolder;

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

    protected CustomFontItem(Parcel in) {
        super(in);
    }

    public int getLayout() {
        return R.layout.imgly_list_item_font_plus;
    }

    @NonNull
    public Class<? extends DataSourceListAdapter.DataSourceViewHolder> getViewHolderClass() {
        return CustomFontViewHolder.class;
    }
}
