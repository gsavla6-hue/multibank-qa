package com.multibank.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * Utility to load external test data from JSON files on the classpath.
 * Keeps all expected values out of the test code itself.
 */
@Slf4j
public class TestDataLoader {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private TestDataLoader() {}

    /**
     * Loads a JSON file from the classpath (src/test/resources/testdata/).
     *
     * @param filename filename relative to testdata/, e.g. "navigation.json"
     * @return parsed Map
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> loadMap(String filename) {
        return load(filename, Map.class);
    }

    /**
     * Loads a JSON file and maps it to a List.
     */
    @SuppressWarnings("unchecked")
    public static List<Object> loadList(String filename) {
        return load(filename, List.class);
    }

    /**
     * Generic loader.
     */
    public static <T> T load(String filename, Class<T> type) {
        String path = "testdata/" + filename;
        try (InputStream in = TestDataLoader.class
                .getClassLoader()
                .getResourceAsStream(path)) {
            if (in == null) {
                throw new RuntimeException("Test data file not found: " + path);
            }
            T data = MAPPER.readValue(in, type);
            log.debug("Loaded test data from: {}", path);
            return data;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load test data: " + path, e);
        }
    }

    /**
     * Convenience method to get a String value from a map file.
     */
    public static String getString(String filename, String key) {
        return String.valueOf(loadMap(filename).get(key));
    }

    /**
     * Convenience method to get a List value from a map file.
     */
    @SuppressWarnings("unchecked")
    public static List<String> getStringList(String filename, String key) {
        Object value = loadMap(filename).get(key);
        if (value instanceof List) {
            return (List<String>) value;
        }
        throw new RuntimeException("Key '" + key + "' is not a list in " + filename);
    }
}
