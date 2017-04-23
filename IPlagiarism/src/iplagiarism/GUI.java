package iplagiarism;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

public class GUI extends JFrame {

    private final JTextField dirPath;
    public static JTextArea processArea;
    private final JButton checkButton;
    private final JButton selectButton;
    private final JPanel contentPanel;
    private JPanel subPanel;
    private final JLabel dirLabel;
    private final JLabel processLabel;
    private JScrollPane pane;
    public static JProgressBar pbar; 

    public GUI() {
        this.processLabel = new JLabel("Process : ");
        this.dirLabel = new JLabel("Enter path of directory : ");
        this.selectButton = new JButton("Select");
        this.dirPath = new JTextField();
        this.processArea = new JTextArea();
        this.checkButton = new JButton("Check");
        this.subPanel = new JPanel();
        this.contentPanel = new JPanel();
        this.pbar = new JProgressBar(0,100);
    }

    public void Display() {
        setBounds(50, 50, 600, 200);
        setMinimumSize(new Dimension(500, 300));
        setMaximumSize(new Dimension(999, 200));

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setTitle("JAM Plagiarism Checker");

        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.PAGE_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setContentPane(contentPanel);

        dirLabel.setPreferredSize(new Dimension(100, 20));
        processLabel.setPreferredSize(new Dimension(150, 20));
        dirPath.setPreferredSize(new Dimension(150, 5));
        processArea.setPreferredSize(new Dimension(0, 1000));
        processArea.setEditable(false);
        pane = new JScrollPane (processArea);
        pane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        pane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        pbar.setPreferredSize(new Dimension(150, 5));
        pbar.setStringPainted(true);
        
        subPanel = new JPanel();
        subPanel.setLayout(new BoxLayout(subPanel, BoxLayout.LINE_AXIS));
        subPanel.add(dirLabel);
        contentPanel.add(subPanel);

        subPanel = new JPanel();
        subPanel.setLayout(new BoxLayout(subPanel, BoxLayout.LINE_AXIS));
        subPanel.add(dirPath);
        subPanel.add(Box.createRigidArea(new Dimension(5,0)));
        subPanel.add(selectButton);
        contentPanel.add(subPanel);

        subPanel = new JPanel();
        subPanel.setLayout(new BoxLayout(subPanel, BoxLayout.LINE_AXIS));
        subPanel.add(processLabel);
        contentPanel.add(subPanel);

        subPanel = new JPanel();
        subPanel.setLayout(new BoxLayout(subPanel, BoxLayout.LINE_AXIS));
        subPanel.add(pane);
        contentPanel.add(subPanel);
        
        subPanel = new JPanel();
        subPanel.setLayout(new BoxLayout(subPanel, BoxLayout.LINE_AXIS));
        subPanel.add(pbar);
        subPanel.add(Box.createRigidArea(new Dimension(5,0)));
        subPanel.add(checkButton);
        contentPanel.add(subPanel);

        EventSb esb = new EventSb();
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

    class EventSb implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            String s1 = GUI.getDirectory();
            dirPath.setText(s1);
        }
    }
}
