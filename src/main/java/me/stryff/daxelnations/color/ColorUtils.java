package me.stryff.daxelnations.color;

import java.util.HashMap;
import java.util.Map;

public class ColorUtils {

    private static final Map<Character, String> colorMap = new HashMap<>();

    static {
        colorMap.put('0', ConsoleColor.BLACK);
        colorMap.put('1', ConsoleColor.BLUE);
        colorMap.put('2', ConsoleColor.GREEN);
        colorMap.put('3', ConsoleColor.CYAN);
        colorMap.put('4', ConsoleColor.RED);
        colorMap.put('5', ConsoleColor.PURPLE);
        colorMap.put('6', ConsoleColor.YELLOW);
        colorMap.put('7', ConsoleColor.WHITE);
        colorMap.put('8', ConsoleColor.BLACK_BOLD);  // Assuming dark grey as bold black
        colorMap.put('9', ConsoleColor.BLUE_BOLD);
        colorMap.put('a', ConsoleColor.GREEN_BOLD);
        colorMap.put('b', ConsoleColor.CYAN_BOLD);
        colorMap.put('c', ConsoleColor.RED_BOLD);
        colorMap.put('d', ConsoleColor.PURPLE_BOLD);
        colorMap.put('e', ConsoleColor.YELLOW_BOLD);
        colorMap.put('f', ConsoleColor.WHITE_BOLD);
        colorMap.put('r', ConsoleColor.RESET);
    }

    public static String toConsoleColor(String input) {
        StringBuilder output = new StringBuilder();
        char[] chars = input.toCharArray();

        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == '&' && i + 1 < chars.length) {
                char colorChar = chars[i + 1];
                if (colorMap.containsKey(colorChar)) {
                    output.append(colorMap.get(colorChar));
                    i++; // Skip the next character as it's part of the color code
                    continue;
                }
            }
            output.append(chars[i]);
        }

        return output.toString();
    }
}
