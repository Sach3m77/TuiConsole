package pl.projekt.tui.component;



import pl.projekt.tui.model.color.Colors;

/**
 * Klasa do tworzenia etykiet.
 */
public class TUILabel implements TUIComponent {

    private String text;
    private String textColor = Colors.TEXT_BLACK.getCode();
    private String bgColor;
    private int x, y, w, h, zIndex;
    private TUIManager screenManager;

    /**
     * Konstruktor z możliwością ustawienia koloru tekstu.
     * @param text Tekst do wyświetlenia.
     * @param x Pozycja x etykiety.
     * @param y Pozycja y etykiety.
     * @param zIndex z-index etykiety.
     * @param textColor Kolor tekstu.
     * @param bgColor Kolor tła.
     * @param TUIManager Obiekt <i>UIManager</i>
     */
    public TUILabel(String text, int x, int y, int zIndex, String textColor, String bgColor, TUIManager TUIManager){
        this.text = text;
        this.x = x;
        this.y = y;
        this.zIndex = zIndex;
        this.textColor = textColor;
        this.bgColor = bgColor;
        this.screenManager = TUIManager;
        countBounds();
    }
    /**
     * Konstruktor z domyślnym kolorem tekstu.
     * @param text Tekst do wyświetlenia.
     * @param x Pozycja x etykiety.
     * @param y Pozycja y etykiety.
     * @param zIndex z-index etykiety.
     * @param bgColor Kolor tła.
     * @param TUIManager Obiekt <i>UIManager</i>
     */
    public TUILabel(String text, int x, int y, int zIndex, String bgColor, TUIManager TUIManager){
        this.text = text;
        this.x = x;
        this.y = y;
        this.zIndex = zIndex;
        this.bgColor = bgColor;
        this.screenManager = TUIManager;
        countBounds();
    }

    /**
     * Liczy wymiary tekstu na ekranie z uwzględnieniem enterów.
     */
    private void countBounds(){
        h = 1;
        int prevIndex = 0;
        for(int i = 0; i < text.length(); i++){
            if(text.charAt(i) == '\n') {
                h++;
                int len = text.substring(prevIndex, i).length();
                if(len > w)
                    w = len;
                prevIndex = i;
            }
        }
        if(prevIndex == 0)
            w = text.length();
    }

    /**
     * Ustawia tekst etykiety.
     * @param text Tekst do zmiany.
     */
    public void setText(String text){
        this.text = text;
        countBounds();
    }

    public String getText() {
        return text;
    }

    @Override
    public void drawComponent(TUIManager TUIManager) {
        TUIScreen screen = TUIManager.getScreen();

        screen.setText(x,y,text,textColor, bgColor, zIndex);
    }

    @Override
    public int getZIndex() {
        return zIndex;
    }

    @Override
    public boolean isInside(int x, int y) {
        return false;
    }

    @Override
    public void performAction() {

    }

    @Override
    public void show() {
        screenManager.addComponent(this);
    }

    @Override
    public void hide() {screenManager.removeComponent(this);}

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
        return w;
    }

    @Override
    public int getHeight() {
        return h;
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
    public void windowResized(int width, int height){

    }

}
