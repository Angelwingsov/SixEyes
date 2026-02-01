package com.sixeyes.client.helpers;

import lombok.experimental.UtilityClass;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;

@UtilityClass
public class ReplaceUtil {
    public Text replace(Text input, String target, String replacement) {
        if (input == null || target == null || replacement == null) return input;
        MutableText result = Text.empty().setStyle(input.getStyle());
        appendReplaced(result, input, target, replacement);
        return result;
    }

    private void appendReplaced(MutableText result, Text current, String target, String replacement) {
        TextContent content = current.getContent();
        Style style = current.getStyle();

        if (content instanceof PlainTextContent.Literal literal) {
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(java.util.regex.Pattern.quote(target), java.util.regex.Pattern.CASE_INSENSITIVE);
            String replaced = pattern.matcher(literal.string()).replaceAll(replacement);
            result.append(Text.literal(replaced).setStyle(style));
        }

        for (Text sibling : current.getSiblings()) {
            appendReplaced(result, sibling, target, replacement);
        }
    }

    public String replaceSymbols(String string) {
        return string
                .replaceAll("ꔗ", Formatting.BLUE + "MODER")
                .replaceAll("ꔥ", Formatting.BLUE + "ST.MODER")
                .replaceAll("ꔡ", Formatting.LIGHT_PURPLE + "MODER+")
                .replaceAll("ꔀ", Formatting.GRAY + "PLAYER")
                .replaceAll("ꔉ", Formatting.YELLOW + "HELPER")
                .replaceAll("◆", "@")
                .replaceAll("┃", "|")
                .replaceAll("ꔳ", Formatting.AQUA + "ML.ADMIN")
                .replaceAll("ꔅ", Formatting.RED + "Y" + Formatting.WHITE + "T")
                .replaceAll("ꔂ", Formatting.BLUE + "D.MODER")
                .replaceAll("ꕠ", Formatting.YELLOW + "D.HELPER")
                .replaceAll("ꕄ", Formatting.RED + "DRACULA")
                .replaceAll("ꔖ", Formatting.AQUA + "OVERLORD")
                .replaceAll("ꕈ", Formatting.GREEN + "COBRA")
                .replaceAll("ꔨ", Formatting.LIGHT_PURPLE + "DRAGON")
                .replaceAll("ꔤ", Formatting.RED + "IMPERATOR")
                .replaceAll("ꔠ", Formatting.GOLD + "MAGISTER")
                .replaceAll("ꔄ", Formatting.BLUE + "HERO")
                .replaceAll("ꔒ", Formatting.GREEN + "AVENGER")
                .replaceAll("ꕒ", Formatting.WHITE + "RABBIT")
                .replaceAll("ꔈ", Formatting.YELLOW + "TITAN")
                .replaceAll("ꕀ", Formatting.DARK_GREEN + "HYDRA")
                .replaceAll("ꔶ", Formatting.GOLD + "TIGER")
                .replaceAll("ꔲ", Formatting.DARK_PURPLE + "BULL")
                .replaceAll("ꕖ", Formatting.BLACK + "BUNNY")
                .replaceAll("ꕗꕘ", Formatting.YELLOW + "SPONSOR")
                .replaceAll("\uD83D\uDD25", "@")
                .replaceAll("ᴀ", "A")
                .replaceAll("ʙ", "B")
                .replaceAll("ᴄ", "C")
                .replaceAll("ᴅ", "D")
                .replaceAll("ᴇ", "E")
                .replaceAll("ғ", "F")
                .replaceAll("ɢ", "G")
                .replaceAll("ʜ", "H")
                .replaceAll("ɪ", "I")
                .replaceAll("ᴊ", "J")
                .replaceAll("ᴋ", "K")
                .replaceAll("ʟ", "L")
                .replaceAll("ᴍ", "M")
                .replaceAll("ɴ", "N")
                .replaceAll("ꜱ", "S")
                .replaceAll("ᴏ", "O")
                .replaceAll("ᴘ", "P")
                .replaceAll("ǫ", "Q")
                .replaceAll("ʀ", "R")
                .replaceAll("ᴛ", "T")
                .replaceAll("ᴜ", "U")
                .replaceAll("ᴠ", "V")
                .replaceAll("ᴡ", "W")
                .replaceAll("ꜰ", "F")
                .replaceAll("ʏ", "Y")
                .replaceAll("ᴢ", "Z");
    }

