package com.multibank.task2;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.LinkedHashMap;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link CharacterFrequency}.
 */
public class CharacterFrequencyTest {

    private CharacterFrequency defaultCf;

    @BeforeClass
    public void setUp() {
        defaultCf = new CharacterFrequency(); // case-sensitive, whitespace included
    }

    // ── Happy-path ────────────────────────────────────────────────────────────

    @Test(description = "Example from the spec: 'hello world'")
    public void testHelloWorld() {
        String result = defaultCf.format("hello world");
        assertThat(result).isEqualTo("h:1, e:1, l:3, o:2,  :1, w:1, r:1, d:1");
    }

    @Test(description = "Single character string")
    public void testSingleChar() {
        LinkedHashMap<Character, Integer> map = defaultCf.count("a");
        assertThat(map).containsEntry('a', 1).hasSize(1);
    }

    @Test(description = "All identical characters")
    public void testAllSame() {
        LinkedHashMap<Character, Integer> map = defaultCf.count("aaaa");
        assertThat(map).containsEntry('a', 4).hasSize(1);
    }

    @Test(description = "Order of output matches first appearance")
    public void testFirstAppearanceOrder() {
        LinkedHashMap<Character, Integer> map = defaultCf.count("bcab");
        // b first, then c, then a
        assertThat(map.keySet()).containsExactly('b', 'c', 'a');
        assertThat(map).containsEntry('b', 2).containsEntry('c', 1).containsEntry('a', 1);
    }

    // ── Edge cases ────────────────────────────────────────────────────────────

    @Test(description = "Empty string returns empty result")
    public void testEmptyString() {
        LinkedHashMap<Character, Integer> map = defaultCf.count("");
        assertThat(map).isEmpty();
        assertThat(defaultCf.format("")).isEqualTo("(empty input)");
    }

    @Test(description = "Null input returns empty result without throwing")
    public void testNullInput() {
        LinkedHashMap<Character, Integer> map = defaultCf.count(null);
        assertThat(map).isEmpty();
    }

    @Test(description = "Whitespace-only string counts spaces")
    public void testWhitespaceOnly() {
        LinkedHashMap<Character, Integer> map = defaultCf.count("   ");
        assertThat(map).containsEntry(' ', 3);
    }

    @Test(description = "Special characters are counted")
    public void testSpecialChars() {
        LinkedHashMap<Character, Integer> map = defaultCf.count("a!a!");
        assertThat(map).containsEntry('a', 2).containsEntry('!', 2);
    }

    @Test(description = "Digits are counted")
    public void testDigits() {
        LinkedHashMap<Character, Integer> map = defaultCf.count("a1a2");
        assertThat(map).containsEntry('a', 2).containsEntry('1', 1).containsEntry('2', 1);
    }

    // ── Case sensitivity ──────────────────────────────────────────────────────

    @Test(description = "Default is case-sensitive: A and a counted separately")
    public void testCaseSensitive() {
        LinkedHashMap<Character, Integer> map = defaultCf.count("AaBb");
        assertThat(map)
                .containsEntry('A', 1)
                .containsEntry('a', 1)
                .containsEntry('B', 1)
                .containsEntry('b', 1)
                .hasSize(4);
    }

    @Test(description = "Case-insensitive mode folds A and a together")
    public void testCaseInsensitive() {
        CharacterFrequency cf = new CharacterFrequency(false, true, false);
        LinkedHashMap<Character, Integer> map = cf.count("AaBb");
        assertThat(map)
                .containsEntry('a', 2)
                .containsEntry('b', 2)
                .hasSize(2);
    }

    // ── Whitespace filtering ──────────────────────────────────────────────────

    @Test(description = "Whitespace excluded when includeWhitespace=false")
    public void testExcludeWhitespace() {
        CharacterFrequency cf = new CharacterFrequency(true, false, false);
        LinkedHashMap<Character, Integer> map = cf.count("hello world");
        assertThat(map).doesNotContainKey(' ');
        assertThat(map).containsEntry('l', 3).containsEntry('o', 2);
    }

    // ── Alphanumeric filter ───────────────────────────────────────────────────

    @Test(description = "Alphanumeric-only mode strips special chars and spaces")
    public void testAlphanumericOnly() {
        CharacterFrequency cf = new CharacterFrequency(true, false, true);
        LinkedHashMap<Character, Integer> map = cf.count("He!lo, 123");
        assertThat(map)
                .containsEntry('H', 1)
                .containsEntry('e', 1)
                .containsEntry('l', 1)
                .containsEntry('o', 1)
                .containsEntry('1', 1)
                .containsEntry('2', 1)
                .containsEntry('3', 1)
                .doesNotContainKey('!')
                .doesNotContainKey(',')
                .doesNotContainKey(' ');
    }

    // ── DataProvider-driven tests ─────────────────────────────────────────────

    @DataProvider(name = "formatTestData")
    public Object[][] formatTestData() {
        return new Object[][] {
                { "ab",      "a:1, b:1"              },
                { "aab",     "a:2, b:1"              },
                { "aba",     "a:2, b:1"              },
                { "z",       "z:1"                   },
                { "112233",  "1:2, 2:2, 3:2"         },
        };
    }

    @Test(dataProvider = "formatTestData",
          description = "Parametrised format output verification")
    public void testFormatOutput(String input, String expected) {
        assertThat(defaultCf.format(input)).isEqualTo(expected);
    }
}
