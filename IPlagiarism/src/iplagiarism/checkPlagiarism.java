/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iplagiarism;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import javax.swing.SwingWorker;

/**
 *
 * @author Jatan
 */
public class checkPlagiarism extends SwingWorker<Void, String> {

    String filePath0;
    String filePath1;
    checkPlagiarism(String filePath0, String filePath1) {
        this.filePath0 = filePath0;
        this.filePath1 = filePath1;
    }

    @Override
    protected Void doInBackground() throws Exception {
        String file0 = addRandomToken(filePath0);
        System.out.println(file0);
        return null;
    }

    private String addRandomToken(String path) throws IOException {
        String result = null;
        Charset encoding = Charset.defaultCharset();
            File file = new File(path);
            result = handleFile(file, encoding);
        return result.toString();
    }
    private static String handleFile(File file, Charset encoding)
            throws IOException {
        try (InputStream in = new FileInputStream(file);
             Reader reader = new InputStreamReader(in, encoding);
             Reader buffer = new BufferedReader(reader)) {
            String result = handleCharacters(buffer);
            return result;
        }
    }
    private static String handleCharacters(Reader reader)
            throws IOException {
        int r;
        String result = null;
        java.util.List<String> fileContent = new ArrayList<>();
        while ((r = reader.read()) != -1) {
            char ch = (char) r;
            fileContent.add(Character.toString(ch));
            Random rand = new Random();
            int rNum = rand.nextInt(1000)+500;
            String ranNum = Integer.toString(rNum);
            if(fileContent.contains("."))
            {
                Collections.replaceAll(fileContent, ".", ranNum);
            }
        }
        result = fileContent.toString();
        return result;
    }
}
