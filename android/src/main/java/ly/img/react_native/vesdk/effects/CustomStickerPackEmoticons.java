package ly.img.react_native.vesdk.effects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import ly.img.android.pesdk.assets.sticker.emoticons.R.drawable;
import ly.img.android.pesdk.assets.sticker.emoticons.R.string;
import ly.img.android.pesdk.backend.decoder.ImageSource;
import ly.img.android.pesdk.backend.model.state.manager.SettingsHolderInterface;
import ly.img.android.pesdk.backend.smart.SmartStickerPack;
import ly.img.android.pesdk.backend.smart.WeatherProvider;
import ly.img.android.pesdk.ui.panels.item.ImageStickerItem;
import ly.img.android.pesdk.ui.panels.item.StickerCategoryItem;
import ly.img.android.pesdk.ui.utils.DataSourceIdItemList;
import ly.img.react_native.vesdk.R;
import ly.img.react_native.vesdk.view.CustomImageStickerItem;


public class CustomStickerPackEmoticons {
    public CustomStickerPackEmoticons() {
    }

    private static DataSourceIdItemList<CustomImageStickerItem> getStickers(boolean childFriendly) {
        DataSourceIdItemList<CustomImageStickerItem> stickers = new DataSourceIdItemList();
        stickers.add(new CustomImageStickerItem("imgly_sticker_emoticons_grin", string.imgly_sticker_name_emoticons_grin, ImageSource.create(drawable.imgly_sticker_emoticons_grin)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_emoticons_laugh", string.imgly_sticker_name_emoticons_laugh, ImageSource.create(drawable.imgly_sticker_emoticons_laugh)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_emoticons_smile", string.imgly_sticker_name_emoticons_smile, ImageSource.create(drawable.imgly_sticker_emoticons_smile)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_emoticons_wink", string.imgly_sticker_name_emoticons_wink, ImageSource.create(drawable.imgly_sticker_emoticons_wink)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_emoticons_tongue_out_wink", string.imgly_sticker_name_emoticons_tongue_out_wink, ImageSource.create(drawable.imgly_sticker_emoticons_tongue_out_wink)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_emoticons_angel", string.imgly_sticker_name_emoticons_angel, ImageSource.create(drawable.imgly_sticker_emoticons_angel)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_emoticons_kisses", string.imgly_sticker_name_emoticons_kisses, ImageSource.create(drawable.imgly_sticker_emoticons_kisses)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_emoticons_loving", string.imgly_sticker_name_emoticons_loving, ImageSource.create(drawable.imgly_sticker_emoticons_loving)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_emoticons_kiss", string.imgly_sticker_name_emoticons_kiss, ImageSource.create(drawable.imgly_sticker_emoticons_kiss)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_emoticons_wave", string.imgly_sticker_name_emoticons_wave, ImageSource.create(drawable.imgly_sticker_emoticons_wave)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_emoticons_nerd", string.imgly_sticker_name_emoticons_nerd, ImageSource.create(drawable.imgly_sticker_emoticons_nerd)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_emoticons_cool", string.imgly_sticker_name_emoticons_cool, ImageSource.create(drawable.imgly_sticker_emoticons_cool)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_emoticons_blush", string.imgly_sticker_name_emoticons_blush, ImageSource.create(drawable.imgly_sticker_emoticons_blush)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_emoticons_duckface", string.imgly_sticker_name_emoticons_duckface, ImageSource.create(drawable.imgly_sticker_emoticons_duckface)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_emoticons_furious", string.imgly_sticker_name_emoticons_furious, ImageSource.create(drawable.imgly_sticker_emoticons_furious)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_emoticons_angry", string.imgly_sticker_name_emoticons_angry, ImageSource.create(drawable.imgly_sticker_emoticons_angry)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_emoticons_steaming_furious", string.imgly_sticker_name_emoticons_steaming_furious, ImageSource.create(drawable.imgly_sticker_emoticons_steaming_furious)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_emoticons_sad", string.imgly_sticker_name_emoticons_sad, ImageSource.create(drawable.imgly_sticker_emoticons_sad)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_emoticons_anxious", string.imgly_sticker_name_emoticons_anxious, ImageSource.create(drawable.imgly_sticker_emoticons_anxious)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_emoticons_cry", string.imgly_sticker_name_emoticons_cry, ImageSource.create(drawable.imgly_sticker_emoticons_cry)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_emoticons_sobbing", string.imgly_sticker_name_emoticons_sobbing, ImageSource.create(drawable.imgly_sticker_emoticons_sobbing)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_emoticons_loud_cry", string.imgly_sticker_name_emoticons_loud_cry, ImageSource.create(drawable.imgly_sticker_emoticons_loud_cry)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_emoticons_wide_grin", string.imgly_sticker_name_emoticons_wide_grin, ImageSource.create(drawable.imgly_sticker_emoticons_wide_grin)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_emoticons_impatient", string.imgly_sticker_name_emoticons_impatient, ImageSource.create(drawable.imgly_sticker_emoticons_impatient)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_emoticons_tired", string.imgly_sticker_name_emoticons_tired, ImageSource.create(drawable.imgly_sticker_emoticons_tired)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_emoticons_asleep", string.imgly_sticker_name_emoticons_asleep, ImageSource.create(drawable.imgly_sticker_emoticons_asleep)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_emoticons_sleepy", string.imgly_sticker_name_emoticons_sleepy, ImageSource.create(drawable.imgly_sticker_emoticons_sleepy)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_emoticons_deceased", string.imgly_sticker_name_emoticons_deceased, ImageSource.create(drawable.imgly_sticker_emoticons_deceased)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_emoticons_attention", string.imgly_sticker_name_emoticons_attention, ImageSource.create(drawable.imgly_sticker_emoticons_attention)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_emoticons_question", string.imgly_sticker_name_emoticons_question, ImageSource.create(drawable.imgly_sticker_emoticons_question)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_emoticons_not_speaking_to_you", string.imgly_sticker_name_emoticons_not_speaking_to_you, ImageSource.create(drawable.imgly_sticker_emoticons_not_speaking_to_you)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_emoticons_sick", string.imgly_sticker_name_emoticons_sick, ImageSource.create(drawable.imgly_sticker_emoticons_sick)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_emoticons_pumpkin", string.imgly_sticker_name_emoticons_pumpkin, ImageSource.create(drawable.imgly_sticker_emoticons_pumpkin)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_emoticons_boxer", string.imgly_sticker_name_emoticons_boxer, ImageSource.create(drawable.imgly_sticker_emoticons_boxer)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_emoticons_idea", string.imgly_sticker_name_emoticons_idea, ImageSource.create(drawable.imgly_sticker_emoticons_idea)));
        if (!childFriendly) {
            stickers.add(new CustomImageStickerItem("imgly_sticker_emoticons_smoking", string.imgly_sticker_name_emoticons_smoking, ImageSource.create(drawable.imgly_sticker_emoticons_smoking)));
        }

        if (!childFriendly) {
            stickers.add(new CustomImageStickerItem("imgly_sticker_emoticons_beer", string.imgly_sticker_name_emoticons_beer, ImageSource.create(drawable.imgly_sticker_emoticons_beer)));
        }

        stickers.add(new CustomImageStickerItem("imgly_sticker_emoticons_skateboard", string.imgly_sticker_name_emoticons_skateboard, ImageSource.create(drawable.imgly_sticker_emoticons_skateboard)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_emoticons_guitar", string.imgly_sticker_name_emoticons_guitar, ImageSource.create(drawable.imgly_sticker_emoticons_guitar)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_emoticons_music", string.imgly_sticker_name_emoticons_music, ImageSource.create(drawable.imgly_sticker_emoticons_music)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_emoticons_sunbathing", string.imgly_sticker_name_emoticons_sunbathing, ImageSource.create(drawable.imgly_sticker_emoticons_sunbathing)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_emoticons_hippie", string.imgly_sticker_name_emoticons_hippie, ImageSource.create(drawable.imgly_sticker_emoticons_hippie)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_emoticons_humourous", string.imgly_sticker_name_emoticons_humourous, ImageSource.create(drawable.imgly_sticker_emoticons_humourous)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_emoticons_hitman", string.imgly_sticker_name_emoticons_hitman, ImageSource.create(drawable.imgly_sticker_emoticons_hitman)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_emoticons_harry_potter", string.imgly_sticker_name_emoticons_harry_potter, ImageSource.create(drawable.imgly_sticker_emoticons_harry_potter)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_emoticons_business", string.imgly_sticker_name_emoticons_business, ImageSource.create(drawable.imgly_sticker_emoticons_business)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_emoticons_batman", string.imgly_sticker_name_emoticons_batman, ImageSource.create(drawable.imgly_sticker_emoticons_batman)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_emoticons_skull", string.imgly_sticker_name_emoticons_skull, ImageSource.create(drawable.imgly_sticker_emoticons_skull)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_emoticons_ninja", string.imgly_sticker_name_emoticons_ninja, ImageSource.create(drawable.imgly_sticker_emoticons_ninja)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_emoticons_masked", string.imgly_sticker_name_emoticons_masked, ImageSource.create(drawable.imgly_sticker_emoticons_masked)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_emoticons_alien", string.imgly_sticker_name_emoticons_alien, ImageSource.create(drawable.imgly_sticker_emoticons_alien)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_emoticons_wrestler", string.imgly_sticker_name_emoticons_wrestler, ImageSource.create(drawable.imgly_sticker_emoticons_wrestler)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_emoticons_devil", string.imgly_sticker_name_emoticons_devil, ImageSource.create(drawable.imgly_sticker_emoticons_devil)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_emoticons_star", string.imgly_sticker_name_emoticons_star, ImageSource.create(drawable.imgly_sticker_emoticons_star)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_emoticons_baby_chicken", string.imgly_sticker_name_emoticons_baby_chicken, ImageSource.create(drawable.imgly_sticker_emoticons_baby_chicken)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_emoticons_rabbit", string.imgly_sticker_name_emoticons_rabbit, ImageSource.create(drawable.imgly_sticker_emoticons_rabbit)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_emoticons_pig", string.imgly_sticker_name_emoticons_pig, ImageSource.create(drawable.imgly_sticker_emoticons_pig)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_emoticons_chicken", string.imgly_sticker_name_emoticons_chicken, ImageSource.create(drawable.imgly_sticker_emoticons_chicken)));
        return stickers;
    }

