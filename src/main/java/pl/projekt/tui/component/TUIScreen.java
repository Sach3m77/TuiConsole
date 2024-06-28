package pl.projekt.tui.component;

import lombok.extern.slf4j.Slf4j;
import pl.projekt.tui.model.color.Colors;

import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

/**
 * TUIScreen class represents a text-based screen in a text-based user interface (TUI).
 * It manages multiple layers of cells that can be rendered to an output stream.
 */
@Slf4j
public class TUIScreen {

    private int width;  // Width of the screen
    private int height;  // Height of the screen
    private final Map<Integer, TUIScreenCell[][]> layers = new HashMap<>();  // Layers of cells organized by zIndex
    private TUIScreenCell[][] mergedLayer;  // Merged layer of cells for rendering

    /**
     * Constructor to initialize the screen with specified width and height.
     * @param width Width of the screen.
     * @param height Height of the screen.
     */
    public TUIScreen(int width, int height) {
        this.width = width;
        this.height = height;
        this.mergedLayer = new TUIScreenCell[height][width];
        clearScreen();  // Initialize the merged layer with default cells
    }

    /**
     * Merges all layers into a single merged layer for rendering.
     */
    private void mergeLayers() {
        clearScreen();  // Clear the merged layer before merging
        log.trace("Merging layers");
        List<Integer> zIndexes = new ArrayList<>(layers.keySet());
        zIndexes.sort(Comparator.naturalOrder());  // Sort zIndexes in ascending order
        for (Integer zIndex : zIndexes) {
            TUIScreenCell[][] layer = layers.get(zIndex);
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    if (layer[i][j] != null) {
                        mergedLayer[i][j] = layer[i][j];
                    }
                }
            }
        }
    }

    /**
     * Ensures that a layer with the specified zIndex exists.
     * @param zIndex zIndex of the layer to ensure.
     */
    private void ensureLayerExists(int zIndex) {
        log.trace("Ensuring layer with z-index {} exists", zIndex);
        if (!layers.containsKey(zIndex)) {
            layers.put(zIndex, new TUIScreenCell[height][width]);
        }
    }

    /**
     * Adds a new layer with the specified zIndex.
     * @param zIndex zIndex of the layer to add.
     */
    public void addLayer(int zIndex) {
        log.trace("Adding layer with z-index {}", zIndex);
        ensureLayerExists(zIndex);
    }

    /**
     * Adds a cell to the specified layer at the specified position.
     * @param x X-coordinate of the cell position.
     * @param y Y-coordinate of the cell position.
     * @param zIndex zIndex of the layer to add the cell to.
     * @param cell TUIScreenCell representing the cell to add.
     */
    public void addPixelToLayer(int x, int y, int zIndex, TUIScreenCell cell) {
        log.trace("Adding cell at ({}, {}) with z-index {}", x, y, zIndex);
        if (x >= width || y >= height || x < 0 || y < 0) {
            log.warn("\033[33mInvalid position set for pixel {} {} (max: {}, {})\033[0m", x, y, width - 1, height - 1);
            return;
        }
        ensureLayerExists(zIndex);
        layers.get(zIndex)[y][x] = cell;
    }

    /**
     * Clears the entire screen by setting all cells to default values.
     */
    public void clearScreen() {
        log.trace("Clearing screen");
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                mergedLayer[i][j] = new TUIScreenCell(' ', Colors.TEXT_WHITE.getCode(), Colors.BG_WHITE.getCode());
            }
        }
    }

    /**
     * Sets text at the specified position on the screen with the specified colors and zIndex.
     * @param x X-coordinate of the text position.
     * @param y Y-coordinate of the text position.
     * @param text Text to set on the screen.
     * @param textColor Text color of the text.
     * @param bgColor Background color of the text.
     * @param zIndex zIndex of the layer to set the text on.
     */
    public void setText(int x, int y, String text, String textColor, String bgColor, int zIndex) {
        log.trace("Setting text at ({}, {}) with z-index {}", x, y, zIndex);
        if (x >= width || y >= height || x < 0 || y < 0) {
            log.warn("\033[33mInvalid position set for text {} {} (max: {}, {})\033[0m", x, y, width - 1, height - 1);
            return;
        }
        ensureLayerExists(zIndex);
        TUIScreenCell[][] targetLayer = layers.get(zIndex);
        for (int i = 0; i < text.length() && x + i < width; i++) {
            log.trace("Setting character {} at ({}, {})", text.charAt(i), x + i, y);
            targetLayer[y][x + i] = new TUIScreenCell(text.charAt(i), textColor, bgColor);
        }
    }

    /**
     * Sets the background color for the entire screen with the specified zIndex.
     * @param bgColor Background color to set.
     * @param zIndex zIndex of the layer to set the background color on.
     */
    public void setBgColor(String bgColor, int zIndex) {
        log.trace("Setting background color with z-index {}", zIndex);
        ensureLayerExists(zIndex);
        TUIScreenCell[][] targetLayer = layers.get(zIndex);
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (targetLayer[i][j] == null) {
                    log.trace("Setting background color at ({}, {})", j, i);
                    targetLayer[i][j] = new TUIScreenCell(' ', Colors.TEXT_BLACK.getCode(), bgColor);
                } else {
                    log.trace("Setting background color at ({}, {})", j, i);
                    targetLayer[i][j].setBackgroundColor(bgColor);
                }
            }
        }
    }

    /**
     * Renders the screen content as a string for display.
     * @return String representing the rendered screen content.
     */
    public String render() {
        log.trace("Rendering screen");
        long start = System.nanoTime();
        StringBuilder sb = new StringBuilder();
        sb.append("\033[H");  // Move cursor to the top left corner of the screen
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                sb.append(mergedLayer[i][j].getBackgroundColor());
                sb.append(mergedLayer[i][j].getTextColor());
                sb.append(mergedLayer[i][j].getCharacter());
            }
            sb.append("\033[E");  // Move cursor to the beginning of the next line
        }
        sb.append("\033[0m");  // Reset text attributes
        long stop = System.nanoTime();
        log.info("Render finished in {} ms", (stop - start) / 1000000.0);
        return sb.toString();
    }

    /**
     * Refreshes the screen content and writes it to the specified output stream.
     * @param out OutputStream to write the rendered screen content.
     * @throws IOException If an I/O error occurs while writing to the output stream.
     */
    public void refresh(OutputStream out) throws IOException {
        mergeLayers();  // Merge all layers before rendering
        String rendered = render();  // Render the screen content as a string
        out.write(rendered.getBytes());  // Write the rendered content to the output stream
        out.flush();  // Flush the output stream to ensure data is written immediately
    }

    /**
     * Clears all layers by setting all cells in each layer to null.
     */
    public void clearLayers() {
        for (Integer zIndex : layers.keySet()) {
            TUIScreenCell[][] layer = layers.get(zIndex);
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    layer[i][j] = null;
                }
            }
        }
    }

    /**
     * Resizes the screen to the specified width and height.
     * @param width New width of the screen.
     * @param height New height of the screen.
     */
    public void resize(int width, int height) {
        this.width = width;
        this.height = height;
        mergedLayer = new TUIScreenCell[height][width];  // Create a new merged layer with the new dimensions
        List<Integer> keys = new ArrayList<>(layers.keySet());
        layers.clear();  // Clear all existing layers
        for (Integer key : keys) {
            layers.put(key, new TUIScreenCell[height][width]);  // Create new layers with the new dimensions
        }

        clearScreen();  // Clear the screen content after resizing
    }

/**
 * Clears the cell at the specified position in the specified layer.
 * @param x X-coordinate of the cell position.
 * @param y Y-coordinate of the cell position.
 * @param zIndex zIndex of the layer from which to clear the cell.
 */
public void clearCellAt(int x, int y, int zIndex) {
    if (x >= width || y >= height || x < 0 || y < 0) {
        return;  // If coordinates are out of bounds, do nothing
    }

    ensureLayerExists(zIndex);  // Ensure that the layer with the specified zIndex exists
    layers.get(zIndex)[y][x] = null;  // Clear the cell at the specified position in the layer
}

    /**
     * Retrieves the current width of the screen.
     * @return Current width of the screen.
     */
    public int getWidth() {
        return width;
    }

    /**
     * Retrieves the current height of the screen.
     * @return Current height of the screen.
     */
    public int getHeight() {
        return height;
    }

    /**
     * Retrieves all layers of cells currently managed by the screen.
     * @return Map containing zIndex as keys and corresponding TUIScreenCell arrays as values.
     */
    public Map<Integer, TUIScreenCell[][]> getLayers() {
        return layers;
    }
}
