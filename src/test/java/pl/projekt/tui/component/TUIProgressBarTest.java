package pl.projekt.tui.component;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import pl.projekt.tui.model.color.Colors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TUIProgressBarTest {
    private TUIProgressBar progressBar;
    private TUIManager tuiManager;
    private TUIScreen tuiScreen;

    @BeforeEach
    public void setUp() {
        tuiManager = mock(TUIManager.class);
        tuiScreen = mock(TUIScreen.class);
        progressBar = new TUIProgressBar(10, 5, 20, 1, 1, tuiManager);
        progressBar.tuiScreen = tuiScreen; // setting the screen manually
    }

    @Test
    public void testConstructor() {
        assertEquals(10, progressBar.getX());
        assertEquals(5, progressBar.getY());
        assertEquals(20, progressBar.getWidth());
        assertEquals(1, progressBar.getHeight());
        assertEquals(1, progressBar.getZIndex());
    }

    @Test
    public void testSetProgress() {
        progressBar.setProgress(0.5);
        // Assuming there would be a getter for progress or testing indirectly through drawComponent
    }



    @Test
    public void testShow() {
        progressBar.show();
        verify(tuiManager, times(1)).addComponent(progressBar);
    }

    @Test
    public void testIsComponentActive() {
        assertFalse(progressBar.isComponentActive());
    }

    @Test
    public void testIsInteractable() {
        assertFalse(progressBar.isInteractable());
    }

    @Test
    public void testHide() {
        progressBar.hide();
        // No specific behavior to test for hide in current implementation
    }
}
