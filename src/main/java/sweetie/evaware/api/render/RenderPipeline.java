package sweetie.evaware.api.render;

public enum RenderPipeline {
    BACKGROUND, // Рисуется первым (фон)
    GAME,       // Обычные элементы
    HUD,        // Интерфейс
    POPUP,      // Всплывашки
    CURSOR;     // Рисуется последним (поверх всего)

    // Массив для быстрого доступа без values() каждый раз
    public static final RenderPipeline[] VALUES = values();
}