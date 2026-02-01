package sweetie.evaware.api.render;

import java.util.ArrayList;
import java.util.List;

public class LayerControl {
    public final List<Renderable> layers = new ArrayList<>();
    public final List<Batchable> batchables = new ArrayList<>();

    public void renderAll() {
        for (RenderPipeline pipeline : RenderPipeline.VALUES) {
            for (Batchable renderer : batchables) {
                renderer.flush(pipeline);
            }
        }
    }
}
