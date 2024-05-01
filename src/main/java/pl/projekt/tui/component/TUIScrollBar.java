package pl.projekt.tui.component;

import pl.projekt.tui.model.color.Colors;


public class TUIScrollBar implements TUIComponent {
    private TUITabela tabela;

    public TUIScrollBar(TUITabela tabela) {
        this.tabela = tabela;
    }

    @Override
    public void drawComponent(TUIManager TUIManager) {
        int scrollBarHeight = (tabela.getVisibleRows() * 3); // Wysokość suwaka zależna od liczby widocznych wierszy w tabeli
        int scrollBarWidth = 1; // Szerokość suwaka (możesz dostosować)

        int scrollBarX = tabela.getX() + tabela.getWidth() + 1; // Prawa krawędź tabeli + odstęp
        int scrollBarY = tabela.getY(); // Górna krawędź tabeli

        // Rysuj suwak jako prostokątny obszar
        for (int y = 0; y < scrollBarHeight; y++) {
            for (int x = 0; x < scrollBarWidth; x++) {
                TUIManager.getScreen().addPixelToLayer(scrollBarX + x, scrollBarY + y, 0, new TUIScreenCell('|', Colors.TEXT_WHITE.getCode(), Colors.BG_BRIGHT_BLUE.getCode()));
            }
        }
    }


    @Override
    public int getZIndex() {
        return 0;
    }

    @Override
    public boolean isInside(int mouseX, int mouseY) {
        int scrollBarHeight = (tabela.getVisibleRows() * 3); // Wysokość suwaka
        int scrollBarWidth = 1; // Szerokość suwaka
        int scrollBarX = tabela.getX() + tabela.getWidth() + 1; // Prawa krawędź tabeli + odstęp
        int scrollBarY = tabela.getY(); // Górna krawędź tabeli

        return mouseX >= scrollBarX && mouseX < scrollBarX + scrollBarWidth && mouseY >= scrollBarY && mouseY < scrollBarY + scrollBarHeight;
    }

    @Override
    public void performAction() {
        // Obsługa przewijania tabeli
    }

    @Override
    public void show() {

    }

    @Override
    public void hide() {

    }

    @Override
    public int getX() {
        return 0;
    }

    @Override
    public int getY() {
        return 0;
    }

    @Override
    public int getWidth() {
        return 0;
    }

    @Override
    public int getHeight() {
        return 0;
    }

    @Override
    public void setActive(boolean active) {

    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    public void highlight() {

    }

    @Override
    public void resetHighlight() {

    }

    @Override
    public boolean isInteractable() {
        return false;
    }

    @Override
    public void windowResized(int width, int height) {

    }

    // Metoda do przewijania tabeli w górę
    public void scrollUp() {
        tabela.scrollUp();
    }

    // Metoda do przewijania tabeli w dół
    public void scrollDown() {
        tabela.scrollDown();
    }
}