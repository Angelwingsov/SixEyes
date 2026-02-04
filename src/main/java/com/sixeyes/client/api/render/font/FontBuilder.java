package com.sixeyes.client.api.render.font;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.util.Identifier;
import com.sixeyes.client.api.utility.other.FileUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class FontBuilder {
    private String name;
    private Identifier dataIdentifier;
    private Identifier atlasIdentifier;

    public FontBuilder() {
    }

    public FontBuilder find(String fontName) {
        this.name = fontName;
        this.dataIdentifier = Identifier.of("sixeyes", "fonts/" + fontName + ".json");
        this.atlasIdentifier = Identifier.of("sixeyes", "fonts/" + fontName + ".png");
        return this;
    }

    public Font load() {
        FontData data = FileUtil.fromJsonToInstance(this.dataIdentifier, FontData.class);
        AbstractTexture texture = MinecraftClient.getInstance().getTextureManager().getTexture(this.atlasIdentifier);

        if (data == null) {
            throw new RuntimeException("Failed to read font data file: " + this.dataIdentifier.toString()
                    + "; Are you sure this is json file? Try to check the correctness of its syntax.");
        }

        texture.setFilter(true, false);

        float aWidth = data.atlas().width();
        float aHeight = data.atlas().height();
        Map<Integer, MsdfGlyph> glyphs = data.glyphs().stream()
                .collect(Collectors.<FontData.GlyphData, Integer, MsdfGlyph>toMap(FontData.GlyphData::unicode,
                        (glyphData) -> new MsdfGlyph(glyphData, aWidth, aHeight)));

        Map<Integer, Map<Integer, Float>> kernings = new HashMap<>();
        data.kernings().forEach((kerning) -> {
            Map<Integer, Float> map = kernings.get(kerning.leftChar());
            if (map == null) {
                map = new HashMap<>();
                kernings.put(kerning.leftChar(), map);
            }

            map.put(kerning.rightChar(), kerning.advance());
        });

        return new Font(name, texture, data.atlas(), data.metrics(), glyphs, kernings);
    }
}
