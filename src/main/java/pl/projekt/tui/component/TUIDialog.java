package pl.projekt.tui.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.projekt.tui.model.color.Colors;
import pl.projekt.tui.model.keys.KeyInfo;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * The TUIDialog class represents a dialog window component in a text-based user interface (TUI).
 */
public class TUIDialog implements TUIComponent {

    // Logger for logging messages related to TUIDialog
    Logger logger = LoggerFactory.getLogger(TUIDialog.class);

    // Characters used for drawing borders of the dialog
    private static final char HORIZONTAL_BORDER = '─';
    private static final char VERTICAL_BORDER = '│';
    private static final char TOP_LEFT_CORNER = '┌';
    private static final char TOP_RIGHT_CORNER = '┐';
    private static final char BOTTOM_LEFT_CORNER = '└';
    private static final char BOTTOM_RIGHT_CORNER = '┘';

    // Constants for button appearance and layout
    private static final int MIN_BUTTON_WIDTH = 10;
    private static final int TEXT_MARGIN = 4;

    // Dialog properties
    private String message = "";
    private Double score = 0.0;
    private int x, y, width, height, currentActiveButton = -1;
    private String title;
    private List<TUIButton> buttons;
    private String bgColor = Colors.BG_WHITE.getCode();
    private String textColor = Colors.TEXT_BLACK.getCode();
    private int zIndex;
    private TUIScreen screen;
    private TUIManager tuiManager;
    private TUITab tuiTab; // Reference to TUITab
    private boolean active;
    private boolean cancelled; // Flag to check if the dialog has been cancelled

    private TUIComponent tuiComponent;
    private Runnable function;

