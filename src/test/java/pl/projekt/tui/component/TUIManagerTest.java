package pl.projekt.tui.component;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.projekt.tui.model.keys.KeyInfo;
import pl.projekt.tui.model.keys.KeyLabel;

import java.io.IOException;
import java.io.OutputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TUIManagerTest {

    private TUIManager tuiManager;
    private TUIScreen mockScreen;
    private OutputStream mockOutputStream;

    @BeforeEach
    void setUp() {
        mockScreen = mock(TUIScreen.class);
        mockOutputStream = mock(OutputStream.class);
        tuiManager = new TUIManager(mockScreen, mockOutputStream);
    }

    @Test
    void render_shouldRenderComponentsAndRefreshScreen() throws IOException {
        TUIComponent mockComponent1 = mock(TUIComponent.class);
        TUIComponent mockComponent2 = mock(TUIComponent.class);
        tuiManager.addComponent(mockComponent1);
        tuiManager.addComponent(mockComponent2);

        tuiManager.render();

        // Verify that components are drawn and screen is refreshed
        verify(mockComponent1, times(1)).drawComponent(tuiManager);
        verify(mockComponent2, times(1)).drawComponent(tuiManager);
        verify(mockScreen, times(1)).refresh(mockOutputStream);
    }

    @Test
    void resizeUI_shouldResizeScreenAndComponents() throws IOException {
        TUITab mockTab = mock(TUITab.class);
        tuiManager.addTab(mockTab);

        int width = 100;
        int height = 50;
        tuiManager.resizeUI(width, height);

        // Verify that screen and tab resize methods are called
        verify(mockScreen, times(1)).resize(width, height);
        verify(mockTab, times(1)).windowResized(width, height);
        verify(mockScreen, times(1)).refresh(mockOutputStream);
    }

    @Test
    void returnCurrentTab_shouldReturnCorrectTabIndex() {
        TUITab mockTab1 = mock(TUITab.class);
        TUITab mockTab2 = mock(TUITab.class);
        tuiManager.addTab(mockTab1);
        tuiManager.addTab(mockTab2);

        int currentTab = tuiManager.returnCurrentTab();

        assertEquals(0, currentTab); // Initially, the first tab should be active
    }
    @Test
    void addTab_shouldIncreaseTabCount() {
        tuiManager.addTab(mock(TUITab.class));
        assertEquals(1, tuiManager.getTabs().size());
        tuiManager.removeComponent(mock(TUITab.class));

    }


    @Test
    void removeTab_shouldDecreaseTabCount() {
        TUITab tab = mock(TUITab.class);
        tuiManager.addTab(tab);
        tuiManager.removeComponent(tab);
        assertEquals(1, tuiManager.getTabs().size());
    }


    @Test
    void resizeUI_shouldChangeScreenSize() {
        tuiManager.resizeUI(100, 50);
        verify(mockScreen, times(1)).resize(100, 50);
    }
}
