package pl.projekt.tui.keys;

import org.junit.jupiter.api.Test;
import pl.projekt.tui.model.keys.KeyInfo;
import pl.projekt.tui.model.keys.KeyLabel;

import static org.junit.jupiter.api.Assertions.*;

public class KeyInfoTest {

    @Test
    public void testKeyInfoWithLabelOnly() {
        KeyLabel label = KeyLabel.ENTER;
        KeyInfo keyInfo = new KeyInfo(label);

        assertEquals("", keyInfo.getValue());
        assertTrue(keyInfo.isFunctional());
        assertEquals(label, keyInfo.getLabel());
    }

    @Test
    public void testKeyInfoWithValueAndLabel() {
        String value = "a";
        KeyLabel label = KeyLabel.CTRL_C;
        KeyInfo keyInfo = new KeyInfo(value, label);

        assertEquals(value, keyInfo.getValue());
        assertFalse(keyInfo.isFunctional());
        assertEquals(label, keyInfo.getLabel());
    }

    @Test
    public void testKeyInfoWithNullValue() {
        KeyLabel label = KeyLabel.CTRL_C;
        assertThrows(NullPointerException.class, () -> new KeyInfo(null, label));
    }

    @Test
    public void testKeyInfoWithNullLabel() {
        String value = "a";
        assertThrows(NullPointerException.class, () -> new KeyInfo(value, null));
    }


}
