package pl.projekt.tui.component;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.projekt.tui.model.color.Colors;
import pl.projekt.tui.model.keys.KeyInfo;
import pl.projekt.tui.model.keys.KeyLabel;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TUIDialogTest {

    private TUIManager tuiManager;
    private TUITab tuiTab;
    private TUIScreen screen;
    private TUIDialog dialog;
    private Runnable mockFunction;

    @BeforeEach
    public void setUp() {
        tuiManager = mock(TUIManager.class);
        tuiTab = mock(TUITab.class);
        screen = mock(TUIScreen.class);
        mockFunction = mock(Runnable.class);

        when(tuiManager.getScreen()).thenReturn(screen);

        dialog = new TUIDialog(10, 10, 50, 20, 0, "Test Dialog", 1.0, tuiManager, tuiTab, null, mockFunction);
    }

    @Test
    public void testDialogInitialization() {
        assertNotNull(dialog);
        assertEquals(10, dialog.getX());
        assertEquals(10, dialog.getY());
        assertEquals(50, dialog.getWidth());
        assertEquals(20, dialog.getHeight());
        assertEquals("Test Dialog", dialog.getTitle());
        assertFalse(dialog.isCancelled());
    }

    @Test
    public void testCancelDialog() {
        dialog.show();
        TUIButton cancelButton = dialog.getButtons().stream().filter(b -> b.getText().equals("CANCEL")).findFirst().orElse(null);
        assertNotNull(cancelButton);

        cancelButton.performAction();

        assertTrue(dialog.isCancelled());
        verify(tuiTab, times(1)).removeComponent(dialog);
    }



    @Test
    public void testSetAndResetColors() {
        dialog.setBgColor(Colors.BG_GREEN.getCode());
        dialog.setTextColor(Colors.TEXT_YELLOW.getCode());

        dialog.highlightComponent();
        assertEquals(Colors.BG_BRIGHT_MAGENTA.getCode(), dialog.getBgColor());
        assertEquals(Colors.TEXT_BLACK.getCode(), dialog.getTextColor());

        dialog.resetHighlightComponent();
        assertEquals(Colors.BG_RED.getCode(), dialog.getBgColor());
        assertEquals(Colors.TEXT_WHITE.getCode(), dialog.getTextColor());
    }

    @Test
    public void testMoveToNextActiveButton() {
        dialog.show();
        dialog.handleKeyboardInput(new KeyInfo(KeyLabel.ARROW_LEFT));
        assertTrue(dialog.getButtons().get(0).isComponentActive());

        dialog.handleKeyboardInput(new KeyInfo(KeyLabel.ARROW_LEFT));
        assertTrue(dialog.getButtons().get(1).isComponentActive());
    }

    @Test
    public void testMoveToPrevActiveButton() {
        dialog.show();
        dialog.handleKeyboardInput(new KeyInfo(KeyLabel.ARROW_RIGHT));
        assertTrue(dialog.getButtons().get(1).isComponentActive());

        dialog.handleKeyboardInput(new KeyInfo(KeyLabel.ARROW_LEFT));
        assertTrue(dialog.getButtons().get(0).isComponentActive());
    }

    @Test
    public void testCloseDialog() {
        dialog.show();
        dialog.close();

        assertFalse(dialog.isComponentActive());
        verify(tuiManager, times(2)).refresh();
        verify(screen, times(1)).render();
    }
}
