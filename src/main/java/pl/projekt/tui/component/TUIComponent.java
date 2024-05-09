package pl.projekt.tui.component;

    public interface TUIComponent {
        void drawComponent(TUIManager TUIManager);
        int getLayerIndex();
        int getHeight();
        int getWidth();
        int getX();
        int getY();
        void performAction();
        void show();
        void setActive(boolean active);
        boolean isComponentActive();
        void highlightComponent();
        void resetHighlightComponent();
        boolean isInteractable();
        void windowResized(int width, int height);
    }
