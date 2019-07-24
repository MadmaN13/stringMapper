package com.string.utils.converter;

import org.junit.Test;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static com.string.utils.converter.StringToMapConverter.fromString;
import static org.junit.Assert.*;

/**
 * Only basic functional tests are implemented here.
 * Test are implemented in procedure style, with hardcoded constants, code duplications, etc.
 * The main goal is to simply check functionality without any time consuming tests.
 */
public class StringToMapConverterTest_FromString {

    private String input;
    private Map<String, Set<String>> sorted;

    @Test(expected = IllegalArgumentException.class)
    public void nullString() {
        input = null;
        fromString(input);
    }

    @Test(expected = IllegalArgumentException.class)
    public void emptyString() throws Exception {
        input = "";
        sorted = fromString(input);
    }

    @Test
    public void spacesString() throws Exception {
        // single space
        input = " ";
        sorted = fromString(input);
        assertEquals(0, sorted.size());
        // multiple spaces
        input = "      ";
        sorted = fromString(input);
        assertEquals(0, sorted.size());
    }

    @Test
    public void singleWordDuplication() throws Exception {
        input = "слово слово слово слово ";
        sorted = fromString(input);
        assertEquals(0, sorted.size());
    }

    @Test
    public void multipleWordDuplication() throws Exception {
        input = "слово слова тест привет пока слова слово";
        sorted = fromString(input);

        assertEquals(2, sorted.size());
        Set<String> wordsByC = sorted.get("с");
        assertNotNull(wordsByC);
        // assert that duplication are removed
        assertEquals(2, wordsByC.size());
        assertTrue(wordsByC.containsAll(Arrays.asList("слово", "слова")));
    }

    @Test
    public void noWordDuplication() throws Exception {
        input = "слово слова тест привет пока";
        sorted = fromString(input);

        Set<String> wordsByT = sorted.get("т");
        assertNull(wordsByT);

        assertEquals(2, sorted.size());
        Set<String> wordsByC = sorted.get("с");
        assertNotNull(wordsByC);
        assertTrue(wordsByC.containsAll(Arrays.asList("слово", "слова")));

        Set<String> wordsByP = sorted.get("п");
        assertNotNull(wordsByP);
        assertTrue(wordsByP.containsAll(Arrays.asList("привет", "пока")));
    }

    @Test
    public void ordered() throws Exception {
        input = "арбуз биржа бокс болт сапог сарай";
        sorted = fromString(input);
        assertSorted();
    }

    @Test
    public void unordered() throws Exception {
        input = "сапог сарай арбуз болт бокс биржа";
        sorted = fromString(input);
        assertSorted();
    }

    private void assertSorted() {
        // check single element set removal
        Set<String> wordsByA = sorted.get("a");
        assertNull(wordsByA);

        // check keys ordering
        Iterator<Map.Entry<String, Set<String>>> firstLetterIterator = sorted.entrySet().iterator();
        assertEquals("б", firstLetterIterator.next().getKey());
        assertEquals("с", firstLetterIterator.next().getKey());


        Set<String> wordsByB = sorted.get("б");
        assertNotNull(wordsByB);
        assertEquals(3, wordsByB.size());

        // check words ordering
        Iterator<String> iteratorByB = wordsByB.iterator();
        assertEquals("биржа", iteratorByB.next());
        assertEquals("бокс", iteratorByB.next());
        assertEquals("болт", iteratorByB.next());

        Set<String> wordsByC = sorted.get("с");
        assertNotNull(wordsByC);
        assertEquals(2, wordsByC.size());

        // check words ordering
        Iterator<String> iteratorByC = wordsByC.iterator();
        assertEquals("сапог", iteratorByC.next());
        assertEquals("сарай", iteratorByC.next());
    }
}