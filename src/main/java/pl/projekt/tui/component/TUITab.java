package pl.projekt.tui.component;

import lombok.extern.slf4j.Slf4j;
import pl.projekt.tui.model.color.Colors;
import pl.projekt.tui.model.keys.KeyInfo;


import java.util.ArrayList;
import java.util.List;

@Slf4j
public class TUITab implements TUIComponent {

    private final String title;
    private String textColor = Colors.TEXT_BLACK.getCode();
    private final String backgroundColor = Colors.BG_BRIGHT_WHITE.getCode();
    private final String tabColor = "\033[33m";
    private final List<TUIComponent> components = new ArrayList<>();
    private final int x, y;
    private int width, height, currentActiveComponent = -1;

    private final int layerIndex;
    private final TUIManager TUIManager;
    boolean isActive;

    public TUITab(String title, int x, int y, int windowWidth, int windowHeight, int layerIndex, TUIManager TUIManager) {
        this.x = x;
        this.y = y;
        this.width = windowWidth;
        this.height = windowHeight;
        this.layerIndex = layerIndex;
        this.TUIManager = TUIManager;
        this.title = title;
        this.isActive = false;
    }

    @Override
    public void drawComponent(TUIManager TUIManager) {
        if (isActive) {
            for (int i = 0; i < width; ++i)
                for (int j = y + 1; j < height; ++j) {
                    TUIScreenCell emptyCell = new TUIScreenCell(' ', textColor, backgroundColor);
                    TUIManager.getScreen().addPixelToLayer(i, j, layerIndex, emptyCell);
                }
        }
        TUIManager.getScreen().addPixelToLayer(x, y, layerIndex, new TUIScreenCell(' ', textColor, tabColor));
        TUIManager.getScreen().addPixelToLayer(x + 1, y, layerIndex, new TUIScreenCell(' ', textColor, tabColor));

        for (int i = 0; i < title.length(); ++i) {
            TUIManager.getScreen().addPixelToLayer(x + 2 + i, y, layerIndex, new TUIScreenCell(title.charAt(i), textColor, tabColor));
        }
        TUIManager.getScreen().addPixelToLayer(x + title.length() + 2, y, layerIndex, new TUIScreenCell(' ', textColor, tabColor));
        TUIManager.getScreen().addPixelToLayer(x + title.length() + 3, y, layerIndex, new TUIScreenCell(' ', textColor, tabColor));
        if (isActive) {
            for (TUIComponent component : components)
                component.drawComponent(TUIManager);
        }
    }

    public void addComponent(TUIComponent component) {
        log.debug("Adding UI component " + component.getClass().getSimpleName());
        components.add(component);
    }

    @Override
    public int getLayerIndex() {
        return layerIndex;
    }

    @Override
    public void performAction() {

    }

    @Override
    public void show() {
        if (TUIManager != null) {
            TUIManager.addComponent(this);
        }
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public void setActive(boolean active) {

        this.isActive = active;
        if (active) highlightComponent();
        else resetHighlightComponent();
    }

    @Override
    public boolean isComponentActive() {
        return isActive;
    }

    @Override
    public void highlightComponent() {
        textColor = Colors.TEXT_RED.getCode();
        isActive = true;

        TUIManager.refresh();
    }
    @Override
    public void resetHighlightComponent() {
        textColor = Colors.TEXT_BLACK.getCode();
        isActive = false;

        TUIManager.refresh();
    }

    @Override
    public boolean isInteractable() {
        return true;
    }

    @Override
    public void windowResized(int width, int height){
        this.width = width;
        this.height = height;
        for(TUIComponent component : components)
            component.windowResized(width, height);
    }

    private void highlightActiveComponent() {
        log.info("Highlighting active component.");
        for (TUIComponent component : components) {
            if (component.isComponentActive()) {
                component.highlightComponent();
            } else {
                component.resetHighlightComponent();
            }
        }
    }

    private void moveToNextActiveComponent() {
        if (components.isEmpty()) {
            log.info("No components to activate.");
            return;
        }

        if (currentActiveComponent != -1) {
            components.get(currentActiveComponent).setActive(false);
            log.info("Deactivating current active component.");
        }
        int startComponent = currentActiveComponent == -1 ? (components.size() - 1) : currentActiveComponent;
        log.info("Starting from: " + startComponent);
        do {
            currentActiveComponent = (currentActiveComponent + 1) % components.size();
        } while (!components.get(currentActiveComponent).isInteractable() && currentActiveComponent != startComponent);

        components.get(currentActiveComponent).setActive(true);
    }

    private void moveToPrevActiveComponent() {
        if (components.isEmpty()) {
            log.info("No components to activate.");
            return;
        }

        if (currentActiveComponent != -1) {
            components.get(currentActiveComponent).setActive(false);
            log.info("Deactivating current active component.");
        }
        int startComponent = currentActiveComponent == -1 ? 0 : currentActiveComponent;
        do {
            currentActiveComponent = (currentActiveComponent - 1);
            if (currentActiveComponent < 0)
                currentActiveComponent = components.size() - 1;
        } while (!components.get(currentActiveComponent).isInteractable() && currentActiveComponent != startComponent);

        components.get(currentActiveComponent).setActive(true);
    }

    public void handleKeyboardInput(KeyInfo keyInfo) {

            switch (keyInfo.getLabel()) {
                case ARROW_DOWN:
                    moveToNextActiveComponent();
                    break;
                case ARROW_UP:
                    moveToPrevActiveComponent();
                    break;
                case ENTER, ENTER_ALT:
                    if (currentActiveComponent != -1)
                        components.get(currentActiveComponent).performAction();
                    break;
                default:
                    if (isActive && currentActiveComponent != -1) {
                        log.info("Is adding a value to field");
                        if (components.get(currentActiveComponent) instanceof TUITextField activeField)
                            activeField.addText(keyInfo);
                    }
                    break;

        }

        highlightActiveComponent();
    }

}
