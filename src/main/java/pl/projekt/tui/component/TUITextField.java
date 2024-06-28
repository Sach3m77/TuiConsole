package pl.projekt.tui.component;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import pl.projekt.tui.model.color.Colors;
import pl.projekt.tui.model.keys.KeyInfo;
import pl.projekt.tui.model.keys.KeyLabel;

/**
 * Represents a text field component for a text-based user interface (TUI).
 * This component allows text input and display on the TUI screen.
 */
@Slf4j
public class TUITextField implements TUIComponent {

    private final StringBuilder textContent; // The text content of the field
    private final int x, y, width, height; // Position and dimensions of the field
    private String backgroundColor = Colors.BG_RED.getCode(); // Background color of the field
    @Setter
    private String textColor = Colors.TEXT_WHITE.getCode(); // Text color of the field
    private boolean isNumeric; // Flag indicating if the input is numeric
    private final int layerIndex; // Layer index for rendering order
    private boolean isActive; // Flag indicating if the field is active
    private final TUIManager tuiManager; // Manager responsible for TUI components
    private final int maxChars = Integer.MAX_VALUE; // Maximum characters allowed in the field

    /**
     * Constructs a new TUITextField.
     *
     * @param x           X-coordinate position
     * @param y           Y-coordinate position
     * @param width       Width of the field
     * @param height      Height of the field
     * @param layerIndex  Layer index for rendering order
     * @param tuiManager  Manager for TUI components
     */
    public TUITextField(int x, int y, int width, int height, int layerIndex, TUIManager tuiManager) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.layerIndex = layerIndex;
        this.tuiManager = tuiManager;
        this.textContent = new StringBuilder();
    }

    /**
     * Sets whether the text field allows only numeric input.
     *
     * @param numeric true if only numeric input is allowed, false otherwise
     */
    public void setNumeric(boolean numeric) {
        isNumeric = numeric;
    }

    /**
     * Parses the current text content into a double value.
     *
     * @return The parsed double value of the text content, or 0 if parsing fails
     */
    public double getParsedNumber() {
        try {
            log.info("Getting a number:" + textContent);
            return Double.parseDouble(textContent.toString());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * Draws the text field on the TUI screen.
     *
     * @param tuiManager The TUI manager responsible for rendering
     */
    @Override
    public void drawComponent(TUIManager tuiManager) {
        // Prepare text for display
        String text = textContent.toString();
        String displayText = text.length() > width ? text.substring(text.length() - width) : text;

        // Draw background of the field
        for (int i = 0; i < width; i++) {
            TUIScreenCell emptyCell = new TUIScreenCell(' ', textColor, backgroundColor);
            tuiManager.getScreen().addPixelToLayer(x + i, y, layerIndex, emptyCell);
        }

        // Draw text content of the field
        for (int i = 0; i < displayText.length(); i++) {
            TUIScreenCell cell = new TUIScreenCell(displayText.charAt(i), textColor, backgroundColor);
            tuiManager.getScreen().addPixelToLayer(x + i, y, layerIndex, cell);
        }
    }

    /**
     * Adds text from a key press to the text field.
     *
     * @param keyInfo Key information containing the pressed key's details
     */
    public void addText(KeyInfo keyInfo) {
        log.info("Appending text: {}", keyInfo.getValue());

        String newCharacter = keyInfo.getValue();

        // Handle non-DELETE key presses
        if (keyInfo.getLabel() != KeyLabel.DELETE) {
            // Validate numeric input if required
            if (isNumeric && !newCharacter.matches("\\d|\\.")) return;

            // Prevent multiple decimal points in numeric input
            if (isNumeric && textContent.indexOf(".") != -1 && keyInfo.getValue().equals(".")) return;
        }

        // Handle DELETE key press
        if (textContent.length() < maxChars || keyInfo.getLabel() == KeyLabel.DELETE) {
            if (keyInfo.getLabel() == KeyLabel.DELETE) {
                if (!textContent.isEmpty()) {
                    textContent.deleteCharAt(textContent.length() - 1);
                    tuiManager.refresh(); // Refresh the screen after deletion
                }
                return;
            }

            // Handle ENTER key press (perform action)
            if (keyInfo.getLabel() == KeyLabel.ENTER) {
                performAction();
                return;
            }

            // Append the new character to the text content
            textContent.append(keyInfo.getValue());

            tuiManager.refresh(); // Refresh the screen after text addition
        }
    }

    /**
     * Performs the action associated with the text field.
     * This method logs the action and current text content.
     */
    @Override
    public void performAction() {
        log.debug("Performing action");
        log.debug("Text content: {}", textContent);
    }

    /**
     * Shows the text field component by adding it to the TUI manager.
     */
    @Override
    public void show() {
        if (tuiManager != null) {
            tuiManager.addComponent(this);
        }
    }

    /**
     * Retrieves the X-coordinate position of the text field.
     *
     * @return The X-coordinate position
     */
    @Override
    public int getX() {
        return x;
    }

    /**
     * Retrieves the Y-coordinate position of the text field.
     *
     * @return The Y-coordinate position
     */
    @Override
    public int getY() {
        return y;
    }

    /**
     * Retrieves the width of the text field.
     *
     * @return The width of the text field
     */
    @Override
    public int getWidth() {
        return width;
    }

    /**
     * Retrieves the height of the text field.
     *
     * @return The height of the text field
     */
    @Override
    public int getHeight() {
        return height;
    }

    /**
     * Sets the active state of the text field.
     *
     * @param active true to set the field as active, false otherwise
     */
    @Override
    public void setActive(boolean active) {
        this.isActive = active;
    }

    /**
     * Checks if the text field is currently active.
     *
     * @return true if the field is active, false otherwise
     */
    @Override
    public boolean isComponentActive() {
        return this.isActive;
    }

    /**
     * Highlights the text field by changing its background and text color.
     */
    @Override
    public void highlightComponent() {
        setBgColor(Colors.BG_YELLOW.getCode());
        setTextColor(Colors.TEXT_BLACK.getCode());

        tuiManager.refresh(); // Refresh the screen after highlighting
    }

    /**
     * Resets the highlight of the text field to its default colors.
     */
    @Override
    public void resetHighlightComponent() {
        setBgColor(Colors.BG_RED.getCode());
        setTextColor(Colors.TEXT_WHITE.getCode());

        tuiManager.refresh(); // Refresh the screen after resetting highlight
    }

    /**
     * Checks if the text field is interactable.
     *
     * @return true if the text field is interactable, false otherwise
     */
    @Override
    public boolean isInteractable() {
        return true;
    }

    /**
     * Retrieves the layer index of the text field.
     *
     * @return The layer index of the text field
     */
    @Override
    public int getZIndex() {
        return layerIndex;
    }

    /**
     * Sets the background color of the text field.
     *
     * @param bgColor Background color code to set
     */
    public void setBgColor(String bgColor) {
        this.backgroundColor = bgColor;
    }

    /**
     * Handles window resize events for the text field.
     *
     * @param width  New width of the window
     * @param height New height of the window
     */
    @Override
    public void windowResized(int width, int height) {
        // No action needed on window resize for text field
    }

    /**
     * Hides the text field (currently not implemented).
     */
    @Override
    public void hide() {
        // No action needed on hiding for text field
    }

    // Getters for various properties

    /**
     * Retrieves the current text color of the text field.
     *
     * @return The current text color of the text field
     */
    public String getTextColor() {
        return textColor;
    }

    /**
     * Retrieves the current text content of the text field.
     *
     * @return The current text content of the text field
     */
    public Object getTextContent() {
        return textContent;
    }

    /**
     * Retrieves the current background color of the text field.
     *
     * @return The current background color of the text field
     */
    public String getBackgroundColor() {
        return backgroundColor;
    }
}
