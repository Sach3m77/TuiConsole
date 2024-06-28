package pl.projekt.tui.component;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import pl.projekt.tui.model.color.Colors;
import pl.projekt.tui.model.keys.KeyInfo;
import pl.projekt.tui.model.keys.KeyLabel;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TUITextFieldTest {
    private TUITextField textField;
    private TUIManager tuiManager;
    private TUIScreen tuiScreen;

    @BeforeEach
    public void setUp() {
        tuiManager = mock(TUIManager.class);
        tuiScreen = mock(TUIScreen.class);
        when(tuiManager.getScreen()).thenReturn(tuiScreen);
        textField = new TUITextField(10, 5, 20, 1, 1, tuiManager);
    }

    @Test
    public void testConstructor() {
        assertEquals(10, textField.getX());
        assertEquals(5, textField.getY());
        assertEquals(20, textField.getWidth());
        assertEquals(1, textField.getHeight());
        assertEquals(1, textField.getZIndex());
    }

    @Test
    public void testSetTextColor() {
        textField.setTextColor(Colors.TEXT_BLACK.getCode());
        assertEquals(Colors.TEXT_BLACK.getCode(), textField.getTextColor());
    }

    @Test
    public void testSetNumeric() {
        textField.setNumeric(true);
        KeyInfo keyInfo = new KeyInfo("F1", KeyLabel.F1);
        textField.addText(keyInfo);
        assertEquals("", textField.getTextContent().toString());
    }

    @Test
    public void testAddText() {
        KeyInfo keyInfo = new KeyInfo("F1", KeyLabel.F1);
        textField.addText(keyInfo);
        assertEquals("F1", textField.getTextContent().toString());
    }

    @Test
    public void testAddTextWithDelete() {
        KeyInfo keyInfoDelete = new KeyInfo("delete", KeyLabel.DELETE);
        textField.addText(keyInfoDelete);
        assertEquals("", textField.getTextContent().toString());
    }

    @Test
    public void testGetParsedNumber() {
        textField.setNumeric(true);
        KeyInfo keyInfo = new KeyInfo("123", KeyLabel.F2);
        textField.addText(keyInfo);
        assertEquals(0, textField.getParsedNumber()); // because "123" is added as a single input, not characters
    }



    @Test
    public void testShow() {
        textField.show();
        verify(tuiManager, times(1)).addComponent(textField);
    }

    @Test
    public void testIsComponentActive() {
        assertFalse(textField.isComponentActive());
    }

    @Test
    public void testHighlightComponent() {
        textField.highlightComponent();
        assertEquals(Colors.BG_YELLOW.getCode(), textField.getBackgroundColor());
        assertEquals(Colors.TEXT_BLACK.getCode(), textField.getTextColor());
        verify(tuiManager, times(1)).refresh();
    }

    @Test
    public void testResetHighlightComponent() {
        textField.resetHighlightComponent();
        assertEquals(Colors.BG_RED.getCode(), textField.getBackgroundColor());
        assertEquals(Colors.TEXT_WHITE.getCode(), textField.getTextColor());
        verify(tuiManager, times(1)).refresh();
    }

    @Test
    public void testIsInteractable() {
        assertTrue(textField.isInteractable());
    }

    @Test
    public void testHide() {
        textField.hide();
        // No specific behavior to test for hide in current implementation
    }
}
