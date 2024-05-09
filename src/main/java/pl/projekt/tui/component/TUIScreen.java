package pl.projekt.tui.component;

import lombok.extern.slf4j.Slf4j;
import pl.projekt.tui.model.color.Colors;

import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

@Slf4j
public class TUIScreen {
    private int width;
    private int height;
    private final Map<Integer, TUIScreenCell[][]> layers = new HashMap<>();
    private TUIScreenCell[][] mergedLayer;

    public TUIScreen(int width, int height) {
        this.width = width;
        this.height = height;
        this.mergedLayer = new TUIScreenCell[height][width];
        clearScreen();
    }

    private void mergeLayers() {
        clearScreen();
        log.trace("Merging layers");
        List<Integer> zIndexes = new ArrayList<>(layers.keySet());
        zIndexes.sort(Comparator.naturalOrder());
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

    private void ensureLayerExists(int zIndex) {
        log.trace("Ensuring layer with z-index {} exists", zIndex);
        if (!layers.containsKey(zIndex)) {
            layers.put(zIndex, new TUIScreenCell[height][width]);
        }
    }

    public void addLayer(int zIndex) {
        log.trace("Adding layer with z-index {}", zIndex);
        ensureLayerExists(zIndex);
    }

    public void addPixelToLayer(int x, int y, int zIndex, TUIScreenCell cell) {
        log.trace("Adding cell at ({}, {}) with z-index {}", x, y, zIndex);
        if (x >= width || y >= height || x < 0 || y < 0) {
            log.warn("\033[33mInvalid position set for pixel {} {} (max: {}, {})\033[0m", x, y, width - 1, height - 1);
            return;
        }
        ensureLayerExists(zIndex);
        layers.get(zIndex)[y][x] = cell;
    }

    public void clearScreen() {
        log.trace("Clearing screen");
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                mergedLayer[i][j] = new TUIScreenCell(' ', Colors.TEXT_WHITE.getCode(), Colors.BG_WHITE.getCode());
            }
        }
    }

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

    private String render() {
        log.trace("Rendering screen");
        long start = System.nanoTime();
        StringBuilder sb = new StringBuilder();
        sb.append("\033[H");
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                sb.append(mergedLayer[i][j].getBackgroundColor());
                sb.append(mergedLayer[i][j].getTextColor());
                sb.append(mergedLayer[i][j].getCharacter());
            }
            sb.append("\033[E");
        }
        sb.append("\033[0m");
        long stop = System.nanoTime();
        log.info("Render finished in {} ms", (stop - start) / 1000000.0);
        return sb.toString();
    }

    public void refresh(OutputStream out) throws IOException {
        mergeLayers();
        String rendered = render();
        out.write(rendered.getBytes());
        out.flush();
    }

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

    public void resize(int width, int height) {
        this.width = width;
        this.height = height;
        mergedLayer = new TUIScreenCell[height][width];
        List<Integer> keys = new ArrayList<>(layers.keySet());
        layers.clear();
        for (Integer key : keys)
            layers.put(key, new TUIScreenCell[height][width]);

        clearScreen();
    }
}