package pl.projekt.tui.component;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import pl.projekt.tui.model.color.Colors;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TUIListTest {
    private TUIList tuiList;
    private TUIManager tuiManager;
    private TUIScreen tuiScreen;

    @BeforeEach
    public void setUp() {
        tuiManager = mock(TUIManager.class);
        tuiScreen = mock(TUIScreen.class);
        List<String> listContents = Arrays.asList("Item 1", "Item 2", "Item 3");
        tuiList = new TUIList(10, 5, 1, tuiScreen, tuiManager, listContents);
    }

    @Test
    public void testConstructor() {
        assertEquals(10, tuiList.getX());
        assertEquals(5, tuiList.getY());
        assertEquals(1, tuiList.getZIndex());
        assertEquals(Colors.BG_BLUE.getCode(), tuiList.getBgColor());
        assertEquals(Colors.TEXT_BLACK.getCode(), tuiList.getTextColor());
    }

    @Test
    public void testDrawComponent() {
        tuiList.drawComponent(tuiManager);

        verify(tuiScreen, times(1)).setText(10, 5, "| Item 1", Colors.TEXT_BLACK.getCode(), Colors.BG_BLUE.getCode(), 1);
        verify(tuiScreen, times(1)).setText(10, 6, "| Item 2", Colors.TEXT_BLACK.getCode(), Colors.BG_BLUE.getCode(), 1);
        verify(tuiScreen, times(1)).setText(10, 7, "| Item 3", Colors.TEXT_BLACK.getCode(), Colors.BG_BLUE.getCode(), 1);
    }

    @Test
    public void testShow() {
        tuiList.show();
        assertTrue(tuiList.isComponentActive());
        verify(tuiManager, times(1)).addComponent(tuiList);
    }

    @Test
    public void testHide() {
        tuiList.hide();
        assertFalse(tuiList.isComponentActive());
        verify(tuiManager, times(1)).removeComponent(tuiList);
    }

    @Test
    public void testSetActive() {
        tuiList.setActive(true);
        assertTrue(tuiList.isComponentActive());

        tuiList.setActive(false);
        assertFalse(tuiList.isComponentActive());
    }

    @Test
    public void testIsInteractable() {
        assertFalse(tuiList.isInteractable());
    }
}
