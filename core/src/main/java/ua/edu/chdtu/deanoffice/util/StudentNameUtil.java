package ua.edu.chdtu.deanoffice.util;

public class StudentNameUtil {
    public static String putNameInCorrectForm(String nameString){
        String processedFullString = "";
        if(nameString != null && nameString != ""){
            String[] nameParts = nameString.split(" ", 10);
            for (String namePart: nameParts){
                if (namePart.equals("оглу") || namePart.equals("огли") || namePart.equals("кизи")){
                    processedFullString += namePart + " ";
                    continue;
                }
                String wordOnProcessing = StringUtil.replaceSingleQuotes(namePart);
                wordOnProcessing = wordOnProcessing.substring(0, 1).toUpperCase() + wordOnProcessing.substring(1).toLowerCase();
                processedFullString += findDashInWord(wordOnProcessing) + " ";
            }
        }
        String processedFullStringByTrim = processedFullString.trim();
        return processedFullStringByTrim;
    }

    private static String findDashInWord(String word){
        String processedWord = "";
        for (int i = 0; i < word.toCharArray().length; i++){
            if(Character.toString(word.toCharArray()[i]).equals("-")){
                processedWord += word.toCharArray()[i] + Character.toString(word.toCharArray()[i + 1]).toUpperCase();
                i++;
            } else {
                processedWord += word.toCharArray()[i];
            }
        }
        return processedWord;
    }
}
