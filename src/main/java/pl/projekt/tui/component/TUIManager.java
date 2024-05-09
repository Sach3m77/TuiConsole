package pl.projekt.tui.component;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import pl.projekt.tui.model.keys.KeyInfo;

import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

@Slf4j
public class TUIManager {

    private final TreeMap<Integer, List<TUIComponent>> layers = new TreeMap<>();
    @Getter
    private final TUIScreen screen;
    private final List<TUITab> tabs = new ArrayList<>();
    private final OutputStream out;
    @Getter
    private int currentTab = 0;
    private boolean shouldRefresh;

    public TUIManager(TUIScreen screen, OutputStream out) {
        log.trace("Creating TUIManager with screen and out");
        this.screen = screen;
        this.out = out;
        this.shouldRefresh = true;
    }

    public void initialize(){
        this.shouldRefresh = true;
        render();
    }

    public void addComponent(TUIComponent component) {
        log.trace("Adding UI component to screen: " + component.getClass().getSimpleName());
        layers.computeIfAbsent(component.getLayerIndex(), k -> new ArrayList<>()).add(component);
    }

    public void addTab(TUITab tab) {
        tab.show();
        if(tabs.isEmpty())
            tab.setActive(true);
        tabs.add(tab);
    }

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

    public void refresh() {
        this.shouldRefresh = true;
    }

    public void handleKeyboardInput(KeyInfo keyInfo) {
        log.info("Handling keyboard input: " + keyInfo);

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
                default:
                    log.info("Default "+ currentTab);
                    tabs.get(currentTab).handleKeyboardInput(keyInfo);
                    break;
            }
        }

        if (shouldRefresh)
            this.render();
    }

    private void switchToTab(int tabIndex) {
        if (tabIndex >= 0 && tabIndex < tabs.size()) {
            tabs.get(currentTab).setActive(false);
            screen.clearLayers();
            currentTab = tabIndex;
            tabs.get(currentTab).setActive(true);
            log.info("Switched to tab: " + tabIndex);
        }
    }
}
