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
        System.out.println(filePath0);
        return null;
    }
}