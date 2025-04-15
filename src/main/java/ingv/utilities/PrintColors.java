package ingv.utilities;

public enum PrintColors {
    RESET("\u001B[0m"),
    RED("\u001B[31m"),
    GREEN("\u001B[32m"),
    YELLOW("\u001B[33m"),
    BLUE("\u001B[34m"),
    PURPLE("\u001B[35m"),
    CYAN("\u001B[36m");

    private final String code;

    PrintColors(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static String format(String text, PrintColors color) {
        return color.getCode() + text + RESET.getCode();
    }
    }