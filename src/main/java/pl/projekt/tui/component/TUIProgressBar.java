package pl.projekt.tui.component;

import pl.projekt.tui.model.color.Colors;

/**
 * TUIProgressBar class represents a text-based graphical progress bar component for a TUI (Text-based User Interface).
 * It visualizes progress as a filled bar with a gradient color and displays a percentage text in the center.
 */
public class TUIProgressBar implements TUIComponent {

    private TUIScreen tuiScreen;
    private int x, y, width, height;
    private int zIndex;
    private TUIManager tuiManager;
    private double progress;  // Progress value between 0 and 1
    private String bgColor = Colors.BG_BLUE.getCode();  // Background color
    private String fgColorStart = Colors.BG_GREEN.getCode();  // Start color for gradient
    private String fgColorEnd = Colors.BG_YELLOW.getCode();  // End color for gradient
    private String textColor = Colors.TEXT_WHITE.getCode();  // Text color for percentage

    /**
     * Constructor to initialize the progress bar with specified dimensions and initial properties.
     * @param x X-coordinate position on the screen.
     * @param y Y-coordinate position on the screen.
     * @param width Width of the progress bar.
     * @param height Height of the progress bar.
     * @param zIndex Z-index to determine the layering of components.
     * @param tuiManager Reference to the TUIManager handling UI components.
     */
    public TUIProgressBar(int x, int y, int width, int height, int zIndex, TUIManager tuiManager) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.zIndex = zIndex;
        this.tuiManager = tuiManager;
        this.progress = 0.0;  // Initialize progress to 0%
    }

    /**
     * Sets the progress value of the progress bar.
     * @param progress Progress value between 0 and 1.
     */
    public void setProgress(double progress) {
        this.progress = progress;
    }

    /**
     * Calculates and returns a gradient color based on the given ratio.
     * @param ratio Ratio indicating the position within the progress bar (0 to 1).
     * @return String representing the gradient color in hexadecimal format.
     */
    private String getGradientColor(double ratio) {
        // Extract RGB components from start and end gradient colors
        int redStart = Integer.parseInt(fgColorStart.substring(1, 3), 16);
        int greenStart = Integer.parseInt(fgColorStart.substring(3, 5), 16);
        int blueStart = Integer.parseInt(fgColorStart.substring(5, 7), 16);

        int redEnd = Integer.parseInt(fgColorEnd.substring(1, 3), 16);
        int greenEnd = Integer.parseInt(fgColorEnd.substring(3, 5), 16);
        int blueEnd = Integer.parseInt(fgColorEnd.substring(5, 7), 16);

        // Calculate interpolated RGB values based on the ratio
        int red = (int) (redStart + ratio * (redEnd - redStart));
        int green = (int) (greenStart + ratio * (greenEnd - greenStart));
        int blue = (int) (blueStart + ratio * (blueEnd - blueStart));

        // Format RGB values to hexadecimal color representation
        return String.format("#%02X%02X%02X", red, green, blue);
    }

    /**
     * Draws the progress bar component on the screen.
     * @param tuiManager TUIManager instance managing the screen and components.
     */
    @Override
    public void drawComponent(TUIManager tuiManager) {
        TUIScreen screen = tuiManager.getScreen();
        int filledWidth = (int) Math.round(width * progress);  // Calculate filled width based on progress

        // Draw filled portion of progress bar with gradient
        for (int i = 0; i < filledWidth; i++) {
            double ratio = (double) i / (double) filledWidth;  // Calculate ratio within filled portion
            String fgColor = getGradientColor(ratio);  // Get gradient color based on ratio
            screen.addPixelToLayer(x + i, y, zIndex, new TUIScreenCell(' ', "", fgColor));
        }

        // Draw background for the remaining portion of the progress bar
        for (int i = filledWidth; i < width; i++) {
            screen.addPixelToLayer(x + i, y, zIndex, new TUIScreenCell(' ', "", bgColor));
        }

        // Draw percentage text in the center of the progress bar
        String percentageText = String.format("%.0f%%", progress * 100);  // Format percentage text
        int textStart = x + (width - percentageText.length()) / 2;  // Calculate starting position for text

        for (int i = 0; i < percentageText.length(); i++) {
            int pos = textStart + i;
            TUIScreenCell cell;
            if (pos < filledWidth) {
                // Use gradient color within filled portion
                cell = new TUIScreenCell(percentageText.charAt(i), textColor, getGradientColor((double) pos / (double) filledWidth));
            } else {
                // Use background color for remaining portion
                cell = new TUIScreenCell(percentageText.charAt(i), textColor, bgColor);
            }
            screen.addPixelToLayer(pos, y, zIndex, cell);
        }

        // Draw border around the progress bar
        for (int i = 0; i < width; i++) {
            screen.addPixelToLayer(x + i, y - 1, zIndex, new TUIScreenCell('-', textColor, ""));  // Top border
            screen.addPixelToLayer(x + i, y + 1, zIndex, new TUIScreenCell('-', textColor, ""));  // Bottom border
        }
        screen.addPixelToLayer(x - 1, y, zIndex, new TUIScreenCell('|', textColor, ""));  // Left border
        screen.addPixelToLayer(x + width, y, zIndex, new TUIScreenCell('|', textColor, ""));  // Right border
    }

    // Implementations for other methods of TUIComponent interface

    @Override
    public int getZIndex() {
        return zIndex;
    }

    @Override
    public void performAction() {
        // No action required as ProgressBar does not have any action
    }

    @Override
    public void show() {
        tuiManager.addComponent(this);
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
    public void setActive(boolean active) {
        // No action required as ProgressBar is not interactive
    }

    @Override
    public boolean isComponentActive() {
        return false;
    }

    @Override
    public void highlightComponent() {
        // No action required as ProgressBar is not interactive
    }

    @Override
    public void resetHighlightComponent() {
        // No action required as ProgressBar is not interactive
    }

    /**
     * Checks if the progress bar is currently active.
     * @return Always returns false since ProgressBar is not interactive.
     */
    public boolean isActive() {
        return false;
    }

    /**
     * Highlights the progress bar (no action taken as ProgressBar is not interactive).
     */
    public void highlight() {
        // No action required as ProgressBar is not interactive
    }

    /**
     * Resets any highlight on the progress bar (no action taken as ProgressBar is not interactive).
     */
    public void resetHighlight() {
        // No action required as ProgressBar is not interactive
    }

    /**
     * Checks if the progress bar is interactable.
     * @return Always returns false since ProgressBar is not interactive.
     */
    @Override
    public boolean isInteractable() {
        return false;
    }

    @Override
    public void windowResized(int width, int height) {
        // Handle window resize if necessary
    }

    @Override
    public void hide() {
        // No action required to hide ProgressBar
    }

}
