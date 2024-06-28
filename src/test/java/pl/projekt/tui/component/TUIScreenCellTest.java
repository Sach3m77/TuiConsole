package pl.projekt.tui.component;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TUIScreenCellTest {

    @Test
    public void testConstructorAndGetters() {
        TUIScreenCell cell = new TUIScreenCell('A', "white", "black");
        assertEquals('A', cell.getCharacter());
        assertEquals("white", cell.getTextColor());
        assertEquals("black", cell.getBackgroundColor());
    }

    @Test
    public void testNonNullTextColor() {
        assertThrows(NullPointerException.class, () -> {
            new TUIScreenCell('A', null, "black");
        });
    }

    @Test
    public void testNonNullBackgroundColor() {
        assertThrows(NullPointerException.class, () -> {
            new TUIScreenCell('A', "white", null);
        });
    }

    @Test
    public void testToString() {
        TUIScreenCell cell = new TUIScreenCell('A', "white", "black");
        String expectedToString = "TUIScreenCell(character=A, textColor=white, backgroundColor=black)";
        assertEquals(expectedToString, cell.toString());
    }


    @Test
    public void testEqualsAndHashCode() {
        TUIScreenCell cell1 = new TUIScreenCell('A', "white", "black");
        TUIScreenCell cell2 = new TUIScreenCell('A', "white", "black");
        TUIScreenCell cell3 = new TUIScreenCell('B', "white", "black");

        assertTrue(cell1.equals(cell2) && cell2.equals(cell1));
        assertEquals(cell1.hashCode(), cell2.hashCode());

        assertFalse(cell1.equals(cell3));
    }


    @Test
    public void testDifferentCharacters() {
        TUIScreenCell cell1 = new TUIScreenCell('A', "white", "black");
        TUIScreenCell cell2 = new TUIScreenCell('B', "white", "black");

        assertNotEquals(cell1, cell2);
    }

    @Test
    public void testDifferentTextColors() {
        TUIScreenCell cell1 = new TUIScreenCell('A', "white", "black");
        TUIScreenCell cell2 = new TUIScreenCell('A', "red", "black");

        assertNotEquals(cell1, cell2);
    }

    @Test
    public void testDifferentBackgroundColors() {
        TUIScreenCell cell1 = new TUIScreenCell('A', "white", "black");
        TUIScreenCell cell2 = new TUIScreenCell('A', "white", "blue");

        assertNotEquals(cell1, cell2);
    }

    @Test
    public void testConstructorWithDefaultValues() {
        TUIScreenCell cell = new TUIScreenCell(' ', "white", "black");

        assertEquals(' ', cell.getCharacter());
        assertEquals("white", cell.getTextColor());
        assertEquals("black", cell.getBackgroundColor());
    }
}
