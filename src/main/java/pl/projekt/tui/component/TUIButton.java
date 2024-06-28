package pl.projekt.tui.component;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import pl.projekt.tui.model.color.Colors;

/**
 * The TUIButton class represents a button component in a Text User Interface (TUI).
 * It implements the TUIComponent interface and handles drawing the button on the screen,
 * setting its properties, and performing actions when the button is interacted with.
 */
@Slf4j
public class TUIButton implements TUIComponent {

    protected String text;
    private final Runnable action;
    private int x, y, width, height;

    @Setter
    private String backgroundColor = Colors.BG_RED.getCode();
    @Setter
    private String textColor = Colors.TEXT_WHITE.getCode();

    private TextAlign textAlign = TextAlign.CENTER;
    private final int layerIndex;
    private final TUIManager tuiManager;
    private TUIDialog tuiDialog;
    private boolean active;

    /**
     * Enumeration for text alignment options.
     */
    public enum TextAlign {
        LEFT, CENTER, RIGHT
    }

    /**
     * Constructor to initialize the TUIButton with specified properties.
     *
     * @param x          The x-coordinate of the top-left corner of the button.
     * @param y          The y-coordinate of the top-left corner of the button.
     * @param width      The width of the button.
     * @param height     The height of the button.
     * @param layerIndex The layer index of the button for layering purposes.
     * @param text       The text displayed on the button.
     * @param action     The action to perform when the button is clicked.
     * @param tuiManager The manager to handle TUI components.
     */
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

    /**
     * Sets the position and size of the button.
     *
     * @param x      The x-coordinate of the top-left corner of the button.
     * @param y      The y-coordinate of the top-left corner of the button.
     * @param width  The width of the button.
     * @param height The height of the button.
     */
    public void setPositionAndSize(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /**
     * Checks if a point (mouseX, mouseY) is inside the button.
     *
     * @param mouseX The x-coordinate to check.
     * @param mouseY The y-coordinate to check.
     * @return True if the point is inside the button, false otherwise.
     */
    public boolean isInside(int mouseX, int mouseY) {
        return mouseX >= x && mouseX <= (x + width) && mouseY >= y && mouseY <= (y + height);
    }

    /**
     * Draws the button on the TUI screen using the specified manager.
     *
     * @param uiManager The manager to handle TUI components.
     */
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

    /**
     * Performs the action associated with the button.
     */
    @Override
    public void performAction() {
        if (action != null) {
            action.run();
        }
    }

    /**
     * Sets the text alignment of the button.
     *
     * @param textAlign The text alignment to set.
     */
    public void setTextAlign(TextAlign textAlign) {
        this.textAlign = textAlign;
    }

    /**
     * Shows the button by adding it to the TUI manager.
     */
    @Override
    public void show() {
        if (tuiManager != null) {
            tuiManager.addComponent(this);
        }
    }

    /**
     * Returns the x-coordinate of the button.
     *
     * @return The x-coordinate of the button.
     */
    @Override
    public int getX() {
        return x;
    }

    /**
     * Returns the y-coordinate of the button.
     *
     * @return The y-coordinate of the button.
     */
    @Override
    public int getY() {
        return y;
    }

    /**
     * Returns the width of the button.
     *
     * @return The width of the button.
     */
    @Override
    public int getWidth() {
        return width;
    }

    /**
     * Returns the height of the button.
     *
     * @return The height of the button.
     */
    @Override
    public int getHeight() {
        return height;
    }

    /**
     * Sets the active state of the button.
     *
     * @param active The active state to set.
     */
    @Override
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Checks if the button is active.
     *
     * @return True if the button is active, false otherwise.
     */
    @Override
    public boolean isComponentActive() {
        return this.active;
    }

    /**
     * Highlights the button by changing its background and text colors.
     */
    @Override
    public void highlightComponent() {
        log.debug("Highlighting button");
        setBackgroundColor(Colors.BG_YELLOW.getCode());
        setTextColor(Colors.TEXT_BLACK.getCode());
        tuiManager.refresh();
    }

    /**
     * Resets the highlight of the button to its original colors.
     */
    @Override
    public void resetHighlightComponent() {
        setBackgroundColor(Colors.BG_RED.getCode());
        setTextColor(Colors.TEXT_WHITE.getCode());
        tuiManager.refresh();
    }

    /**
     * Hides the button by removing it from the TUI manager.
     */
    public void hide() {
        this.active = false;
        if (tuiManager != null) {
            tuiManager.removeComponent(this);
            log.info("Delete button logger in Button class");
        }
    }

    /**
     * Hides the button in the dialog by removing it from the TUI manager.
     */
    public void hideInDialog() {
        if (tuiManager != null) {
            tuiManager.removeComponent(this);
            log.info("Delete button logger in Button class");
        }
    }

    /**
     * Checks if the button is interactable.
     *
     * @return True as the button is interactable.
     */
    @Override
    public boolean isInteractable() {
        return true;
    }

    /**
     * Returns the z-index of the button.
     *
     * @return The z-index of the button.
     */
    @Override
    public int getZIndex() {
        return layerIndex;
    }

    /**
     * Handles the window resize event (no implementation in this version).
     *
     * @param width  The new width of the window.
     * @param height The new height of the window.
     */
    @Override
    public void windowResized(int width, int height) {
        // No implementation
    }

    /**
     * Returns the text displayed on the button.
     *
     * @return The text displayed on the button.
     */
    public String getText() {
        return this.text;
    }

    /**
     * Returns the text alignment of the button.
     *
     * @return The text alignment of the button.
     */
    public TextAlign getTextAlign() {
        return textAlign;
    }

    /**
     * Returns the background color of the button.
     *
     * @return The background color code.
     */
    public String getBackgroundColor() {
        return backgroundColor;
    }

    /**
     * Returns the text color of the button.
     *
     * @return The text color code.
     */
    public String getTextColor() {
        return textColor;
    }
}
