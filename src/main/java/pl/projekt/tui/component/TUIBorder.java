package pl.projekt.tui.component;

import org.slf4j.Logger;
import pl.projekt.tui.model.color.Colors;

public class TUIBorder implements TUIComponent {

    Logger logger = org.slf4j.LoggerFactory.getLogger(TUIBorder.class);

    private enum BorderCharacter {
        HORIZONTAL('─'),
        VERTICAL('│'),
        TOP_LEFT('┌'),
        TOP_RIGHT('┐'),
        BOTTOM_LEFT('└'),
        BOTTOM_RIGHT('┘');

        private final char character;

        BorderCharacter(char character) {
            this.character = character;
        }

        public char getCharacter() {
            return character;
        }
    }

    private int x, y, width, height;
    private final int initialH, initialW;
    private int zIndex;
    private String bgColor = Colors.BG_BLUE.getCode();
    private String textColor = Colors.TEXT_BLACK.getCode();

    private String textContent;

    private boolean isVisible = false;
    private TUIManager TUIManager;

    public TUIBorder(int x, int y, int width, int height, int zIndex, TUIManager TUIManager) {
        this.x = x;
        this.y = y;
        this.width = this.initialW = width;
        this.height = this.initialH = height;
        this.zIndex = zIndex;
        this.TUIManager = TUIManager;
    }

    public TUIBorder(int x, int y, int width, int height, int zIndex, TUIManager TUIManager, String textContent) {
        this.x = x;
        this.y = y;
        this.width = this.initialW = width;
        this.height = this.initialH = height;
        this.zIndex = zIndex;
        this.TUIManager = TUIManager;
        this.textContent = textContent;
    }

    public void setBgColor(String bgColor) {
        this.bgColor = bgColor;
    }

    public void setTextColor(String textColor) {
        this.textColor = textColor;
    }

    public void setTextInBorder(TUITab uiTab) {
        TUILabel label = new TUILabel(this.textContent, this.x + 3, this.y + 1, 0, Colors.BG_BRIGHT_BLUE.getCode(), TUIManager);
        uiTab.addComponent(label);
    }

    @Override
    public void drawComponent(TUIManager TUIManager) {
        logger.debug("Drawing border");
        TUIScreen screen = TUIManager.getScreen();
        for (int i = x; i < x + width; i++) {
            screen.addPixelToLayer(i, y, zIndex, new TUIScreenCell(BorderCharacter.HORIZONTAL.getCharacter(), textColor, bgColor));
            screen.addPixelToLayer(i, y + height - 1, zIndex, new TUIScreenCell(BorderCharacter.HORIZONTAL.getCharacter(), textColor, bgColor));
        }
        for (int i = y; i < y + height; i++) {
            screen.addPixelToLayer(x, i, zIndex, new TUIScreenCell(BorderCharacter.VERTICAL.getCharacter(), textColor, bgColor));
            screen.addPixelToLayer(x + width - 1, i, zIndex, new TUIScreenCell(BorderCharacter.VERTICAL.getCharacter(), textColor, bgColor));
        }
        screen.addPixelToLayer(x, y, zIndex, new TUIScreenCell(BorderCharacter.TOP_LEFT.getCharacter(), textColor, bgColor));
        screen.addPixelToLayer(x + width - 1, y, zIndex, new TUIScreenCell(BorderCharacter.TOP_RIGHT.getCharacter(), textColor, bgColor));
        screen.addPixelToLayer(x, y + height - 1, zIndex, new TUIScreenCell(BorderCharacter.BOTTOM_LEFT.getCharacter(), textColor, bgColor));
        screen.addPixelToLayer(x + width - 1, y + height - 1, zIndex, new TUIScreenCell(BorderCharacter.BOTTOM_RIGHT.getCharacter(), textColor, bgColor));
    }

    @Override
    public boolean isInside(int x, int y) {
        return false;
    }

    @Override
    public void performAction() {
        return;
    }

    public void show() {
        TUIManager.addComponent(this);
    }

    public void hide() {
        TUIManager.removeComponent(this);
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

    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    public void highlight() {

    }

    @Override
    public void resetHighlight() {

    }

    @Override
    public boolean isInteractable() {
        return false;
    }

    @Override
    public int getZIndex() {
        return zIndex;
    }

    @Override
    public void windowResized(int width, int height) {
        if (width < (x + this.width))
            this.width += width - (this.width + x);
        else if (width != (x + this.width))
            this.width = Math.min(width - x, initialW);
        if (height < (y + this.height))
            this.height += height - (this.height + y);
        else if (height != (y + this.height))
            this.height = Math.min(height - y, initialH);

        logger.trace("\033[32mNew border size is {}x{}\033[0m", this.width, this.height);
    }
}
