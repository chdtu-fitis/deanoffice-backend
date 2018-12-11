package ua.edu.chdtu.deanoffice.util;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

public class PersonUtil {

    public static String toCapitalizedCase(String string) {
        if (string == null || string.isEmpty()) {
            return "";
        }
        return wordsToCapitalizedCase(string);
    }

    private static String wordsToCapitalizedCase(String string) {
        List<String> words = asList(string.split("\\s+"));
        return words.stream()
                .filter(s -> !s.isEmpty())
                .map(PersonUtil::wordToCapitalizedCase)
                .collect(Collectors.joining(" "));
    }

    private static String wordToCapitalizedCase(String string) {
        return string.substring(0, 1).toUpperCase() + string.substring(1).toLowerCase();
    }

    public static String makeInitialsSurnameLast(String fullName) {
        if (fullName == null)
            return "";
        List<String> fullNameParts = Arrays.asList(fullName.split(" "));
        if (fullNameParts.size() < 3) {
            if (fullNameParts.size() > 0)
                return fullNameParts.get(0);
            else
                return "";
        }
        return fullNameParts.get(1).substring(0, 1).toUpperCase() + "."
                + fullNameParts.get(2).substring(0, 1).toUpperCase() + ". "
                + fullNameParts.get(0);
    }

    /**
     * Даний метод оброблює повне імя за такими правилами:
     * 1 - починає всі частини ім'я з великої літери;
     * 2 - замінює апострофи на правильний символ;
     * 3 - шукає подвійну частину ім'я (де слова розділені дефісом) та починає другу частину з великої літери;
     * 4 - деякі частини по-батькові іноземних студентів робить маленькими буквами
     **/
    public static String correctCaseInName(String name){
        String processedFullString = "";
        if(name != null && !name.isEmpty()){
            String[] nameParts = name.split(" +");
            for (String namePart: nameParts){
                String partInLower = namePart.toLowerCase();
                if (partInLower.equals("оглу") || partInLower.equals("огли") || partInLower.equals("кизи")){
                    processedFullString += partInLower + " ";
                    continue;
                }
                String wordOnProcessing = StringUtil.replaceSingleQuotes(namePart);
                wordOnProcessing = wordOnProcessing.substring(0, 1).toUpperCase() + wordOnProcessing.substring(1).toLowerCase();
                processedFullString += processDashInWord(wordOnProcessing) + " ";
            }
        }
        return processedFullString.trim();
    }

    private static String processDashInWord(String word){
        String processedWord = "";
        if (word.indexOf("-") == -1){
            return word;
        }
        processedWord += word.substring(0, word.indexOf("-") + 1)
                + word.substring(word.indexOf("-") + 1, word.indexOf("-") + 2).toUpperCase()
                + word.substring(word.indexOf("-") + 2);
        return processedWord;
    }
}
