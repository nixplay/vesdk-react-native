package ly.img.react_native.vesdk.effects;

import ly.img.android.pesdk.backend.decoder.ImageSource;
import ly.img.android.pesdk.ui.panels.item.ImageStickerItem;
import ly.img.android.pesdk.ui.panels.item.StickerCategoryItem;
import ly.img.android.pesdk.ui.utils.DataSourceIdItemList;
import ly.img.react_native.vesdk.R;
import ly.img.react_native.vesdk.view.CustomImageStickerItem;

public class CustomStickerPackShapes {
    public CustomStickerPackShapes() {
    }

    public static DataSourceIdItemList<ImageStickerItem> getStickerPack() {
        DataSourceIdItemList<ImageStickerItem> stickers = new DataSourceIdItemList();
        stickers.add(new CustomImageStickerItem("imgly_sticker_shapes_badge_01", R.string.imgly_sticker_name_shapes_badge_01, ImageSource.create(R.drawable.imgly_sticker_shapes_badge_01)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_shapes_badge_04", R.string.imgly_sticker_name_shapes_badge_04, ImageSource.create(R.drawable.imgly_sticker_shapes_badge_04)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_shapes_badge_12", R.string.imgly_sticker_name_shapes_badge_12, ImageSource.create(R.drawable.imgly_sticker_shapes_badge_12)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_shapes_badge_06", R.string.imgly_sticker_name_shapes_badge_06, ImageSource.create(R.drawable.imgly_sticker_shapes_badge_06)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_shapes_badge_13", R.string.imgly_sticker_name_shapes_badge_13, ImageSource.create(R.drawable.imgly_sticker_shapes_badge_13)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_shapes_badge_36", R.string.imgly_sticker_name_shapes_badge_36, ImageSource.create(R.drawable.imgly_sticker_shapes_badge_36)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_shapes_badge_08", R.string.imgly_sticker_name_shapes_badge_08, ImageSource.create(R.drawable.imgly_sticker_shapes_badge_08)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_shapes_badge_11", R.string.imgly_sticker_name_shapes_badge_11, ImageSource.create(R.drawable.imgly_sticker_shapes_badge_11)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_shapes_badge_35", R.string.imgly_sticker_name_shapes_badge_35, ImageSource.create(R.drawable.imgly_sticker_shapes_badge_35)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_shapes_badge_28", R.string.imgly_sticker_name_shapes_badge_28, ImageSource.create(R.drawable.imgly_sticker_shapes_badge_28)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_shapes_badge_32", R.string.imgly_sticker_name_shapes_badge_32, ImageSource.create(R.drawable.imgly_sticker_shapes_badge_32)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_shapes_badge_15", R.string.imgly_sticker_name_shapes_badge_15, ImageSource.create(R.drawable.imgly_sticker_shapes_badge_15)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_shapes_badge_20", R.string.imgly_sticker_name_shapes_badge_20, ImageSource.create(R.drawable.imgly_sticker_shapes_badge_20)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_shapes_badge_18", R.string.imgly_sticker_name_shapes_badge_18, ImageSource.create(R.drawable.imgly_sticker_shapes_badge_18)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_shapes_badge_19", R.string.imgly_sticker_name_shapes_badge_19, ImageSource.create(R.drawable.imgly_sticker_shapes_badge_19)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_shapes_arrow_02", R.string.imgly_sticker_name_shapes_arrow_02, ImageSource.create(R.drawable.imgly_sticker_shapes_arrow_02)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_shapes_arrow_03", R.string.imgly_sticker_name_shapes_arrow_03, ImageSource.create(R.drawable.imgly_sticker_shapes_arrow_03)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_shapes_spray_01", R.string.imgly_sticker_name_shapes_spray_01, ImageSource.create(R.drawable.imgly_sticker_shapes_spray_01)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_shapes_spray_04", R.string.imgly_sticker_name_shapes_spray_04, ImageSource.create(R.drawable.imgly_sticker_shapes_spray_04)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_shapes_spray_03", R.string.imgly_sticker_name_shapes_spray_03, ImageSource.create(R.drawable.imgly_sticker_shapes_spray_03)));
        return stickers;
    }

    public static StickerCategoryItem getStickerCategory() {
        return new StickerCategoryItem("imgly_sticker_category_shapes", R.string.imgly_sticker_category_name_shapes, ImageSource.create(R.drawable.imgly_sticker_shapes_badge_12), getStickerPack());
    }
}
