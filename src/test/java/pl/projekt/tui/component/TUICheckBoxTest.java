package pl.projekt.tui.component;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import pl.projekt.tui.model.keys.KeyInfo;
import pl.projekt.tui.model.keys.KeyLabel;
import pl.projekt.tui.model.color.Colors;

import static org.junit.jupiter.api.Assertions.*;

class TUICheckBoxTest {
    private TUICheckBox checkBox;
    private TUIManager tuiManager;
    private TUIScreen screen;
    private TUIRadioButtonGroup radioButtonGroup;

    @BeforeEach
    void setUp() {
        tuiManager = Mockito.mock(TUIManager.class);
        screen = Mockito.mock(TUIScreen.class);
        radioButtonGroup = Mockito.mock(TUIRadioButtonGroup.class);

        Mockito.when(tuiManager.getScreen()).thenReturn(screen);

        checkBox = new TUICheckBox(1, 1, 3, 1, "TestLabel", "TestValue", tuiManager, radioButtonGroup);
    }

    @Test
    void testToggleCheck() {
        checkBox.toggleCheck();
        Mockito.verify(radioButtonGroup).handleCheckBoxSelection(checkBox);
    }

//    @Test
//    void testSetChecked() {
//        checkBox.setChecked(true);
//        assertTrue(checkBox.isChecked);
//        checkBox.setChecked(false);
//        assertFalse(checkBox.isChecked);
//    }

    @Test
    void testGetLabel() {
        assertEquals("TestLabel", checkBox.getLabel());
    }

    @Test
    void testDrawComponent() {
        checkBox.drawComponent(tuiManager);
        Mockito.verify(screen, Mockito.times(1)).setText(Mockito.eq(1), Mockito.eq(1), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.eq(1));
    }

    @Test
    void testHandleKeyboardInput() {
        KeyInfo enterKeyInfo = new KeyInfo(KeyLabel.ENTER);
        checkBox.handleKeyboardInput(enterKeyInfo);
        Mockito.verify(radioButtonGroup).handleCheckBoxSelection(checkBox);

        KeyInfo spaceKeyInfo = new KeyInfo(KeyLabel.SPACE);
        checkBox.handleKeyboardInput(spaceKeyInfo);
        Mockito.verify(radioButtonGroup, Mockito.times(2)).handleCheckBoxSelection(checkBox);
    }

//    @Test
//    void testShow() {
//        checkBox.show();
//        assertTrue(checkBox.isActive);
//        Mockito.verify(tuiManager).refresh();
//    }

//    @Test
//    void testHighlightComponent() {
//        checkBox.highlightComponent();
//        assertEquals(Colors.BG_BRIGHT_BLUE.getCode(), checkBox.bgColor);
//        Mockito.verify(tuiManager).refresh();
//    }
//
//    @Test
//    void testResetHighlightComponent() {
//        checkBox.resetHighlightComponent();
//        assertEquals(Colors.BG_BLUE.getCode(), checkBox.bgColor);
//        Mockito.verify(tuiManager).refresh();
//    }

    @Test
    void testIsComponentActive() {
        assertFalse(checkBox.isComponentActive());
        checkBox.setActive(true);
        assertTrue(checkBox.isComponentActive());
    }

    @Test
    void testGetValue() {
        assertEquals("TestValue", checkBox.getValue());
    }
}
