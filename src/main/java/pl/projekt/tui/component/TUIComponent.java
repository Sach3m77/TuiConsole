package pl.projekt.tui.component;

/**
 * The TUIComponent interface represents a generic component in a Text User Interface (TUI).
 * It provides methods to draw the component, manage its properties and state, handle user interaction,
 * and respond to changes in the environment such as window resizing.
 */
public interface TUIComponent {

    /**
     * Draws the component using the provided TUIManager.
     *
     * @param tuiManager The manager responsible for handling TUI components.
     */
    void drawComponent(TUIManager tuiManager);

    /**
     * Returns the x-coordinate of the component.
     *
     * @return The x-coordinate.
     */
    int getX();

    /**
     * Returns the y-coordinate of the component.
     *
     * @return The y-coordinate.
     */
    int getY();

    /**
     * Returns the width of the component.
     *
     * @return The width.
     */
    int getWidth();

    /**
     * Returns the height of the component.
     *
     * @return The height.
     */
    int getHeight();

    /**
     * Returns the z-index of the component for layering purposes.
     *
     * @return The z-index.
     */
    int getZIndex();

    /**
     * Performs the action associated with the component.
     */
    void performAction();

    /**
     * Shows the component.
     */
    void show();

    /**
     * Sets the active state of the component.
     *
     * @param active The active state to set.
     */
    void setActive(boolean active);

    /**
     * Checks if the component is active.
     *
     * @return True if the component is active, false otherwise.
     */
    boolean isComponentActive();

    /**
     * Highlights the component.
     */
    void highlightComponent();

    /**
     * Resets the highlight of the component to its original state.
     */
    void resetHighlightComponent();

    /**
     * Checks if the component is interactable.
     *
     * @return True if the component is interactable, false otherwise.
     */
    boolean isInteractable();

    /**
     * Responds to a window resize event by adjusting the component's size and position if necessary.
     *
     * @param width  The new width of the window.
     * @param height The new height of the window.
     */
    void windowResized(int width, int height);

    /**
     * Hides the component.
     */
    void hide();
}
