package pl.projekt.tui.component;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import pl.projekt.tui.model.color.Colors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TUIButtonTest {
    private TUIButton button;
    private Runnable action;
    private TUIManager tuiManager;

    @BeforeEach
    public void setUp() {
        action = mock(Runnable.class);
        tuiManager = mock(TUIManager.class);
        button = new TUIButton(10, 10, 100, 20, 1, "Test Button", action, tuiManager);
    }

    @Test
    public void testIsInside() {
        assertTrue(button.isInside(15, 15));
        assertFalse(button.isInside(5, 5));
    }

    @Test
    public void testPerformAction() {
        button.performAction();
        verify(action, times(1)).run();
    }

    @Test
    public void testSetTextAlign() {
        button.setTextAlign(TUIButton.TextAlign.LEFT);
        assertEquals(TUIButton.TextAlign.LEFT, button.getTextAlign());

        button.setTextAlign(TUIButton.TextAlign.RIGHT);
        assertEquals(TUIButton.TextAlign.RIGHT, button.getTextAlign());

        button.setTextAlign(TUIButton.TextAlign.CENTER);
        assertEquals(TUIButton.TextAlign.CENTER, button.getTextAlign());
    }

    @Test
    public void testSetBgColor() {
        button.setBgColor(Colors.BG_BLUE.getCode());
        assertEquals(Colors.BG_BLUE.getCode(), button.getBackgroundColor());
    }

    @Test
    public void testSetTextColor() {
        button.setTextColor(Colors.TEXT_GREEN.getCode());
        assertEquals(Colors.TEXT_GREEN.getCode(), button.getTextColor());
    }

    @Test
    public void testShow() {
        button.show();
        verify(tuiManager, times(1)).addComponent(button);
    }

    @Test
    public void testHide() {
        button.hide();
        verify(tuiManager, times(1)).removeComponent(button);
        assertFalse(button.isComponentActive());
    }

    @Test
    public void testHighlightComponent() {
        button.highlightComponent();
        assertEquals(Colors.BG_YELLOW.getCode(), button.getBackgroundColor());
        assertEquals(Colors.TEXT_BLACK.getCode(), button.getTextColor());
        verify(tuiManager, times(1)).refresh();
    }

    @Test
    public void testResetHighlightComponent() {
        button.resetHighlightComponent();
        assertEquals(Colors.BG_RED.getCode(), button.getBackgroundColor());
        assertEquals(Colors.TEXT_WHITE.getCode(), button.getTextColor());
        verify(tuiManager, times(1)).refresh();
    }
}
