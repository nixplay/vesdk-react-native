package ly.img.react_native.vesdk.effects;

import ly.img.android.pesdk.assets.sticker.shapes.R.drawable;
import ly.img.android.pesdk.assets.sticker.shapes.R.string;
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
        stickers.add(new CustomImageStickerItem("imgly_sticker_shapes_badge_01", string.imgly_sticker_name_shapes_badge_01, ImageSource.create(drawable.imgly_sticker_shapes_badge_01)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_shapes_badge_04", string.imgly_sticker_name_shapes_badge_04, ImageSource.create(drawable.imgly_sticker_shapes_badge_04)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_shapes_badge_12", string.imgly_sticker_name_shapes_badge_12, ImageSource.create(drawable.imgly_sticker_shapes_badge_12)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_shapes_badge_06", string.imgly_sticker_name_shapes_badge_06, ImageSource.create(drawable.imgly_sticker_shapes_badge_06)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_shapes_badge_13", string.imgly_sticker_name_shapes_badge_13, ImageSource.create(drawable.imgly_sticker_shapes_badge_13)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_shapes_badge_36", string.imgly_sticker_name_shapes_badge_36, ImageSource.create(drawable.imgly_sticker_shapes_badge_36)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_shapes_badge_08", string.imgly_sticker_name_shapes_badge_08, ImageSource.create(drawable.imgly_sticker_shapes_badge_08)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_shapes_badge_11", string.imgly_sticker_name_shapes_badge_11, ImageSource.create(drawable.imgly_sticker_shapes_badge_11)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_shapes_badge_35", string.imgly_sticker_name_shapes_badge_35, ImageSource.create(drawable.imgly_sticker_shapes_badge_35)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_shapes_badge_28", string.imgly_sticker_name_shapes_badge_28, ImageSource.create(drawable.imgly_sticker_shapes_badge_28)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_shapes_badge_32", string.imgly_sticker_name_shapes_badge_32, ImageSource.create(drawable.imgly_sticker_shapes_badge_32)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_shapes_badge_15", string.imgly_sticker_name_shapes_badge_15, ImageSource.create(drawable.imgly_sticker_shapes_badge_15)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_shapes_badge_20", string.imgly_sticker_name_shapes_badge_20, ImageSource.create(drawable.imgly_sticker_shapes_badge_20)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_shapes_badge_18", string.imgly_sticker_name_shapes_badge_18, ImageSource.create(drawable.imgly_sticker_shapes_badge_18)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_shapes_badge_19", string.imgly_sticker_name_shapes_badge_19, ImageSource.create(drawable.imgly_sticker_shapes_badge_19)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_shapes_arrow_02", string.imgly_sticker_name_shapes_arrow_02, ImageSource.create(drawable.imgly_sticker_shapes_arrow_02)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_shapes_arrow_03", string.imgly_sticker_name_shapes_arrow_03, ImageSource.create(drawable.imgly_sticker_shapes_arrow_03)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_shapes_spray_01", string.imgly_sticker_name_shapes_spray_01, ImageSource.create(drawable.imgly_sticker_shapes_spray_01)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_shapes_spray_04", string.imgly_sticker_name_shapes_spray_04, ImageSource.create(drawable.imgly_sticker_shapes_spray_04)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_shapes_spray_03", string.imgly_sticker_name_shapes_spray_03, ImageSource.create(drawable.imgly_sticker_shapes_spray_03)));
        return stickers;
    }

    public static StickerCategoryItem getStickerCategory() {
        return new StickerCategoryItem("imgly_sticker_category_shapes", string.imgly_sticker_category_name_shapes, ImageSource.create(drawable.imgly_sticker_shapes_badge_12), getStickerPack());
    }
}
