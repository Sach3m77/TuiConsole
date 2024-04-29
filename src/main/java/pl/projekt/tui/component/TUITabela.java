package pl.projekt.tui.component;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.projekt.tui.model.color.ANSIColors;


import java.util.List;
import java.util.regex.Pattern;

public class TUITabela implements TUIComponent {
    Logger logger = LoggerFactory.getLogger(TUITabela.class);

    private StringBuilder textContent;
    private int x, y, width, height;
    private String bgColor = ANSIColors.BG_RED.getCode();
    private String textColor = ANSIColors.TEXT_WHITE.getCode();
    private boolean isNumeric, isPassword;
    private Pattern regexPattern = null;
    private int zIndex;
    private boolean isActive;
    private TUIManager TUIManager;
    private int maxCharacters = Integer.MAX_VALUE;

    // UIBorder = prostokacik w tabeli
    private List<TUIBorder> borders;

    //UILabel teksty nad tabela

    private List<String> newLabels;

    private List<String> rowContents;



    public TUITabela(int x, int y, int width, int height, int zIndex, TUIManager TUIManager, List<String> newLabels, List<String> rowContents) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.zIndex = zIndex;
        this.TUIManager = TUIManager;
        this.textContent = new StringBuilder();
        this.newLabels = newLabels;
        this.rowContents = rowContents;
    }

    public void drawAllHeaders(TUITab uiTab) {
        int xPosition = this.x + 3;
        System.out.println(this.newLabels);
        for (String label : this.newLabels) {
            TUILabel labelObject = new TUILabel(label, xPosition, this.y -3 , 0, ANSIColors.BG_BRIGHT_BLUE.getCode(), TUIManager);
            xPosition += (this.width / this.newLabels.size()) + 3;
            uiTab.addComponent(labelObject);
        }
    }

    public void drawAllRows(TUITab uiTab) {
        int startingY = this.y - 2;
        int startingX = this.x;
        int count  = 1;
        int elementsPerRow = 3; // Ilość elementów w jednym rzędzie
        int totalElements = this.rowContents.size();
        System.out.println(this.newLabels.size());
        for(String rowContent : this.rowContents) {
            if (totalElements >= elementsPerRow && count > 1 && (count - 1) % elementsPerRow == 0) {
//                    startingY = startingY + this.height / (this.rowContents.size() / 3);
                startingY = startingY + 3;
                startingX = this.x;
            }
            TUIBorder border = new TUIBorder(startingX, startingY, this.width / 3 + 2, 3 , 0, TUIManager, rowContent);
            count++;
            border.setTextInBorder(uiTab);
            border.setTextColor(ANSIColors.TEXT_WHITE.getCode());
            border.setBgColor(ANSIColors.BG_BRIGHT_BLUE.getCode());
            startingX += this.width / 3 + 2;
            uiTab.addComponent(border);
        }
    }


    public void setMaxCharacters(int maxCharacters) {
        this.maxCharacters = maxCharacters;
    }

    public void setNumeric(boolean numeric) {
        isNumeric = numeric;
    }

    public void setPassword(boolean password) {
        isPassword = password;
    }

    public void setRegexPattern(String regex) {
        this.regexPattern = Pattern.compile(regex);
    }

    public String getText() {
        return textContent.toString();
    }

    @Override
    public void drawComponent(TUIManager TUIManager) {

//            String fullText = isPassword ? "*".repeat(textContent.length()) : textContent.toString();
//            String displayText = fullText;
//
//            if (fullText.length() > width) {
//                displayText = fullText.substring(fullText.length() - width);
//            }
//            // Czyść cały obszar pola tekstowego
//            for (int i = 0; i < width; i++) {
//                ScreenCell emptyCell = new ScreenCell(' ', textColor, bgColor);  // Użyj pustego znaku do wyczyszczenia
//                uiManager.getScreen().addPixelToLayer(x + i, y, zIndex, emptyCell);
//            }
//
//            // Następnie dodaj nowy tekst
//            for (int i = 0; i < displayText.length(); i++) {
//                ScreenCell cell = new ScreenCell(displayText.charAt(i), textColor, bgColor);
//                uiManager.getScreen().addPixelToLayer(x + i, y, zIndex, cell);
//            }
    }

    @Override
    public boolean isInside(int mouseX, int mouseY) {
        return mouseX >= x && mouseX <= (x + width) && mouseY >= y && mouseY <= (y + height);
    }

    @Override
    public void performAction() {
        logger.debug("Performing action");
        logger.debug("Text content: {}", textContent.toString());
    }

    @Override
    public void show() {
        if (TUIManager != null) {
            TUIManager.addComponent(this);
        }
    }

    @Override
    public void hide() {
        if (TUIManager != null) {
            TUIManager.removeComponent(this);
        }
    }

    @Override
    public int getX() {return x;}

    @Override
    public int getY() {return y;}

    @Override
    public int getWidth() {return width;}

    @Override
    public int getHeight() {return height;}

    @Override
    public void setActive(boolean active) {this.isActive = active;}

    @Override
    public boolean isActive() {return this.isActive;}

    @Override
    public void highlight() {

        setBgColor(ANSIColors.BG_YELLOW.getCode());
        setTextColor(ANSIColors.TEXT_BLACK.getCode());

        TUIManager.refresh();
    }

    @Override
    public void resetHighlight() {
        setBgColor(ANSIColors.BG_RED.getCode());
        setTextColor(ANSIColors.TEXT_WHITE.getCode());

        TUIManager.refresh();
    }

    @Override
    public boolean isInteractable() {return true;}


    @Override
    public int getZIndex() {return zIndex;}

    public void setBgColor(String bgColor) {this.bgColor = bgColor;}

    public void setTextColor(String textColor) {this.textColor = textColor;}

    @Override
    public void windowResized(int width, int height){

    }

    private int scrollPosition = 0; // Aktualna pozycja suwaka
    private int visibleRows = 5; // Liczba widocznych wierszy


    public void setVisibleRows(int visibleRows) {
        this.visibleRows = visibleRows;
    }

    public int getVisibleRows() {
        return visibleRows;
    }

    public void scrollUp() {
        if (scrollPosition > 0) {
            scrollPosition--;
            TUIManager.refresh(); // Odświeżenie interfejsu po przewinięciu
        }
    }

    public void scrollDown() {
        int maxScroll = Math.max(0, rowContents.size() - visibleRows);
        if (scrollPosition < maxScroll) {
            scrollPosition++;
            TUIManager.refresh(); // Odświeżenie interfejsu po przewinięciu
        }
    }

}

