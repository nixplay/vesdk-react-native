package ly.img.react_native.vesdk.effects;

import ly.img.android.pesdk.backend.decoder.ImageSource;
import ly.img.android.pesdk.ui.panels.item.OverlayItem;
import ly.img.android.pesdk.ui.utils.DataSourceIdItemList;
import ly.img.react_native.vesdk.view.CustomOverlayItem;
import ly.img.react_native.vesdk.R;

public class CustomOverlayPack {

    public CustomOverlayPack() {
    }

    public static DataSourceIdItemList<OverlayItem> getOverlayPack() {
        DataSourceIdItemList<OverlayItem> overlayList = new DataSourceIdItemList();
        overlayList.add(new CustomOverlayItem("imgly_overlay_none", R.string.pesdk_overlay_asset_none, ImageSource.create(ly.img.react_native.pesdk.R.drawable.imgly_filter_preview_photo)));
        overlayList.add(new CustomOverlayItem("imgly_overlay_golden", R.string.pesdk_overlay_asset_golden, ImageSource.create(ly.img.react_native.pesdk.R.drawable.imgly_overlay_golden_thumb)));
        overlayList.add(new CustomOverlayItem("imgly_overlay_lightleak1", R.string.pesdk_overlay_asset_lightleak1, ImageSource.create(ly.img.react_native.pesdk.R.drawable.imgly_overlay_lightleak1_thumb)));
        overlayList.add(new CustomOverlayItem("imgly_overlay_rain", R.string.pesdk_overlay_asset_rain, ImageSource.create(ly.img.react_native.pesdk.R.drawable.imgly_overlay_rain_thumb)));
        overlayList.add(new CustomOverlayItem("imgly_overlay_mosaic", R.string.pesdk_overlay_asset_mosaic, ImageSource.create(ly.img.react_native.pesdk.R.drawable.imgly_overlay_mosaic_thumb)));
        overlayList.add(new CustomOverlayItem("imgly_overlay_vintage", R.string.pesdk_overlay_asset_vintage, ImageSource.create(ly.img.react_native.pesdk.R.drawable.imgly_overlay_vintage_thumb)));
        overlayList.add(new CustomOverlayItem("imgly_overlay_paper", R.string.pesdk_overlay_asset_paper, ImageSource.create(ly.img.react_native.pesdk.R.drawable.imgly_overlay_paper_thumb)));
        return overlayList;
    }
}
