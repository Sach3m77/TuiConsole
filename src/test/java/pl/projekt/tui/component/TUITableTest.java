package pl.projekt.tui.component;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class TUITableTest {

    private TUIScreen mockTUIScreen;
    private TUIManager mockTUIManager;

    @BeforeEach
    void setUp() {
        mockTUIScreen = mock(TUIScreen.class);
        mockTUIManager = mock(TUIManager.class);
    }


    @Test
    void testShow() {
        List<String> cells = new ArrayList<>();
        TUITable table = new TUITable(2, 3, 2, cells, 1, mockTUIScreen, mockTUIManager);

        table.show();

        verify(mockTUIManager, times(1)).addComponent(table);
        assertEquals(true, table.isComponentActive());
    }

    @Test
    void testHide() {
        List<String> cells = new ArrayList<>();
        TUITable table = new TUITable(2, 3, 2, cells, 1, mockTUIScreen, mockTUIManager);
        table.show(); // Ensure table is initially shown

        table.hide();

        verify(mockTUIManager, times(1)).removeComponent(table);
        assertEquals(false, table.isComponentActive());
    }



    @Test
    void testWindowResized() {
        List<String> cells = Arrays.asList("Cell 1", "Cell 2", "Cell 3", "Cell 4", "Cell 5");
        TUITable table = new TUITable(2, 3, 2, cells, 1, mockTUIScreen, mockTUIManager);
        table.drawComponent(mockTUIManager); // Draw table initially

        // Simulate window resize to a smaller width and height
        table.windowResized(20, 10);

        // Assert that the table is redrawn with the new dimensions
        verify(mockTUIScreen, atLeastOnce()).setText(anyInt(), anyInt(), anyString(), anyString(), anyString(), anyInt());
        // You can add specific assertions based on the new dimensions and expected rendering behavior
    }

    @Test
    void testGetX() {
        List<String> cells = new ArrayList<>();
        TUITable table = new TUITable(2, 3, 2, cells, 1, mockTUIScreen, mockTUIManager);

        assertEquals(2, table.getX());
    }

    @Test
    void testGetY() {
        List<String> cells = new ArrayList<>();
        TUITable table = new TUITable(2, 3, 2, cells, 1, mockTUIScreen, mockTUIManager);

        assertEquals(3, table.getY());
    }

    @Test
    void testGetWidthInitially() {
        List<String> cells = new ArrayList<>();
        TUITable table = new TUITable(2, 3, 2, cells, 1, mockTUIScreen, mockTUIManager);

        assertEquals(0, table.getWidth()); // Initially width should be 0
    }

    @Test
    void testGetHeightInitially() {
        List<String> cells = new ArrayList<>();
        TUITable table = new TUITable(2, 3, 2, cells, 1, mockTUIScreen, mockTUIManager);

        assertEquals(0, table.getHeight()); // Initially height should be 0
    }

    @Test
    void testGetZIndex() {
        List<String> cells = new ArrayList<>();
        TUITable table = new TUITable(2, 3, 2, cells, 5, mockTUIScreen, mockTUIManager);

        assertEquals(5, table.getZIndex());
    }


}
