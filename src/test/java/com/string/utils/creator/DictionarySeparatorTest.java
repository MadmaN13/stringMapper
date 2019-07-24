package com.string.utils.creator;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

public class DictionarySeparatorTest {

    private final static String DICTIONARY = "C:\\Users\\NM.Rabotaev\\Documents\\exams\\stringMapper\\input\\uniqueRussianWords.txt";
    private final static String DESTINATION_DIR = "C:\\Users\\NM.Rabotaev\\Documents\\exams\\stringMapper\\input\\byFirstLetter";

    private static DictionarySeparator separator;

    @BeforeClass
    public static void setUp() {
        separator = new DictionarySeparator(DICTIONARY);
    }

    @Test
    public void byFirstLetter() throws Exception {
        separator.byFirstLetter(DESTINATION_DIR);
    }

}