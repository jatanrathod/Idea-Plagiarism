package iplagiarism;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GUI extends JFrame {

    private JTextField dirPath = new JTextField();
    private JTextArea processArea = new JTextArea();
    private JButton checkButton = new JButton("Check");
    private JButton selectButton = new JButton("Select");
    private JPanel contentPanel;
    private JPanel subPanel;
    private JLabel dirLabel = new JLabel("Enter path of directory : ");
    private JLabel processLabel = new JLabel("Process : ");

    public GUI() {
        setBounds(0, 0, 600, 300);
        setMinimumSize(new Dimension(600, 200));
        setMaximumSize(new Dimension(999, 200));

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setTitle("Plagiarism Checker");

        contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.PAGE_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setContentPane(contentPanel);

        dirLabel.setPreferredSize(new Dimension(150, 10));
        processLabel.setPreferredSize(new Dimension(150, 10));
        dirPath.setPreferredSize(new Dimension(150, 5));
        processArea.setPreferredSize(new Dimension(150, 150));
    }

    public void Display() {
        subPanel = new JPanel();
        subPanel.setLayout(new BoxLayout(subPanel, BoxLayout.LINE_AXIS));
        subPanel.add(dirLabel);
        contentPanel.add(subPanel);

        subPanel = new JPanel();
        subPanel.setLayout(new BoxLayout(subPanel, BoxLayout.LINE_AXIS));
        subPanel.add(dirPath);
        subPanel.add(selectButton);
        contentPanel.add(subPanel);

        subPanel = new JPanel();
        subPanel.setLayout(new BoxLayout(subPanel, BoxLayout.LINE_AXIS));
        subPanel.add(processLabel);
        contentPanel.add(subPanel);

        subPanel = new JPanel();
        subPanel.setLayout(new BoxLayout(subPanel, BoxLayout.LINE_AXIS));
        subPanel.add(processArea);
        contentPanel.add(subPanel);

        subPanel = new JPanel();
        subPanel.setLayout(new BoxLayout(subPanel, BoxLayout.LINE_AXIS));
        subPanel.add(checkButton);
        contentPanel.add(subPanel);

        EventSb0 esb = new EventSb0();
        selectButton.addActionListener(esb);

        EventCB ecb = new EventCB();
        checkButton.addActionListener(ecb);
        setVisible(true);
    }

    static String getDirectory() {
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new java.io.File("~"));
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);
        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile().getAbsolutePath();
        }
        return null;
    }

    class EventCB implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            String path = dirPath.getText();
            if (path.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please select Directory to proceed..");
            } else {
                (new checkPlagiarism(path)).execute();
            }
        }
    }

    class EventSb0 implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            String s1 = GUI.getDirectory();
            dirPath.setText(s1);
        }
    }
}
