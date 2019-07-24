package com.string.utils.converter;

import com.string.utils.creator.PathChecker;
import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.*;
import java.util.stream.Collectors;

import static java.nio.channels.FileChannel.MapMode.READ_ONLY;

/**
 * General class for task requirements implementation.
 */
@Slf4j
public class StringToMapConverter {

    public static int DEFAULT_BUFFER_SIZE = 8192;
    public static String DEFAULT_CHARSET_NAME = "UTF-8";

    /**
     * Byte buffer size to read from file. Default value is defined by {@link #DEFAULT_BUFFER_SIZE} constant.
     */
    private int bufferSize = DEFAULT_BUFFER_SIZE;
    /**
     * Character encoding of input one line file. Default value is  defined by {@link #DEFAULT_CHARSET_NAME} constant.
     */
    private String charsetName = DEFAULT_CHARSET_NAME;

    /**
     * If instance of the class is created with this constructor, passed params override defaults.
     */
    public StringToMapConverter(int bufferSize, String charsetName) {
        validateArguments(bufferSize, charsetName);
        this.bufferSize = bufferSize;
        this.charsetName = charsetName;
    }

    /**
     * If instance of the class is created with this constructor, {@link #DEFAULT_BUFFER_SIZE} and {@link #DEFAULT_CHARSET_NAME} are used.
     */
    public StringToMapConverter(){};

    /**
     * Overloaded version of {@link #fromFile(String)}.
     * Used parameters depends on the constructor which was used to create the instance.
     * See {@link #StringToMapConverter(int, String)}, {@link StringToMapConverter}.
     */
    public Map<String,Set<String>> fromFile(String filePath) {
        return fromFile(filePath, bufferSize, charsetName);
    }

    /**
     * Overloaded version of {@link #fromFile(String)}.
     * Used parameters depends on the constructor which was used to create the instance.
     * Passed param overrides any pre-initialized value.
     */
    public Map<String, Set<String>> fromFile(String filePath, String charsetName) {
        return fromFile(filePath, bufferSize, charsetName);
    }

    /**
     * Transforms single line file into map according to task requirements.
     * It is considered that file contains only one line, in which words are separated by one space.
     * This method can be used to transform huge files into requested map, assuming that the resulting map is stored in memory.
     * So for huge files method must be invoked on jvm with corresponding heap size.
     * It is recommended that the heap size is at least equals to file size, otherwise method invocation can cause jvm errors.
     * Params passed into this method override default and constructor values.
     */
    public Map<String,Set<String>> fromFile(String filePath, int bufferSize, String charsetName) {
        validateArguments(bufferSize, charsetName);
        if (!PathChecker.isValid(filePath)) throw new IllegalArgumentException("Illegal one line file path");
        // According to the task text it is not necessary to store words in a set.
        // This impl is chosen because it is ordered, assuming that we do not want duplicates in out resulting set.
        // Also TreeMap is used because of the same reason - it is ordered impl.
        Map<String,Set<String>> result = new TreeMap<>();
        // this impl is chosen to process huge files
        try(FileInputStream stream = new FileInputStream(filePath); FileChannel channel = stream.getChannel()) {
            MappedByteBuffer mappedFileBuffer = channel.map(READ_ONLY, 0L, channel.size());
            byte[] buffer = new byte[bufferSize];
            int readCount;
            String splittedStart = "";
            String splittedEnd = "";
            boolean splitted = false;
            String[] words;
            String bufferString;
            while(mappedFileBuffer.hasRemaining())
            {
                // calculate number of bytes to read
                readCount = Math.min(mappedFileBuffer.remaining(), bufferSize);
                // simple case for files smaller than buffer - no splitted words will appear
                if (readCount<bufferSize) {
                    buffer = new byte[readCount];
                    mappedFileBuffer.get(buffer, 0,readCount);
                    bufferString = new String(buffer, Charset.forName(charsetName));
                    words = bufferString.split("\\s");
                }
                // case for files which does not fit into buffer
                // in this case the word may "split" into buffer end and next buffer start
                else {
                    mappedFileBuffer.get(buffer, 0, readCount);
                    bufferString = new String(buffer, Charset.forName(charsetName));
                    words = bufferString.split("\\s");
                    // assemble the splitted word and reassign it
                    if (splitted) {
                        splittedEnd = words[0];
                        words[0] = splittedStart.concat(splittedEnd);
                        splitted = false;
                    }
                    // memorize splitted part
                    if (!bufferString.endsWith(" ") && mappedFileBuffer.hasRemaining()) {
                        splittedStart = words[words.length - 1];
                        words[words.length - 1] = null;
                        splitted = true;
                    }
                }
                addElements(result, words);
            }
            // leave only mapping which contains one or more words
            // may be a small duplication in fromString() method, but not necessary to move to separate function
            result.entrySet().removeIf(byFirstLetter -> byFirstLetter.getValue().size()<2);
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
        return result;
    }

    /**
     * Transforms single line into map according to task requirements.
     * This method should be used only for small strings which can be initialized by literals in code,
     * or which size is inconsiderable to heap size.
     * This method is implemented as static method as it is stateless,
     * so it can be shared by all instances or invoked without any state, e.g. w/o class instance.
     */
    public static Map<String,Set<String>> fromString(String input) {
        // only dummy checks
        if (input == null || input.isEmpty()) throw new IllegalArgumentException("Illegal string is passed");
        Map<String, Set<String>> result = Arrays.asList(input.split("\\s")).stream()
                // check for strings like "", "   "
                .filter(word -> !word.isEmpty())
                .collect(
                        Collectors.toMap(
                                // keyMapper
                                (String word) -> word.substring(0, 1),
                                // valueMapper
                                word -> {
                                    // small downside of the impl - needs to create new temporary single value set
                                    Set<String> set = new TreeSet<>((o1, o2) -> {
                                        return o1.length() == o2.length() ? o1.compareTo(o2) : o2.length() - o1.length();
                                    });
                                    set.add(word);
                                    return set;
                                },
                                // value merger - just add temporary set into regular set
                                (oldValue, newValue) -> {oldValue.addAll(newValue); return oldValue;},
                                // map impl supplier - provides sorted keys
                                TreeMap::new
                        ));
        result.entrySet().removeIf(byFirstLetter -> byFirstLetter.getValue().size()<2);
        return result;
    }

    private void addElements(Map<String,Set<String>> result, String[] words) {
        for (String word : words) {
            if (word != null && !word.isEmpty()) {
                String firstLetter = word.substring(0, 1);
                // Check is needed when the entry, corresponding to the letter is not initialized.
                // Time complexity can be considered as constant, because it depends on the alphabet length.
                // Alternative impl may use pre-initialized map with empty sets. In this case this codeblock is ambiguous.
                if (!result.containsKey(firstLetter)) {
                    // Comparator is implemented according to the task.
                    // If words lengths are equal we delegate comparison to String class, which implementation of compareTo() meets the task requirements.
                    // Otherwise we sort descendingly by word length.
                    result.put(firstLetter, new TreeSet<>((o1, o2) -> o1.length() == o2.length() ? o1.compareTo(o2) : o2.length() - o1.length()));
                }
                // add element to set - O(log(n))
                result.get(firstLetter).add(word);
            }
        }
    }

    private void validateArguments(int bufferSize, String charsetName) {
        if (bufferSize <= 1) throw new IllegalArgumentException("Illegal value of buffer size");
        if (charsetName == null || charsetName.isEmpty()) throw new IllegalArgumentException("Illegal charset name");
    }
}
