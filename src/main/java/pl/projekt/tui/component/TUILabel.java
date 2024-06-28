package pl.projekt.tui.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.projekt.tui.model.color.Colors;

/**
 * Represents a text label component for a Text User Interface (TUI).
 */
public class TUILabel implements TUIComponent {

    private Logger logger = LoggerFactory.getLogger(TUILabel.class);
    private String text;             // The text content of the label
    private final String textColor = Colors.TEXT_BLACK.getCode();  // Default text color
    private String backgroundColor;  // Background color of the label
    private final int x, y;          // Position coordinates on the screen
    private final int layerIndex;    // Z-index or layer index for component stacking
    private boolean visible;         // Visibility state of the label
    private int width, height;       // Width and height of the label
    private final TUIManager tuiManager;  // Manager for TUI components

    /**
     * Constructs a new TUILabel with specified parameters.
     *
     * @param text The text to display in the label
     * @param x X-coordinate position on the screen
     * @param y Y-coordinate position on the screen
     * @param layerIndex Z-index or layer index for component stacking
     * @param backgroundColor Background color of the label
     * @param tuiManager The TUI manager responsible for handling components
     */
    public TUILabel(String text, int x, int y, int layerIndex, String backgroundColor, TUIManager tuiManager) {
        this.text = text;
        this.x = x;
        this.y = y;
        this.layerIndex = layerIndex;
        this.backgroundColor = backgroundColor;
        this.tuiManager = tuiManager;
        countBounds();  // Calculate initial width and height based on text content
    }

    /**
     * Helper method to calculate the width and height of the label based on text content.
     */
    void countBounds() {
        height = 1;
        int prevIndex = 0;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == '\n') {
                height++;
                int len = text.substring(prevIndex, i).length();
                if (len > width)
                    width = len;
                prevIndex = i;
            }
        }
        if (prevIndex == 0)
            width = text.length();
    }

    /**
     * Draws the label component on the TUI screen.
     *
     * @param tuiManager The TUI manager instance to use for drawing
     */
    @Override
    public void drawComponent(TUIManager tuiManager) {
        TUIScreen screen = tuiManager.getScreen();
        screen.setText(x, y, text, textColor, backgroundColor, layerIndex);
    }

    /**
     * Sets the background color of the label.
     *
     * @param bgColor The new background color to set
     */
    public void setBgColor(String bgColor) {
        this.backgroundColor = bgColor;
    }

    /**
     * Sets the text content of the label.
     *
     * @param text The new text content to set
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Retrieves the Z-index or layer index of the label.
     *
     * @return The layer index of the label
     */
    @Override
    public int getZIndex() {
        return layerIndex;
    }

    /**
     * Placeholder method for performing an action (not implemented in this class).
     */
    @Override
    public void performAction() {
        // Placeholder method, not implemented
    }

    /**
     * Adds the label component to the TUI manager for display.
     */
    @Override
    public void show() {
        tuiManager.addComponent(this);
    }

    /**
     * Retrieves the X-coordinate position of the label.
     *
     * @return The X-coordinate position
     */
    @Override
    public int getX() {
        return x;
    }

    /**
     * Retrieves the Y-coordinate position of the label.
     *
     * @return The Y-coordinate position
     */
    @Override
    public int getY() {
        return y;
    }

    /**
     * Retrieves the width of the label.
     *
     * @return The width of the label
     */
    @Override
    public int getWidth() {
        return width;
    }

    /**
     * Retrieves the height of the label.
     *
     * @return The height of the label
     */
    @Override
    public int getHeight() {
        return height;
    }

    /**
     * Sets whether the label is active (not implemented in this class).
     *
     * @param active True to set as active, false otherwise
     */
    @Override
    public void setActive(boolean active) {
        // Placeholder method, not implemented
    }

    /**
     * Checks if the label component is active (always returns false).
     *
     * @return Always false
     */
    @Override
    public boolean isComponentActive() {
        return false;
    }

    /**
     * Placeholder method for highlighting the label (not implemented in this class).
     */
    @Override
    public void highlightComponent() {
        // Placeholder method, not implemented
    }

    /**
     * Placeholder method for resetting highlight of the label (not implemented in this class).
     */
    @Override
    public void resetHighlightComponent() {
        // Placeholder method, not implemented
    }

    /**
     * Checks if the label is interactable (always returns false).
     *
     * @return Always false
     */
    @Override
    public boolean isInteractable() {
        return false;
    }

    /**
     * Placeholder method for handling window resize (not implemented in this class).
     *
     * @param width New width of the window
     * @param height New height of the window
     */
    @Override
    public void windowResized(int width, int height) {
        // Placeholder method, not implemented
    }

    /**
     * Removes the label component from the TUI manager.
     */
    @Override
    public void hide() {
        tuiManager.removeComponent(this);
    }

    /**
     * Retrieves the text content of the label.
     *
     * @return The text content of the label
     */
    public String getText() {
        return text;
    }

    /**
     * Retrieves the background color of the label.
     *
     * @return The background color of the label
     */
    public String getBackgroundColor() {
        return backgroundColor;
    }
}
