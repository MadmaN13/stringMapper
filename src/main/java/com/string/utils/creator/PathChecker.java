package com.string.utils.creator;

import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Simple util to check system path correctness.
 */
public class PathChecker {

    public static boolean isValid(String path) {
        // only basic dummy checks
        return path != null &&
                !path.isEmpty() &&
                Files.exists(Paths.get(path));
    }
}
