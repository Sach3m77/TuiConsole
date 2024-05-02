package pl.projekt.tui.model.keys;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;



/**
 * Klasa mapująca <i>KeyInfo</i> na sekwencje bajtów.
 */
public class KeyboardHandler {

    // Logger for debugging and trace information
    private static final Logger logger = LoggerFactory.getLogger(KeyboardHandler.class);

    // Map to hold key codes and corresponding KeyInfo
    private final Map<String, KeyInfo> keys = new HashMap<>();

    /**
     * Domyślny konstruktor. Wywołuje metodę initKeys();
     */
    public KeyboardHandler() {
        initKeys();
    }

    /**
     * Metoda inicjująca mapę klawiszy.
     */
    public void initKeys() {
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
        addKey(new KeyInfo(KeyLabel.ENTER), 13, 10);
        addKey(new KeyInfo(KeyLabel.ENTER_ALT), 13);
        addKey(new KeyInfo(KeyLabel.CTRL_C), 3);
        addKey(new KeyInfo(KeyLabel.ESC), 27);
        addKey(new KeyInfo(KeyLabel.ENTER), 13, 10);
        addKey(new KeyInfo(KeyLabel.INTERNAL_WIN_RESIZE), 255,255,0,255,255);

    }

    /**
     * Convert array of key codes to a string key.
     *
     * @param arr Array of key codes
     * @return String representation
     */
    private String arrayToKey(int[] arr) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            sb.append(arr[i]);
            if (i < arr.length - 1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }

    /**
     * Dodaje nowy klawisz i informacje o nim do mapy.
     * @param info Obiekt KeyInfo.
     * @param keyCodes Sekwencje bajtów reprezentujące dany klawisz.
     */
    private void addKey(KeyInfo info, int... keyCodes) {
        keys.put(arrayToKey(keyCodes), info);
    }

    /**
     * Pobiera obiekt <i>KeyInfo</i> dla danej sekwencji bajtów.
     *
     * @param keyCodes Tablica z sekwencją kodów dla klawisza.
     * @return Obiekt <i>KeyInfo</i> z odpowiadającym klawiszem lub <i style="color:orange;">null</i> jeśli nie znaleziono.
     */
    public KeyInfo getKeyInfo(int[] keyCodes) {
        logger.trace("Searching for key: " + Arrays.toString(keyCodes));
        return keys.getOrDefault(arrayToKey(keyCodes), null);
    }
}