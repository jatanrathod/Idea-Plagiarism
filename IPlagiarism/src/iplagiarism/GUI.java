package iplagiarism;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

public class GUI {
    private static JFrame mainFrame;
    private static JTextField path0;
    private static JTextField path1;
    private JPanel panelFile1;
    private JPanel panelFile2;
    private JButton check;
    public static String absPath0;
    public static String absPath1;
    
    //constructor
    public GUI() {
        prepareGUI();
    }
    
    private void prepareGUI() {
        mainFrame = new JFrame("Plagiarism Checker");
        mainFrame.setSize(400, 400);
        mainFrame.setLayout(new BorderLayout());
        mainFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                System.exit(0);
            }
        });
        path0 = new JTextField(20);
        path1 = new JTextField(20);
        check = new JButton("Check");

        panelFile1 = new JPanel();
        panelFile1.setLayout(new FlowLayout());
        panelFile2 = new JPanel();
        panelFile2.setLayout(new FlowLayout());
        
        mainFrame.add(panelFile1, BorderLayout.CENTER);
        mainFrame.add(panelFile2, BorderLayout.SOUTH);
        mainFrame.setVisible(true);
    }
    
    public void Display()
    {
        JButton selectButton0 = new JButton("Select");
        JButton selectButton1 = new JButton("Select");
        
        EventSb0 esb0 = new EventSb0();
        selectButton0.addActionListener(esb0);
        
        EventSb1 esb1 = new EventSb1();
        selectButton1.addActionListener(esb1);
        
        EventCB ecb = new EventCB();
        check.addActionListener(ecb);
        
        panelFile1.add(path0);
        panelFile1.add(selectButton0);
        panelFile1.add(path1);
        panelFile1.add(selectButton1);
        panelFile2.add(check);
        mainFrame.setVisible(true);
    }
    
    public static String openExplorer(){
        String path = null;
        FileFilter filter = new FileNameExtensionFilter("doc", "txt", "docx", "pdf");
        final JFileChooser fileDialog = new JFileChooser();
        fileDialog.addChoosableFileFilter(filter);
        int returnVal = fileDialog.showOpenDialog(mainFrame);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fileDialog.getSelectedFile();
                    if (filter.accept(file)) {   
                        path = file.getAbsolutePath();
                    } else {
                        JOptionPane.showMessageDialog(null, "Choose Text Documents only");
                    }
                }
        return path;
    }
    
    class EventCB implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            String filePath0 = path0.getText();
            String filePath1 = path1.getText();
            if(filePath0.isEmpty() || filePath1.isEmpty()){
                JOptionPane.showMessageDialog(null, "Please select two files to check Plagiarism!!");
            }else{
            (new checkPlagiarism(filePath0, filePath1)).execute();
            }
        }
        
    }
    
    class EventSb0 implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            String s1 = GUI.openExplorer();
            path0.setText(s1);            
        }  
    }
    
    class EventSb1 implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            String s2 = GUI.openExplorer();
            path1.setText(s2);
        }
    }
}