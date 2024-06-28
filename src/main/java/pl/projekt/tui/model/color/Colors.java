package pl.projekt.tui.model.color;

import lombok.Getter;

/**
 * Enum containing ANSI escape codes for text and background colors.
 * These codes are used for colorizing text and backgrounds in a text-based user interface (TUI).
 */
@Getter
public enum Colors {

    /**
     * Black text.
     */
    TEXT_BLACK("\033[30m"),
    /**
     * Red text.
     */
    TEXT_RED("\033[31m"),
    /**
     * Green text.
     */
    TEXT_GREEN("\033[32m"),
    /**
     * Yellow text.
     */
    TEXT_YELLOW("\033[33m"),
    /**
     * Blue text.
     */
    TEXT_BLUE("\033[34m"),
    /**
     * Magenta text.
     */
    TEXT_MAGENTA("\033[35m"),
    /**
     * Cyan text.
     */
    TEXT_CYAN("\033[36m"),
    /**
     * White text.
     */
    TEXT_WHITE("\033[37m"),
    /**
     * Bright black text.
     */
    TEXT_BRIGHT_BLACK("\033[90m"),
    /**
     * Bright red text.
     */
    TEXT_BRIGHT_RED("\033[91m"),
    /**
     * Bright green text.
     */
    TEXT_BRIGHT_GREEN("\033[92m"),
    /**
     * Bright yellow text.
     */
    TEXT_BRIGHT_YELLOW("\033[93m"),
    /**
     * Bright blue text.
     */
    TEXT_BRIGHT_BLUE("\033[94m"),
    /**
     * Bright magenta text.
     */
    TEXT_BRIGHT_MAGENTA("\033[95m"),
    /**
     * Bright cyan text.
     */
    TEXT_BRIGHT_CYAN("\033[96m"),
    /**
     * Bright white text.
     */
    TEXT_BRIGHT_WHITE("\033[97m"),

    /**
     * Black background.
     */
    BG_BLACK("\033[40m"),
    /**
     * Red background.
     */
    BG_RED("\033[41m"),
    /**
     * Green background.
     */
    BG_GREEN("\033[42m"),
    /**
     * Yellow background.
     */
    BG_YELLOW("\033[43m"),
    /**
     * Blue background.
     */
    BG_BLUE("\033[44m"),
    /**
     * Magenta background.
     */
    BG_MAGENTA("\033[45m"),
    /**
     * Cyan background.
     */
    BG_CYAN("\033[46m"),
    /**
     * White background.
     */
    BG_WHITE("\033[47m"),
    /**
     * Bright black background.
     */
    BG_BRIGHT_BLACK("\033[100m"),
    /**
     * Bright red background.
     */
    BG_BRIGHT_RED("\033[101m"),
    /**
     * Bright green background.
     */
    BG_BRIGHT_GREEN("\033[102m"),
    /**
     * Bright yellow background.
     */
    BG_BRIGHT_YELLOW("\033[103m"),
    /**
     * Bright blue background.
     */
    BG_BRIGHT_BLUE("\033[104m"),
    /**
     * Bright magenta background.
     */
    BG_BRIGHT_MAGENTA("\033[105m"),
    /**
     * Bright cyan background.
     */
    BG_BRIGHT_CYAN("\033[106m"),
    /**
     * Bright white background.
     */
    BG_BRIGHT_WHITE("\033[107m"),

    /**
     * Resets text and background color effects.
     */
    RESET("\033[0m");

    private final String code;

    /**
     * Constructor for Colors enum.
     *
     * @param code ANSI escape code for the color.
     */
    Colors(String code) {
        this.code = code;
    }

}
