package pl.projekt.tui.component;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import pl.projekt.tui.model.keys.KeyInfo;

import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

/**
 * Manages the overall Text User Interface (TUI) components and rendering.
 */
@Slf4j
public class TUIManager {

    private final TreeMap<Integer, List<TUIComponent>> layers = new TreeMap<>();  // Layers of UI components sorted by Z-index
    @Getter
    private final TUIScreen screen;        // Screen for displaying TUI components
    private final List<TUITab> tabs = new ArrayList<>();  // List of tabs in the TUI
    private final OutputStream out;        // Output stream for rendering TUI
    @Getter
    private int currentTab = 0;            // Index of the current active tab
    private boolean shouldRefresh;         // Flag indicating if screen refresh is needed

    /**
     * Constructs a new TUIManager with the specified screen and output stream.
     *
     * @param screen The TUIScreen instance for rendering components
     * @param out The OutputStream for TUI rendering
     */
    public TUIManager(TUIScreen screen, OutputStream out) {
        log.trace("Creating TUIManager with screen and out");
        this.screen = screen;
        this.out = out;
        this.shouldRefresh = true;
    }

    /**
     * Initializes the TUIManager, setting up for rendering.
     */
    public void initialize(){
        this.shouldRefresh = true;
        render();
    }

    /**
     * Adds a TUI component to the manager.
     *
     * @param component The TUIComponent to add
     */
    public void addComponent(TUIComponent component) {
        log.trace("Adding UI component to screen: {}", component.getClass().getSimpleName());
        layers.computeIfAbsent(component.getZIndex(), k -> new ArrayList<>()).add(component);
    }

    /**
     * Adds a tab to the TUIManager and shows it.
     *
     * @param tab The TUITab to add
     */
    public void addTab(TUITab tab) {
        tab.show();
        if(tabs.isEmpty())
            tab.setActive(true);
        tabs.add(tab);
    }

    /**
     * Renders all UI components on the screen.
     */
    public void render() {
        if(shouldRefresh) {
            log.trace("Rendering UI components.");
            for (List<TUIComponent> layer : layers.values()) {
                for (TUIComponent component : layer) {
                    component.drawComponent(this);
                }
            }
            log.trace("Refreshing screen.");
            if (out != null) {
                try {
                    screen.refresh(this.out);
                } catch (IOException e) {
                    log.error("Error occurred while refreshing screen: {}", e.getMessage());
                }
            } else {
                log.warn("OutputStream is null, skipping refresh.");
            }
            shouldRefresh = false;
        }
    }

    /**
     * Resizes the UI components and refreshes the screen accordingly.
     *
     * @param width New width of the screen
     * @param height New height of the screen
     */
    public void resizeUI(int width, int height){
        screen.resize(width, height);
        for(TUITab tab : tabs)
            tab.windowResized(width, height);
        if(out != null) {
            shouldRefresh = true;
            render();
            log.info("Screen resized");
        } else
            log.warn("OutputStream is null, not refreshing.");
    }

    /**
     * Forces a refresh of the screen.
     */
    public void refresh() {
        this.shouldRefresh = true;
    }

    /**
     * Handles keyboard input events for navigation and interaction.
     *
     * @param keyInfo The KeyInfo object containing keyboard input details
     */
    public void handleKeyboardInput(KeyInfo keyInfo) {
        log.info("Handling keyboard input: {}", keyInfo);

        if (!tabs.isEmpty()) {
            switch (keyInfo.getLabel()) {
                case F1:
                    switchToTab(0);
                    break;
                case F2:
                    switchToTab(1);
                    break;
                case F3:
                    switchToTab(2);
                    break;
                case F4:
                    switchToTab(3);
                    break;
                case F5:
                    switchToTab(4);
                    break;
                case F6:
                    switchToTab(5);
                    break;
                case F7:
                    switchToTab(6);
                    break;
//                case ESC:
//                    switchToTab(6);
//                    break;
                default:
                    log.info("Default {}", currentTab);
                    tabs.get(currentTab).handleKeyboardInput(keyInfo);
                    break;
            }
        }

        if (shouldRefresh)
            this.render();
    }

    /**
     * Switches to the specified tab index.
     *
     * @param tabIndex The index of the tab to switch to
     */
    private void switchToTab(int tabIndex) {
        if (tabIndex >= 0 && tabIndex < tabs.size()) {
            tabs.get(currentTab).setActive(false);
            screen.clearLayers();
            currentTab = tabIndex;
            tabs.get(currentTab).setActive(true);
            log.info("Switched to tab: {}", tabIndex);
        }
    }

    /**
     * Removes a TUI component from the manager.
     *
     * @param component The TUIComponent to remove
     */
    public void removeComponent(TUIComponent component) {

        int zIndex = component.getZIndex();
        if (layers.containsKey(zIndex)) {
            layers.get(zIndex).remove(component);
        }
        // Clear component from screen
        TUIScreen screen = getScreen();
        for (int i = 0; i < component.getWidth(); i++) {
            for (int j = 0; j < component.getHeight(); j++) {
                screen.clearScreen();
            }
        }
    }

    /**
     * Retrieves the current active tab index.
     *
     * @return The index of the current active tab
     */
    public int returnCurrentTab (){
        return this.currentTab;
    }

    /**
     * Retrieves the list of tabs managed by the TUIManager.
     *
     * @return The list of TUITab objects
     */
    public List<TUITab> getTabs() {
        return tabs;
    }
}
