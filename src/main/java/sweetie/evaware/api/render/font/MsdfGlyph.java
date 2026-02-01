package sweetie.evaware.api.render.font;

import com.ferra13671.cometrenderer.vertex.element.VertexElementType;
import com.ferra13671.cometrenderer.vertex.mesh.MeshBuilder;

public final class MsdfGlyph {
    public record ColoredGlyph(char c, int color) {}

    private final int code;
    private final float minU, maxU, minV, maxV;
    private final float advance, topPosition, width, height;

    public MsdfGlyph(FontData.GlyphData data, float atlasWidth, float atlasHeight) {
        this.code = data.unicode();
        this.advance = data.advance();

        FontData.BoundsData atlasBounds = data.atlasBounds();
        if (atlasBounds != null) {
            this.minU = atlasBounds.left() / atlasWidth;
            this.maxU = atlasBounds.right() / atlasWidth;
            this.minV = 1.0F - atlasBounds.top() / atlasHeight;
            this.maxV = 1.0F - atlasBounds.bottom() / atlasHeight;
        } else {
            this.minU = this.maxU = this.minV = this.maxV = 0.0f;
        }

        FontData.BoundsData planeBounds = data.planeBounds();
        if (planeBounds != null) {
            this.width = planeBounds.right() - planeBounds.left();
            this.height = planeBounds.top() - planeBounds.bottom();
            this.topPosition = planeBounds.top();
        } else {
            this.width = this.height = this.topPosition = 0.0f;
        }
    }

    public float apply(MeshBuilder buffer, float x, float y, float z, float size, int color,
                       float thickness, float smoothness, float outlineThickness,
                       float outR, float outG, float outB, float outA) {

        float localY = y - this.topPosition * size;
        float w = this.width * size;
        float h = this.height * size;

        // Распаковка основного цвета
        float r = (color >> 16 & 0xFF) / 255f;
        float g = (color >> 8 & 0xFF) / 255f;
        float b = (color & 0xFF) / 255f;
        float a = (color >> 24 & 0xFF) / 255f;

        // LB
        buffer.vertex(x, localY, z)
                .element("Texture", VertexElementType.FLOAT, minU, minV)
                .element("Color", VertexElementType.FLOAT, r, g, b, a)
                .element("Style", VertexElementType.FLOAT, thickness, smoothness, outlineThickness)
                .element("OutlineColor", VertexElementType.FLOAT, outR, outG, outB, outA);

        // LT
        buffer.vertex(x, localY + h, z)
                .element("Texture", VertexElementType.FLOAT, minU, maxV)
                .element("Color", VertexElementType.FLOAT, r, g, b, a)
                .element("Style", VertexElementType.FLOAT, thickness, smoothness, outlineThickness)
                .element("OutlineColor", VertexElementType.FLOAT, outR, outG, outB, outA);

        // RT
        buffer.vertex(x + w, localY + h, z)
                .element("Texture", VertexElementType.FLOAT, maxU, maxV)
                .element("Color", VertexElementType.FLOAT, r, g, b, a)
                .element("Style", VertexElementType.FLOAT, thickness, smoothness, outlineThickness)
                .element("OutlineColor", VertexElementType.FLOAT, outR, outG, outB, outA);

        // RB
        buffer.vertex(x + w, localY, z)
                .element("Texture", VertexElementType.FLOAT, maxU, minV)
                .element("Color", VertexElementType.FLOAT, r, g, b, a)
                .element("Style", VertexElementType.FLOAT, thickness, smoothness, outlineThickness)
                .element("OutlineColor", VertexElementType.FLOAT, outR, outG, outB, outA);

        return this.advance * size;
    }

    public float getWidth(float size) {
        return this.advance * size;
    }

    public int getCharCode() {
        return code;
    }
}