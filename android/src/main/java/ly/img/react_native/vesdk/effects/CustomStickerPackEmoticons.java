package ly.img.react_native.vesdk.effects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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
        stickers.add(new CustomImageStickerItem("imgly_sticker_emoticons_grin", R.string.imgly_sticker_name_emoticons_grin, ImageSource.create(R.drawable.imgly_sticker_emoticons_grin)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_emoticons_laugh", R.string.imgly_sticker_name_emoticons_laugh, ImageSource.create(R.drawable.imgly_sticker_emoticons_laugh)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_emoticons_smile", R.string.imgly_sticker_name_emoticons_smile, ImageSource.create(R.drawable.imgly_sticker_emoticons_smile)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_emoticons_wink", R.string.imgly_sticker_name_emoticons_wink, ImageSource.create(R.drawable.imgly_sticker_emoticons_wink)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_emoticons_tongue_out_wink", R.string.imgly_sticker_name_emoticons_tongue_out_wink, ImageSource.create(R.drawable.imgly_sticker_emoticons_tongue_out_wink)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_emoticons_angel", R.string.imgly_sticker_name_emoticons_angel, ImageSource.create(R.drawable.imgly_sticker_emoticons_angel)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_emoticons_kisses", R.string.imgly_sticker_name_emoticons_kisses, ImageSource.create(R.drawable.imgly_sticker_emoticons_kisses)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_emoticons_loving", R.string.imgly_sticker_name_emoticons_loving, ImageSource.create(R.drawable.imgly_sticker_emoticons_loving)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_emoticons_kiss", R.string.imgly_sticker_name_emoticons_kiss, ImageSource.create(R.drawable.imgly_sticker_emoticons_kiss)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_emoticons_wave", R.string.imgly_sticker_name_emoticons_wave, ImageSource.create(R.drawable.imgly_sticker_emoticons_wave)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_emoticons_nerd", R.string.imgly_sticker_name_emoticons_nerd, ImageSource.create(R.drawable.imgly_sticker_emoticons_nerd)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_emoticons_cool", R.string.imgly_sticker_name_emoticons_cool, ImageSource.create(R.drawable.imgly_sticker_emoticons_cool)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_emoticons_blush", R.string.imgly_sticker_name_emoticons_blush, ImageSource.create(R.drawable.imgly_sticker_emoticons_blush)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_emoticons_duckface", R.string.imgly_sticker_name_emoticons_duckface, ImageSource.create(R.drawable.imgly_sticker_emoticons_duckface)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_emoticons_furious", R.string.imgly_sticker_name_emoticons_furious, ImageSource.create(R.drawable.imgly_sticker_emoticons_furious)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_emoticons_angry", R.string.imgly_sticker_name_emoticons_angry, ImageSource.create(R.drawable.imgly_sticker_emoticons_angry)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_emoticons_steaming_furious", R.string.imgly_sticker_name_emoticons_steaming_furious, ImageSource.create(R.drawable.imgly_sticker_emoticons_steaming_furious)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_emoticons_sad", R.string.imgly_sticker_name_emoticons_sad, ImageSource.create(R.drawable.imgly_sticker_emoticons_sad)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_emoticons_anxious", R.string.imgly_sticker_name_emoticons_anxious, ImageSource.create(R.drawable.imgly_sticker_emoticons_anxious)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_emoticons_cry", R.string.imgly_sticker_name_emoticons_cry, ImageSource.create(R.drawable.imgly_sticker_emoticons_cry)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_emoticons_sobbing", R.string.imgly_sticker_name_emoticons_sobbing, ImageSource.create(R.drawable.imgly_sticker_emoticons_sobbing)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_emoticons_loud_cry", R.string.imgly_sticker_name_emoticons_loud_cry, ImageSource.create(R.drawable.imgly_sticker_emoticons_loud_cry)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_emoticons_wide_grin", R.string.imgly_sticker_name_emoticons_wide_grin, ImageSource.create(R.drawable.imgly_sticker_emoticons_wide_grin)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_emoticons_impatient", R.string.imgly_sticker_name_emoticons_impatient, ImageSource.create(R.drawable.imgly_sticker_emoticons_impatient)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_emoticons_tired", R.string.imgly_sticker_name_emoticons_tired, ImageSource.create(R.drawable.imgly_sticker_emoticons_tired)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_emoticons_asleep", R.string.imgly_sticker_name_emoticons_asleep, ImageSource.create(R.drawable.imgly_sticker_emoticons_asleep)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_emoticons_sleepy", R.string.imgly_sticker_name_emoticons_sleepy, ImageSource.create(R.drawable.imgly_sticker_emoticons_sleepy)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_emoticons_deceased", R.string.imgly_sticker_name_emoticons_deceased, ImageSource.create(R.drawable.imgly_sticker_emoticons_deceased)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_emoticons_attention", R.string.imgly_sticker_name_emoticons_attention, ImageSource.create(R.drawable.imgly_sticker_emoticons_attention)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_emoticons_question", R.string.imgly_sticker_name_emoticons_question, ImageSource.create(R.drawable.imgly_sticker_emoticons_question)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_emoticons_not_speaking_to_you", R.string.imgly_sticker_name_emoticons_not_speaking_to_you, ImageSource.create(R.drawable.imgly_sticker_emoticons_not_speaking_to_you)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_emoticons_sick", R.string.imgly_sticker_name_emoticons_sick, ImageSource.create(R.drawable.imgly_sticker_emoticons_sick)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_emoticons_pumpkin", R.string.imgly_sticker_name_emoticons_pumpkin, ImageSource.create(R.drawable.imgly_sticker_emoticons_pumpkin)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_emoticons_boxer", R.string.imgly_sticker_name_emoticons_boxer, ImageSource.create(R.drawable.imgly_sticker_emoticons_boxer)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_emoticons_idea", R.string.imgly_sticker_name_emoticons_idea, ImageSource.create(R.drawable.imgly_sticker_emoticons_idea)));
        if (!childFriendly) {
            stickers.add(new CustomImageStickerItem("imgly_sticker_emoticons_smoking", R.string.imgly_sticker_name_emoticons_smoking, ImageSource.create(R.drawable.imgly_sticker_emoticons_smoking)));
        }

        if (!childFriendly) {
            stickers.add(new CustomImageStickerItem("imgly_sticker_emoticons_beer", R.string.imgly_sticker_name_emoticons_beer, ImageSource.create(R.drawable.imgly_sticker_emoticons_beer)));
        }

        stickers.add(new CustomImageStickerItem("imgly_sticker_emoticons_skateboard", R.string.imgly_sticker_name_emoticons_skateboard, ImageSource.create(R.drawable.imgly_sticker_emoticons_skateboard)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_emoticons_guitar", R.string.imgly_sticker_name_emoticons_guitar, ImageSource.create(R.drawable.imgly_sticker_emoticons_guitar)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_emoticons_music", R.string.imgly_sticker_name_emoticons_music, ImageSource.create(R.drawable.imgly_sticker_emoticons_music)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_emoticons_sunbathing", R.string.imgly_sticker_name_emoticons_sunbathing, ImageSource.create(R.drawable.imgly_sticker_emoticons_sunbathing)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_emoticons_hippie", R.string.imgly_sticker_name_emoticons_hippie, ImageSource.create(R.drawable.imgly_sticker_emoticons_hippie)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_emoticons_humourous", R.string.imgly_sticker_name_emoticons_humourous, ImageSource.create(R.drawable.imgly_sticker_emoticons_humourous)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_emoticons_hitman", R.string.imgly_sticker_name_emoticons_hitman, ImageSource.create(R.drawable.imgly_sticker_emoticons_hitman)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_emoticons_harry_potter", R.string.imgly_sticker_name_emoticons_harry_potter, ImageSource.create(R.drawable.imgly_sticker_emoticons_harry_potter)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_emoticons_business", R.string.imgly_sticker_name_emoticons_business, ImageSource.create(R.drawable.imgly_sticker_emoticons_business)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_emoticons_batman", R.string.imgly_sticker_name_emoticons_batman, ImageSource.create(R.drawable.imgly_sticker_emoticons_batman)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_emoticons_skull", R.string.imgly_sticker_name_emoticons_skull, ImageSource.create(R.drawable.imgly_sticker_emoticons_skull)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_emoticons_ninja", R.string.imgly_sticker_name_emoticons_ninja, ImageSource.create(R.drawable.imgly_sticker_emoticons_ninja)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_emoticons_masked", R.string.imgly_sticker_name_emoticons_masked, ImageSource.create(R.drawable.imgly_sticker_emoticons_masked)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_emoticons_alien", R.string.imgly_sticker_name_emoticons_alien, ImageSource.create(R.drawable.imgly_sticker_emoticons_alien)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_emoticons_wrestler", R.string.imgly_sticker_name_emoticons_wrestler, ImageSource.create(R.drawable.imgly_sticker_emoticons_wrestler)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_emoticons_devil", R.string.imgly_sticker_name_emoticons_devil, ImageSource.create(R.drawable.imgly_sticker_emoticons_devil)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_emoticons_star", R.string.imgly_sticker_name_emoticons_star, ImageSource.create(R.drawable.imgly_sticker_emoticons_star)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_emoticons_baby_chicken", R.string.imgly_sticker_name_emoticons_baby_chicken, ImageSource.create(R.drawable.imgly_sticker_emoticons_baby_chicken)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_emoticons_rabbit", R.string.imgly_sticker_name_emoticons_rabbit, ImageSource.create(R.drawable.imgly_sticker_emoticons_rabbit)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_emoticons_pig", R.string.imgly_sticker_name_emoticons_pig, ImageSource.create(R.drawable.imgly_sticker_emoticons_pig)));
        stickers.add(new CustomImageStickerItem("imgly_sticker_emoticons_chicken", R.string.imgly_sticker_name_emoticons_chicken, ImageSource.create(R.drawable.imgly_sticker_emoticons_chicken)));
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
        return new StickerCategoryItem("imgly_sticker_category_emoticons", R.string.imgly_sticker_category_name_emoticons, ImageSource.create(R.drawable.imgly_sticker_emoticons_grin), getStickerPack(childFriendly));
    }

    public static StickerCategoryItem getStickerCategory(boolean childFriendly, @NonNull SettingsHolderInterface holderInterface, @Nullable Class<? extends WeatherProvider> weatherProvider) {
        return new StickerCategoryItem("imgly_sticker_category_emoticons", R.string.imgly_sticker_category_name_emoticons, ImageSource.create(R.drawable.imgly_sticker_emoticons_grin), getStickerPack(childFriendly, holderInterface, weatherProvider));
    }
}
