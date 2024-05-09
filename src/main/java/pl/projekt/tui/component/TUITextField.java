package pl.projekt.tui.component;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import pl.projekt.tui.model.color.Colors;
import pl.projekt.tui.model.keys.KeyInfo;
import pl.projekt.tui.model.keys.KeyLabel;

@Slf4j
public class TUITextField implements TUIComponent {

    private final StringBuilder textContent;
    private final int x, y, width, height;
    private String backgroundColor = Colors.BG_RED.getCode();
    @Setter
    private String textColor = Colors.TEXT_WHITE.getCode();
    private boolean isNumeric;
    private final int layerIndex;
    private boolean isActive;
    private final TUIManager tuiManager;
    private final int maxChars = Integer.MAX_VALUE;

    public TUITextField(int x, int y, int width, int height, int layerIndex, TUIManager tuiManager) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.layerIndex = layerIndex;
        this.tuiManager = tuiManager;
        this.textContent = new StringBuilder();

    }

    public void setNumeric(boolean numeric) {
        isNumeric = numeric;
    }

    public double getParsedNumber(){
        try {
            log.info("Getting a number:" + textContent);
            return Double.parseDouble(textContent.toString());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    @Override
    public void drawComponent(TUIManager tuiManager) {
        String text = textContent.toString();
        String displayText = text.length() > width ? text.substring(text.length() - width) : text;

        for (int i = 0; i < width; i++) {
            TUIScreenCell emptyCell = new TUIScreenCell(' ', textColor, backgroundColor);
            tuiManager.getScreen().addPixelToLayer(x + i, y, layerIndex, emptyCell);
        }

        for (int i = 0; i < displayText.length(); i++) {
            TUIScreenCell cell = new TUIScreenCell(displayText.charAt(i), textColor, backgroundColor);
            tuiManager.getScreen().addPixelToLayer(x + i, y, layerIndex, cell);
        }
    }

    public void addText(KeyInfo keyInfo) {
        log.info("Appending text: {}", keyInfo.getValue());

        String newCharacter = keyInfo.getValue();

        if(keyInfo.getLabel() != KeyLabel.DELETE) {

            if (isNumeric && !newCharacter.matches("\\d|\\.")) return;

            if (isNumeric && textContent.indexOf(".") != -1 && keyInfo.getValue().equals(".")) return;
        }

        if (textContent.length() < maxChars || keyInfo.getLabel() == KeyLabel.DELETE) {
            if (keyInfo.getLabel() == KeyLabel.DELETE) {
                if (!textContent.isEmpty()) {
                    textContent.deleteCharAt(textContent.length() - 1);

                    tuiManager.refresh();
                }
                return;
            }

            if (keyInfo.getLabel() == KeyLabel.ENTER) {
                performAction();
                return;
            }

            textContent.append(keyInfo.getValue());

            tuiManager.refresh();
        }
    }

    @Override
    public void performAction() {
        log.debug("Performing action");
        log.debug("Text content: {}", textContent);
    }

    @Override
    public void show() {
        if (tuiManager != null) {
            tuiManager.addComponent(this);
        }
    }

    @Override
    public int getX() {return x;}

    @Override
    public int getY() {return y;}

    @Override
    public int getWidth() {return width;}

    @Override
    public int getHeight() {return height;}

    @Override
    public void setActive(boolean active) {this.isActive = active;}

    @Override
    public boolean isComponentActive() {return this.isActive;}

    @Override
    public void highlightComponent() {

        setBgColor(Colors.BG_YELLOW.getCode());
        setTextColor(Colors.TEXT_BLACK.getCode());

        tuiManager.refresh();
    }

    @Override
    public void resetHighlightComponent() {
        setBgColor(Colors.BG_RED.getCode());
        setTextColor(Colors.TEXT_WHITE.getCode());

        tuiManager.refresh();
    }

    @Override
    public boolean isInteractable() {return true;}

    @Override
    public int getLayerIndex() {return layerIndex;}

    public void setBgColor(String bgColor) {this.backgroundColor = bgColor;}

    @Override
    public void windowResized(int width, int height) {}
}
