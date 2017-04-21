package iplagiarism;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import javax.swing.SwingWorker;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ArrayUtils;

public class checkPlagiarism extends SwingWorker<Void, String> {

    String randomNum;
    String dirPath;
    String s3file1;
    String s4file1;
    String[] listOfPaths;
    ArrayList<String> s2file1;
    ArrayList<String> s2file2;
    ArrayList<String> s1file1;
    ArrayList<String> s1file2;
    HashMap<String, ArrayList<String>> s5file1 = null;
    ArrayList<String> synonymList = null;
    String[] common = {"a", "are", "an", "am", "the", "has", "it", "on", "and",
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
        "most", "us", "hello", "day", "night", "afternoon"};

    checkPlagiarism(String path) {
        this.dirPath = path;
    }

    @Override
    protected Void doInBackground() throws Exception {
        File dir = new File(dirPath);
        if (dir.isDirectory()) {
            File[] files1 = dir.listFiles(new FilenameFilter() {
                public boolean accept(File dir, String filename) {
                    return filename.endsWith(".txt");
                }
            });
            File[] files2 = dir.listFiles(new FilenameFilter() {
                public boolean accept(File dir, String filename) {
                    return filename.endsWith(".docx");
                }
            });
            File[] files = (File[]) ArrayUtils.addAll(files1, files2);
            publish("Retrieving all Files from directory " + dirPath);
            this.listOfPaths = Arrays.stream(files).map(File::getAbsolutePath)
                    .toArray(String[]::new);

            for (String names : listOfPaths) {
                publish(getFileName(names));
            }

            for (int i = 0; i < this.listOfPaths.length; i++) {
                for (int j = i + 1; j < this.listOfPaths.length; j++) {
                    check(this.listOfPaths[i], this.listOfPaths[j]);
                }
            }
        }
        return null;
    }

    private void check(String filePath0, String filePath1) throws IOException, Exception {
        double total_number_of_words = 0;
        double number_of_words_matched = 0;

        String f1 = readFile(filePath0);
        String f2 = readFile(filePath1);

        s1file1 = splitLines(f1);
        s1file2 = splitLines(f2);
        s2file1 = extractMainWords(s1file1);
        //s2file2 = extractMainWords(s1file2);
        s3file1 = singleString(s2file1);
        String s3file2 = singleString(s1file2);
        s4file1 = removeDuplicates(s3file1);
        s5file1 = getAllSynonyms(s4file1);//s5file1 has all synonyms.

        //finalString has all keys of synonyms.
        Set<String> key = s5file1.keySet();
        String[] keyWord = key.toArray(new String[key.size()]);
        List<String> finalString = new ArrayList<>(Arrays.asList(keyWord));

        // counting total words from 2nd file..
        total_number_of_words = countTotalWords(s3file2);
        System.out.println("total words of 2nd file : " + (total_number_of_words));

        //now finalString contains all main words to be match.
        System.out.println("final string of 1st file to be Match with it's synonyms: " + finalString);
        for (String wordFromList : finalString) {
            KMPMatcher matcher = new KMPMatcher();
            synonymList = s5file1.get(wordFromList);
            for (String word : synonymList) {
                KMPMatcher matcher1 = new KMPMatcher();
                number_of_words_matched += matcher1.KMPSearch(word, s3file2);
            }
        }
        ArrayList<String> mainWords = new ArrayList<>(Arrays.asList(s4file1.replace("\\s+", "").split(" ")));
        for (String mainWord : mainWords) {
            KMPMatcher matcher = new KMPMatcher();
            number_of_words_matched += matcher.KMPSearch(mainWord, s3file2);
        }
        System.out.println("found matches considering it's synonyms : " + number_of_words_matched);
        String print = getFileName(filePath0) + " -> " + getFileName(filePath1)
                + " : " + Double.parseDouble(new DecimalFormat("##.##").format(((number_of_words_matched) / (total_number_of_words)) * 100))
                + "%";
        System.out.println(print);
        publish(print);

    }

    private String getFileName(String path) {
        int index = path.lastIndexOf("\\");
        String fileName = path.substring(index + 1);
        return fileName;
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
                    .replaceAll("[^0-9][.]|[.][^0-9]|(?![.,])\\p{Punct}|\\,", "")
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

    private HashMap<String, ArrayList<String>> getAllSynonyms(String mainWords) throws Exception {
        HashMap<String, ArrayList<String>> synonyms = null;
        String[] words;
        words = mainWords.replace("\\s+", "").split(" ");
        Thesaurus thesaurus = new Thesaurus(words);
        try {
            synonyms = thesaurus.getSynonyms();
        } catch (IOException e) {
        }
        return synonyms;
    }

    private int countTotalWords(String str) {
        int cnt = 0;
        String trimmed = str.trim();
        cnt = trimmed.isEmpty() ? 0 : trimmed.split("\\s+").length;
        return cnt;
    }

    private String removeDuplicates(String file) {
        ArrayList<String> al = new ArrayList<>(Arrays.asList(file.split(" ")));
        Set<String> hs = new HashSet<>();
        hs.addAll(al);
        al.clear();
        al.addAll(hs);
        String result = singleString(al);
        return result;
    }

    @Override
    protected void process(List<String> chunks) {
        for (final String string : chunks) {
            GUI.processArea.append(string);
            GUI.processArea.append("\n");
        }
    }
}
