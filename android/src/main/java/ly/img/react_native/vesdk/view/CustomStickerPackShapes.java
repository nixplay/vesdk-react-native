package ly.img.react_native.vesdk.view;

import ly.img.android.pesdk.backend.decoder.ImageSource;
import ly.img.android.pesdk.ui.panels.item.ImageStickerItem;
import ly.img.android.pesdk.ui.panels.item.StickerCategoryItem;
import ly.img.android.pesdk.ui.utils.DataSourceIdItemList;
import ly.img.react_native.vesdk.R;

public class CustomStickerPackShapes {

    public CustomStickerPackShapes() {
    }

    private static DataSourceIdItemList<ImageStickerItem> stickerItemList;

    public static void setStickerItemList(DataSourceIdItemList<ImageStickerItem> stickerItemList) {
        CustomStickerPackShapes.stickerItemList = stickerItemList;
    }



    public static DataSourceIdItemList<ImageStickerItem> getStickerPack() {
        DataSourceIdItemList stickers = new DataSourceIdItemList();

        stickers.addAll(stickerItemList);
        return stickers;
    }


    public static StickerCategoryItem getStickerCategory() {
        return new StickerCategoryItem("imgly_sticker_category_shapes", R.string.imgly_sticker_category_name_shapes, ImageSource.create(R.drawable.imgly_sticker_shapes_badge_12), getStickerPack());
    }
}
