package sweetie.evaware.api.render;

public interface Batchable {
    void begin();

    // Отрисовывает всё, что накопилось в конкретном пайплайне
    void flush(RenderPipeline pipeline);

    void end(); // Можно использовать для очистки, если нужно
}
