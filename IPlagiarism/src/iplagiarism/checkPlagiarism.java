package iplagiarism;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import javax.swing.SwingWorker;
import org.apache.commons.io.FileUtils;

public class checkPlagiarism extends SwingWorker<Void, String> {

    double total_number_of_words;
    double number_of_words_matched = 0;
    String filePath0;
    String filePath1;
    String randomNum;
    String s3file1;
    String[] words;
    ArrayList<String> s2file1;
    ArrayList<String> s2file2;
    ArrayList<String> s1File1;
    ArrayList<String> s1File2;
    HashMap<String, ArrayList<String>> synonyms = null;
    HashMap<String, ArrayList<String>> s4file1 = null;
    ArrayList<String> synonymList = null;
    String[] common = {"a", "are", "an", "the", "has", "it", "on", "and",
        "of", "for", "then", "than", "upto", "be", "is", "i", "to", "and",
        "in", "that", "have", "not", "on", "with", "he", "she", "as", "you",
        "do", "at", "this", "but", "his", "by", "from", "they", "we", "say",
        "her", "him", "or", "will", "my", "all", "would", "could", "there",
        "their", "what", "when", "why", "who", "how", "so", "up", "down",
        "if", "out", "in", "about", "get", "which", "go", "me", "make",
        "can", "like", "know", "time", "knew", "just", "put", "take", "took",
        "into", "your", "some", "them", "see", "other", "now", "only", "come",
        "its", "it's", "over", "also", "back", "after", "our", "well", "way",
        "even", "new", "want", "because", "any", "these", "those", "day",
        "most", "us"};

    checkPlagiarism(String filePath0, String filePath1) {
        this.total_number_of_words = 0;
        this.filePath0 = filePath0;
        this.filePath1 = filePath1;
    }

    @Override
    protected Void doInBackground() throws Exception {
        String f1 = readFile(filePath0);
        String f2 = readFile(filePath1);

        total_number_of_words = countTotalWords(f2);
        System.out.println("total : " + total_number_of_words);

        s1File1 = splitLines(f1);
        s2file1 = extractMainWords(s1File1);
        s3file1 = singleString(s2file1);
        s4file1 = getAllSynonyms(s3file1);
        KMPMatcher matcher = new KMPMatcher();
        for (String wordList : words) {
            number_of_words_matched += matcher.KMPSearch(wordList, f2);
            synonymList = synonyms.get(wordList);
            for (String word : synonymList) {
                number_of_words_matched += matcher.KMPSearch(word, f2);
            }
        }
        System.out.println("found : " + number_of_words_matched);
        System.out.println("Percentage plagiarised : "
                + Double.parseDouble(new DecimalFormat("##.##").format((number_of_words_matched / total_number_of_words) * 100)));
        return null;
    }

    private String readFile(String path) throws IOException {
        String contents;
        File file = new File(path);
        contents = FileUtils.readFileToString(file, "UTF-8");
        return contents;
    }

    private ArrayList<String> splitLines(String contents) {
        contents = replaceRegex(contents);
        String[] lines = contents.split(randomNum);
        ArrayList<String> lineList = new ArrayList<>(Arrays.asList(lines));
        return lineList;
    }

    private String replaceRegex(String contents) {
        String result;
        int rand = 3000 + (int) (Math.random() * 6000);
        this.randomNum = " " + String.valueOf(rand) + " ";
        result = contents.replace(".", randomNum).replace("?", randomNum).replace("!", randomNum);
        return result;
    }

    private ArrayList<String> extractMainWords(ArrayList<String> file) {
        List<String> commonWords = new ArrayList<>(Arrays.asList(common));
        ListIterator<String> iterator = file.listIterator();
        while (iterator.hasNext()) {
            iterator.set(iterator.next()
                    .replaceAll("[^0-9][.,]|[.,][^0-9]|(?![.,])\\p{Punct}", "")
                    .replace("\n", "")
                    .replace("\r", "")
                    .replaceAll("\\s+", " ")
                    .toLowerCase());
        }
        for (int k = 0; k < file.size(); k++) {
            String[] words = file.get(k).split(" ");
            ArrayList<String> wordsList = new ArrayList<>(Arrays.asList(words));
            for (int i = 0; i < wordsList.size(); i++) {
                for (int j = 0; j < common.length; j++) {
                    if (wordsList.contains(common[j])) {
                        wordsList.remove(common[j]);
                    }
                }
            }
            StringBuilder sb = new StringBuilder();
            for (String s : wordsList) {
                sb.append(s);
                sb.append(" ");
            }
            file.set(k, sb.toString());
        }
        return file;
    }

    private String singleString(ArrayList<String> list) {
        StringBuilder sb = new StringBuilder();
        for (String s : list) {
            sb.append(s);
            sb.append(" ");
        }
        String result = sb.toString();
        result = result.replaceAll("\\s+", " ");
        return result;
    }

    private HashMap<String, ArrayList<String>> getAllSynonyms(String mainWords) {
        words = mainWords.replace("\\s+", "").split(" ");
        Thesaurus thesaurus = new Thesaurus(words);
        try {
            synonyms = thesaurus.getSynonyms();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return synonyms;
    }

    private int countTotalWords(String str) {
        int cnt = 0;
        String trimmed = str.trim();
        cnt = trimmed.isEmpty() ? 0 : trimmed.split("\\s+").length;
        return cnt;
    }
}
