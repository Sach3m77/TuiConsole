package pl.projekt.tui.component;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.projekt.tui.model.color.Colors;


import java.util.ArrayList;
import java.util.List;

/**
 * Klasa implementująca karty w postaci TUI.
 */
public class TUITab implements TUIComponent {

    Logger logger = LoggerFactory.getLogger(TUITab.class);
    private String title;
    private String textColor = Colors.TEXT_BLACK.getCode();
    private String bgColor = Colors.BG_BRIGHT_WHITE.getCode();

    private String tabColor = "\033[33m";
    private List<TUIComponent> components = new ArrayList<>();
    private int x, y;
    private int w, h, zIndex, currentActiveComponent = -1;
    private final TUIManager TUIManager;
    boolean active;

    /**
     * Domyślny konstruktor.
     * @param title Tytuł karty, który będzie wyświetlany na liście kart.
     * @param x Pozycja x karty (tytułu).
     * @param y Pozycja y karty (tytułu)
     * @param windowWidth Szerokość okna.
     * @param windowHeight Wysokość okna.
     * @param zIndex z-index.
     * @param TUIManager Obiekt UIManager, z którym współpracować będzie karta.
     */
    public TUITab(String title, int x, int y, int windowWidth, int windowHeight, int zIndex, TUIManager TUIManager) {
        this.x = x;
        this.y = y;
        this.w = windowWidth;
        this.h = windowHeight;
        this.zIndex = zIndex;
        this.TUIManager = TUIManager;
        this.title = title;
        this.active = false;
    }

    @Override
    public void drawComponent(TUIManager TUIManager) {
        if (active) {
            for (int i = 0; i < w; ++i)
                for (int j = y + 1; j < h; ++j) {
                    TUIScreenCell emptyCell = new TUIScreenCell(' ', textColor, bgColor);
                    TUIManager.getScreen().addPixelToLayer(i, j, zIndex, emptyCell);
                }
        }
        TUIManager.getScreen().addPixelToLayer(x, y, zIndex, new TUIScreenCell(' ', textColor, tabColor));
        TUIManager.getScreen().addPixelToLayer(x + 1, y, zIndex, new TUIScreenCell(' ', textColor, tabColor));

        for (int i = 0; i < title.length(); ++i) {
            TUIManager.getScreen().addPixelToLayer(x + 2 + i, y, zIndex, new TUIScreenCell(title.charAt(i), textColor, tabColor));
        }
        TUIManager.getScreen().addPixelToLayer(x + title.length() + 2, y, zIndex, new TUIScreenCell(' ', textColor, tabColor));
        TUIManager.getScreen().addPixelToLayer(x + title.length() + 3, y, zIndex, new TUIScreenCell(' ', textColor, tabColor));
        if (active) {
            for (TUIComponent component : components)
                component.drawComponent(TUIManager);
        }
    }

    /**
     * Pozwala na dodanie komponentu do karty.
     * @param component Komponent. który ma zostać dodany.
     */
    public void addComponent(TUIComponent component) {
        logger.debug("Adding UI component " + component.getClass().getSimpleName());
        components.add(component);
    }

    /**
     * Pozwala na usunięcie komponentu z karty.
     * @param component Komponent, który ma zostać usunięty.
     */
    public void removeComponent(TUIComponent component) {
        logger.debug("Removing UI component " + component.getClass().getSimpleName());
        components.remove(component);
    }

    @Override
    public int getZIndex() {
        return zIndex;
    }

    @Override
    public boolean isInside(int mouseX, int mouseY) {
        return mouseX >= x && mouseX <= (x + w) && mouseY >= y && mouseY <= (y + h);
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
    public void hide() {
        if (TUIManager != null) {
            TUIManager.removeComponent(this);
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
        return h;
    }

    @Override
    public int getHeight() {
        return w;
    }

    @Override
    public void setActive(boolean active) {

        this.active = active;
        if (active) highlight();
        else resetHighlight();
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void highlight() {
        textColor = Colors.TEXT_RED.getCode();
        active = true;

        TUIManager.refresh();

    }
    @Override
    public void resetHighlight() {
        textColor = Colors.TEXT_WHITE.getCode();
        active = false;

        TUIManager.refresh();
    }

    @Override
    public boolean isInteractable() {
        return true;
    }


    private void highlightActiveComponent() {
        logger.trace("Highlighting active component.");
        for (TUIComponent component : components) {
            if (component.isActive()) {
                component.highlight();
            } else {
                component.resetHighlight();
            }
        }
    }

    private void moveToNextActiveComponent() {
        if (components.isEmpty()) {
            logger.trace("No components to activate.");
            return;
        }

        if (currentActiveComponent != -1) {
            components.get(currentActiveComponent).setActive(false);
            logger.trace("Deactivating current active component.");
        }
        int startComponent = currentActiveComponent == -1 ? (components.size() - 1) : currentActiveComponent;
        logger.trace("Starting from: " + startComponent);
        do {
            currentActiveComponent = (currentActiveComponent + 1) % components.size();
        } while (!components.get(currentActiveComponent).isInteractable() && currentActiveComponent != startComponent);

        components.get(currentActiveComponent).setActive(true);
    }

    private void moveToPrevActiveComponent() {
        if (components.isEmpty()) {
            logger.trace("No components to activate.");
            return;
        }

        if (currentActiveComponent != -1) {
            components.get(currentActiveComponent).setActive(false);
            logger.trace("Deactivating current active component.");
        }
        int startComponent = currentActiveComponent == -1 ? 0 : currentActiveComponent;
        do {
            currentActiveComponent = (currentActiveComponent - 1);
            if (currentActiveComponent < 0)
                currentActiveComponent = components.size() - 1;
        } while (!components.get(currentActiveComponent).isInteractable() && currentActiveComponent != startComponent);

        components.get(currentActiveComponent).setActive(true);
    }

    @Override
    public void windowResized(int width, int height){
        this.w = width;
        this.h = height;
        for(TUIComponent component : components)
            component.windowResized(width, height);
    }
}
