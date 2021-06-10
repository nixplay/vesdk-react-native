package ly.img.react_native.vesdk.view;

import ly.img.android.pesdk.backend.decoder.ImageSource;
import ly.img.android.pesdk.ui.panels.item.ImageStickerItem;
import ly.img.android.pesdk.ui.panels.item.StickerCategoryItem;
import ly.img.android.pesdk.ui.utils.DataSourceIdItemList;
import ly.img.react_native.vesdk.R;

public class CustomStickerPackEmoticons {
    public CustomStickerPackEmoticons() {
    }

    private static DataSourceIdItemList<ImageStickerItem> stickerItemList;

    public static void setStickerItemList(DataSourceIdItemList<ImageStickerItem> stickerItemList) {
        CustomStickerPackEmoticons.stickerItemList = stickerItemList;
    }



    public static DataSourceIdItemList<ImageStickerItem> getStickerPack() {
        DataSourceIdItemList stickers = new DataSourceIdItemList();

        stickers.addAll(stickerItemList);
        return stickers;
    }


    public static StickerCategoryItem getStickerCategory() {
        return new StickerCategoryItem("imgly_sticker_category_emoticons", R.string.imgly_sticker_category_name_emoticons, ImageSource.create(R.drawable.imgly_sticker_emoticons_grin), getStickerPack());
    }

}
