package pl.projekt.tui.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.projekt.tui.model.color.Colors;

import java.util.List;

/**
 * Represents a table component in a text-based user interface (TUI).
 */
public class TUITable implements TUIComponent {

    private final int x;
    private final int y;
    private int cols;
    private List<String> cells;
    private String bgColor = Colors.BG_BLUE.getCode();
    private String textColor = Colors.TEXT_BLACK.getCode();
    private final int zIndex;
    private final TUIScreen tuiScreen;
    private final TUIManager tuiManager;
    private String[] drawnLines;

    private boolean isActive;
    private final Logger logger = LoggerFactory.getLogger(TUITable.class);

    /**
     * Constructs a TUITable object.
     *
     * @param x          X-coordinate position of the table.
     * @param y          Y-coordinate position of the table.
     * @param cols       Number of columns in the table.
     * @param cells      List of cells containing table data.
     * @param zIndex     Z-index for rendering order.
     * @param tuiScreen  TUIScreen object where the table will be rendered.
     * @param tuiManager TUIManager object for managing components.
     */
    public TUITable(int x, int y, int cols, List<String> cells, int zIndex, TUIScreen tuiScreen, TUIManager tuiManager) {
        this.x = x;
        this.y = y;
        this.cols = cols;
        this.cells = cells;
        this.zIndex = zIndex;
        this.tuiScreen = tuiScreen;
        this.tuiManager = tuiManager;
    }

    /**
     * Draws the table component onto the TUIScreen.
     *
     * @param tuiManager TUIManager object for managing components.
     */
    @Override
    public void drawComponent(TUIManager tuiManager) {
        char HORIZONTAL_BORDER = '-';
        char VERTICAL_BORDER = '|';
        char CORNER = '+';

        int rows = (int) Math.ceil((double) cells.size() / cols);

        int[] colWidths = new int[cols];
        for (int col = 0; col < cols; col++) {
            int maxColWidth = 0;
            for (int row = 0; row < rows; row++) {
                int cellIndex = row * cols + col;
                if (cellIndex < cells.size()) {
                    maxColWidth = Math.max(maxColWidth, cells.get(cellIndex).length());
                }
            }
            colWidths[col] = maxColWidth;
        }

        StringBuilder tableBuilder = new StringBuilder();

        // Draw top border
        tableBuilder.append(CORNER);
        for (int col = 0; col < cols; col++) {
            for (int i = 0; i < colWidths[col] + 2; i++) {
                tableBuilder.append(HORIZONTAL_BORDER);
            }
            tableBuilder.append(CORNER);
        }
        tableBuilder.append("\n");

        // Draw rows with cells
        for (int row = 0; row < rows; row++) {
            // Draw cell row
            tableBuilder.append(VERTICAL_BORDER);
            for (int col = 0; col < cols; col++) {
                int cellIndex = row * cols + col;
                if (cellIndex < cells.size()) {
                    String cell = cells.get(cellIndex);
                    tableBuilder.append(" ").append(cell);
                    for (int i = 0; i < colWidths[col] - cell.length(); i++) {
                        tableBuilder.append(" ");
                    }
                    tableBuilder.append(" ");
                } else {
                    for (int i = 0; i < colWidths[col] + 2; i++) {
                        tableBuilder.append(" ");
                    }
                }
                tableBuilder.append(VERTICAL_BORDER);
            }
            tableBuilder.append("\n");

            // Draw separator row
            tableBuilder.append(CORNER);
            for (int col = 0; col < cols; col++) {
                for (int i = 0; i < colWidths[col] + 2; i++) {
                    tableBuilder.append(HORIZONTAL_BORDER);
                }
                tableBuilder.append(CORNER);
            }
            tableBuilder.append("\n");
        }

        // Split the drawn table into lines
        drawnLines = tableBuilder.toString().split("\n");

        // Print each line at the correct y position on the TUIScreen
        for (int i = 0; i < drawnLines.length; i++) {
            tuiScreen.setText(x, y + i, drawnLines[i], textColor, bgColor, zIndex);
        }
    }

    /**
     * Gets the X-coordinate position of the table.
     *
     * @return X-coordinate position.
     */
    @Override
    public int getX() {
        return x;
    }

    /**
     * Gets the Y-coordinate position of the table.
     *
     * @return Y-coordinate position.
     */
    @Override
    public int getY() {
        return y;
    }

    /**
     * Gets the width of the table.
     *
     * @return Width of the table.
     */
    @Override
    public int getWidth() {
        return 0; // Width can be calculated dynamically based on the content
    }

    /**
     * Gets the height of the table.
     *
     * @return Height of the table.
     */
    @Override
    public int getHeight() {
        return 0; // Height can be calculated dynamically based on the content
    }

    /**
     * Gets the Z-index for rendering order.
     *
     * @return Z-index value.
     */
    @Override
    public int getZIndex() {
        return zIndex;
    }

    /**
     * Performs an action associated with the table component.
     */
    @Override
    public void performAction() {
        // Implement any specific actions if needed
    }

    /**
     * Shows the table component by adding it to the TUIManager.
     */
    @Override
    public void show() {
        isActive = true;
        tuiManager.addComponent(this);
    }

    /**
     * Sets the active state of the table component.
     *
     * @param active True to set the component active, false otherwise.
     */
    @Override
    public void setActive(boolean active) {
        isActive = active;
    }

    /**
     * Checks if the table component is currently active.
     *
     * @return True if the component is active, false otherwise.
     */
    @Override
    public boolean isComponentActive() {
        return isActive;
    }

    /**
     * Highlights the table component.
     * (Note: Highlighting logic can be implemented if needed)
     */
    @Override
    public void highlightComponent() {
        // Implement highlighting logic if needed
    }

    /**
     * Resets the highlighting of the table component.
     * (Note: Resetting highlight logic can be implemented if needed)
     */
    @Override
    public void resetHighlightComponent() {
        // Implement logic to reset highlighting if needed
    }

    /**
     * Checks if the table component is interactable.
     *
     * @return True if the component is interactable, false otherwise.
     */
    @Override
    public boolean isInteractable() {
        return false; // Change if the component should be interactable
    }

    /**
     * Handles window resize events for the table component.
     *
     * @param width  New width of the window.
     * @param height New height of the window.
     */
    @Override
    public void windowResized(int width, int height) {
        // Implement logic to handle window resize if needed
    }

    /**
     * Hides the table component by removing it from the TUIManager.
     */
    public void hide() {
        this.isActive = false;
        logger.info("Table component hidden");
        tuiManager.removeComponent(this);
    }
}
