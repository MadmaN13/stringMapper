package com.string.utils.converter;

import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static com.string.utils.converter.StringToMapConverter.fromString;
import static org.junit.Assert.*;

/**
 * Only basic functional tests are implemented here.
 * Only functionality is checked, not performance.
 */
public class StringToMapConverterTest_FromFile {

    private static final String FILE_PATH = "C:\\Users\\NM.Rabotaev\\IdeaProjects\\ToMapConverter\\src\\test\\resources\\testFile.txt";
    private static final String HUGE_FILE_PATH = "C:\\Users\\NM.Rabotaev\\Documents\\exams\\stringMapper\\input\\randomLine.txt";
    private static final int SMALL_BUFFER = 8;
    private Map<String, Set<String>> sorted;
    private StringToMapConverter converter = new StringToMapConverter();

    private final static String WIN1251 = "Windows-1251";

    @Test(expected = IllegalArgumentException.class)
    public void emptyFilePath() {
        converter.fromFile("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullFilePath() {
        converter.fromFile(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullCharsetName() {
        converter = new StringToMapConverter(8,null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void emptyCharsetName() {
        converter = new StringToMapConverter(8,"");
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidBufferSize() {
        converter = new StringToMapConverter(0, WIN1251);
    }

    /**
     * Sample with reading line from file and using fromString().
     * Will cause in OutOfMemory error for huge files if heap size in not set to corresponding value.
     * Not effective for huge files.
     */
    @Test
    public void readFromFile() throws IOException {
        converter = new StringToMapConverter();
        Files.lines(Paths.get(FILE_PATH), Charset.forName(WIN1251)).forEach(line -> {
            sorted = fromString(line);
            assertSorted();
            System.out.println(sorted);
        });
    }

    @Test
    public void unorderedLineWithBufferGreaterThanFile() {
        converter = new StringToMapConverter();
        sorted = converter.fromFile(FILE_PATH, WIN1251);
        assertSorted();
        System.out.println(sorted);
    }

    @Test
    public void unorderedLineWithBufferSmallerThanFile() {
        converter = new StringToMapConverter();
        sorted = converter.fromFile(FILE_PATH, SMALL_BUFFER, WIN1251);
        assertSorted();
        System.out.println(sorted);
    }

    private void assertSorted() {
        // check single element set removal
        Set<String> wordsByA = sorted.get("a");
        assertNull(wordsByA);

        Iterator<Map.Entry<String, Set<String>>> firstLetterIterator = sorted.entrySet().iterator();
        assertEquals("б", firstLetterIterator.next().getKey());
        assertEquals("п", firstLetterIterator.next().getKey());
        assertEquals("с", firstLetterIterator.next().getKey());
        assertEquals("т", firstLetterIterator.next().getKey());

        // check words starting with "б"
        Set<String> wordsByB = sorted.get("б");
        assertNotNull(wordsByB);
        assertEquals(2, wordsByB.size());

        Iterator<String> iteratorByB = wordsByB.iterator();
        assertEquals("болты", iteratorByB.next());
        assertEquals("болт", iteratorByB.next());

        // check words starting with "п"
        Set<String> wordsByP = sorted.get("п");
        assertNotNull(wordsByP);
        assertEquals(3, wordsByP.size());

        Iterator<String> iteratorByP = wordsByP.iterator();
        assertEquals("привет", iteratorByP.next());
        assertEquals("прилет", iteratorByP.next());
        assertEquals("пока", iteratorByP.next());

        // check words starting with "с"
        Set<String> wordsByC = sorted.get("с");
        assertNotNull(wordsByC);
        assertEquals(6, wordsByC.size());

        Iterator<String> iteratorByC = wordsByC.iterator();
        assertEquals("сапоги", iteratorByC.next());
        assertEquals("строка", iteratorByC.next());
        assertEquals("сапог", iteratorByC.next());
        assertEquals("сараи", iteratorByC.next());
        assertEquals("сарай", iteratorByC.next());
        assertEquals("слово", iteratorByC.next());

        // check words starting with "т"
        Set<String> wordsByT = sorted.get("т");
        assertNotNull(wordsByT);
        assertEquals(2, wordsByT.size());

        Iterator<String> iteratorByT = wordsByT.iterator();
        assertEquals("только", iteratorByT.next());
        assertEquals("тест", iteratorByT.next());
    }

    /**
     * Sample for performance test.
     */
    @Test
    @Ignore
    public void processHugeFile() {
        sorted = converter.fromFile(HUGE_FILE_PATH);
    }
}