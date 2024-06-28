package pl.projekt.tui.model.keys;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Handles keyboard input by mapping key codes to KeyInfo objects.
 * This class initializes and manages key mappings for various key combinations.
 */
@Slf4j
public class KeyboardHandler {

    private final Map<String, KeyInfo> keys = new HashMap<>();

    /**
     * Initializes the KeyboardHandler by setting up key mappings.
     */
    public KeyboardHandler() {
        initKeys();
    }

    /**
     * Adds a key mapping to the internal map.
     *
     * @param info     KeyInfo object containing key details
     * @param keyCodes Array of key codes representing the key combination
     */
    private void addKey(KeyInfo info, int... keyCodes) {
        keys.put(convertArrayToString(keyCodes), info);
    }

    /**
     * Initializes key mappings for various key combinations.
     */
    public void initKeys() {
        // Adding key mappings for function keys, navigation keys, digits, and special keys
        addKey(new KeyInfo(KeyLabel.CTRL_C), 3);
        addKey(new KeyInfo(KeyLabel.ARROW_UP), 27, 91, 65);
        addKey(new KeyInfo(KeyLabel.ARROW_DOWN), 27, 91, 66);
        addKey(new KeyInfo(KeyLabel.ARROW_RIGHT), 27, 91, 67);
        addKey(new KeyInfo(KeyLabel.ARROW_LEFT), 27, 91, 68);
        addKey(new KeyInfo(KeyLabel.F1), 27, 79, 80);
        addKey(new KeyInfo(KeyLabel.F2), 27, 79, 81);
        addKey(new KeyInfo(KeyLabel.F3), 27, 79, 82);
        addKey(new KeyInfo(KeyLabel.F4), 27, 79, 83);
        addKey(new KeyInfo(KeyLabel.F5), 27, 91, 49, 53, 126);
        addKey(new KeyInfo(KeyLabel.F6), 27, 91, 49, 55, 126);
        addKey(new KeyInfo(KeyLabel.F7), 27, 91, 49, 56, 126);
        addKey(new KeyInfo(KeyLabel.F8), 27, 91, 49, 57, 126);
        addKey(new KeyInfo(KeyLabel.F9), 27, 91, 50, 48, 126);
        addKey(new KeyInfo(KeyLabel.F10), 27, 91, 50, 49, 126);
        addKey(new KeyInfo(KeyLabel.F11), 27, 91, 50, 51, 126);
        addKey(new KeyInfo(KeyLabel.F12), 27, 91, 50, 52, 126);
        addKey(new KeyInfo("0", KeyLabel.DIGIT_0), 48);
        addKey(new KeyInfo("1", KeyLabel.DIGIT_1), 49);
        addKey(new KeyInfo("2", KeyLabel.DIGIT_2), 50);
        addKey(new KeyInfo("3", KeyLabel.DIGIT_3), 51);
        addKey(new KeyInfo("4", KeyLabel.DIGIT_4), 52);
        addKey(new KeyInfo("5", KeyLabel.DIGIT_5), 53);
        addKey(new KeyInfo("6", KeyLabel.DIGIT_6), 54);
        addKey(new KeyInfo("7", KeyLabel.DIGIT_7), 55);
        addKey(new KeyInfo("8", KeyLabel.DIGIT_8), 56);
        addKey(new KeyInfo("9", KeyLabel.DIGIT_9), 57);
        addKey(new KeyInfo(",", KeyLabel.COMMA), 44);
        addKey(new KeyInfo(".", KeyLabel.PERIOD), 46);
        addKey(new KeyInfo(KeyLabel.ENTER), 13, 10);
        addKey(new KeyInfo(KeyLabel.ENTER_ALT), 13);
        addKey(new KeyInfo(KeyLabel.CTRL_C), 3);
        addKey(new KeyInfo(KeyLabel.ESC), 27);
        addKey(new KeyInfo(KeyLabel.ENTER), 13, 10);
        addKey(new KeyInfo(KeyLabel.DELETE), 127);
        addKey(new KeyInfo(KeyLabel.INTERNAL_WIN_RESIZE), 255, 255, 0, 255, 255);
        addKey(new KeyInfo(" ", KeyLabel.SPACE), 32);
    }

    /**
     * Converts an array of integers to a comma-separated string representation.
     *
     * @param arr Array of integers to convert
     * @return Comma-separated string representation of the array
     */
    private String convertArrayToString(int[] arr) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (int num : arr) {
            sb.append(num);
            if (i < arr.length - 1) {
                sb.append(",");
            }
            i++;
        }
        return sb.toString();
    }

    /**
     * Retrieves KeyInfo object associated with the provided key codes.
     *
     * @param keyCodes Array of key codes representing the key combination
     * @return KeyInfo object if found, null otherwise
     */
    public KeyInfo getKeyInfo(int[] keyCodes) {
        log.trace("Searching for key: {}", Arrays.toString(keyCodes));
        return keys.getOrDefault(convertArrayToString(keyCodes), null);
    }
}
