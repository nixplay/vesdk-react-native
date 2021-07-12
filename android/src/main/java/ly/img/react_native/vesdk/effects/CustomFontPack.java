package ly.img.react_native.vesdk.effects;

import ly.img.android.pesdk.ui.panels.item.FontItem;
import ly.img.android.pesdk.ui.utils.DataSourceIdItemList;
import ly.img.react_native.vesdk.view.CustomFontItem;

public class CustomFontPack {
    public CustomFontPack() {
    }

    public static DataSourceIdItemList<FontItem> getFontPack() {
        DataSourceIdItemList<FontItem> fontList = new DataSourceIdItemList();
        fontList.add(new CustomFontItem("imgly_font_open_sans_bold", "Open Sans"));
        fontList.add(new CustomFontItem("imgly_font_aleo_bold", "Aleo"));
        fontList.add(new CustomFontItem("imgly_font_amaticsc", "Amaticsc"));
        fontList.add(new CustomFontItem("imgly_font_imgly_font_archivo_black", "Archivo"));
        fontList.add(new CustomFontItem("imgly_font_bungee_inline", "Bungee"));
        fontList.add(new CustomFontItem("imgly_font_campton_bold", "Campton"));
        fontList.add(new CustomFontItem("imgly_font_carter_one", "Carter"));
        fontList.add(new CustomFontItem("imgly_font_codystar", "Codystar"));
        fontList.add(new CustomFontItem("imgly_font_fira_sans_regular", "Fira Sans"));
        fontList.add(new CustomFontItem("imgly_font_galano_grotesque_bold", "Galano"));
        fontList.add(new CustomFontItem("imgly_font_krona_one", "Krona"));
        fontList.add(new CustomFontItem("imgly_font_kumar_one_outline", "Kumar"));
        fontList.add(new CustomFontItem("imgly_font_lobster", "Lobster"));
        fontList.add(new CustomFontItem("imgly_font_molle", "Molle"));
        fontList.add(new CustomFontItem("imgly_font_monoton", "Monoton"));
        fontList.add(new CustomFontItem("imgly_font_nixie_one", "Nixie"));
        fontList.add(new CustomFontItem("imgly_font_notable", "Notable"));
        fontList.add(new CustomFontItem("imgly_font_ostrich_sans_black", "OstrichBlk"));
        fontList.add(new CustomFontItem("imgly_font_ostrich_sans_bold", "OstrichBld"));
        fontList.add(new CustomFontItem("imgly_font_oswald_semi_bold", "Oswald"));
        fontList.add(new CustomFontItem("imgly_font_palanquin_dark_semi_bold", "Palanquin"));
        fontList.add(new CustomFontItem("imgly_font_permanent_marker", "Marker"));
        fontList.add(new CustomFontItem("imgly_font_poppins", "Poppins"));
        fontList.add(new CustomFontItem("imgly_font_roboto_black_italic", "RobotoBlk"));
        fontList.add(new CustomFontItem("imgly_font_roboto_light_italic", "RobotoLt"));
        fontList.add(new CustomFontItem("imgly_font_sancreek", "Sancreek"));
        fontList.add(new CustomFontItem("imgly_font_stint_ultra_expanded", "Stint"));
        fontList.add(new CustomFontItem("imgly_font_trash_hand", "Trashhand"));
        fontList.add(new CustomFontItem("imgly_font_vt323", "VT323"));
        fontList.add(new CustomFontItem("imgly_font_yeseva_one", "Yeseva"));
        return fontList;
    }
}
