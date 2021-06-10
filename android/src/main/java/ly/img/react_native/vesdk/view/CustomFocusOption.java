package ly.img.react_native.vesdk.view;

import android.os.Parcel;

import org.jetbrains.annotations.NotNull;

import ly.img.android.pesdk.ui.adapter.DataSourceListAdapter;
import ly.img.android.pesdk.ui.panels.item.FocusOption;
import ly.img.react_native.vesdk.R;
import ly.img.react_native.vesdk.viewholder.CustomAdjustOptionViewHolder;

public class CustomFocusOption extends FocusOption {

    public static final Creator<CustomFocusOption> CREATOR = new Creator<CustomFocusOption>() {
        public CustomFocusOption createFromParcel(Parcel source) {
            return new CustomFocusOption(source);
        }

        public CustomFocusOption[] newArray(int size) {
            return new CustomFocusOption[size];
        }
    };


    public CustomFocusOption(int id) {
        super(id);
    }

    protected CustomFocusOption(Parcel in) {
        super(in);
    }

    @Override
    public int getLayout() {
        return R.layout.imgly_list_item_option_plus;
    }

    @NotNull
    public Class<? extends DataSourceListAdapter.DataSourceViewHolder> getViewHolderClass() {
        return CustomAdjustOptionViewHolder.class;
    }
}