    public static DataSourceIdItemList<ImageStickerItem> getStickerPack(boolean childFriendly) {
        DataSourceIdItemList stickers = new DataSourceIdItemList();

        try {
            stickers.addAll(CustomSmartStickerPack.getStickers());
        } catch (NoClassDefFoundError var3) {
        } catch (Exception var4) {
        }

        stickers.addAll(getStickers(childFriendly));
        return stickers;
    }

    public static DataSourceIdItemList<ImageStickerItem> getStickerPack(boolean childFriendly, @NonNull SettingsHolderInterface holderInterface, @Nullable Class<? extends WeatherProvider> weatherProvider) {
        DataSourceIdItemList stickers = new DataSourceIdItemList();

        try {
            stickers.addAll(CustomSmartStickerPack.getStickers());
            stickers.addAll(CustomSmartStickerPack.getStickersWithWeather(holderInterface, weatherProvider));
        } catch (NoClassDefFoundError var5) {
        } catch (Exception var6) {
        }

        stickers.addAll(getStickers(childFriendly));
        return stickers;
    }

    public static DataSourceIdItemList<ImageStickerItem> getStickerPack() {
        return getStickerPack(false);
    }

    public static StickerCategoryItem getStickerCategory() {
        return getStickerCategory(false);
    }

    public static StickerCategoryItem getStickerCategory(@NonNull SettingsHolderInterface holderInterface, @Nullable Class<? extends WeatherProvider> weatherProvider) {
        return getStickerCategory(false, holderInterface, weatherProvider);
    }

    public static StickerCategoryItem getStickerCategory(boolean childFriendly) {
        return new StickerCategoryItem("imgly_sticker_category_emoticons", string.imgly_sticker_category_name_emoticons, ImageSource.create(drawable.imgly_sticker_emoticons_grin), getStickerPack(childFriendly));
    }

    public static StickerCategoryItem getStickerCategory(boolean childFriendly, @NonNull SettingsHolderInterface holderInterface, @Nullable Class<? extends WeatherProvider> weatherProvider) {
        return new StickerCategoryItem("imgly_sticker_category_emoticons", string.imgly_sticker_category_name_emoticons, ImageSource.create(drawable.imgly_sticker_emoticons_grin), getStickerPack(childFriendly, holderInterface, weatherProvider));
    }
}
