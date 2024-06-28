package pl.projekt.tui.component;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TUIRadioButtonGroupTest {

    private TUIManager tuiManager;
    private TUIRadioButtonGroup radioButtonGroup;
    private TUICheckBox checkBox1;
    private TUICheckBox checkBox2;

    @BeforeEach
    public void setUp() {
        tuiManager = mock(TUIManager.class);
        radioButtonGroup = new TUIRadioButtonGroup(tuiManager);

        // Create TUICheckBox instances with the correct constructor
        checkBox1 = new TUICheckBox(1, 1, 3, 1, "Option 1", "Option1", tuiManager, radioButtonGroup);
        checkBox2 = new TUICheckBox(1, 2, 3, 1, "Option 2", "Option2", tuiManager, radioButtonGroup);

        radioButtonGroup.addCheckBox(checkBox1);
        radioButtonGroup.addCheckBox(checkBox2);
    }

    @Test
    public void testHandleCheckBoxSelectionUniqueSelection() {
        radioButtonGroup.handleCheckBoxSelection(checkBox1);
        TUICheckBox selectedCheckBox1 = radioButtonGroup.getSelectedCheckBox();

        radioButtonGroup.handleCheckBoxSelection(checkBox2);
        TUICheckBox selectedCheckBox2 = radioButtonGroup.getSelectedCheckBox();

        assertNotEquals(selectedCheckBox1, selectedCheckBox2);
    }

    @Test
    public void testHandleCheckBoxSelection() {
        radioButtonGroup.handleCheckBoxSelection(checkBox1);

        assertTrue(checkBox1.isChecked());
        assertFalse(checkBox2.isChecked());
        assertEquals(checkBox1, radioButtonGroup.getSelectedCheckBox());
        verify(tuiManager, times(1)).refresh();
    }

    @Test
    public void testHandleCheckBoxSelectionTwice() {
        radioButtonGroup.handleCheckBoxSelection(checkBox1);
        radioButtonGroup.handleCheckBoxSelection(checkBox2);

        assertFalse(checkBox1.isChecked());
        assertTrue(checkBox2.isChecked());
        assertEquals(checkBox2, radioButtonGroup.getSelectedCheckBox());
        verify(tuiManager, times(2)).refresh();
    }

    @Test
    public void testGetSelectedCheckBox() {
        assertNull(radioButtonGroup.getSelectedCheckBox());

        radioButtonGroup.handleCheckBoxSelection(checkBox1);
        assertEquals(checkBox1, radioButtonGroup.getSelectedCheckBox());
    }

    @Test
    public void testSelectAndDeselectCheckBox() {
        assertNull(radioButtonGroup.getSelectedCheckBox());

        // Select checkBox1
        radioButtonGroup.handleCheckBoxSelection(checkBox1);
        assertEquals(checkBox1, radioButtonGroup.getSelectedCheckBox());

        // Select checkBox2
        radioButtonGroup.handleCheckBoxSelection(checkBox2);
        assertEquals(checkBox2, radioButtonGroup.getSelectedCheckBox());

        // Select checkBox1 again (should deselect checkBox2)
        radioButtonGroup.handleCheckBoxSelection(checkBox1);
        assertEquals(checkBox1, radioButtonGroup.getSelectedCheckBox());
    }


    @Test
    public void testFirstCheckBoxSelection() {
        assertNull(radioButtonGroup.getSelectedCheckBox());

        radioButtonGroup.handleCheckBoxSelection(checkBox1);
        assertEquals(checkBox1, radioButtonGroup.getSelectedCheckBox());
    }


    @Test
    public void testHandleCheckBoxSelectionWithEmptyGroup() {
        TUIRadioButtonGroup emptyGroup = new TUIRadioButtonGroup(tuiManager);

        assertDoesNotThrow(() -> {
            emptyGroup.handleCheckBoxSelection(null); // Should not throw exception even with null input
        });
    }
}
