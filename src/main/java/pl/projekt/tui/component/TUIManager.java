package pl.projekt.tui.component;

import lombok.Getter;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.projekt.tui.model.keys.KeyInfo;


import java.awt.event.KeyEvent;
import java.io.OutputStream;
import java.util.*;


/**
 * Class responsible for managing cards and components.
 * Allows you to render your screen and pass it over the network.
 */
public class TUIManager {

    private final Logger logger = LoggerFactory.getLogger(TUIManager.class);

    private final TreeMap<Integer, List<TUIComponent>> layers = new TreeMap<>();

    /**
     * The <i>TUIScreen</i> screen where components are rendered.
     */
    @Getter
    private TUIScreen screen;

    private final List<TUITab> tabs = new ArrayList<>();

    private final OutputStream out;

    private int currentTab = 0;

    private boolean shouldRefresh;

    private final Map<Integer, String> functionKeysMap = new HashMap<>();


    /**
     * Default class constructor.
     * @param screen The screen on which the components are displayed.
     * @param out The output stream to which the screen will be sent after rendering.
     */
    public TUIManager(TUIScreen screen, OutputStream out) {
        logger.trace("Constructor UIManager with screen and out");
        this.screen = screen;
        this.out = out;
        this.shouldRefresh = true;
    }


    /**
     * Orders a screen refresh and performs the first render.
     */
    public void initialize(){
        this.shouldRefresh = true;
        render();
    }

    /**
     * Allows you to add a component to the screen.
     * @param component The component to add.
     */
    public void addComponent(TUIComponent component) {
        logger.trace("Adding UI component to screen: " + component.getClass().getSimpleName());
        layers.computeIfAbsent(component.getZIndex(), k -> new ArrayList<>()).add(component);
    }

    /**
     * Adds a new tab.
     * @param tab The tab to add.
     */
    public void addTab(TUITab tab) {
        tab.show();
        if(tabs.isEmpty())
            tab.setActive(true);
        tabs.add(tab);
    }


    /**
     * Deletes a card.
     * @param tab The tab to remove.
     */
    public void removeTab(TUITab tab) {
        tab.hide();
        tabs.remove(tab);
    }

    /**
     * Updates the screen if <i>shouldRefresh</i> is set to <i style="color:orange;">true</i>.
     */
    @SneakyThrows
    private void render() {
        if(shouldRefresh) {
            logger.trace("Rendering UI components.");
            for (List<TUIComponent> layer : layers.values()) {
                for (TUIComponent component : layer) {
                    component.drawComponent(this);
                }
            }
            logger.trace("Refreshing screen.");
            if (out != null) {
                screen.refresh(this.out);
            } else {
                logger.warn("OutputStream is null, skipping refresh.");
            }
            shouldRefresh = false;
        }
    }


    /**
     * Removes a component from the screen.
     * @param component The component to remove.
     */
    public void removeComponent(TUIComponent component) {
        logger.trace("Removing UI component: " + component.getClass().getSimpleName());
        int zIndex = component.getZIndex();
        if (layers.containsKey(zIndex)) {
            layers.get(zIndex).remove(component);
        }
        // Delete component from screen
        TUIScreen screen = getScreen();
        for (int i = 0; i < component.getWidth(); i++) {
            for (int j = 0; j < component.getHeight(); j++) {
                screen.clearCellAt(component.getX() + i, component.getY() + j, zIndex);
            }
        }
    }


    public void setCurrentActiveComponent(int index) {
        // logger.trace("Setting current active component to: " + index);
        // this.currentActiveComponent = index;
    }


    /**
     * Supports changing window dimensions (remote).
     * @param width The new width of the window.
     * @param height The new height of the window.
     */
    @SneakyThrows
    public void resizeUI(int width, int height){
        screen.resize(width, height);
        for(TUITab tab : tabs)
            tab.windowResized(width, height);
        if(out != null) {
            shouldRefresh = true;
            render();
            logger.info("Screen resized");
        } else
            logger.warn("OutputStream is null, not refreshing.");
    }


    /**
     * Sets the <i>shouldRefresh</i> flag to <i style="color:orange;">true</i>.
     */
    @SneakyThrows
    public void refresh() {
        this.shouldRefresh = true;
    }


        /**
     * Obsługuje klawiaturę.
     * @param keyInfo Informacja o wciśniętym klawiszu.
     */
        public void handleKeyboardInput(KeyInfo keyInfo, List<TUITab> tabs) {
            logger.trace("Handling keyboard input: " + keyInfo);

            if (!tabs.isEmpty()) {
                switch (keyInfo.getLabel()) {
                    case F1:
                        switchToTab(tabs, 0);
                        break;
                    case F2:
                        switchToTab(tabs, 1);
                        break;
                    case F3:
                        switchToTab(tabs, 2);
                        break;
                    case F4:
                        switchToTab(tabs, 3);
                        break;
                    case F5:
                        switchToTab(tabs, 4);
                        break;
                    case F6:
                        switchToTab(tabs, 5);
                        break;
                    case F7:
                        switchToTab(tabs, 6);
                        break;
                    default:
                        // Handle other keys if needed
                        break;
                }
            }

            if (shouldRefresh)
                this.render();
        }

    private void switchToTab(List<TUITab> tabs, int tabIndex) {
        if (tabIndex >= 0 && tabIndex < tabs.size()) {
            tabs.get(currentTab).setActive(false);
            screen.clearLayers();
            currentTab = tabIndex;
            tabs.get(currentTab).setActive(true);
        }
    }


}