    public Text replaceSymbols(Text text) {
        if (text.getString().contains("ꔗ")) text = replace(text, "ꔗ", Formatting.BLUE + "MODER");
        if (text.getString().contains("ꔥ")) text = replace(text, "ꔥ", Formatting.BLUE + "ST.MODER");
        if (text.getString().contains("ꔡ")) text = replace(text, "ꔡ", Formatting.LIGHT_PURPLE + "MODER+");
        if (text.getString().contains("ꔀ")) text = replace(text, "ꔀ", Formatting.GRAY + "PLAYER");
        if (text.getString().contains("ꔉ")) text = replace(text, "ꔉ", Formatting.YELLOW + "HELPER");
        if (text.getString().contains("◆")) text = replace(text, "◆", "@");
        if (text.getString().contains("┃")) text = replace(text, "┃", "|");
        if (text.getString().contains("ꔳ")) text = replace(text, "ꔳ", Formatting.AQUA + "ML.ADMIN");
        if (text.getString().contains("ꔅ")) text = replace(text, "ꔅ", Formatting.RED + "Y" + Formatting.WHITE + "T");
        if (text.getString().contains("ꔂ")) text = replace(text, "ꔂ", Formatting.BLUE + "D.MODER");
        if (text.getString().contains("ꕠ")) text = replace(text, "ꕠ", Formatting.YELLOW + "D.HELPER");
        if (text.getString().contains("ꕄ")) text = replace(text, "ꕄ", Formatting.RED + "DRACULA");
        if (text.getString().contains("ꔖ")) text = replace(text, "ꔖ", Formatting.AQUA + "OVERLORD");
        if (text.getString().contains("ꕈ")) text = replace(text, "ꕈ", Formatting.GREEN + "COBRA");
        if (text.getString().contains("ꔨ")) text = replace(text, "ꔨ", Formatting.LIGHT_PURPLE + "DRAGON");
        if (text.getString().contains("ꔤ")) text = replace(text, "ꔤ", Formatting.RED + "IMPERATOR");
        if (text.getString().contains("ꔠ")) text = replace(text, "ꔠ", Formatting.GOLD + "MAGISTER");
        if (text.getString().contains("ꔄ")) text = replace(text, "ꔄ", Formatting.BLUE + "HERO");
        if (text.getString().contains("ꔒ")) text = replace(text, "ꔒ", Formatting.GREEN + "AVENGER");
        if (text.getString().contains("ꕒ")) text = replace(text, "ꕒ", Formatting.WHITE + "RABBIT");
        if (text.getString().contains("ꔈ")) text = replace(text, "ꔈ", Formatting.YELLOW + "TITAN");
        if (text.getString().contains("ꕀ")) text = replace(text, "ꕀ", Formatting.DARK_GREEN + "HYDRA");
        if (text.getString().contains("ꔶ")) text = replace(text, "ꔶ", Formatting.GOLD + "TIGER");
        if (text.getString().contains("ꔲ")) text = replace(text, "ꔲ", Formatting.DARK_PURPLE + "BULL");
        if (text.getString().contains("ꕖ")) text = replace(text, "ꕖ", Formatting.BLACK + "BUNNY");
        if (text.getString().contains("ꕗꕘ")) text = replace(text, "ꕗꕘ", Formatting.YELLOW + "SPONSOR");
        if (text.getString().contains("\uD83D\uDD25")) text = replace(text, "\uD83D\uDD25", "@");

        if (text.getString().contains("ᴀ")) text = replace(text, "ᴀ", "A");
        if (text.getString().contains("ʙ")) text = replace(text, "ʙ", "B");
        if (text.getString().contains("ᴄ")) text = replace(text, "ᴄ", "C");
        if (text.getString().contains("ᴅ")) text = replace(text, "ᴅ", "D");
        if (text.getString().contains("ᴇ")) text = replace(text, "ᴇ", "E");
        if (text.getString().contains("ғ")) text = replace(text, "ғ", "F");
        if (text.getString().contains("ɢ")) text = replace(text, "ɢ", "G");
        if (text.getString().contains("ʜ")) text = replace(text, "ʜ", "H");
        if (text.getString().contains("ɪ")) text = replace(text, "ɪ", "I");
        if (text.getString().contains("ᴊ")) text = replace(text, "ᴊ", "J");
        if (text.getString().contains("ᴋ")) text = replace(text, "ᴋ", "K");
        if (text.getString().contains("ʟ")) text = replace(text, "ʟ", "L");
        if (text.getString().contains("ᴍ")) text = replace(text, "ᴍ", "M");
        if (text.getString().contains("ɴ")) text = replace(text, "ɴ", "N");
        if (text.getString().contains("ꜱ")) text = replace(text, "ꜱ", "S");
        if (text.getString().contains("ᴏ")) text = replace(text, "ᴏ", "O");
        if (text.getString().contains("ᴘ")) text = replace(text, "ᴘ", "P");
        if (text.getString().contains("ǫ")) text = replace(text, "ǫ", "Q");
        if (text.getString().contains("ʀ")) text = replace(text, "ʀ", "R");
        if (text.getString().contains("ᴛ")) text = replace(text, "ᴛ", "T");
        if (text.getString().contains("ᴜ")) text = replace(text, "ᴜ", "U");
        if (text.getString().contains("ᴠ")) text = replace(text, "ᴠ", "V");
        if (text.getString().contains("ᴡ")) text = replace(text, "ᴡ", "W");
        if (text.getString().contains("ꜰ")) text = replace(text, "ꜰ", "F");
        if (text.getString().contains("ʏ")) text = replace(text, "ʏ", "Y");
        if (text.getString().contains("ᴢ")) text = replace(text, "ᴢ", "Z");

        return text;
    }
}

