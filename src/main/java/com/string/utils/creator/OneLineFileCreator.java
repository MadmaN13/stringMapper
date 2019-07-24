package com.string.utils.creator;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

/**
 * Util class to create file with single line
 */
@Slf4j
public class OneLineFileCreator {

    /**
     * Filesystem path to dictionary files directory.
     */
    private String dictionariesDirPath;
    private int dictionariesCount;
    private Map<Integer,File> dictionaries = new HashMap<>();

    public OneLineFileCreator(String dictionariesDirPath) {
        if (!PathChecker.isValid(dictionariesDirPath)) throw new IllegalArgumentException("Illegal dictionaries directory path");
        this.dictionariesDirPath = dictionariesDirPath;
    }

    /**
     * Creates single line which contains the number of words, represented by wordsCount, separated by space symbol,
     * randomly choosing words from dictionary files, which are placed in destinationPath. Writes this line into destinationPath file.
     * @param destinationPath path to resulting file.
     * @param wordsCount number of words to generate.
     */
    public void randomLine(String destinationPath, long wordsCount) {
        Map<Integer,Scanner> dictionaryScanners = new HashMap<>();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(destinationPath))) {
            dictionaryScanners = initializeDictionaries();
            Random fileNumberGenerator = new Random();
            for (int i=0; i<wordsCount;) {
                int fileNumber = fileNumberGenerator.nextInt(dictionariesCount);
                Scanner dictionaryScanner = dictionaryScanners.get(fileNumber);
                if (dictionaryScanner != null && dictionaryScanner.hasNextLine()) {
                    String word = dictionaryScanner.nextLine();
                    writer.write(word + " ");
                    i++;
                }
                else {
                    dictionaryScanner = new Scanner(new BufferedReader(new FileReader(dictionaries.get(fileNumber))));
                    dictionaryScanners.put(fileNumber,dictionaryScanner);
                }
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
        dictionaryScanners.values().forEach(Scanner::close);
    }

    private Map<Integer,Scanner> initializeDictionaries() throws IOException {
        Map<Integer,Scanner> scanners = new HashMap<>();
        File dictionariesDir = new File(dictionariesDirPath);
        int dictionaryNumber = 0;
        if (dictionariesDir.isDirectory()) {
            for (File dictionary : dictionariesDir.listFiles()) {
                scanners.put(dictionaryNumber, new Scanner(new BufferedReader(new FileReader(dictionary))));
                dictionaries.put(dictionaryNumber, dictionary);
                dictionaryNumber++;
            }
        }
        dictionariesCount = dictionaryNumber;
        return scanners;
    }
}
