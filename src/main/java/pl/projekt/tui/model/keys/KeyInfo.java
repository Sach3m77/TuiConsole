package pl.projekt.tui.model.keys;

import lombok.NonNull;
import lombok.Value;

/**
 * Represents information about a key, including its value, functionality, and label.
 */
@Value
public class KeyInfo {
    String value;          // The actual value associated with the key (e.g., character or empty string for functional keys)
    boolean isFunctional;  // Indicates if the key is functional (true) or represents a character (false)
    KeyLabel label;        // Enum representing the type or label of the key

    /**
     * Constructor for functional keys.
     *
     * @param label KeyLabel enum representing the type of functional key
     */
    public KeyInfo(KeyLabel label) {
        this.value = "";        // Functional keys typically have an empty value
        this.isFunctional = true;
        this.label = label;
    }

    /**
     * Constructor for keys representing characters or specific values.
     *
     * @param value Actual value associated with the key (e.g., a character)
     * @param label KeyLabel enum representing the type of key
     * @throws NullPointerException if value or label is null
     */
    public KeyInfo(@NonNull String value, @NonNull KeyLabel label) {
        this.value = value;      // Assigns the provided value to the key
        this.isFunctional = false;
        this.label = label;
    }

}
