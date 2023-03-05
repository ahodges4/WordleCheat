import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.Scanner;

public class Main {
    public static ArrayList<String> getWords(String filePath) {
        ArrayList<String> result = new ArrayList<>();

        try (FileReader f = new FileReader(filePath)) {
            StringBuffer sb = new StringBuffer();
            while (f.ready()) {
                char c = (char) f.read();
                if (c == '\n') {
                    result.add(sb.toString());
                    sb = new StringBuffer();
                } else {
                    sb.append(c);
                }
            }
            if (sb.length() > 0) {
                result.add(sb.toString());
            }
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
        return result;
    }

    public static String pickRandWord(ArrayList<String> Words) {
        Random Rand = new Random();
        int RandNum = Rand.nextInt(Words.size());
        return Words.get(RandNum);
    }

    public static ArrayList<String> filterWords(ArrayList<String> Words, ArrayList<String> knownLetters,
            ArrayList<String> Solution, ArrayList<String> invalidCharacters) {
        ArrayList<String> reducedWords = new ArrayList<>();

        int SolutionKnownAmount = 5;
        for (int i = 0; i < Solution.size(); i++) {
            if (Solution.get(i).equals(""))
                SolutionKnownAmount = SolutionKnownAmount - 1;
        }

        // Remove words containing invalid characters
        for (int i = 0; i < Words.size(); i++) {
            boolean found = false;
            for (int k = 0; k < invalidCharacters.size(); k++) {
                if (Words.get(i).contains(invalidCharacters.get(k))) {
                    found = true;
                    break; // Exit inner loop after finding the first invalid character
                }
            }
            if (found) {
                Words.remove(i);
                i--; // Decrement index to account for removed word
            }
        }

        // Remove words that don't contain all known letters
        for (int i = 0; i < Words.size(); i++) {
            int match = 0;
            String[] wordChar = Words.get(i).split("");

            for (int k = 0; k < 5; k++) {
                for (int w = 0; w < knownLetters.size(); w++) {
                    if (wordChar[k].equals(knownLetters.get(w)))
                        match++;
                }
            }
            if (match < knownLetters.size()) {
                Words.remove(i);
                i--; // Decrement index to account for removed word
            } else {
                // Check for matches with solution letters
                int solutionMatches = 0;
                for (int k = 0; k < 5; k++) {
                    if (wordChar[k].equals(Solution.get(k))) {
                        solutionMatches++;
                    }
                }
                if (solutionMatches == SolutionKnownAmount) {
                    reducedWords.add(Words.get(i));
                }
            }
        }

        return reducedWords;
    }

    public static ArrayList<Integer> generateLetterValue(ArrayList<String> Words) {
        String alphabet = "abcdefghijklmnopqrstuvwxyz";
        String[] alphabetChar = alphabet.split("");
        ArrayList<Integer> Worth = new ArrayList<>();

        for (int i = 0; i < alphabetChar.length; i++) {
            Worth.add(0);
        }

        for (String Word : Words) {
            String[] WordChar = Word.split("");
            for (int i = 0; i < WordChar.length; i++) {
                for (int k = 0; k < alphabetChar.length; k++) {
                    if (WordChar[i].equals(alphabetChar[k]))
                        Worth.set(k, Worth.get(k) + 1);
                }
            }
        }

        return Worth;
    }

    public static ArrayList<Character> generateCommonLetters(ArrayList<String> Words, ArrayList<Integer> LetterValue) {
        String alphabet = "abcdefghijklmnopqrstuvwxyz";
        ArrayList<Character> alphabetChar = new ArrayList<>();

        for (int i = 0; i < alphabet.length(); i++) {
            alphabetChar.add(alphabet.charAt(i));
        }

        int moves = 1;
        char tempStr = ' ';
        int tempInt;
        while (moves != 0) {
            moves = 0;
            for (int i = 1; i < LetterValue.size(); i++) {
                if (LetterValue.get(i) < LetterValue.get(i - 1)) {

                    tempInt = LetterValue.get(i);
                    LetterValue.remove(i);
                    LetterValue.add(i - 1, tempInt);

                    tempStr = alphabetChar.get(i);
                    alphabetChar.remove(i);
                    alphabetChar.add(i - 1, tempStr);
                    moves++;

                }
            }
        }
        return alphabetChar;
    }

    public static ArrayList<String> generateWordValue(ArrayList<String> Words, ArrayList<Character> alphabetChar) {
        ArrayList<Integer> WordValues = new ArrayList<>();

        for (String Word : Words) {
            int wordValue = 0;
            for (int i = 0; i < Word.length(); i++) {
                for (int k = 0; k < alphabetChar.size(); k++) {
                    if (Word.charAt(i) == alphabetChar.get(k))
                        wordValue = wordValue + k;
                }
            }
            WordValues.add(wordValue);
        }

        int moves = 1;
        String tempWord = "";
        int tempInt;
        while (moves != 0) {
            moves = 0;
            for (int i = 0; i < WordValues.size() - 1; i++) {
                if (WordValues.get(i) < WordValues.get(i + 1)) {

                    tempInt = WordValues.get(i);
                    WordValues.remove(i);
                    WordValues.add(i + 1, tempInt);

                    tempWord = Words.get(i);
                    Words.remove(i);
                    Words.add(i + 1, tempWord);
                    moves++;

                }
            }
        }
        return Words;
    }

    public static void main(String[] args) throws IOException {
        ArrayList<String> Words = getWords("wordle-answers-alphabetical.txt");
        ArrayList<String> knownLetters = new ArrayList<>();
        ArrayList<String> InvalidLetters = new ArrayList<>();
        ArrayList<String> Solution = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Solution.add("");
        }

        // knownLetters.add("");

        // Solution.set(,"");

        // InvalidLetters.add("");

        Scanner scanner = new Scanner(System.in);
        while (true) {
            ArrayList<String> ReducedWords = filterWords(Words, knownLetters, Solution, InvalidLetters);
            ArrayList<String> WordValues = generateWordValue(ReducedWords,
                    generateCommonLetters(ReducedWords, generateLetterValue(ReducedWords)));

            for (int i = 0; i < 10; i++) {
                try {
                    System.out.print(WordValues.get(i) + ", ");
                } catch (Exception e) {
                    continue;
                }
            }
            System.out.println(
                    "\nPlease Enter an option:\n1: Add known Letter\n2: Add Solution Letter\n3: Add Invalid Letter\n4: Add \"Eater\" to invalid");
            String userInput = scanner.nextLine();
            int userInputInt = 0;
            try {
                userInputInt = Integer.parseInt(userInput);
            } catch (Exception e) {
                System.out.println("Invalid Input \n");
                continue;
            }

            if (userInputInt == 1) {
                System.out.println("Please enter a letter to add to the know Letters");
                String[] list = scanner.nextLine().split("");
                for (String letter : list) {
                    knownLetters.add(letter);
                }
            }
            if (userInputInt == 2) {
                System.out.println("Please enter a letter to add to the know solution");
                String Letter = scanner.nextLine();
                System.out.println("Please enter the position of the letter");
                int position = Integer.parseInt(scanner.nextLine());
                Solution.set(position - 1, Letter);
            }
            if (userInputInt == 3) {
                System.out.println("Please enter a letter to add to the invalid Letters");
                String[] list = scanner.nextLine().split("");
                for (String letter : list) {
                    InvalidLetters.add(letter);
                }
            }
            if (userInputInt == 4) {
                InvalidLetters.add("e");
                InvalidLetters.add("a");
                InvalidLetters.add("t");
                InvalidLetters.add("r");
            }
            System.out.println("Current Known Letters: " + knownLetters);
            System.out.println("Current Known Solution: " + Solution);
            System.out.println("Current Invalid Letters: " + InvalidLetters);

        }
    }
}