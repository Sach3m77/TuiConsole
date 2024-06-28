package pl.projekt.tui.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.projekt.tui.model.color.Colors;

import java.util.List;

/**
 * Represents a list component for a Text User Interface (TUI).
 */
public class TUIList implements TUIComponent {

    private Logger logger = LoggerFactory.getLogger(TUIList.class);
    private StringBuilder text;           // Unused StringBuilder for future expansion
    private int x;                        // X-coordinate position of the list
    private int y;                        // Y-coordinate position of the list
    private String bgColor = Colors.BG_BLUE.getCode();  // Background color of the list
    private String textColor = Colors.TEXT_BLACK.getCode();  // Text color of the list
    private int zIndex;                   // Z-index or layer index for component stacking
    private boolean isActive;             // Activity state of the list
    private TUIManager tuiManager;        // Manager for TUI components
    private List<String> listContents;    // Contents of the list
    private int listMargin = 1;           // Margin between list items
    private static final String LIST_DOT = ">";  // Symbol for list item marker
    private List<TUILabel> labels;        // Labels associated with list items
    private TUIScreen tuiScreen;          // Screen for displaying TUI components

    /**
     * Constructs a new TUIList with specified parameters.
     *
     * @param x X-coordinate position of the list
     * @param y Y-coordinate position of the list
     * @param zIndex Z-index or layer index for component stacking
     * @param tuiScreen TUI screen for rendering components
     * @param tuiManager TUI manager for managing components
     * @param listContents Contents of the list
     */
    public TUIList(int x, int y, int zIndex, TUIScreen tuiScreen, TUIManager tuiManager, List<String> listContents) {
        this.x = x;
        this.y = y;
        this.zIndex = zIndex;
        this.tuiScreen = tuiScreen;
        this.tuiManager = tuiManager;
        this.text = new StringBuilder();  // Initialize unused StringBuilder
        this.listContents = listContents;
    }

    /**
     * Draws the list component on the TUI screen.
     *
     * @param tuiManager The TUI manager instance to use for drawing
     */
    @Override
    public void drawComponent(TUIManager tuiManager) {
        char VERTICAL_BORDER = '|';

        // Find the maximum length of any item in the list
        int maxItemLength = 0;
        for (String item : listContents) {
            maxItemLength = Math.max(maxItemLength, item.length());
        }

        StringBuilder listBuilder = new StringBuilder();

        // Iterate through each item in the list
        for (int i = 0; i < listContents.size(); i++) {
            String item = listContents.get(i);

            // Calculate padding to align items
            int padding = maxItemLength - item.length();

            // Append each item with proper formatting and padding
            listBuilder.append(VERTICAL_BORDER).append(" ").append(item);

            // Add spaces for padding to align items
            for (int j = 0; j < padding; j++) {
                listBuilder.append(" ");
            }

            listBuilder.append("\n");
        }

        // Split the drawn list into lines
        String[] drawnLines = listBuilder.toString().split("\n");

        // Print each line at the correct y position
        for (int i = 0; i < drawnLines.length; i++) {
            tuiScreen.setText(x, y + i, drawnLines[i], textColor, bgColor, 1);
        }
    }

    /**
     * Retrieves the X-coordinate position of the list.
     *
     * @return The X-coordinate position
     */
    @Override
    public int getX() {
        return x;
    }

    /**
     * Retrieves the Y-coordinate position of the list.
     *
     * @return The Y-coordinate position
     */
    @Override
    public int getY() {
        return y;
    }

    /**
     * Placeholder method for retrieving the width of the list (not implemented).
     *
     * @return Always returns 0
     */
    @Override
    public int getWidth() {
        return 0;  // Placeholder, not implemented
    }

    /**
     * Placeholder method for retrieving the height of the list (not implemented).
     *
     * @return Always returns 0
     */
    @Override
    public int getHeight() {
        return 0;  // Placeholder, not implemented
    }

    /**
     * Retrieves the Z-index or layer index of the list.
     *
     * @return The Z-index or layer index
     */
    @Override
    public int getZIndex() {
        return zIndex;
    }

    /**
     * Placeholder method for performing an action on the list (not implemented).
     */
    @Override
    public void performAction() {
        // Placeholder, not implemented
    }

    /**
     * Activates the list component, making it visible in the TUI.
     */
    @Override
    public void show() {
        isActive = true;
        tuiManager.addComponent(this);
    }

    /**
     * Sets the active state of the list component.
     *
     * @param active True to set the list as active, false otherwise
     */
    @Override
    public void setActive(boolean active) {
        isActive = active;
    }

    /**
     * Checks if the list component is currently active.
     *
     * @return True if the list is active, false otherwise
     */
    @Override
    public boolean isComponentActive() {
        return isActive;
    }

    /**
     * Placeholder method for highlighting the list component (not implemented).
     */
    @Override
    public void highlightComponent() {
        // Placeholder, not implemented
    }

    /**
     * Placeholder method for resetting highlight of the list component (not implemented).
     */
    @Override
    public void resetHighlightComponent() {
        // Placeholder, not implemented
    }

    /**
     * Checks if the list component is interactable (always returns false).
     *
     * @return Always false
     */
    @Override
    public boolean isInteractable() {
        return false;  // Lists are not interactable in this implementation
    }

    /**
     * Placeholder method for handling window resize (not implemented).
     *
     * @param width New width of the window
     * @param height New height of the window
     */
    @Override
    public void windowResized(int width, int height) {
        // Placeholder, not implemented
    }

    /**
     * Deactivates the list component, hiding it from the TUI.
     */
    @Override
    public void hide() {
        isActive = false;
        tuiManager.removeComponent(this);
    }

    /**
     * Retrieves the background color of the list.
     *
     * @return The background color
     */
    public String getBgColor() {
        return bgColor;
    }

    /**
     * Retrieves the text color of the list.
     *
     * @return The text color
     */
    public String getTextColor() {
        return textColor;
    }
}
