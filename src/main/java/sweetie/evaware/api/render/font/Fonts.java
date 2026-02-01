package sweetie.evaware.api.render.font;

import lombok.experimental.UtilityClass;

import java.util.List;

@UtilityClass
public class Fonts {
    public final String sf = "sf_pro";
    public final String ps = "product_sans";

    public final Font SF_BOLD = get(sf + "/sf_bold");
    public final Font SF_SEMIBOLD = get(sf + "/sf_semibold");
    public final Font SF_MEDIUM = get(sf + "/sf_medium");
    public final Font SF_REGULAR = get(sf + "/sf_regular");
    public final Font SF_LIGHT = get(sf + "/sf_light");

    public final Font PS_BLACK = get(ps + "/productsans_black");
    public final Font PS_BOLD = get(ps + "/productsans_bold");
    public final Font PS_MEDIUM = get(ps + "/productsans_medium");
    public final Font PS_REGULAR = get(ps + "/productsans_regular");
    public final Font PS_LIGHT = get(ps + "/productsans_light");
    public final Font PS_THIN = get(ps + "/productsans_thin");

    public final Font ICONS = get("other/icons");

    public float getMediumThickness() { return 0.07f; }
    public float getBoldThickness() { return 0.1f; }

    private Font get(String input) {
        return Font.builder().find(input).load();
    }

    public List<Font> getFonts() {
        return List.of(
                SF_BOLD, SF_SEMIBOLD, SF_MEDIUM, SF_REGULAR, SF_LIGHT,
                PS_BLACK, PS_BOLD, PS_MEDIUM, PS_REGULAR, PS_LIGHT, PS_THIN,
                ICONS
        );
    }
}
