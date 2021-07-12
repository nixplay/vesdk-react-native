package ly.img.react_native.vesdk.effects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import ly.img.android.pesdk.backend.decoder.ImageSource;
import ly.img.android.pesdk.backend.model.state.SmartStickerConfig;
import ly.img.android.pesdk.backend.model.state.manager.SettingsHolderInterface;
import ly.img.android.pesdk.backend.smart.WeatherProvider;
import ly.img.android.pesdk.backend.sticker_smart.SmartDateSticker0;
import ly.img.android.pesdk.backend.sticker_smart.SmartTimeClockSticker0;
import ly.img.android.pesdk.backend.sticker_smart.SmartTimeSticker0;
import ly.img.android.pesdk.backend.sticker_smart.SmartWeatherCloudSticker0;
import ly.img.android.pesdk.backend.sticker_smart.SmartWeatherThermostatSticker0;
import ly.img.android.pesdk.backend.sticker_smart.SmartWeekdaySticker0;
import ly.img.android.pesdk.ui.panels.item.ImageStickerItem;
import ly.img.android.pesdk.ui.panels.item.StickerCategoryItem;
import ly.img.android.pesdk.ui.utils.DataSourceIdItemList;
import ly.img.react_native.vesdk.view.CustomImageStickerItem;

public class CustomSmartStickerPack {
    public CustomSmartStickerPack() {
    }

    public static DataSourceIdItemList<ImageStickerItem> getStickerPack(@NonNull SettingsHolderInterface holderInterface, @Nullable Class<? extends WeatherProvider> weatherProvider) {
        DataSourceIdItemList<ImageStickerItem> smartSticker = new DataSourceIdItemList();
        smartSticker.addAll(getStickers());
        smartSticker.addAll(getStickersWithWeather(holderInterface, weatherProvider));
        return smartSticker;
    }

    public static DataSourceIdItemList<ImageStickerItem> getStickers() {
        DataSourceIdItemList<ImageStickerItem> smartSticker = new DataSourceIdItemList();
        smartSticker.add(new CustomImageStickerItem("imgly_smart_sticker_weekday", "Weekday", ImageSource.create(SmartWeekdaySticker0.class)));
        smartSticker.add(new CustomImageStickerItem("imgly_smart_sticker_date", "Date", ImageSource.create(SmartDateSticker0.class)));
        smartSticker.add(new CustomImageStickerItem("imgly_smart_sticker_time", "Time", ImageSource.create(SmartTimeSticker0.class)));
        smartSticker.add(new CustomImageStickerItem("imgly_smart_sticker_time_clock", "Time Clock", ImageSource.create(SmartTimeClockSticker0.class)));
        return smartSticker;
    }

    public static DataSourceIdItemList<ImageStickerItem> getStickersWithWeather(@NonNull SettingsHolderInterface holderInterface, @Nullable Class<? extends WeatherProvider> weatherProvider) {
        DataSourceIdItemList<ImageStickerItem> smartSticker = new DataSourceIdItemList();
        if (weatherProvider != null) {
            smartSticker.add(new CustomImageStickerItem("imgly_smart_sticker_weather_cloud", "Weather Cloud", ImageSource.create(SmartWeatherCloudSticker0.class)));
            smartSticker.add(new CustomImageStickerItem("imgly_smart_sticker_weather_thermostat", "Weather Thermostat", ImageSource.create(SmartWeatherThermostatSticker0.class)));
        }

        ((SmartStickerConfig)holderInterface.getSettingsModel(SmartStickerConfig.class)).setWeatherProviderClass(weatherProvider);
        return smartSticker;
    }

    public static StickerCategoryItem getStickerCategory(@NonNull SettingsHolderInterface holderInterface, @Nullable Class<? extends WeatherProvider> weatherProvider) {
        return new StickerCategoryItem("imgly_smart_sticker_category", "Smart Stickers", weatherProvider != null ? ImageSource.create(SmartWeatherCloudSticker0.class) : ImageSource.create(SmartTimeSticker0.class), getStickerPack(holderInterface, weatherProvider));
    }
}
