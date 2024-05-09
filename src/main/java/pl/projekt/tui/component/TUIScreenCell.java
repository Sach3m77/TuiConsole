package pl.projekt.tui.component;


import lombok.Data;
import lombok.NonNull;

@Data
public class TUIScreenCell {

    private char character;

    @NonNull
    private String textColor;

    @NonNull
    private String backgroundColor;

    public TUIScreenCell(char character, @NonNull String textColor, @NonNull String backgroundColor) {
        this.character = character;
        this.textColor = textColor;
        this.backgroundColor = backgroundColor;
    }

}

