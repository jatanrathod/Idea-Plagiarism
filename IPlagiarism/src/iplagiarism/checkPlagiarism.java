package iplagiarism;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;

import java.text.DecimalFormat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ArrayUtils;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

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
    String[] common = null;

    checkPlagiarism(String path) {
        this.dirPath = path;
    }

    private static void failIfInterrupted() throws InterruptedException {
        if (Thread.currentThread().isInterrupted()) {
            throw new InterruptedException("Interrupted while searching files");
        }
    }

    @Override
    protected Void doInBackground() throws Exception {
        failIfInterrupted();
        File dir = new File(dirPath);
        if (dir.isDirectory()) {
            File[] files1 = dir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String filename) {
                    return filename.endsWith(".txt");
                }
            });
            File[] files2 = dir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String filename) {
                    return filename.endsWith(".doc");
                }
            });
            File[] files3 = dir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String filename) {
                    return filename.endsWith(".docx");
                }
            });
            File[] files4 = dir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String filename) {
                    return filename.endsWith(".pdf");
                }
            });
            File[] files12 = (File[]) ArrayUtils.addAll(files1, files2);
            File[] files123 = (File[]) ArrayUtils.addAll(files12, files3);
            File[] files1234 = (File[]) ArrayUtils.addAll(files123, files4);
            publish("Retrieving all Files from directory " + dirPath);
            this.listOfPaths = Arrays.stream(files1234).map(File::getAbsolutePath)
                    .toArray(String[]::new);

            List<String> tList = new ArrayList<>(Arrays.asList(listOfPaths));
            Collections.sort(tList);
            listOfPaths = tList.toArray(new String[tList.size()]);

            for (String names : listOfPaths) {
                publish(getFileName(names));
            }

            if (listOfPaths.length < 2) {
                JOptionPane.showMessageDialog(null, "Please Enter atleast 2 files to compare.");
            }

            int n = listOfPaths.length;
            int x = 0;
            int cnt = 0;
            String progress = null;
            for (int i = 0; i < this.listOfPaths.length; i++) {
                for (int j = i + 1; j < this.listOfPaths.length; j++) {
                    check(this.listOfPaths[i], this.listOfPaths[j]);
                    cnt++;
                    x = (cnt * 100) / (n * (n - 1) / 2);
                    progress = Integer.toString(x);
                    publish(progress);
                }
            }
        }
        return null;
    }

    private void check(String filePath0, String filePath1) throws IOException, Exception {
        FileHandler handler = new FileHandler("plagiarism_logs.log", true);
        Logger logger = Logger.getLogger("iplagiarism");
        logger.addHandler(handler);

        getStopWords();

        double total_number_of_words = 0;
        double number_of_words_matched = 0;

        String f1 = readFile(filePath0);
        String f2 = readFile(filePath1);

        s1file1 = splitLines(f1);
        s1file2 = splitLines(f2);
        s2file1 = extractMainWords(s1file1);
        s2file2 = extractMainWords(s1file2);

        if (equalLists(s2file1, s2file2)) {
            String print = getFileName(filePath0) + " --> " + getFileName(filePath1)
                    + " : " + Double.parseDouble(new DecimalFormat("##.##").format(100))
                    + "%";
            System.out.println(print);
            System.out.println();
            logger.info(print);
            publish(print);
            return;
        }

        s3file1 = singleString(s2file1);
        String s3file2 = singleString(s2file2);
        s4file1 = removeDuplicates(s3file1);
        s5file1 = getAllSynonyms(s4file1);//s5file1 has all synonyms.

        //finalString has all keys of synonyms.
        Set<String> key = s5file1.keySet();
        String[] keyWord = key.toArray(new String[key.size()]);
        List<String> finalString = new ArrayList<>(Arrays.asList(keyWord));

        /* ******************** Search ************************  */
        //now finalString contains all main words to be match.
        KMPMatcher matcher = new KMPMatcher();
        for (String wordFromList : finalString) {
            synonymList = s5file1.get(wordFromList);
            for (String word : synonymList) {
                matcher.KMPSearch(word, s3file2);
            }
        }
        ArrayList<String> mainWords = new ArrayList<>(Arrays.asList(s3file1.replace("\\s+", "").split(" ")));
        for (String mainWord : mainWords) {
            matcher.KMPSearch(mainWord, s3file2);
        }

        /* ************************ Calculate **************************** */
        // counting total words from 2nd file..
        total_number_of_words = countTotalWords(s3file2);
        System.out.println("total words of 2nd file : " + (total_number_of_words));

        System.out.println("we used synonyms of : " + finalString);

        String result = singleString((ArrayList<String>) matcher.matchedWords);
        String filteredResult = removeDuplicates(result);
        System.out.println("Words Matched With First File are : " + filteredResult);

        number_of_words_matched = countTotalWords(filteredResult);
        System.out.println("found matches considering it's synonyms : " + number_of_words_matched);

        /* ***************** Display Result ********************** */
        String print = getFileName(filePath0) + " --> " + getFileName(filePath1)
                + " : " + Double.parseDouble(new DecimalFormat("##.##").format(((number_of_words_matched) / (total_number_of_words)) * 100))
                + "%";
        System.out.println(print);
        System.out.println();
        logger.info(print);
        publish(print);

    }

    private void getStopWords() throws IOException {
        String fileWords = readFile("stopwords.txt");
        this.common = fileWords.split("\\r?\\n");
    }

    public boolean equalLists(List<String> one, List<String> two) {
        if (one == null && two == null) {
            return true;
        }

        if ((one == null && two != null)
                || one != null && two == null
                || one.size() != two.size()) {
            return false;
        }
        one = new ArrayList<>(one);
        two = new ArrayList<>(two);

        Collections.sort(one);
        Collections.sort(two);
        return one.equals(two);
    }

    private String getFileName(String path) {
        int index = path.lastIndexOf("\\");
        String fileName = path.substring(index + 1);
        return fileName;
    }

    private String readFile(String path) throws IOException {
        String contents = null;
        if (path.endsWith(".txt")) {
            File file = new File(path);
            contents = FileUtils.readFileToString(file, "UTF-8");
            return contents;
        } else if (path.endsWith(".doc")) {
            contents = readDoc(path);
            return contents;
        } else if (path.endsWith(".docx")) {
            contents = readDocx(path);
            return contents;
        } else if (path.endsWith(".pdf")) {
            contents = readPdf(path);
            return contents;
        } else {
            publish(path + " is not Supported. ");
            return contents;
        }
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

    private ArrayList<Integer> removeDuplicateInt(ArrayList<Integer> al) {
        Set<Integer> hs = new HashSet<>();
        hs.addAll(al);
        al.clear();
        al.addAll(hs);
        return al;
    }

    private String readDoc(String path) {
        String content = "";
        try {
            File file = new File(path);
            FileInputStream fis = new FileInputStream(file.getAbsolutePath());

            HWPFDocument doc = new HWPFDocument(fis);

            WordExtractor we = new WordExtractor(doc);
            String[] paragraphs = we.getParagraphText();
            for (String para : paragraphs) {
                content += para.toString();
            }
            fis.close();
            return content;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content;
    }

    private String readDocx(String path) {
        String content = "";
        try {
            File file = new File(path);
            FileInputStream fis = new FileInputStream(file.getAbsolutePath());

            XWPFDocument document = new XWPFDocument(fis);

            List<XWPFParagraph> paragraphs = document.getParagraphs();

            for (XWPFParagraph para : paragraphs) {
                content += para.getText();
            }
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content;
    }

    private String readPdf(String path) throws IOException {
        String content = "";
        PDFTextStripper pdfStripper = null;
        PDDocument pdDoc = null;
        try {
            pdDoc = PDDocument.load(new File(path));
            pdfStripper = new PDFTextStripper();
            pdfStripper.setStartPage(1);
            pdfStripper.setEndPage(pdDoc.getNumberOfPages());
            content = pdfStripper.getText(pdDoc);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (pdDoc != null) {
                pdDoc.close();
            }
        }
        return content.replaceAll("\\r?\\n", " ");
    }

    @Override
    protected void process(List<String> chunks) {
        for (final String string : chunks) {
            int progress = 0;
            if (string.matches("[0-9]+")) {
                progress = Integer.parseInt(string);
                GUI.pbar.setValue(progress);
            } else {
                GUI.processArea.append(string);
                GUI.processArea.append("\n");
            }
        }
    }

    @Override
    protected void done() {
        JOptionPane.showMessageDialog(null, "Done.\n See output.txt for More Details.");
    }
}
