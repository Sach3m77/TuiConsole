package pl.projekt.tui.component;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class TUIBorderTest {

    private TUIBorder tuiBorder;
    private TUIManager tuiManager;

    @BeforeEach
    public void setUp() {
        tuiManager = Mockito.mock(TUIManager.class);
        tuiBorder = new TUIBorder(10, 10, 20, 5, 0, tuiManager);
    }

    @Test
    public void testConstructor() {
        assertEquals(10, tuiBorder.getX());
        assertEquals(10, tuiBorder.getY());
        assertEquals(20, tuiBorder.getWidth());
        assertEquals(5, tuiBorder.getHeight());
        assertEquals(0, tuiBorder.getZIndex());
    }

    @Test
    public void testSetBgColor() {
        tuiBorder.setBgColor("#FFFFFF");
        assertEquals("#FFFFFF", tuiBorder.getBgColor());
    }



    @Test
    public void testShow() {
        tuiBorder.show();
        Mockito.verify(tuiManager).addComponent(tuiBorder);
    }

    @Test
    public void testHide() {
        tuiBorder.hide();
        Mockito.verify(tuiManager).removeComponent(tuiBorder);
    }

}
