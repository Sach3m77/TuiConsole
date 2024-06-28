package pl.projekt.tui.component;

import lombok.extern.slf4j.Slf4j;
import pl.projekt.tui.model.color.Colors;
import pl.projekt.tui.model.keys.KeyInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * TUITab class represents a tab component in a text-based user interface (TUI).
 * It contains UI components and manages their activation, interaction, and rendering within the tab.
 */
@Slf4j
public class TUITab implements TUIComponent {

    private final String title;  // Title of the tab
    private String textColor = Colors.TEXT_BLACK.getCode();  // Text color of the tab
    private final String backgroundColor = Colors.BG_BRIGHT_WHITE.getCode();  // Background color of the tab
    private final String tabColor = "\033[33m";  // Color for rendering the tab header
    private final List<TUIComponent> components = new ArrayList<>();  // List of components within the tab
    private final int x, y;  // Position of the tab on the screen
    private int width, height;  // Dimensions of the tab
    private int currentActiveComponent = -1;  // Index of the currently active component
    private final int layerIndex;  // Layer index for rendering
    private final TUIManager TUIManager;  // Reference to the TUIManager for managing UI components
    private boolean isActive;  // Flag indicating if the tab is currently active

    /**
     * Constructor to initialize a TUITab with specified title, position, dimensions, layer index, and TUIManager reference.
     * @param title Title of the tab.
     * @param x X-coordinate position of the tab.
     * @param y Y-coordinate position of the tab.
     * @param windowWidth Width of the tab.
     * @param windowHeight Height of the tab.
     * @param layerIndex Layer index for rendering the tab.
     * @param TUIManager Reference to the TUIManager for managing UI components.
     */
    public TUITab(String title, int x, int y, int windowWidth, int windowHeight, int layerIndex, TUIManager TUIManager) {
        this.x = x;
        this.y = y;
        this.width = windowWidth;
        this.height = windowHeight;
        this.layerIndex = layerIndex;
        this.TUIManager = TUIManager;
        this.title = title;
        this.isActive = false;
    }

    /**
     * Method to draw the tab component and its contents on the screen.
     * @param TUIManager TUIManager instance for rendering components.
     */
    @Override
    public void drawComponent(TUIManager TUIManager) {
        // Render tab header
        if (isActive) {
            for (int i = 0; i < width; ++i) {
                for (int j = y + 1; j < height; ++j) {
                    TUIScreenCell emptyCell = new TUIScreenCell(' ', textColor, backgroundColor);
                    TUIManager.getScreen().addPixelToLayer(i, j, layerIndex, emptyCell);
                }
            }
        }
        TUIManager.getScreen().addPixelToLayer(x, y, layerIndex, new TUIScreenCell(' ', textColor, tabColor));
        TUIManager.getScreen().addPixelToLayer(x + 1, y, layerIndex, new TUIScreenCell(' ', textColor, tabColor));

        // Render tab title
        for (int i = 0; i < title.length(); ++i) {
            TUIManager.getScreen().addPixelToLayer(x + 2 + i, y, layerIndex, new TUIScreenCell(title.charAt(i), textColor, tabColor));
        }
        TUIManager.getScreen().addPixelToLayer(x + title.length() + 2, y, layerIndex, new TUIScreenCell(' ', textColor, tabColor));
        TUIManager.getScreen().addPixelToLayer(x + title.length() + 3, y, layerIndex, new TUIScreenCell(' ', textColor, tabColor));

        // Render components within the tab if active
        if (isActive) {
            for (TUIComponent component : components) {
                component.drawComponent(TUIManager);
            }
        }
    }

    /**
     * Method to add a UI component to the tab.
     * @param component UI component to add.
     */
    public void addComponent(TUIComponent component) {
        log.debug("Adding UI component " + component.getClass().getSimpleName());
        components.add(component);
    }

    /**
     * Retrieves the layer index of the tab for rendering.
     * @return Layer index of the tab.
     */
    @Override
    public int getZIndex() {
        return layerIndex;
    }

    /**
     * Placeholder method for performing action on the tab (currently not implemented).
     */
    @Override
    public void performAction() {
        // Placeholder method for performing action on the tab (currently not implemented)
    }

    /**
     * Method to show the tab by adding it to the TUIManager.
     */
    @Override
    public void show() {
        if (TUIManager != null) {
            TUIManager.addComponent(this);
        }
    }

    /**
     * Retrieves the X-coordinate position of the tab.
     * @return X-coordinate position of the tab.
     */
    @Override
    public int getX() {
        return x;
    }

    /**
     * Retrieves the Y-coordinate position of the tab.
     * @return Y-coordinate position of the tab.
     */
    @Override
    public int getY() {
        return y;
    }

    /**
     * Retrieves the width of the tab.
     * @return Width of the tab.
     */
    @Override
    public int getWidth() {
        return width;
    }

    /**
     * Retrieves the height of the tab.
     * @return Height of the tab.
     */
    @Override
    public int getHeight() {
        return height;
    }

    /**
     * Sets the active state of the tab.
     * @param active True to set the tab as active, false otherwise.
     */
    @Override
    public void setActive(boolean active) {
        this.isActive = active;
        if (active) {
            highlightComponent();
        } else {
            resetHighlightComponent();
        }
    }

