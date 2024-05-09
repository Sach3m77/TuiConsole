package pl.projekt.tui.component;

import pl.projekt.tui.model.color.Colors;

public class TUILabel implements TUIComponent {

    private final String text;
    private final String textColor = Colors.TEXT_BLACK.getCode();
    private final String backgroundColor;
    private final int x, y, layerIndex;
    private int width, height;
    private final TUIManager screenManager;

    public TUILabel(String text, int x, int y, int layerIndex, String backgroundColor, TUIManager TUIManager){
        this.text = text;
        this.x = x;
        this.y = y;
        this.layerIndex = layerIndex;
        this.backgroundColor = backgroundColor;
        this.screenManager = TUIManager;
        countBounds();
    }

    private void countBounds(){
        height = 1;
        int prevIndex = 0;
        for(int i = 0; i < text.length(); i++){
            if(text.charAt(i) == '\n') {
                height++;
                int len = text.substring(prevIndex, i).length();
                if(len > width)
                    width = len;
                prevIndex = i;
            }
        }
        if(prevIndex == 0)
            width = text.length();
    }

    @Override
    public void drawComponent(TUIManager TUIManager) {
        TUIScreen screen = TUIManager.getScreen();

        screen.setText(x,y,text,textColor, backgroundColor, layerIndex);
    }

    @Override
    public int getLayerIndex() {
        return layerIndex;
    }

    @Override
    public void performAction() {}

    @Override
    public void show() {
        screenManager.addComponent(this);
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
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public void setActive(boolean active) {}

    @Override
    public boolean isComponentActive() {
        return false;
    }

    @Override
    public void highlightComponent() {}

    @Override
    public void resetHighlightComponent() {}

    @Override
    public boolean isInteractable() {
        return false;
    }

    @Override
    public void windowResized(int width, int height){}

}
