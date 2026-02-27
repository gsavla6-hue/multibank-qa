package com.multibank.task2;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Task 2 – String Character Frequency Counter
 *
 * <p>Counts occurrences of each character in a string and outputs them
 * in order of <b>first appearance</b>.
 *
 * <h3>Assumptions</h3>
 * <ul>
 *   <li><b>Case-sensitive</b> by default: 'A' and 'a' are counted separately.
 *       Pass {@code caseSensitive=false} to fold to lowercase before counting.</li>
 *   <li><b>Whitespace is counted</b> by default (the space in "hello world" contributes ' ':1).
 *       Pass {@code includeWhitespace=false} to ignore all whitespace characters.</li>
 *   <li><b>Special characters are counted</b> – no filtering is applied unless
 *       {@code alphanumericOnly=true} is used.</li>
 *   <li>A {@code null} or empty input returns an empty result (no exception).</li>
 * </ul>
 *
 * <h3>Algorithm</h3>
 * <ol>
 *   <li>Iterate the string once, O(n).</li>
 *   <li>Use a {@link LinkedHashMap} to preserve insertion (first-appearance) order
 *       while storing counts. Overall O(n) time and O(k) space where k = unique chars.</li>
 * </ol>
 *
 * <h3>Example</h3>
 * <pre>
 *   Input : "hello world"
 *   Output: h:1, e:1, l:3, o:2, :1, w:1, r:1, d:1
 * </pre>
 */
public class CharacterFrequency {

    // ── Configuration ─────────────────────────────────────────────────────────

    private final boolean caseSensitive;
    private final boolean includeWhitespace;
    private final boolean alphanumericOnly;

    /**
     * Default configuration:
     * case-sensitive, whitespace included, all characters counted.
     */
    public CharacterFrequency() {
        this(true, true, false);
    }

    public CharacterFrequency(boolean caseSensitive,
                               boolean includeWhitespace,
                               boolean alphanumericOnly) {
        this.caseSensitive     = caseSensitive;
        this.includeWhitespace = includeWhitespace;
        this.alphanumericOnly  = alphanumericOnly;
    }

    // ── Core API ──────────────────────────────────────────────────────────────

    /**
     * Counts character frequencies in the given string.
     *
     * @param input the string to analyse (may be null or empty)
     * @return a {@link LinkedHashMap} mapping each character to its count,
     *         ordered by first appearance
     */
    public LinkedHashMap<Character, Integer> count(String input) {
        LinkedHashMap<Character, Integer> frequencyMap = new LinkedHashMap<>();

        if (input == null || input.isEmpty()) {
            return frequencyMap;
        }

        String processed = caseSensitive ? input : input.toLowerCase();

        for (char ch : processed.toCharArray()) {
            // Apply filters
            if (!includeWhitespace && Character.isWhitespace(ch)) continue;
            if (alphanumericOnly && !Character.isLetterOrDigit(ch))  continue;

            frequencyMap.merge(ch, 1, Integer::sum);
        }

        return frequencyMap;
    }

    /**
     * Returns the result as a formatted string: {@code h:1, e:1, l:3, ...}
     *
     * @param input the string to analyse
     * @return formatted frequency string, or {@code "(empty input)"} for blank input
     */
    public String format(String input) {
        LinkedHashMap<Character, Integer> map = count(input);
        if (map.isEmpty()) {
            return "(empty input)";
        }
        return map.entrySet()
                  .stream()
                  .map(e -> e.getKey() + ":" + e.getValue())
                  .collect(Collectors.joining(", "));
    }

    // ── Entry point ───────────────────────────────────────────────────────────

    public static void main(String[] args) {
        CharacterFrequency cf = new CharacterFrequency();

        // Provided example
        runExample(cf, "hello world");

        // Edge cases
        runExample(cf, "");
        runExample(cf, null);
        runExample(cf, "aAbBcC");                        // case sensitivity
        runExample(cf, "   ");                           // only whitespace
        runExample(cf, "Hello, World! 123");             // special chars + digits

        // Case-insensitive, no whitespace
        CharacterFrequency cfNoCase = new CharacterFrequency(false, false, false);
        System.out.println("\n--- Case-insensitive, whitespace ignored ---");
        runExample(cfNoCase, "Hello World");

        // Alphanumeric only
        CharacterFrequency cfAlnum = new CharacterFrequency(true, false, true);
        System.out.println("\n--- Alphanumeric only ---");
        runExample(cfAlnum, "Hello, World! 123");
    }

    private static void runExample(CharacterFrequency cf, String input) {
        System.out.printf("Input : \"%s\"%n", input);
        System.out.printf("Output: %s%n%n", cf.format(input));
    }
}
