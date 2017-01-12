package iplagiarism;

import javax.swing.SwingUtilities;

public class IPlagiarism {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
        public void run() {
            GUI gui = new GUI();
            gui.Display();
            
        }
        });
    }
    
}
