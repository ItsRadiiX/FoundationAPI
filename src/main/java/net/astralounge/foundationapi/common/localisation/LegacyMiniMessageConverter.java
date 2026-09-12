package net.astralounge.foundationapi.common.localisation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class LegacyMiniMessageConverter {

    private static final Pattern HEX_PATTERN =
            Pattern.compile("(?i)&?#([0-9a-f]{6})");

    private static final Pattern LEGACY_PATTERN =
            Pattern.compile("(?i)&([0-9a-fk-or])");

    private static final String[] COLORS = {
            "black", "dark_blue", "dark_green", "dark_aqua",
            "dark_red", "dark_purple", "gold", "gray",
            "dark_gray", "blue", "green", "aqua",
            "red", "light_purple", "yellow", "white"
    };

    private static final String[] FORMATS = {
            "obfuscated", // k
            "bold",       // l
            "strikethrough", // m
            "underline",  // n
            "italic",     // o
            "reset"       // r
    };

    public static String legacyToMini(String input) {
        if (input == null || input.isEmpty()) return input;

        // --- HEX COLORS ---
        Matcher hexMatcher = HEX_PATTERN.matcher(input);
        StringBuilder hexBuffer = new StringBuilder();

        while (hexMatcher.find()) {
            String hex = hexMatcher.group(1);
            hexMatcher.appendReplacement(hexBuffer, "<#" + hex + ">");
        }
        hexMatcher.appendTail(hexBuffer);

        // --- LEGACY FORMATTING ---
        Matcher legacyMatcher = LEGACY_PATTERN.matcher(hexBuffer.toString());
        StringBuilder legacyBuffer = new StringBuilder();

        while (legacyMatcher.find()) {
            char code = Character.toLowerCase(legacyMatcher.group(1).charAt(0));

            String replacement;
            if (code >= '0' && code <= '9') {
                replacement = "<" + COLORS[code - '0'] + ">";
            } else if (code >= 'a' && code <= 'f') {
                replacement = "<" + COLORS[10 + (code - 'a')] + ">";
            } else {
                replacement = "<" + FORMATS[code - 'k'] + ">";
            }

            legacyMatcher.appendReplacement(legacyBuffer, replacement);
        }
        legacyMatcher.appendTail(legacyBuffer);

        return legacyBuffer.toString();
    }

    public static String legacyToMiniFast(String input) {
        if (input == null || input.isEmpty()) return input;

        // Quick exit if no legacy markers
        if (input.indexOf('&') == -1 && input.indexOf('§') == -1) {
            return input;
        }

        StringBuilder out = new StringBuilder(input.length() + 16);

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);

            if ((c == '&' || c == '§') && i + 1 < input.length()) {
                char next = input.charAt(i + 1);

                // -------------------------------------------------
                // Spigot hex format: §x§R§R§G§G§B§B
                // -------------------------------------------------
                if (next == 'x' && i + 13 < input.length()) {
                    String hex = extractSpigotHex(input, i);
                    if (hex != null) {
                        out.append("<#").append(hex).append(">");
                        i += 13;
                        continue;
                    }
                }

                // -------------------------------------------------
                // Modern legacy hex: &#RRGGBB or §#RRGGBB
                // -------------------------------------------------
                if (next == '#' && i + 7 < input.length()) {
                    String hex = input.substring(i + 2, i + 8);
                    if (isHex(hex)) {
                        out.append("<#").append(hex).append(">");
                        i += 7;
                        continue;
                    }
                }

                // -------------------------------------------------
                // Standard legacy codes
                // -------------------------------------------------
                char code = Character.toLowerCase(next);
                String replacement = legacyCodeToMini(code);

                if (replacement != null) {
                    out.append(replacement);
                    i++; // skip formatting character
                    continue;
                }
            }

            out.append(c);
        }

        return out.toString();
    }

    private static String extractSpigotHex(String input, int index) {
        // Expected format: §x§R§R§G§G§B§B
        if (index + 13 >= input.length()) return null;

        StringBuilder hex = new StringBuilder(6);
        char prefix = input.charAt(index);

        for (int i = 0; i < 6; i++) {
            int pos = index + 2 + (i * 2);

            if (input.charAt(pos - 1) != prefix) return null;

            char c = input.charAt(pos);
            if (!isHexChar(c)) return null;

            hex.append(c);
        }

        return hex.toString();
    }

    private static String legacyCodeToMini(char code) {
        return switch (code) {
            case '0' -> "<black>";
            case '1' -> "<dark_blue>";
            case '2' -> "<dark_green>";
            case '3' -> "<dark_aqua>";
            case '4' -> "<dark_red>";
            case '5' -> "<dark_purple>";
            case '6' -> "<gold>";
            case '7' -> "<gray>";
            case '8' -> "<dark_gray>";
            case '9' -> "<blue>";
            case 'a' -> "<green>";
            case 'b' -> "<aqua>";
            case 'c' -> "<red>";
            case 'd' -> "<light_purple>";
            case 'e' -> "<yellow>";
            case 'f' -> "<white>";
            case 'k' -> "<obfuscated>";
            case 'l' -> "<bold>";
            case 'm' -> "<strikethrough>";
            case 'n' -> "<underline>";
            case 'o' -> "<italic>";
            case 'r' -> "<reset>";
            default -> null;
        };
    }

    private static boolean isHex(String s) {
        if (s.length() != 6) return false;
        for (int i = 0; i < 6; i++) {
            if (!isHexChar(s.charAt(i))) return false;
        }
        return true;
    }

    private static boolean isHexChar(char c) {
        return (c >= '0' && c <= '9')
                || (c >= 'a' && c <= 'f')
                || (c >= 'A' && c <= 'F');
    }
}
