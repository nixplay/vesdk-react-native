package ly.img.react_native.vesdk.view;

import android.os.Parcel;

import org.jetbrains.annotations.NotNull;

import ly.img.android.pesdk.backend.decoder.ImageSource;
import ly.img.android.pesdk.ui.adapter.DataSourceListAdapter;
import ly.img.android.pesdk.ui.panels.item.AdjustOption;
import ly.img.react_native.vesdk.viewholder.CustomAdjustOptionViewHolder;
import ly.img.react_native.vesdk.R;

import static ly.img.react_native.vesdk.RNVideoEditorSDKModule.adjustConfig;
import static ly.img.react_native.vesdk.RNVideoEditorSDKModule.getTrialFlag;

public class CustomAdjustOption extends AdjustOption {

    public static final Creator<CustomAdjustOption> CREATOR = new Creator<CustomAdjustOption>() {
        public CustomAdjustOption createFromParcel(Parcel source) {
            return new CustomAdjustOption(source);
        }

        public CustomAdjustOption[] newArray(int size) {
            return new CustomAdjustOption[size];
        }
    };

    public CustomAdjustOption(int id, int name, ImageSource iconSource) {
        super(id, name, iconSource);
    }

    public CustomAdjustOption(int id, String name, ImageSource iconSource) {
        super(id, name, iconSource);
    }

    public boolean isEnabled() {
        return adjustConfig.toArrayList().contains(getName());
    }

    protected CustomAdjustOption(Parcel in) {
        super(in);
    }

    public boolean isSelectable() {
        return true;
    }

    public int describeContents() {
        return 0;
    }

    public int getLayout() {
        return getTrialFlag() ? R.layout.imgly_list_item_option_plus_trial : R.layout.imgly_list_item_option_plus;
    }

    @NotNull
    public Class<? extends DataSourceListAdapter.DataSourceViewHolder> getViewHolderClass() {
        return CustomAdjustOptionViewHolder.class;
    }

    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }

}
