package pl.projekt.tui.component;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pl.projekt.tui.model.color.Colors;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TUITabTest {

    private TUITab tab;

    @Mock
    private TUIManager mockTUIManager;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        tab = new TUITab("Test Tab", 5, 5, 20, 10, 1, mockTUIManager);
    }

    @Test
    public void testConstructor() {
        assertEquals("Test Tab", tab.getTitle());
        assertEquals(5, tab.getX());
        assertEquals(5, tab.getY());
        assertEquals(20, tab.getWidth());
        assertEquals(10, tab.getHeight());
        assertEquals(1, tab.getZIndex());
    }

    @Test
    public void testAddComponent() {
        TUIComponent mockComponent = mock(TUIComponent.class);
        tab.addComponent(mockComponent);
        verify(mockComponent, times(0)).show();
    }

    @Test
    public void testSetActive() {
        tab.setActive(true);
        assertTrue(tab.isComponentActive());
    }

    @Test
    public void testResetActive() {
        tab.setActive(true);
        tab.resetHighlightComponent();
        assertTrue(!tab.isComponentActive());
    }

    @Test
    public void testWindowResized() {
        tab.windowResized(30, 15);
        assertEquals(30, tab.getWidth());
        assertEquals(15, tab.getHeight());
    }

    // Dodatkowe testy związane z wartościami x, y, width, height, layerIndex

    @Test
    public void testGetX() {
        assertEquals(5, tab.getX());
    }

    @Test
    public void testGetY() {
        assertEquals(5, tab.getY());
    }

    @Test
    public void testGetWidth() {
        assertEquals(20, tab.getWidth());
    }

    @Test
    public void testGetHeight() {
        assertEquals(10, tab.getHeight());
    }

    @Test
    public void testGetZIndex() {
        assertEquals(1, tab.getZIndex());
    }



    @Test
    public void testHighlightComponent() {
        tab.highlightComponent();
        assertTrue(tab.isComponentActive());
        assertEquals(Colors.TEXT_RED.getCode(), tab.getTextColor());
    }

    @Test
    public void testResetHighlightComponent() {
        tab.highlightComponent();
        tab.resetHighlightComponent();
        assertFalse(tab.isComponentActive());
        assertEquals(Colors.TEXT_BLACK.getCode(), tab.getTextColor());
    }

    @Test
    public void testShow() {
        tab.show();
        verify(mockTUIManager, times(1)).addComponent(tab);
    }

    @Test
    public void testHide() {
        tab.hide();
        // Testuję, że metoda hide nie powoduje błędów, ale nie ma efektów ubocznych do sprawdzenia.
        assertTrue(true);
    }

    @Test
    public void testIsInteractable() {
        assertTrue(tab.isInteractable());
    }



}
