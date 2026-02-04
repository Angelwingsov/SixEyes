package com.sixeyes.client.api.utility.other;

import lombok.experimental.UtilityClass;
import net.minecraft.text.*;
import com.sixeyes.client.api.render.font.MsdfGlyph;
import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class TextUtil {
    public List<MsdfGlyph.ColoredGlyph> parseTextToColoredGlyphs(Text text) {
        List<MsdfGlyph.ColoredGlyph> result = new ArrayList<>();
        parseTextRecursive(text, 0xFFFFFFFF, result);
        return result;
    }

    private void parseTextRecursive(Text text, int currentColor, List<MsdfGlyph.ColoredGlyph> result) {
        Style style = text.getStyle();
        int color = style.getColor() != null ? style.getColor().getRgb() | 0xFF000000 : currentColor;

        TextContent content = text.getContent();
        String raw = "";

        if (content instanceof PlainTextContent.Literal(String string)) {
            raw = string;
        } else if (content instanceof TranslatableTextContent translatable) {
            raw = translatable.getKey();
        } else if (content instanceof KeybindTextContent keybind) {
            raw = keybind.getKey();
        }

        raw = ReplaceUtil.replaceSymbols(raw);

        for (int i = 0; i < raw.length(); i++) {
            char c = raw.charAt(i);

            if (c == '\u00A7' && i + 1 < raw.length()) {
                i++;
                continue;
            }
            result.add(new MsdfGlyph.ColoredGlyph(c, color));
        }

        for (Text sibling : text.getSiblings()) {
            parseTextRecursive(sibling, color, result);
        }
    }
}


