package ly.img.react_native.vesdk;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import ly.img.android.IMGLY;
import ly.img.android.VESDK;
import ly.img.android.pesdk.VideoEditorSettingsList;
import ly.img.android.pesdk.assets.filter.basic.FilterPackBasic;
import ly.img.android.pesdk.assets.font.basic.FontPackBasic;
import ly.img.android.pesdk.assets.frame.basic.FramePackBasic;
import ly.img.android.pesdk.assets.overlay.basic.OverlayPackBasic;
import ly.img.android.pesdk.assets.sticker.emoticons.StickerPackEmoticons;
import ly.img.android.pesdk.assets.sticker.shapes.StickerPackShapes;
import ly.img.android.pesdk.backend.decoder.ImageSource;
import ly.img.android.pesdk.backend.model.EditorSDKResult;
import ly.img.android.pesdk.backend.model.config.CropAspectAsset;
import ly.img.android.pesdk.backend.model.state.AssetConfig;
import ly.img.android.pesdk.backend.model.state.LoadSettings;
import ly.img.android.pesdk.backend.model.state.manager.SettingsList;
import ly.img.android.pesdk.ui.activity.VideoEditorBuilder;
import ly.img.android.pesdk.ui.model.state.UiConfigAdjustment;
import ly.img.android.pesdk.ui.model.state.UiConfigAspect;
import ly.img.android.pesdk.ui.model.state.UiConfigFilter;
import ly.img.android.pesdk.ui.model.state.UiConfigFocus;
import ly.img.android.pesdk.ui.model.state.UiConfigFrame;
import ly.img.android.pesdk.ui.model.state.UiConfigMainMenu;
import ly.img.android.pesdk.ui.model.state.UiConfigOverlay;
import ly.img.android.pesdk.ui.model.state.UiConfigSticker;
import ly.img.android.pesdk.ui.model.state.UiConfigText;
import ly.img.android.pesdk.ui.model.state.UiConfigTextDesign;
import ly.img.android.pesdk.ui.panels.AdjustmentToolPanel;
import ly.img.android.pesdk.ui.panels.BrushToolPanel;
import ly.img.android.pesdk.ui.panels.FilterToolPanel;
import ly.img.android.pesdk.ui.panels.FocusToolPanel;
import ly.img.android.pesdk.ui.panels.FrameToolPanel;
import ly.img.android.pesdk.ui.panels.OverlayToolPanel;
import ly.img.android.pesdk.ui.panels.StickerToolPanel;
import ly.img.android.pesdk.ui.panels.TextDesignToolPanel;
import ly.img.android.pesdk.ui.panels.TextToolPanel;
import ly.img.android.pesdk.ui.panels.TransformToolPanel;
import ly.img.android.pesdk.ui.panels.item.AdjustOption;
import ly.img.android.pesdk.ui.panels.item.CropAspectItem;
import ly.img.android.pesdk.ui.panels.item.CropResetItem;
import ly.img.android.pesdk.ui.panels.item.FocusOption;
import ly.img.android.pesdk.ui.panels.item.TextDesignItem;
import ly.img.android.pesdk.ui.panels.item.ToolItem;
import ly.img.android.pesdk.ui.utils.DataSourceIdItemList;
import ly.img.android.pesdk.utils.DataSourceArrayList;
import ly.img.android.serializer._3.IMGLYFileReader;

