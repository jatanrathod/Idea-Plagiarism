/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iplagiarism;

import java.io.File;
import java.io.IOException;
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
    checkPlagiarism(String filePath0, String filePath1) {
        this.filePath0 = filePath0;
        this.filePath1 = filePath1;
    }

    @Override
    protected Void doInBackground() throws Exception {
        String f1 = readFile(filePath0);
        String f2 = readFile(filePath1);
        String file1 = splitLines(f1);
        String file2 = splitLines(f2);
        return null;
    }

    private String readFile(String path) throws IOException {
        String contents = null;
        File file = new File(path);
        contents = FileUtils.readFileToString(file, "UTF-8");
        return contents;
    }

    private String splitLines(String contents) {
        String result = replaceRegex(contents);
        return result;
    }

    private String replaceRegex(String contents) {
        String result = null;
        int rand = 500 + (int)(Math.random() * 1500);
        this.randomNum = " "+String.valueOf(rand)+" ";
        result = contents.replace(".", randomNum).replace(",", randomNum).replace("?", randomNum).replace("!", randomNum).replace("@", randomNum);
        return result;
    }
}
