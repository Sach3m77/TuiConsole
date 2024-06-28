package pl.projekt.tui.component;

import lombok.Data;
import lombok.NonNull;

/**
 * TUIScreenCell class represents a single cell on a text-based screen in a TUI.
 * It contains a character with specified text and background colors.
 */
@Data
public class TUIScreenCell {

    private char character;  // Character to display in the cell

    @NonNull
    private String textColor;  // Text color of the character

    @NonNull
    private String backgroundColor;  // Background color of the cell

    /**
     * Constructor to initialize a TUIScreenCell with specified character, text color, and background color.
     * @param character Character to display in the cell.
     * @param textColor Text color of the character.
     * @param backgroundColor Background color of the cell.
     */
    public TUIScreenCell(char character, @NonNull String textColor, @NonNull String backgroundColor) {
        this.character = character;
        this.textColor = textColor;
        this.backgroundColor = backgroundColor;
    }

}