import ly.img.react_native.vesdk.effects.CustomFilterPack;
import ly.img.react_native.vesdk.effects.CustomFontPack;
import ly.img.react_native.vesdk.effects.CustomFramePack;
import ly.img.react_native.vesdk.effects.CustomOverlayPack;
import ly.img.react_native.vesdk.effects.CustomStickerPackEmoticons;
import ly.img.react_native.vesdk.effects.CustomStickerPackShapes;
import ly.img.react_native.vesdk.view.CustomAdjustOption;
import ly.img.react_native.vesdk.view.CustomFocusOption;
import ly.img.react_native.vesdk.view.CustomTextDesignItem;
import ly.img.react_native.vesdk.view.CustomToolItem;
import ly.img.react_native.vesdk.view.CustomToolItemDisabled;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class RNVideoEditorSDKModule extends ReactContextBaseJavaModule implements ActivityEventListener {
    private static final String TAG = "IMGLY_REACT_NATIVE";
    private Promise _promise;

    RNVideoEditorSDKModule(ReactApplicationContext context) {
        super(context);
        getReactApplicationContext().addActivityEventListener(this);
    }

    public static int VESDK_RESULT = 32;
    public static int RESULT_SUBSCRIBE = 35;
    public static int RESULT_DISCARD = 36;
    public static boolean _isSubscriber = false;
    public static boolean _isCameOnSubscription = false;
    public static ReadableMap configMap;
    public static ReadableArray filterConfig;
    public static ReadableArray adjustConfig;
    public static ReadableArray focusConfig;
    public static ReadableArray stickerConfig;
    public static ReadableArray textConfig;
    public static ReadableArray textDesignConfig;
    public static ReadableArray overlayConfig;
    public static ReadableArray frameConfig;

    private static final String URI = "uri";
    private static final String FILENAME = "fileName";
    private static final String FULLFILEPATH = "fullFilePath";
    private static final String MIME_TYPE = "mime";
    private static final String TYPE = "type";
    private static final String ID = "id";
    private static final String FILE_SIZE = "fileSize";

    private VideoEditorSettingsList createsVesdkSettingsList() {
        VideoEditorSettingsList settingsList = new VideoEditorSettingsList();
        if (_isSubscriber) {
            settingsList.getSettingsModel(UiConfigFilter.class).setFilterList(
                    FilterPackBasic.getFilterPack()
            );

            settingsList.getSettingsModel(UiConfigText.class).setFontList(
                    FontPackBasic.getFontPack()
            );

            settingsList.getSettingsModel(UiConfigFrame.class).setFrameList(
                    FramePackBasic.getFramePack()
            );

            settingsList.getSettingsModel(UiConfigOverlay.class).setOverlayList(
                    OverlayPackBasic.getOverlayPack()
            );

            settingsList.getSettingsModel(UiConfigSticker.class).setStickerLists(
                    StickerPackEmoticons.getStickerCategory(),
                    StickerPackShapes.getStickerCategory()
            );
        } else {
            settingsList.getSettingsModel(UiConfigFilter.class).setFilterList(
                    CustomFilterPack.getFilterPack()
            );

            settingsList.getSettingsModel(UiConfigFrame.class).setFrameList(
                    CustomFramePack.getFramePack()
            );

            settingsList.getSettingsModel(UiConfigOverlay.class).setOverlayList(
                    CustomOverlayPack.getOverlayPack()
            );

            settingsList.getSettingsModel(UiConfigText.class).setFontList(
                    CustomFontPack.getFontPack()
            );

            settingsList.getSettingsModel(UiConfigSticker.class).setStickerLists(
                    CustomStickerPackEmoticons.getStickerCategory(),
                    CustomStickerPackShapes.getStickerCategory()
            );

            UiConfigAdjustment uiConfigAdjustment = settingsList.getSettingsModel(UiConfigAdjustment.class);
            DataSourceArrayList<AdjustOption> adjustOptionsList = uiConfigAdjustment.getOptionList();
            DataSourceArrayList<AdjustOption> customAdjustOptionsList = new DataSourceArrayList<>();
            for (int i = 0; i < adjustOptionsList.size(); i++) {
                customAdjustOptionsList.add(new CustomAdjustOption(adjustOptionsList.get(i).getId(), adjustOptionsList.get(i).getName(), adjustOptionsList.get(i).getThumbnailSource()));
            }
            uiConfigAdjustment.setOptionList(customAdjustOptionsList);

            UiConfigFocus uiConfigFocus = settingsList.getSettingsModel(UiConfigFocus.class);
            DataSourceArrayList<FocusOption> focusOptionList = uiConfigFocus.getOptionList();
            DataSourceArrayList<FocusOption> customFocusOptionList = new DataSourceArrayList<>();
            for (int i = 0; i < focusOptionList.size(); i++) {
                customFocusOptionList.add(new CustomFocusOption(focusOptionList.get(i).getId()));
            }
            uiConfigFocus.setOptionList(customFocusOptionList);

            UiConfigTextDesign uiConfigTextDesign = settingsList.getSettingsModel(UiConfigTextDesign.class);
            DataSourceIdItemList<TextDesignItem> textDesignList = uiConfigTextDesign.getTextDesignList();
            DataSourceIdItemList<TextDesignItem> customTextDesignList = new DataSourceIdItemList<>();
            for (int i = 0; i < textDesignList.size(); i++) {
                customTextDesignList.add(new CustomTextDesignItem(textDesignList.get(i).getId(), textDesignList.get(i).getName(), textDesignList.get(i).getThumbnailSource()));
            }
            uiConfigTextDesign.setTextDesignList(customTextDesignList);

            UiConfigMainMenu uiConfigMainMenu = settingsList.getSettingsModel(UiConfigMainMenu.class);
            uiConfigMainMenu.setToolList(
                    new ToolItem(TransformToolPanel.TOOL_ID, R.string.pesdk_transform_title_name, ImageSource.create(R.drawable.imgly_icon_tool_transform)),
                    new CustomToolItem(FilterToolPanel.TOOL_ID, R.string.pesdk_filter_title_name, ImageSource.create(R.drawable.imgly_icon_tool_filters)),
                    new CustomToolItem(AdjustmentToolPanel.TOOL_ID, R.string.pesdk_adjustments_title_name, ImageSource.create(R.drawable.imgly_icon_tool_adjust)),
                    new CustomToolItem(FocusToolPanel.TOOL_ID, R.string.pesdk_focus_title_name, ImageSource.create(R.drawable.imgly_icon_tool_focus)),
                    new CustomToolItem(StickerToolPanel.TOOL_ID, R.string.pesdk_sticker_title_name, ImageSource.create(R.drawable.imgly_icon_tool_sticker)),
                    new CustomToolItem(TextToolPanel.TOOL_ID, R.string.pesdk_text_title_name, ImageSource.create(R.drawable.imgly_icon_tool_text)),
                    new CustomToolItem(TextDesignToolPanel.TOOL_ID, R.string.pesdk_textDesign_title_name, ImageSource.create(R.drawable.imgly_icon_tool_text_design)),
                    new CustomToolItem(OverlayToolPanel.TOOL_ID, R.string.pesdk_overlay_title_name, ImageSource.create(R.drawable.imgly_icon_tool_overlay)),
                    new CustomToolItem(FrameToolPanel.TOOL_ID, R.string.pesdk_frame_title_name, ImageSource.create(R.drawable.imgly_icon_tool_frame))
            );
        }

        AssetConfig assetConfig = settingsList.getConfig();
        assetConfig.getAssetMap(CropAspectAsset.class).clear();
        UiConfigAspect uiConfigAspect = settingsList.getSettingsModel(UiConfigAspect.class);
        ArrayList<CropAspectItem> cropAspectItems = new ArrayList<>();

        if (configMap.getMap("transform").getBoolean("showResetButton")) {
            cropAspectItems.add(new CropResetItem());
        }
        if (configMap.getMap("transform").getBoolean("allowFreeCrop")) {
            cropAspectItems.add(new CropAspectItem("my_crop_free", R.string.pesdk_transform_button_freeCrop, ImageSource.create(R.drawable.imgly_icon_custom_crop)));
        }
        for (int i = 0; i < configMap.getMap("transform").getArray("items").size(); i++) {
            String name = configMap.getMap("transform").getArray("items").getMap(i).getString("name");
            int width = configMap.getMap("transform").getArray("items").getMap(i).getInt("width");
            int height = configMap.getMap("transform").getArray("items").getMap(i).getInt("height");
            String cropName = "aspect" + "_" + width + "_" + height;
            assetConfig.getAssetMap(CropAspectAsset.class).add(new CropAspectAsset(cropName, width, height, false));
            if (name != null && name.equals("Square")) {
                cropAspectItems.add(new CropAspectItem(cropName, R.string.pesdk_transform_button_squareCrop));
            } else {
                cropAspectItems.add(new CropAspectItem(cropName));
            }
        }

        uiConfigAspect.setAspectList(cropAspectItems);

        return settingsList;
    }


    @NonNull
    @Override
    public String getName() {
        return "RNVideoEditorSDK";
    }

    @ReactMethod
    public void unlockWithLicense(String license) {
        VESDK.initSDKWithLicenseData(license);
        IMGLY.authorize();
    }

    @ReactMethod
    public void present(String name, ReadableMap config, String serialization, Promise promise) {
        _promise = promise;
        Uri uri = Uri.parse(name);
        SettingsList settingsList;
        configMap = config;
        filterConfig = configMap.getMap("nixFilter").getArray("list");
        adjustConfig = configMap.getMap("nixAdjust").getArray("list");
        focusConfig = configMap.getMap("nixFocus").getArray("list");
        stickerConfig = configMap.getMap("nixSticker").getArray("list");
        textConfig = configMap.getMap("nixText").getArray("list");
        textDesignConfig = configMap.getMap("nixTextDesign").getArray("list");
        overlayConfig = configMap.getMap("nixOverlay").getArray("list");
        frameConfig = configMap.getMap("nixFrame").getArray("list");
        _isSubscriber = config.getBoolean("isSubscriber");
        _isCameOnSubscription = config.getBoolean("isCameOnSubscription");

        settingsList = createsVesdkSettingsList();
        settingsList.getSettingsModel(LoadSettings.class).setSource(uri);

        new CustomVideoEditorBuilder(getCurrentActivity())
            .setSettingsList(settingsList)
            .startActivityForResult(getCurrentActivity(), VESDK_RESULT);
    }


    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent intent) {
        if (resultCode == RESULT_OK && requestCode == VESDK_RESULT) {
            EditorSDKResult data = new EditorSDKResult(intent);

            Log.i("PESDK", "Source image is located here " + data.getSourceUri());
            Log.i("PESDK", "Result image is located here " + data.getResultUri());


            JSONObject mDetails = new JSONObject();

            String mimeType = FileUtils.getMimeType(getCurrentActivity(), data.getResultUri());
            String fileName = FileUtils.getFileName(getCurrentActivity(), data.getResultUri());
            MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
            long durationMs = 0;
            long videoWidth = 0;
            long videoHeight = 0;

            try {
                if (data.getResultUri() != null) {
                    Uri exportedFileUri = data.getResultUri();
                    mediaMetadataRetriever.setDataSource(getCurrentActivity(), Uri.parse(exportedFileUri.toString()));
                    mDetails.put(ID, data.getResultUri());
                    mDetails.put(FILENAME, fileName);
                    mDetails.put(MIME_TYPE, mimeType);
                    mDetails.put(TYPE, mimeType);
                    mDetails.put(URI, data.getResultUri());
                    mDetails.put(FULLFILEPATH, data.getResultUri());
                    mDetails.put(FILE_SIZE, new File(data.getResultUri().getPath()).length());
                    mDetails.put("mime", mimeType);

                    String duration = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                    String width = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
                    String height = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
                    String minType = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE);
                    mDetails.put("mime", minType);
                    if (duration != null) {
                        durationMs = Long.parseLong(duration);
                    }
                    if (width != null) {
                        videoWidth = Long.parseLong(width);
                    }
                    if (height != null) {
                        videoHeight = Long.parseLong(height);
                    }

                    mDetails.put("duration", durationMs / 1000.0f);
                    mDetails.put("endTime", durationMs / 1000.0f);
                    mDetails.put("startTime", 0);
                    mDetails.put("width", videoWidth);
                    mDetails.put("height", videoHeight);
                }

                Log.d(TAG, "onActivityResult: " + mDetails);

                WritableMap map = Arguments.createMap();

                JSONArray dataOut = new JSONArray();
                dataOut.put(mDetails);

                map.putArray("NEW_OUT", convertJsonToArray(dataOut));
                _promise.resolve(map);
            } catch (Exception e) {
                e.printStackTrace();
            }


        } else if (resultCode == RESULT_SUBSCRIBE) {
            int targetScreen = intent.getIntExtra("targetScreen", 1);
            WritableMap map = Arguments.createMap();
            map.putString("action", " ");
            map.putInt("path", targetScreen);
            _promise.resolve(map);
        } else if (resultCode == RESULT_CANCELED && requestCode == VESDK_RESULT) {
            EditorSDKResult data = new EditorSDKResult(intent);
            Uri sourceURI = data.getSourceUri();
        } else if (resultCode == RESULT_DISCARD && requestCode == VESDK_RESULT) {
            WritableMap map = Arguments.createMap();
            map.putString("action", " ");
            map.putInt("path", 0);
            _promise.resolve(map);
        }
    }

    @Override
    public void onNewIntent(Intent intent) {

    }

    private static WritableMap convertJsonToMap(JSONObject jsonObject) throws JSONException {
        WritableMap map = Arguments.createMap();

        Iterator<String> iterator = jsonObject.keys();
        while (iterator.hasNext()) {
            String key = iterator.next();
            Object value = jsonObject.get(key);
            if (value instanceof JSONObject) {
                map.putMap(key, convertJsonToMap((JSONObject) value));
            } else if (value instanceof JSONArray) {
                map.putArray(key, convertJsonToArray((JSONArray) value));
            } else if (value instanceof Boolean) {
                map.putBoolean(key, (Boolean) value);
            } else if (value instanceof Integer) {
                map.putInt(key, (Integer) value);
            } else if (value instanceof Double) {
                map.putDouble(key, (Double) value);
            } else if (value instanceof String) {
                map.putString(key, (String) value);
            } else {
                map.putString(key, value.toString());
            }
        }
        return map;
    }

    private static WritableArray convertJsonToArray(JSONArray jsonArray) throws JSONException {
        WritableArray array = new WritableNativeArray();

        for (int i = 0; i < jsonArray.length(); i++) {
            Object value = jsonArray.get(i);
            if (value instanceof JSONObject) {
                array.pushMap(convertJsonToMap((JSONObject) value));
            } else if (value instanceof JSONArray) {
                array.pushArray(convertJsonToArray((JSONArray) value));
            } else if (value instanceof Boolean) {
                array.pushBoolean((Boolean) value);
            } else if (value instanceof Integer) {
                array.pushInt((Integer) value);
            } else if (value instanceof Double) {
                array.pushDouble((Double) value);
            } else if (value instanceof String) {
                array.pushString((String) value);
            } else {
                array.pushString(value.toString());
            }
        }
        return array;
    }
}