    /**
     * Checks if the tab is currently active.
     * @return True if the tab is active, false otherwise.
     */
    @Override
    public boolean isComponentActive() {
        return isActive;
    }

    /**
     * Highlights the tab component.
     */
    @Override
    public void highlightComponent() {
        textColor = Colors.TEXT_RED.getCode();  // Change text color to highlight
        isActive = true;  // Set tab as active
        TUIManager.refresh();  // Refresh the TUIManager to reflect changes
    }

    /**
     * Resets the highlight state of the tab component.
     */
    @Override
    public void resetHighlightComponent() {
        textColor = Colors.TEXT_BLACK.getCode();  // Reset text color to default
        isActive = false;  // Set tab as inactive
        TUIManager.refresh();  // Refresh the TUIManager to reflect changes
    }

    /**
     * Checks if the tab is interactable.
     * @return True if the tab is interactable, false otherwise.
     */
    @Override
    public boolean isInteractable() {
        return true;  // Tab is always interactable
    }

    /**
     * Method called when the window is resized, adjusts the dimensions of the tab and its components.
     * @param width New width of the window.
     * @param height New height of the window.
     */
    @Override
    public void windowResized(int width, int height) {
        this.width = width;
        this.height = height;
        for (TUIComponent component : components) {
            component.windowResized(width, height);
        }
    }

    /**
     * Placeholder method for hiding the tab (currently not implemented).
     */
    @Override
    public void hide() {
        // Placeholder method for hiding the tab (currently not implemented)
    }

    /**
     * Helper method to highlight the active component within the tab.
     */
    private void highlightActiveComponent() {
        log.info("Highlighting active component.");
        for (TUIComponent component : components) {
            if (component.isComponentActive()) {
                component.highlightComponent();
            } else {
                component.resetHighlightComponent();
            }
        }
    }

    /**
     * Helper method to move focus to the next active component within the tab.
     */
    private void moveToNextActiveComponent() {
        if (components.isEmpty()) {
            log.info("No components to activate.");
            return;
        }
        if (currentActiveComponent >= components.size()) {
            log.warn("Current active component index out of bounds. Resetting to first component.");
            this.currentActiveComponent = -1;
        }
        if (currentActiveComponent != -1) {
            components.get(currentActiveComponent).setActive(false);
            log.info("Deactivating current active component.");
        }
        int startComponent = currentActiveComponent == -1 ? (components.size() - 1) : currentActiveComponent;
        log.info("Starting from: " + startComponent);
        do {
            currentActiveComponent = (currentActiveComponent + 1) % components.size();
        } while (!components.get(currentActiveComponent).isInteractable() && currentActiveComponent != startComponent);

        components.get(currentActiveComponent).setActive(true);
    }

/**
 * Helper method to move focus to the previous active component within the tab.
 */
private void moveToPrevActiveComponent() {
    if (components.isEmpty()) {
        log.info("No components to activate.");
        return;
    }
    if (currentActiveComponent >= components.size()) {
        log.warn("Current active component index out of bounds. Resetting to first component.");
        currentActiveComponent = -1;
    }

    if (currentActiveComponent != -1) {
        components.get(currentActiveComponent).setActive(false);
        log.info("Deactivating current active component.");
    }
    int startComponent = currentActiveComponent == -1 ? 0 : currentActiveComponent;
    do {
        currentActiveComponent = (currentActiveComponent - 1);
        if (currentActiveComponent < 0)
            currentActiveComponent = components.size() - 1;
    } while (!components.get(currentActiveComponent).isInteractable() && currentActiveComponent != startComponent);

    components.get(currentActiveComponent).setActive(true);
}

    /**
     * Method to handle keyboard input for the tab, directing actions based on the key pressed.
     * @param keyInfo KeyInfo object containing information about the key pressed.
     */
    public void handleKeyboardInput(KeyInfo keyInfo) {
        switch (keyInfo.getLabel()) {
            case ARROW_DOWN:
                moveToNextActiveComponent();
                break;
            case ARROW_UP:
                moveToPrevActiveComponent();
                break;
            case ENTER, ENTER_ALT:
                if (currentActiveComponent != -1)
                    components.get(currentActiveComponent).performAction();
                break;
            default:
                if (isActive && currentActiveComponent != -1) {
                    if (components.get(currentActiveComponent) instanceof TUITextField activeField) {
                        log.info("Adding text value to field");
                        activeField.addText(keyInfo);
                    } else if (components.get(currentActiveComponent) instanceof TUIDialog dialogWindow && !dialogWindow.isCancelled()){
                        log.info("Dialog window");
                        dialogWindow.handleKeyboardInput(keyInfo);
                    }

                }
                break;
        }

        highlightActiveComponent();  // Highlight the active component after handling input
    }

    /**
     * Method to remove a UI component from the tab.
     * @param component UI component to remove.
     */
    public void removeComponent(TUIComponent component) {
        currentActiveComponent = -1;  // Reset current active component index
        this.components.remove(component);  // Remove the specified component
    }

    /**
     * Getter for checking if the tab is currently active.
     * @return True if the tab is active, false otherwise.
     */
    public boolean isActive() {
        return isActive;
    }

    /**
     * Getter for retrieving the title of the tab.
     * @return Title of the tab.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Getter for retrieving the text color of the tab.
     * @return Text color of the tab.
     */
    public String getTextColor() {
        return textColor;
    }
}

