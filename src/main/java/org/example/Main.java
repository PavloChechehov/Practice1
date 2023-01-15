package org.example;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        String word = "so man" +
                "y work" +
                "s need" +
                " to re" +
                "member";

        String columnSecretKey = "crypto";
        String rowSecretKey = "encry";
        String encryptedWord = encrypt(word, columnSecretKey, rowSecretKey);
        String decryptedWord = decrypt(encryptedWord, columnSecretKey, rowSecretKey);
        System.out.println("decryptedWord = " + decryptedWord);
    }


    private static String encrypt(String word, String columnKey, String rowKey) {
        // 1. create arr 2 dimensions
        // 2. save words
        // 3. create the new encryptedWord word

        /*
          c r y p t o
        c h e l l o _
        o w o r l d !
        d
        e
        * */

        int columnLength = columnKey.length();
        int rowLength = word.length() / columnLength + 1;

        String[][] arr = new String[rowLength][columnLength];

        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < columnLength; j++) {
                int currentPosition = i * columnLength + j;
                if (currentPosition < word.length()) {
                    String letter = String.valueOf(word.charAt(currentPosition));
                    arr[i][j] = letter;
                } else {
                    break;
                }
            }
        }

        Map<String, List<String>> columnMap = new TreeMap<>();
        for (int i = 0; i < columnLength; i++) {
            List<String> columnValuesList = new ArrayList<>();

            String letter = String.valueOf(columnKey.charAt(i));
            for (String[] columnValues : arr) {
                String columnValue = columnValues[i];
                columnValuesList.add(columnValue);
            }
            columnMap.put(letter, columnValuesList);
        }


        TreeMap<String, List<String>> rowMap = new TreeMap<>();
        // c -> h w
        // r -> e o
        // y -> l r
        // ..

        for (Map.Entry<String, List<String>> entry : columnMap.entrySet()) {

            for (int i = 0; i < rowKey.length(); i++) {

                String rowKeyLetter = String.valueOf(rowKey.charAt(i));
                List<String> columnValues = entry.getValue();

                if (i >= columnValues.size()) {
                    break;
                }

                String letter = columnValues.get(i);

                rowMap.computeIfAbsent(rowKeyLetter, s -> new ArrayList<>());
                rowMap.computeIfPresent(rowKeyLetter, (s, list) -> {
                    list.add(letter);
                    return list;
                });
            }
        }


        StringBuilder encryptedWord = new StringBuilder();
        for (Map.Entry<String, List<String>> entry : rowMap.entrySet()) {
            List<String> values = entry.getValue();
            values.removeIf(Objects::isNull);
            encryptedWord.append(String.join("", values));
        }


        return encryptedWord.toString();
    }

    private static String decrypt(String word, String columnKey, String rowKey) {
        int columnLength = columnKey.length();
        int rowLength = word.length() / columnLength;

        String[][] arr = new String[rowLength][columnLength];

        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < columnLength; j++) {
                int currentPosition = i * columnLength + j;
                if (currentPosition < word.length()) {
                    String letter = String.valueOf(word.charAt(currentPosition));
                    arr[i][j] = letter;
                } else {
                    break;
                }
            }
        }

        char[] rowKeyArr = rowKey.toCharArray();
        Arrays.sort(rowKeyArr);

        Map<String, List<String>> rowMap = new TreeMap<>();

        for (int i = 0; i < rowKeyArr.length; i++) {
            char c = rowKeyArr[i];
            rowMap.put(String.valueOf(c), Arrays.asList(arr[i]));
        }

        Map<Integer, List<String>> decryptRowMap = new TreeMap<>();
        for (int i = 0; i < rowLength; i++) {
            String rowLetter = String.valueOf(rowKey.charAt(i));
            decryptRowMap.put(i, rowMap.get(rowLetter));
        }

        System.out.println(Arrays.toString(rowKeyArr));

        char[] columnKeyArr = columnKey.toCharArray();
        Arrays.sort(columnKeyArr);

        Map<String, List<String>> columnMap = new TreeMap<>();

        for (int i = 0; i < columnKey.length(); i++) {
            String colLetter = String.valueOf(columnKeyArr[i]);

            for (Map.Entry<Integer, List<String>> entry : decryptRowMap.entrySet()) {
                List<String> rowValues = entry.getValue();
                String rowValue = rowValues.get(i);
                columnMap.putIfAbsent(colLetter, new ArrayList<>());
                columnMap.computeIfPresent(colLetter, (s, list) -> {
                    list.add(rowValue);
                    return list;
                });
            }

        }
        Map<Integer, List<String>> decryptColumnMap = new LinkedHashMap<>();

        for (int i = 0; i < columnLength; i++) {
            String columnLetter = String.valueOf(columnKey.charAt(i));
            decryptColumnMap.put(i, columnMap.get(columnLetter));
        }

        StringBuilder decryptWord = new StringBuilder();

        for (int i = 0; i < rowKey.length(); i++) {

            for (Map.Entry<Integer, List<String>> entry : decryptColumnMap.entrySet()) {

                String letter = entry.getValue().get(i);

                decryptWord.append(letter != null ? letter : " ");

            }
        }

        System.out.println("decryptWord = " + decryptWord);

        return decryptWord.toString();
    }
}
