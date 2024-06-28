package pl.projekt.tui.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.projekt.tui.model.keys.KeyInfo;
import pl.projekt.tui.model.keys.KeyLabel;
import pl.projekt.tui.model.color.Colors;

/**
 * The TUICheckBox class represents a checkbox component in a Text User Interface (TUI).
 * It implements the TUIComponent interface and handles drawing the checkbox on the screen,
 * toggling its checked state, and interacting with the user via keyboard input.
 */
public class TUICheckBox implements TUIComponent {

    private static final Logger logger = LoggerFactory.getLogger(TUICheckBox.class);

    private final String label;
    private boolean isChecked;
    private final int x;
    private final int y;
    private final int height;
    private String bgColor = Colors.BG_BLUE.getCode();
    private String textColor = Colors.TEXT_WHITE.getCode();
    private final int zIndex;
    private boolean isActive;
    private final TUIScreen screen;
    private final TUIManager tuiManager;
    private final TUIRadioButtonGroup radioButtonGroup;
    private final String value;

    /**
     * Constructor to initialize the TUICheckBox with specified properties.
     *
     * @param x                The x-coordinate of the top-left corner of the checkbox.
     * @param y                The y-coordinate of the top-left corner of the checkbox.
     * @param height           The height of the checkbox.
     * @param zIndex           The z-index of the checkbox for layering purposes.
     * @param label            The text label associated with the checkbox.
     * @param value            The value associated with the checkbox.
     * @param tuiManager       The manager to handle TUI components.
     * @param radioButtonGroup The radio button group this checkbox belongs to.
     */
    public TUICheckBox(int x, int y, int height, int zIndex, String label, String value, TUIManager tuiManager, TUIRadioButtonGroup radioButtonGroup) {
        this.x = x;
        this.y = y;
        this.height = height;
        this.zIndex = zIndex;
        this.screen = tuiManager.getScreen();
        this.label = label;
        this.value = value;
        this.tuiManager = tuiManager;
        this.radioButtonGroup = radioButtonGroup;
        this.isChecked = false;
        this.isActive = false;
        this.radioButtonGroup.addCheckBox(this);
    }

    /**
     * Toggles the checked state of the checkbox.
     */
    public void toggleCheck() {
        radioButtonGroup.handleCheckBoxSelection(this);
    }

    /**
     * Sets the checked state of the checkbox.
     *
     * @param isChecked The checked state to set.
     */
    public void setChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    /**
     * Returns the label associated with the checkbox.
     *
     * @return The label.
     */
    public String getLabel() {
        return label;
    }

    /**
     * Draws the checkbox on the TUI screen using the specified manager.
     *
     * @param tuiManager The manager to handle TUI components.
     */
    @Override
    public void drawComponent(TUIManager tuiManager) {
        char TOP_LEFT_CORNER = '┌';
        char TOP_RIGHT_CORNER = '┐';
        char BOTTOM_LEFT_CORNER = '└';
        char BOTTOM_RIGHT_CORNER = '┘';
        char HORIZONTAL_BORDER = '-';
        char VERTICAL_BORDER = '|';

        // Calculate the length of the label
        int labelLength = label.length();

        // Adjust the frame width to fit the label and add space for the checkbox
        int frameWidth = labelLength + 10; // 4 frame characters (left and right edges, spaces) + 2 checkbox characters

        // Draw the frame
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < frameWidth; j++) {
                char borderChar = ' ';
                if (i == 0 && j == 0) {
                    borderChar = TOP_LEFT_CORNER;
                } else if (i == 0 && j == frameWidth - 1) {
                    borderChar = TOP_RIGHT_CORNER;
                } else if (i == height - 1 && j == 0) {
                    borderChar = BOTTOM_LEFT_CORNER;
                } else if (i == height - 1 && j == frameWidth - 1) {
                    borderChar = BOTTOM_RIGHT_CORNER;
                } else if (i == 0 || i == height - 1) {
                    borderChar = HORIZONTAL_BORDER;
                } else if (j == 0 || j == frameWidth - 1) {
                    borderChar = VERTICAL_BORDER;
                }
                screen.setText(x + j, y + i, String.valueOf(borderChar), textColor, bgColor, zIndex);
            }
        }

        // Draw the label and checkbox
        String checkBoxRepresentation = isChecked ? "[X] " : "[ ] ";
        int checkBoxX = x + 2;
        int checkBoxY = y + height / 2;
        screen.setText(checkBoxX, checkBoxY, checkBoxRepresentation + label, textColor, bgColor, zIndex);
    }

    /**
     * Handles keyboard input for the checkbox.
     *
     * @param keyInfo The key information object containing key details.
     */
    public void handleKeyboardInput(KeyInfo keyInfo) {
        if (keyInfo.getLabel() == KeyLabel.ENTER || keyInfo.getLabel() == KeyLabel.SPACE) {
            toggleCheck();
        }
    }

    /**
     * Returns the z-index of the checkbox.
     *
     * @return The z-index.
     */
    @Override
    public int getZIndex() {
        return zIndex;
    }

    /**
     * Returns the height of the checkbox.
     *
     * @return The height.
     */
    @Override
    public int getHeight() {
        return height;
    }

    /**
     * Returns the width of the checkbox (not applicable in this implementation).
     *
     * @return The width.
     */
    @Override
    public int getWidth() {
        return 0;
    }

    /**
     * Returns the x-coordinate of the checkbox.
     *
     * @return The x-coordinate.
     */
    @Override
    public int getX() {
        return x;
    }

    /**
     * Returns the y-coordinate of the checkbox.
     *
     * @return The y-coordinate.
     */
    @Override
    public int getY() {
        return y;
    }

    /**
     * Performs the action associated with the checkbox (toggles the checked state).
     */
    @Override
    public void performAction() {
        toggleCheck();
    }

    /**
     * Shows the checkbox by setting it active and drawing it.
     */
    @Override
    public void show() {
        isActive = true;
        drawComponent(tuiManager);
    }

    /**
     * Sets the active state of the checkbox.
     *
     * @param active The active state to set.
     */
    @Override
    public void setActive(boolean active) {
        isActive = active;
    }

    /**
     * Checks if the checkbox is active.
     *
     * @return True if the checkbox is active, false otherwise.
     */
    @Override
    public boolean isComponentActive() {
        return isActive;
    }

    /**
     * Highlights the checkbox by changing its background color.
     */
    @Override
    public void highlightComponent() {
        bgColor = Colors.BG_BRIGHT_BLUE.getCode();
        tuiManager.refresh();
    }

    /**
     * Resets the highlight of the checkbox to its original background color.
     */
    @Override
    public void resetHighlightComponent() {
        bgColor = Colors.BG_BLUE.getCode();
        tuiManager.refresh();
    }

    /**
     * Checks if the checkbox is interactable.
     *
     * @return True as the checkbox is interactable.
     */
    @Override
    public boolean isInteractable() {
        return true;
    }

    /**
     * Handles the window resize event (no implementation in this version).
     *
     * @param newWidth  The new width of the window.
     * @param newHeight The new height of the window.
     */
    @Override
    public void windowResized(int newWidth, int newHeight) {
        // Adjust the size and position of the checkbox if necessary
    }

    /**
     * Hides the checkbox (no implementation in this version).
     */
    @Override
    public void hide() {
        // No implementation
    }

    /**
     * Returns the value associated with the checkbox.
     *
     * @return The value.
     */
    public String getValue() {
        return this.value;
    }

    /**
     * Checks if the checkbox is checked.
     *
     * @return True if the checkbox is checked, false otherwise.
     */
    public boolean isChecked() {
        return isChecked;
    }
}