    /**
     * Constructor for TUIDialog.
     */
    public TUIDialog(int x, int y, int width, int height, int zIndex, String title, Double score, TUIManager tuiManager, TUITab tuiTab) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.title = title;
        this.score = score;
        this.buttons = new ArrayList<>();
        this.screen = tuiManager.getScreen();
        this.zIndex = zIndex;
        this.tuiManager = tuiManager;
        this.tuiTab = tuiTab; // Initialize reference to TUITab
        this.cancelled = false; // Initialize cancelled flag
        initializeButtons();
    }

    /**
     * Overloaded constructor for TUIDialog with additional parameters.
     */
    public TUIDialog(int x, int y, int width, int height, int zIndex, String title, Double score, TUIManager tuiManager, TUITab tuiTab, TUIComponent tuiComponent, Runnable function) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.title = title;
        this.score = score;
        this.buttons = new ArrayList<>();
        this.screen = tuiManager.getScreen();
        this.zIndex = zIndex;
        this.tuiManager = tuiManager;
        this.tuiTab = tuiTab; // Initialize reference to TUITab
        this.cancelled = false; // Initialize cancelled flag
        this.function = function;
        this.tuiComponent = tuiComponent;
        initializeButtons();
    }

    /**
     * Initializes the buttons for the dialog.
     */
    private void initializeButtons() {

        TUIButton okButton = new TUIButton(x + width / 4, y + height - 3, MIN_BUTTON_WIDTH, 1, zIndex + 1, "OK", () -> {
            if (score != null) {
                try {
                    setMessage(String.valueOf(String.format("%.2f", score)));
                    tuiManager.refresh();
                    tuiManager.render();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else {
                function.run();
                tuiManager.refresh();
                tuiManager.render();
            }
        }, tuiManager);

        TUIButton cancelButton = new TUIButton(x + 3 * width / 4 - MIN_BUTTON_WIDTH, y + height - 3, MIN_BUTTON_WIDTH, 1, zIndex + 1, "CANCEL", () -> {
            logger.info("Dialog cancelled");
            setCancelled(true); // Set the cancelled state

            tuiTab.removeComponent(this); // Remove the dialog from TUITab
            close(); // Close the dialog

            if (tuiComponent != null) {
                tuiComponent.hide();
            }

            tuiManager.refresh();
            tuiManager.render();
            screen.clearLayers();
        }, tuiManager);

        buttons.add(okButton);
        buttons.add(cancelButton);
    }

    /**
     * Returns the list of buttons in the dialog.
     */
    public List<TUIButton> getButtons() {
        return buttons;
    }

    /**
     * Shows the dialog if it is not cancelled.
     */
    public void show() {
        if (isCancelled()) {
            logger.info("Dialog will not be shown because it was cancelled.");
            return;
        }
        drawComponent(tuiManager);
    }
    /**
     * Return x
     */
    @Override
    public int getX() {
        return x;
    }
    /**
     * Return y
     */
    @Override
    public int getY() {
        return y;
    }
    /**
     * Return width
     */
    @Override
    public int getWidth() {
        return width;
    }
    /**
     * Return height
     */
    @Override
    public int getHeight() {
        return height;
    }
    /**
     * Set active
     */
    @Override
    public void setActive(boolean active) {
        this.active = active;
    }
    /**
     * Return active
     */
    @Override
    public boolean isComponentActive() {
        return this.active;
    }
    /**
     * Highlight component.
     */
    @Override
    public void highlightComponent() {
        logger.debug("Highlighting button");
        setBgColor(Colors.BG_BRIGHT_MAGENTA.getCode());
        setTextColor(Colors.TEXT_BLACK.getCode());
        tuiManager.refresh();
    }
    /**
     * Reset highlight component
     */
    @Override
    public void resetHighlightComponent() {
        setBgColor(Colors.BG_RED.getCode());
        setTextColor(Colors.TEXT_WHITE.getCode());
        tuiManager.refresh();
    }
    /**
     * Return true is component interactable
     */
    @Override
    public boolean isInteractable() {
        return true;
    }

    /**
     * Hides the dialog and clears the screen.
     */
    public void hide() {
        this.active = false;
        logger.info("Dialog hidden{}", this.message);

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                screen.clearCellAt(x + j, y + i, zIndex);
            }
        }
        // Remove shadow (added)
        for (int i = 1; i <= height; i++) {
            screen.clearCellAt(x + width, y + i, zIndex);
        }
        for (int j = 0; j <= width; j++) {
            screen.clearCellAt(x + j, y + height, zIndex);
        }
        // End shadow removal
        for (TUIButton button : buttons) {
            button.hide();
            logger.info("Dialog hidden{}", button.getText());
        }

        tuiManager.removeComponent(this);
        tuiManager.refresh();
        tuiManager.render();
    }

    /**
     * Sets the message of the dialog.
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Sets the background color of the dialog.
     */
    public void setBgColor(String color) {
        this.bgColor = color;
    }

    /**
     * Sets the text color of the dialog.
     */
    public void setTextColor(String color) {
        this.textColor = color;
    }

    @Override
    public void drawComponent(TUIManager tuiManager) {

        String firstLine = message;
        String secondLine = "";

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                char borderChar = ' ';  // Default empty space
                if (i == 0 && j == 0) {
                    borderChar = TOP_LEFT_CORNER;
                } else if (i == 0 && j == width - 1) {
                    borderChar = TOP_RIGHT_CORNER;
                } else if (i == height - 1 && j == 0) {
                    borderChar = BOTTOM_LEFT_CORNER;
                } else if (i == height - 1 && j == width - 1) {
                    borderChar = BOTTOM_RIGHT_CORNER;
                } else if (i == 0 || i == height - 1) {
                    borderChar = HORIZONTAL_BORDER;
                } else if (j == 0 || j == width - 1) {
                    borderChar = VERTICAL_BORDER;
                }
                screen.setText(x + j, y + i, String.valueOf(borderChar), textColor, bgColor, zIndex);
            }
        }

        if (message.length() > width - TEXT_MARGIN) {
            int breakIndex = message.substring(0, width - TEXT_MARGIN).lastIndexOf(' ');
            if (breakIndex == -1) {
                breakIndex = width - TEXT_MARGIN;
            }
            firstLine = message.substring(0, breakIndex).trim();
            secondLine = message.substring(breakIndex).trim();
        }

        int firstLineX = x + (width - firstLine.length()) / 2;
        screen.setText(firstLineX, y + (height / 2) - 1, firstLine, textColor, bgColor, zIndex);

        if (!secondLine.isEmpty()) {
            int secondLineX = x + (width - secondLine.length()) / 2;
            if (secondLineX >= 0 && secondLineX + secondLine.length() < width) {
                screen.setText(secondLineX, y + (height / 2), secondLine, textColor, bgColor, zIndex);
            } else {
                logger.warn("Second line of the message is too long to fit in the dialog window");
            }
        }

        // Shadow for the right and bottom edges
        for (int i = 1; i <= height; i++) {
            screen.setText(x + width, y + i, " ", Colors.BG_BLACK.getCode(), bgColor, zIndex);
        }
        for (int j = 0; j <= width; j++) {
            screen.setText(x + j, y + height, " ", Colors.BG_BLACK.getCode(), bgColor, zIndex);
        }

        int titleX = x + (width - title.length()) / 2;
        screen.setText(titleX, y, title, textColor, bgColor, zIndex);

        // Draw buttons
        for (TUIButton button : buttons) {
            button.drawComponent(tuiManager);
        }
    }
    /**
     * Return z index
     */
    @Override
    public int getZIndex() {
        return zIndex;
    }
    /**
     * Perform action
     */
    @Override
    public void performAction() {
        // No default action
    }
    /**
     * Resized window
     */
    @Override
    public void windowResized(int width, int height) {
        // Handle window resize if necessary
    }

    /**
     * Highlights the currently active button.
     */
    private void highlightActiveButton() {
        logger.info("Highlighting active button.");
        for (TUIButton button : buttons) {
            if (button.isComponentActive()) {
                logger.info("Dialog window name:{}", this.title);
                logger.info("Highlighting button:{}", button.getText());
                button.highlightComponent();
            } else {
                logger.info("Reset highlighting button:{}", button.getText());
                button.resetHighlightComponent();
            }
        }
    }

    /**
     * Moves to the next active button in the dialog.
     */
    private void moveToNextActiveButton() {
        if (buttons.isEmpty()) {
            logger.info("No components to activate.");
            return;
        }
        if (currentActiveButton >= buttons.size()) {
            logger.warn("Current active button index out of bounds. Resetting to first component.");
            currentActiveButton = -1;
        }

        if (currentActiveButton != -1) {
            buttons.get(currentActiveButton).setActive(false);
            logger.info("Deactivating current active component.");
        }
        int startComponent = currentActiveButton == -1 ? (buttons.size() - 1) : currentActiveButton;
        logger.info("Starting from: {}", startComponent);
        do {
            currentActiveButton = (currentActiveButton + 1) % buttons.size();
        } while (!buttons.get(currentActiveButton).isInteractable() && currentActiveButton != startComponent);

        buttons.get(currentActiveButton).setActive(true);
    }

    /**
     * Moves to the previous active button in the dialog.
     */
    private void moveToPrevActiveButton() {
        if (buttons.isEmpty()) {
            logger.info("No components to activate in moveToPrevButton.");
            return;
        }
        if (currentActiveButton >= buttons.size()) {
            logger.warn("Current active button index out of bounds. Resetting to first component.");
            currentActiveButton = -1;
        }

        if (currentActiveButton != -1) {
            buttons.get(currentActiveButton).setActive(false);
            logger.info("Deactivating current active component.");
        }
        int startComponent = currentActiveButton == -1 ? 0 : currentActiveButton;
        do {
            currentActiveButton = (currentActiveButton - 1);
            currentActiveButton = (currentActiveButton - 1);
            if (currentActiveButton < 0)
                currentActiveButton = buttons.size() - 1;
        } while (!buttons.get(currentActiveButton).isInteractable() && currentActiveButton != startComponent);

        buttons.get(currentActiveButton).setActive(true);
    }

    /**
     * Handles keyboard input for the dialog.
     */
    public void handleKeyboardInput(KeyInfo keyInfo) {

        switch (keyInfo.getLabel()) {
            case ARROW_LEFT:
                if (!isCancelled())
                    moveToNextActiveButton();
                break;
            case ARROW_RIGHT:
                if (!isCancelled())
                    moveToPrevActiveButton();
                break;
            case SPACE:
                if (isComponentActive() && currentActiveButton != -1)
                    buttons.get(currentActiveButton).performAction();
                break;
        }
        highlightActiveButton();
    }

    /**
     * Checks if the dialog is cancelled.
     */
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * Sets the cancelled state of the dialog.
     */
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    /**
     * Closes the dialog and removes all components.
     */
    public void close() {
        for (TUIButton button : buttons) {
            tuiManager.removeComponent(button);
        }
        this.hide();
        logger.info("Close a dialog window");

        tuiManager.refresh(); // Refresh screen after removing buttons
        screen.render();
    }

    /**
     * Returns the title of the dialog.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Returns the background color of the dialog.
     */
    public String getBgColor() {
        return bgColor;
    }

    /**
     * Returns the text color of the dialog.
     */
    public String getTextColor() {
        return textColor;
    }

    /**
     * Returns the message of the dialog.
     */
    public String getMessage() {
        return message;
    }
}
