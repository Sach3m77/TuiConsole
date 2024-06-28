package pl.projekt.tui.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.projekt.tui.model.color.Colors;

/**
 * The TUIBorder class represents a border component in a Text User Interface (TUI).
 * It implements the TUIComponent interface and handles drawing the border on the screen.
 */
public class TUIBorder implements TUIComponent {

    Logger logger = LoggerFactory.getLogger(TUIBorder.class);

    // Constants for the border characters
    private static final char HORIZONTAL_BORDER = '$';
    private static final char VERTICAL_BORDER = '€';
    private static final char TOP_LEFT_CORNER = '┌';
    private static final char TOP_RIGHT_CORNER = '┐';
    private static final char BOTTOM_LEFT_CORNER = '└';
    private static final char BOTTOM_RIGHT_CORNER = '┘';

    // Position and size of the border
    private int x, y, width, height;
    private final int initialH, initialW;
    private int zIndex;

    // Colors for the border
    private String bgColor = Colors.BG_BLUE.getCode();
    private String textColor = Colors.TEXT_WHITE.getCode();

    // Text content to be displayed inside the border
    private String textContent;

    // Visibility state of the border
    private boolean isVisible = false;

    // Manager for handling TUI components
    private TUIManager tuiManager;

    /**
     * Constructor to initialize the TUIBorder with specified position, size, and manager.
     *
     * @param x        The x-coordinate of the top-left corner of the border.
     * @param y        The y-coordinate of the top-left corner of the border.
     * @param width    The width of the border.
     * @param height   The height of the border.
     * @param zIndex   The z-index of the border for layering purposes.
     * @param tuiManager The manager to handle TUI components.
     */
    public TUIBorder(int x, int y, int width, int height, int zIndex, TUIManager tuiManager) {
        this.x = x;
        this.y = y;
        this.width = this.initialW = width;
        this.height = this.initialH = height;
        this.zIndex = zIndex;
        this.tuiManager = tuiManager;
    }

    /**
     * Sets the background color of the border.
     *
     * @param bgColor The background color code.
     */
    public void setBgColor(String bgColor) {
        this.bgColor = bgColor;
    }

    /**
     * Sets the text color of the border.
     *
     * @param textColor The text color code.
     */
    public void setTextColor(String textColor) {
        this.textColor = textColor;
    }

    /**
     * Sets text inside the border and adds it to the specified tab.
     *
     * @param uiTab The tab to which the text label will be added.
     */
    public void setTextInBorder(TUITab uiTab) {
        TUILabel label = new TUILabel(this.textContent, this.x + 3, this.y + 1, 0, Colors.BG_BRIGHT_BLUE.getCode(), tuiManager);
        uiTab.addComponent(label);
    }

    /**
     * Draws the border on the TUI screen using the specified manager.
     *
     * @param uiManager The manager to handle TUI components.
     */
    @Override
    public void drawComponent(TUIManager uiManager) {
        logger.debug("Drawing border");
        TUIScreen screen = uiManager.getScreen();
        for (int i = x; i < x + width; i++) {
            screen.addPixelToLayer(i, y, zIndex, new TUIScreenCell(HORIZONTAL_BORDER, textColor, bgColor));
            screen.addPixelToLayer(i, y + height - 1, zIndex, new TUIScreenCell(HORIZONTAL_BORDER, textColor, bgColor));
        }
        for (int i = y; i < y + height; i++) {
            screen.addPixelToLayer(x, i, zIndex, new TUIScreenCell(VERTICAL_BORDER, textColor, bgColor));
            screen.addPixelToLayer(x + width - 1, i, zIndex, new TUIScreenCell(VERTICAL_BORDER, textColor, bgColor));
        }
        screen.addPixelToLayer(x, y, zIndex, new TUIScreenCell(TOP_LEFT_CORNER, textColor, bgColor));
        screen.addPixelToLayer(x + width - 1, y, zIndex, new TUIScreenCell(TOP_RIGHT_CORNER, textColor, bgColor));
        screen.addPixelToLayer(x, y + height - 1, zIndex, new TUIScreenCell(BOTTOM_LEFT_CORNER, textColor, bgColor));
        screen.addPixelToLayer(x + width - 1, y + height - 1, zIndex, new TUIScreenCell(BOTTOM_RIGHT_CORNER, textColor, bgColor));
    }

    /**
     * Returns the z-index of the border.
     *
     * @return The z-index of the border.
     */
    @Override
    public int getZIndex() {
        return zIndex;
    }

    /**
     * Checks if a point (x, y) is inside the border.
     *
     * @param x The x-coordinate to check.
     * @param y The y-coordinate to check.
     * @return False as the border itself does not handle interactions.
     */
    public boolean isInside(int x, int y) {
        return false;
    }

    /**
     * Performs an action associated with the border (no action in this implementation).
     */
    @Override
    public void performAction() {
        return;
    }

    /**
     * Shows the border by adding it to the TUI manager.
     */
    public void show() {
        tuiManager.addComponent(this);
    }

    /**
     * Hides the border by removing it from the TUI manager.
     */
    public void hide() {
        tuiManager.removeComponent(this);
    }

    /**
     * Returns the x-coordinate of the border.
     *
     * @return The x-coordinate of the border.
     */
    @Override
    public int getX() {
        return x;
    }

    /**
     * Returns the y-coordinate of the border.
     *
     * @return The y-coordinate of the border.
     */
    @Override
    public int getY() {
        return y;
    }

    /**
     * Returns the width of the border.
     *
     * @return The width of the border.
     */
    @Override
    public int getWidth() {
        return width;
    }

    /**
     * Returns the height of the border.
     *
     * @return The height of the border.
     */
    @Override
    public int getHeight() {
        return height;
    }

    /**
     * Sets the active state of the border (no implementation in this version).
     *
     * @param active The active state to set.
     */
    @Override
    public void setActive(boolean active) {
        // No implementation
    }

    /**
     * Checks if the component is active.
     *
     * @return False as the border does not handle active state in this implementation.
     */
    @Override
    public boolean isComponentActive() {
        return false;
    }

    /**
     * Highlights the component (no implementation in this version).
     */
    @Override
    public void highlightComponent() {
        // No implementation
    }

    /**
     * Resets the highlight of the component (no implementation in this version).
     */
    @Override
    public void resetHighlightComponent() {
        // No implementation
    }

    /**
     * Checks if the component is active.
     *
     * @return False as the border does not handle active state.
     */
    public boolean isActive() {
        return false;
    }

    /**
     * Checks if the component is interactable.
     *
     * @return False as the border does not handle interactions.
     */
    @Override
    public boolean isInteractable() {
        return false;
    }

    /**
     * Adjusts the border size based on the new window dimensions.
     *
     * @param width  The new width of the window.
     * @param height The new height of the window.
     */
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

    /**
     * Returns the background color of the border.
     *
     * @return The background color code.
     */
    public String getBgColor() {
        return bgColor;
    }
}
