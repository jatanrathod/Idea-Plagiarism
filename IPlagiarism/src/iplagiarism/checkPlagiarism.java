/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iplagiarism;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.ListIterator;
import javax.swing.SwingWorker;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.*;
import org.apache.commons.lang.ArrayUtils;

/**
 *
 * @author Jatan
 */
public class checkPlagiarism extends SwingWorker<Void, String> {

    String filePath0;
    String filePath1;
    String randomNum;
    ArrayList<String> file1;
    ArrayList<String> file2;
    LinkedList<ArrayList<String>> treeFile1;
    LinkedList<ArrayList<String>> treeFile2;
    private static final String[] common = {"a", "an", "the", "it", "on", "and", "of", "for", "then", "than", "upto", "be", "is", "i", "to", "and", "in", "that", "have",
        "not", "on", "with", "he", "she", "as", "you", "do", "at", "this", "but", "his", "by", "from", "they", "we", "say", "her", "him", "or", "will",
        "my", "all", "would", "could", "there", "their", "what", "when", "why", "who", "how", "so", "up", "down", "if", "out", "in", "about", "get", "which",
        "go", "me", "make", "can", "like", "know", "time", "knew", "just", "put", "take", "took", "into", "your", "some", "them", "see", "other", "now",
        "only", "come", "its", "it's", "over", "also", "back", "after", "our", "well", "way", "even", "new", "want", "because", "any", "these", "those",
        "day", "most", "us"};

    checkPlagiarism(String filePath0, String filePath1) {
        this.filePath0 = filePath0;
        this.filePath1 = filePath1;
    }

    @Override
    protected Void doInBackground() throws Exception {
        String f1 = readFile(filePath0);
        String f2 = readFile(filePath1);
        file1 = splitLines(f1);
        file2 = splitLines(f2);
        treeFile1 = removeCommonWords(file1);
        //treeFile2 = removeCommonWords(file2);
        return null;
    }

    private String readFile(String path) throws IOException {
        String contents = null;
        File file = new File(path);
        contents = FileUtils.readFileToString(file, "UTF-8");
        return contents;
    }

    private ArrayList<String> splitLines(String contents) {
        contents = replaceRegex(contents);
        String[] lines = contents.split(randomNum);
        ArrayList<String> lineList = new ArrayList<String>(Arrays.asList(lines));
        return lineList;
    }

    private String replaceRegex(String contents) {
        String result = null;
        int rand = 3000 + (int) (Math.random() * 6000);
        this.randomNum = " " + String.valueOf(rand) + " ";
        result = contents.replace(".", randomNum).replace("?", randomNum).replace("!", randomNum);
        return result;
    }

    private LinkedList<ArrayList<String>> removeCommonWords(ArrayList<String> file) {
        LinkedList<ArrayList<String>> result = null;
        ListIterator<String> iterator = file.listIterator();
        while (iterator.hasNext()) {
            iterator.set(iterator.next().replaceAll("\\s*[^\\w\\s]\\s*", "").toLowerCase());
        }
        LinkedList<ArrayList<String>> treeFile = new LinkedList<>();
        for (int i = 0; i < file.size(); i++) {
            String[] tArray = file.get(i).split(" ");
            ArrayList<String> list = new ArrayList<String>(Arrays.asList(tArray));
            treeFile.add(list);
        }
        
        for (int i = 0; i < treeFile.size(); i++) {
            System.out.println("Linked List " + i);
            for (int j = 0; j < treeFile.get(i).size(); j++) {
                System.out.println(j + " " + treeFile.get(i).get(j));
            }
        }
        return result;
    }
}
