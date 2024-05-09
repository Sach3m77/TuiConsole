package pl.projekt.tui.model.keys;

import lombok.NonNull;
import lombok.Value;

@Value
public class KeyInfo {
    String value;
    boolean isFunctional;
    KeyLabel label;

    public KeyInfo(KeyLabel label) {
        this.value = "";
        this.isFunctional = true;
        this.label = label;
    }

    public KeyInfo(@NonNull String value, @NonNull KeyLabel label) {
        this.value = value;
        this.isFunctional = false;
        this.label = label;
    }

}
