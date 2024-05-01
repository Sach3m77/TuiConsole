package pl.projekt.tui.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.projekt.tui.model.color.Colors;

import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

/**
 * Class representing the application screen.
 */
public class TUIScreen {

    /**
     * Logger object.
     */
    private static final Logger logger = LoggerFactory.getLogger(TUIScreen.class);

    /**
     * Screen width.
     */
    private int width;
    /**
     * Screen height.
     */
    private int height;

    /**
     * Layers.
     */
    private final Map<Integer, TUIScreenCell[][]> layers = new HashMap<>();

    /**
     * Merged layers for presentation.
     */
    private TUIScreenCell[][] mergedLayer;

    /**
     * Screen constructor.
     * @param width Screen width.
     * @param height Screen height.
     */
    public TUIScreen(int width, int height) {
        this.width = width;
        this.height = height;
        this.mergedLayer = new TUIScreenCell[height][width];
        clearScreen();
    }


    /**
     * Merges all screen layers into the output <i>mergedLayer</i>.
     */
    private void mergeLayers() {
        clearScreen();
        logger.trace("Merging layers");
        var zIndexes = new ArrayList<>(layers.keySet());
        Collections.sort(zIndexes);
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
     * Ensures that the layer with the specified z-index exists.
     * @param zIndex The z-index of the layer.
     */
    private void ensureLayerExists(int zIndex) {
//        logger.trace("Ensuring layer with z-index {} exists", zIndex);
        if (!layers.containsKey(zIndex)) {
            layers.put(zIndex, new TUIScreenCell[height][width]);
        }
    }

    /**
     * Clears the screen cell at the specified position.
     * @param x Cell x-position.
     * @param y Cell y-position.
     * @param zIndex Cell z-index parameter.
     */
    public void clearCellAt(int x, int y, int zIndex) {
        if(x >= width || y >= height || x < 0 || y < 0){
            logger.warn("\033[33mInvalid position set for pixel {} {} (max: {}, {})\033[0m", x, y, width - 1, height - 1);
            return;
        }

        ensureLayerExists(zIndex);
        layers.get(zIndex)[y][x] = null;
        // mergeLayers();
    }

    /**
     * Adds a new layer to the screen.
     * @param zIndex Layer z-index.
     */
    public void addLayer(int zIndex) {
//        logger.trace("Adding layer with z-index {}", zIndex);
        ensureLayerExists(zIndex);
    }


    /**
     * Adds a cell to the layer with a specified z-index.
     * @param x Cell x-position.
     * @param y Cell y-position.
     * @param zIndex Target layer z-index.
     * @param cell Cell to add.
     */
    public void addPixelToLayer(int x, int y, int zIndex, TUIScreenCell cell) {
//        logger.trace("Adding cell at ({}, {}) with z-index {}", x, y, zIndex);
        if(x >= width || y >= height || x < 0 || y < 0){
            logger.warn("\033[33mInvalid position set for pixel {} {} (max: {}, {})\033[0m", x, y, width - 1, height - 1);
            return;
        }
        ensureLayerExists(zIndex);
        layers.get(zIndex)[y][x] = cell;
        // mergeLayers();
    }

    /**
     * Clears the screen.
     */
    public void clearScreen() {
        logger.trace("Clearing screen");
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                mergedLayer[i][j] = new TUIScreenCell(' ', Colors.TEXT_WHITE.getCode(), Colors.BG_WHITE.getCode());
            }
        }
    }

    /**
     * Sets text at the specified position and z-index.
     * @param x Text x-position.
     * @param y Text y-position.
     * @param text Text to display.
     * @param textColor Text color.
     * @param bgColor Background color.
     * @param zIndex Target layer z-index.
     */
    public void setText(int x, int y, String text, String textColor, String bgColor, int zIndex) {
        logger.trace("Setting text at ({}, {}) with z-index {}", x, y, zIndex);
        if(x >= width || y  >= height || x < 0 || y < 0){
            logger.warn("\033[33mInvalid position set for text {} {} (max: {}, {})\033[0m", x, y, width - 1, height - 1);
            return;
        }
        ensureLayerExists(zIndex);
        TUIScreenCell[][] targetLayer = layers.get(zIndex);
        for (int i = 0; i < text.length() && x + i < width; i++) {
            //logger.trace("Setting character {} at ({}, {})", text.charAt(i), x + i, y);
            targetLayer[y][x + i] = new TUIScreenCell(text.charAt(i), textColor, bgColor);
        }
        //mergeLayers();
    }

    /**
     * Sets the background for a given layer.
     * @param bgColor Color code.
     * @param zIndex Target layer z-index.
     */
    public void setBgColor(String bgColor, int zIndex) {
        logger.trace("Setting background color with z-index {}", zIndex);
        ensureLayerExists(zIndex);
        TUIScreenCell[][] targetLayer = layers.get(zIndex);
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (targetLayer[i][j] == null) {
                    // logger.trace("Setting background color at ({}, {})", j, i);
                    targetLayer[i][j] = new TUIScreenCell(' ', Colors.TEXT_BLACK.getCode(), bgColor);
                } else {
                    // logger.trace("Setting background color at ({}, {})", j, i);
                    targetLayer[i][j].setBgColor(bgColor);
                }
            }
        }
        //mergeLayers();
    }

    /**
     * Renders the screen.
     * @return Rendered screen.
     */
    private String render() {
        logger.trace("Rendering screen");
        long start = System.nanoTime();
        StringBuilder sb = new StringBuilder();
        sb.append("\033[H");
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                sb.append(mergedLayer[i][j].getBgColor());
                sb.append(mergedLayer[i][j].getTextColor());
                sb.append(mergedLayer[i][j].getCharacter());
            }
            sb.append("\033[E");
        }
        sb.append("\033[0m");
        long stop = System.nanoTime();
        logger.info("Render finished in {} ms", (stop - start) / 1000000.0);
        return sb.toString();
    }

    /**
     * Refreshes the screen.
     * @param out Output stream to send the screen after rendering.
     * @throws IOException If an I/O error occurs.
     */
    public void refresh(OutputStream out) throws IOException {
        mergeLayers();
        String rendered = render();
        out.write(rendered.getBytes());
        out.flush();
    }

    /**
     * Clears all layers.<br/>
     * Use only once - otherwise, the screen will be empty.
     */
    public void clearLayers(){
        for(Integer zIndex : layers.keySet()){
            TUIScreenCell[][] layer = layers.get(zIndex);
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    layer[i][j] = null;
                }
            }
        }
    }

    /**
     * Returns the cell from the specified position.
     * @param x Pixel x-position.
     * @param y Pixel y-position.
     * @param zIndex Layer z-index from which to retrieve the cell.
     * @return Pixel at the specified position.
     */
    public TUIScreenCell getPixelFromLayer(int x, int y, int zIndex) {
        if (layers.containsKey(zIndex)) {
            return layers.get(zIndex)[y][x];
        }
        return null;
    }

    /**
     * Changes the screen dimensions.
     * @param width New screen width.
     * @param height New screen height.
     */
    public void resize(int width, int height){
        this.width = width;
        this.height = height;
        mergedLayer = new TUIScreenCell[height][width];
        List<Integer> keys = layers.keySet().stream().toList();
        layers.clear();
        for(Integer key : keys )
            layers.put(key, new TUIScreenCell[height][width]);

        clearScreen();
    }
}
