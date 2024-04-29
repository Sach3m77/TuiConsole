package pl.projekt.tui.component;

    /**
     * Interface for all TUI (Text-based User Interface) components.
     */
    public interface TUIComponent {
        /**
         * Draws the component.
         * @param TUIManager The UIManager object responsible for drawing the component.
         */
        void drawComponent(TUIManager TUIManager);

        /**
         * Returns the z-index of the component.
         * @return The z-index of the component.
         */
        int getZIndex();


        /**
         * Returns the height of the component.
         * @return The height of the component.
         */
        int getHeight();

        /**
         * Returns the width of the component.
         * @return The width of the component.
         */
        int getWidth();

        /**
         * Returns the x position of the component.
         * @return The x position of the component.
         */

        int getX();
        /**
         * Returns the y position of the component.
         * @return The y position of the component.
         */
        int getY();


        /**
         * Hides the component from the screen.
         */
        void hide();

        /**
         * Checks whether the position x, y is inside the component.
         * @param x The x-coordinate.
         * @param y The y-coordinate.
         * @return true if it's inside, false otherwise.
         */
        boolean isInside(int x, int y);

        /**
         * Allows performing an action for functional components.
         */
        void performAction();

        /**
         * Shows the component on the screen.
         */
        void show();


        /**
         * Allows activating the component.
         * @param active The state of the component.
         */
        void setActive(boolean active);

        /**
         * Returns whether the component is active.
         * @return true if the component is active, false otherwise.
         */
        boolean isActive();

        /**
         * Highlights the component.
         */
        void highlight();

        /**
         * Resets the highlight of the component.
         */
        void resetHighlight();

        /**
         * Indicates whether the component is interactable.
         * @return true if the component is interactable, false otherwise.
         */
        boolean isInteractable();

        /**
         * Handles window resize.
         * @param width The new width of the window.
         * @param height The new height of the window.
         */
        void windowResized(int width, int height);
    }
