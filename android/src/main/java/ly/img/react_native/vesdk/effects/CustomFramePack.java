package ly.img.react_native.vesdk.effects;

import ly.img.android.pesdk.assets.frame.basic.R.string;
import ly.img.android.pesdk.backend.decoder.ImageSource;
import ly.img.android.pesdk.ui.frame.R.drawable;
import ly.img.android.pesdk.ui.panels.item.FrameItem;
import ly.img.android.pesdk.ui.utils.DataSourceIdItemList;
import ly.img.react_native.vesdk.R;
import ly.img.react_native.vesdk.view.CustomFrameItem;

public class CustomFramePack {
    public CustomFramePack() {
    }

    public static DataSourceIdItemList<FrameItem> getFramePack() {
        DataSourceIdItemList<FrameItem> frameList = new DataSourceIdItemList();
        frameList.add(new CustomFrameItem("imgly_frame_none", R.string.pesdk_frame_button_none, ImageSource.create(drawable.imgly_icon_option_frame_none)));
        frameList.add(new CustomFrameItem("imgly_frame_dia", string.pesdk_frame_asset_dia, ImageSource.create(ly.img.android.pesdk.assets.frame.basic.R.drawable.imgly_frame_dia_thumb)));
        frameList.add(new CustomFrameItem("imgly_frame_art_decor", string.pesdk_frame_asset_artDecor, ImageSource.create(ly.img.android.pesdk.assets.frame.basic.R.drawable.imgly_frame_art_decor_thumb)));
        frameList.add(new CustomFrameItem("imgly_frame_black_passepartout", string.pesdk_frame_asset_blackPassepartout, ImageSource.create(ly.img.android.pesdk.assets.frame.basic.R.drawable.imgly_frame_black_passepartout_thumb)));
        frameList.add(new CustomFrameItem("imgly_frame_wood_passepartout", string.pesdk_frame_asset_woodPassepartout, ImageSource.create(ly.img.android.pesdk.assets.frame.basic.R.drawable.imgly_frame_wood_passepartout_thumb)));
        return frameList;
    }
}
