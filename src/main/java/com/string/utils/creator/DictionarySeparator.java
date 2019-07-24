package com.string.utils.creator;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Util class to split dictionary file into smaller ones.
 * Dictionary file is represented by file with one word in every single line.
 */
@Slf4j
public class DictionarySeparator {

    private String dictionaryFilePath;

    public DictionarySeparator(String dictionaryFilePath) {
        if (!PathChecker.isValid(dictionaryFilePath)) throw new IllegalArgumentException("Illegal basic directory path");
        this.dictionaryFilePath = dictionaryFilePath;
    }

    public void byFirstLetter(String destinationDirPath) {
        Map<String, BufferedWriter> dictionaries = new HashMap<>();
        try (Scanner scanner = new Scanner(new BufferedReader(new FileReader(dictionaryFilePath)))) {
            while (scanner.hasNextLine()) {
                String word = scanner.nextLine();
                String firstLetter = word.substring(0,1);
                BufferedWriter writer = dictionaries.get(firstLetter);
                if (writer == null) {
                    writer = new BufferedWriter(new FileWriter(destinationDirPath + "\\" + firstLetter));
                    dictionaries.put(firstLetter, writer);
                }
                writer.write(word);
                writer.newLine();
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
        finally {
            closeWriters(dictionaries.values());
        }
    }

    private void closeWriters(Collection<BufferedWriter> writers) {
        for (BufferedWriter writer : writers) {
            try {
                writer.close();
            } catch (IOException e) {
                LOGGER.error(e.getMessage());
            }
        }
    }
}
