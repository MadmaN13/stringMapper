# stringMapper

Brief overview of com.string.utils package:
1. General implementation is in class com.string.utils.converter.StringToMapConverter.
2. com.string.utils.creator contains helper classes, which are script style implementations, style and design requirements were not applied to them.

Brief overview of tests:
1. com.string.utils.converter.StringToMapConverterTest_FromFile contains tests for file processing algorithm.
2. com.string.utils.converter.StringToMapConverterTest_FromString contains tests for string processing algorithm.
3. test/java/resources/testFile.txt contains example Windows-1251 encoded line for tests. 
Important: While loading tests please do make sure that file encoding and converter encoding correspond each other.

Also I can provide dictionary files on your request.
These files represent sorted by first letter morphological forms of russian words.
