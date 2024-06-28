package pl.projekt.tui.component;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.projekt.tui.model.color.Colors;

import static org.junit.jupiter.api.Assertions.*;

public class TUIScreenTest {
    private TUIScreen screen;

    @BeforeEach
    public void setUp() {
        screen = new TUIScreen(100, 50);
    }

    @Test
    public void testInitialization() {
        assertEquals(100, screen.getWidth());
        assertEquals(50, screen.getHeight());
    }

    @Test
    public void testAddLayer() {
        screen.addLayer(0);
        assertTrue(screen.getLayers().containsKey(0));
    }

    @Test
    public void testAddPixelToLayer() {
        TUIScreenCell cell = new TUIScreenCell('X', Colors.TEXT_WHITE.getCode(), Colors.BG_BLACK.getCode());
        screen.addPixelToLayer(10, 20, 0, cell);
        assertEquals('X', screen.getLayers().get(0)[20][10].getCharacter());
    }

    @Test
    public void testSetText() {
        screen.setText(5, 5, "Hello", Colors.TEXT_WHITE.getCode(), Colors.BG_BLACK.getCode(), 0);
        assertEquals('H', screen.getLayers().get(0)[5][5].getCharacter());
        assertEquals('o', screen.getLayers().get(0)[5][9].getCharacter());
    }

    @Test
    public void testSetBgColor() {
        screen.setBgColor(Colors.BG_RED.getCode(), 0);
        assertEquals(Colors.BG_RED.getCode(), screen.getLayers().get(0)[0][0].getBackgroundColor());
    }

    @Test
    public void testClearLayers() {
        screen.addPixelToLayer(10, 20, 0, new TUIScreenCell('X', Colors.TEXT_WHITE.getCode(), Colors.BG_BLACK.getCode()));
        screen.clearLayers();
        assertNull(screen.getLayers().get(0)[20][10]);
    }

    @Test
    public void testResize() {
        screen.resize(200, 100);
        assertEquals(200, screen.getWidth());
        assertEquals(100, screen.getHeight());
    }

    @Test
    public void testClearCellAt() {
        screen.addPixelToLayer(10, 20, 0, new TUIScreenCell('X', Colors.TEXT_WHITE.getCode(), Colors.BG_BLACK.getCode()));
        screen.clearCellAt(10, 20, 0);
        assertNull(screen.getLayers().get(0)[20][10]);
    }
}
