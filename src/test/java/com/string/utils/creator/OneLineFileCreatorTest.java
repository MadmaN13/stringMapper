package com.string.utils.creator;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

public class OneLineFileCreatorTest {

    private static final String DICTIONARIES_DIR = "C:\\Users\\NM.Rabotaev\\Documents\\exams\\stringMapper\\input\\byFirstLetter";
    private static final String DESTINATION = "C:\\Users\\NM.Rabotaev\\Documents\\exams\\stringMapper\\input\\randomLine.txt";
    private static final long WORDS_COUNT = 50_000;

    private static OneLineFileCreator creator;

    @BeforeClass
    public static void setUp() {
        creator = new OneLineFileCreator(DICTIONARIES_DIR);
    }

    @Test
    public void randomLine() {
        creator.randomLine(DESTINATION, WORDS_COUNT);
    }
}