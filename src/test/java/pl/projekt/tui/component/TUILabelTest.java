package pl.projekt.tui.component;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import pl.projekt.tui.model.color.Colors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TUILabelTest {
    private TUILabel label;
    private TUIManager tuiManager;

    @BeforeEach
    public void setUp() {
        tuiManager = mock(TUIManager.class);
        label = new TUILabel("Test Label", 5, 10, 1, Colors.BG_WHITE.getCode(), tuiManager);
    }

    @Test
    public void testConstructor() {
        assertEquals("Test Label", label.getText());
        assertEquals(5, label.getX());
        assertEquals(10, label.getY());
        assertEquals(1, label.getZIndex());
        assertEquals(Colors.BG_WHITE.getCode(), label.getBackgroundColor());
    }

    @Test
    public void testSetBgColor() {
        label.setBgColor(Colors.BG_BLUE.getCode());
        assertEquals(Colors.BG_BLUE.getCode(), label.getBackgroundColor());
    }

    @Test
    public void testSetText() {
        label.setText("New Text");
        assertEquals("New Text", label.getText());
    }


    @Test
    public void testDrawComponent() {
        TUIScreen screen = mock(TUIScreen.class);
        when(tuiManager.getScreen()).thenReturn(screen);

        label.drawComponent(tuiManager);
        verify(screen, times(1)).setText(5, 10, "Test Label", Colors.TEXT_BLACK.getCode(), Colors.BG_WHITE.getCode(), 1);
    }

    @Test
    public void testShow() {
        label.show();
        verify(tuiManager, times(1)).addComponent(label);
    }

    @Test
    public void testHide() {
        label.hide();
        verify(tuiManager, times(1)).removeComponent(label);
    }

    @Test
    public void testIsComponentActive() {
        assertFalse(label.isComponentActive());
    }

    @Test
    public void testIsInteractable() {
        assertFalse(label.isInteractable());
    }
}
