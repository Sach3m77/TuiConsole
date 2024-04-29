package pl.projekt.tui.component;


import lombok.Data;
import lombok.NonNull;

/**
 * Class responsible for a single "pixel" - a screen cell.
 */
@Data
public class TUIScreenCell {
    /**
     * Character inside the cell.
     */
    private char character;

    /**
     * Text color.
     */
    @NonNull
    private String textColor;

    /**
     * Background color in the cell.
     */
    @NonNull
    private String bgColor;

    /**
     * Default constructor. Contains the character, text color, and background color.
     *
     * @param character The character to be displayed.
     * @param textColor Text color. Cannot be <i style="color: orange;">null</i>.
     * @param bgColor Background color in the cell. Cannot be <i style="color: orange;">null</i>.
     * @throws IllegalArgumentException If <i>textColor</i> or <i>bgColor</i> is null.
     */
    public TUIScreenCell(char character, @NonNull String textColor, @NonNull String bgColor) {
        this.character = character;
        this.textColor = textColor;
        this.bgColor = bgColor;
    }
}

