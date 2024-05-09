package pl.projekt.tui.component;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import pl.projekt.tui.model.color.Colors;

@Slf4j
public class TUIButton implements TUIComponent {
    protected String text;
    private final Runnable action;
    private final int x, y, width, height;
    @Setter
    private String backgroundColor = Colors.BG_RED.getCode();
    @Setter
    private String textColor = Colors.TEXT_WHITE.getCode();
    private final TextAlign textAlign = TextAlign.CENTER;
    private final int layerIndex;

    private final TUIManager tuiManager;

    private boolean active;

    public enum TextAlign {
        LEFT, CENTER, RIGHT
    }

    public TUIButton(int x, int y, int width, int height, int layerIndex, String text, Runnable action, TUIManager tuiManager) {
        this.text = text;
        this.action = action;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.layerIndex = layerIndex;
        this.tuiManager = tuiManager;
    }

    @Override
    public void drawComponent(TUIManager uiManager) {
        log.debug("Drawing button");
        String paddedText = switch (textAlign) {
            case LEFT -> String.format("%-" + width + "s", text);
            case RIGHT -> String.format("%" + width + "s", text);
            default -> {
                int padding = (width - text.length()) / 2;
                yield String.format("%" + (text.length() + padding) + "s", text);
            }
        };
        for (int i = 0; i < paddedText.length(); i++) {
            TUIScreenCell cell = new TUIScreenCell(paddedText.charAt(i), textColor, backgroundColor);
            uiManager.getScreen().addPixelToLayer(x + i, y, layerIndex, cell);
        }
    }

    @Override
    public void performAction() {
        if (action != null) {
            action.run();
        }
    }

    @Override
    public void show() {
        if (tuiManager != null) {
            tuiManager.addComponent(this);
        }
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public boolean isComponentActive() {
        return this.active;
    }

    @Override
    public void highlightComponent() {
        log.debug("Highlighting button");
        setBackgroundColor(Colors.BG_YELLOW.getCode());
        setTextColor(Colors.TEXT_BLACK.getCode());
        tuiManager.refresh();
    }

    @Override
    public void resetHighlightComponent() {
        setBackgroundColor(Colors.BG_RED.getCode());
        setTextColor(Colors.TEXT_WHITE.getCode());

        tuiManager.refresh();
    }

    @Override
    public boolean isInteractable() {
        return true;
    }
    @Override
    public int getLayerIndex() {
        return layerIndex;
    }
    @Override
    public void windowResized(int width, int height){}
}
