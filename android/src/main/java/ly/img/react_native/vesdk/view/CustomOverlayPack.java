package ly.img.react_native.vesdk.view;

import ly.img.android.pesdk.backend.decoder.ImageSource;
import ly.img.android.pesdk.ui.panels.item.OverlayItem;
import ly.img.android.pesdk.ui.utils.DataSourceIdItemList;
import ly.img.react_native.vesdk.R;

public class CustomOverlayPack {
    public CustomOverlayPack() {
    }

    public static DataSourceIdItemList<OverlayItem> getOverlayPack() {
        DataSourceIdItemList<OverlayItem> overlayList = new DataSourceIdItemList();
        overlayList.add(new OverlayItem("imgly_overlay_none", R.string.pesdk_overlay_asset_none, ImageSource.create(R.drawable.imgly_filter_preview_photo)));
        overlayList.add(new OverlayItem("imgly_overlay_golden", ly.img.android.pesdk.ui.overlay.R.string.pesdk_overlay_asset_golden, ImageSource.create(R.drawable.imgly_overlay_golden_thumb)));
        overlayList.add(new OverlayItem("imgly_overlay_lightleak1", ly.img.android.pesdk.ui.overlay.R.string.pesdk_overlay_asset_lightleak1, ImageSource.create(R.drawable.imgly_overlay_lightleak1_thumb)));
        overlayList.add(new OverlayItem("imgly_overlay_rain", ly.img.android.pesdk.ui.overlay.R.string.pesdk_overlay_asset_rain, ImageSource.create(R.drawable.imgly_overlay_rain_thumb)));
        overlayList.add(new OverlayItem("imgly_overlay_mosaic", ly.img.android.pesdk.ui.overlay.R.string.pesdk_overlay_asset_mosaic, ImageSource.create(R.drawable.imgly_overlay_mosaic_thumb)));
        overlayList.add(new OverlayItem("imgly_overlay_vintage", ly.img.android.pesdk.ui.overlay.R.string.pesdk_overlay_asset_vintage, ImageSource.create(R.drawable.imgly_overlay_vintage_thumb)));
        overlayList.add(new OverlayItem("imgly_overlay_paper", ly.img.android.pesdk.ui.overlay.R.string.pesdk_overlay_asset_paper, ImageSource.create(R.drawable.imgly_overlay_paper_thumb)));
        return overlayList;
    }
}
