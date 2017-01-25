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
import java.util.List;
import javax.swing.SwingWorker;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author Jatan
 */
public class checkPlagiarism extends SwingWorker<Void, String> {

    String filePath0;
    String filePath1;
    String randomNum;
    ArrayList<String> lineList;
    checkPlagiarism(String filePath0, String filePath1) {
        this.filePath0 = filePath0;
        this.filePath1 = filePath1;
    }

    @Override
    protected Void doInBackground() throws Exception {
        String f1 = readFile(filePath0);
        String f2 = readFile(filePath1);
        ArrayList<String> file1 = splitLines(f1);
        int length = file1.size();
        System.out.println("size "+ length);
        for(String s : file1){
            System.out.println(s);
        }
        ArrayList<String> file2 = splitLines(f2);
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
        lineList = new ArrayList<String>(Arrays.asList(lines));
        return lineList;
    }

    private String replaceRegex(String contents) {
        String result = null;
        int rand = 3000 + (int)(Math.random() * 6000);
        this.randomNum = " "+String.valueOf(rand)+" ";
        result = contents.replaceAll("\\.", randomNum);
        return result;
    }
}
