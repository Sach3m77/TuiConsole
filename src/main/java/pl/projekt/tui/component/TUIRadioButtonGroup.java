package pl.projekt.tui.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.List;

/**
 * TUIRadioButtonGroup class represents a group of radio buttons in a text-based user interface (TUI).
 * It manages multiple checkboxes, allowing only one to be selected at a time.
 */
public class TUIRadioButtonGroup {

    private Logger logger = LoggerFactory.getLogger(TUIRadioButtonGroup.class);

    private final List<TUICheckBox> checkBoxes;  // List of checkboxes in the group
    private final TUIManager tuiManager;  // Reference to the TUIManager for UI updates
    private TUICheckBox selectedCheckBox;  // Currently selected checkbox

    /**
     * Constructor to initialize the radio button group with a TUIManager instance.
     * @param tuiManager TUIManager instance managing the UI components.
     */
    public TUIRadioButtonGroup(TUIManager tuiManager) {
        this.checkBoxes = new ArrayList<>();
        this.tuiManager = tuiManager;
    }

    /**
     * Adds a checkbox to the radio button group.
     * @param checkBox TUICheckBox instance to be added.
     */
    public void addCheckBox(TUICheckBox checkBox) {
        checkBoxes.add(checkBox);
    }

    /**
     * Handles the selection of a checkbox within the group.
     * Only the selected checkbox is set to checked; others are set to unchecked.
     * @param selectedCheckBox TUICheckBox that has been selected.
     */
    public void handleCheckBoxSelection(TUICheckBox selectedCheckBox) {
        for (TUICheckBox checkBox : checkBoxes) {
            if (checkBox == selectedCheckBox) {
                checkBox.setChecked(true);
                logger.info("Checkbox selected: {}", checkBox.getLabel());
                this.selectedCheckBox = checkBox;
            } else {
                checkBox.setChecked(false);
            }
        }
        tuiManager.refresh();  // Refresh the UI to reflect the selection change
    }

    /**
     * Retrieves the currently selected checkbox in the radio button group.
     * @return TUICheckBox instance representing the selected checkbox.
     */
    public TUICheckBox getSelectedCheckBox() {
        return this.selectedCheckBox;
    }
}
