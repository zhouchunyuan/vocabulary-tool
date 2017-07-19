/*
set the source file encode to be GB2312 to use Chinese
*/
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.nio.file.Files;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.io.IOException;
import java.util.*;
import java.util.Random;


public class VocaBuild extends JPanel
            implements ActionListener {
        JLabel question,picture;
        JRadioButton[] buttons;
        int currentWordIndex;
        //Group the radio buttons.
        ButtonGroup group = new ButtonGroup();
        String [] picNames ={"Bird","Cat","Dog","Rabbit","Pig"};
        
        public VocaBuild() {
            super(new BorderLayout());
            readfile();

            //Set up the picture label.
            question = new JLabel("words");

            //The preferred size is hard-coded to be the width of the
            //widest image and the height of the tallest image.
            //A real program would compute this.
            question.setPreferredSize(new Dimension(177, 120));
            
            //Set up the picture label.
            picture = new JLabel(createImageIcon("images/"
                                             + "bird"
                                             + ".gif"));
            picture.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    fillwords();
                }

            });

            //The preferred size is hard-coded to be the width of the
            //widest image and the height of the tallest image.
            //A real program would compute this.
            picture.setPreferredSize(new Dimension(100, 100));

            //Put the radio buttons in a column in a panel.
            JPanel radioPanel = new JPanel(new GridLayout(0, 1));

            int n = 5;
            buttons= new JRadioButton[n];
            for (int i=0;i<n;i++) {
                buttons[i]=new JRadioButton("");
                
                buttons[i].addActionListener(this);
                group.add(buttons[i]);
                radioPanel.add(buttons[i]);
            }
            add(radioPanel, BorderLayout.CENTER);
            add(picture, BorderLayout.EAST);
            add(question, BorderLayout.LINE_START);
            setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
            
            //fillwords();
        }
        private void fillwords(){
                picture.setVisible(false);
                group.clearSelection();
                Random rand = new Random();
                int listlen = wordlist.size();
                currentWordIndex = rand.nextInt(listlen);
                String [] s = wordlist.get(currentWordIndex).split("=");
                question.setText(s[0]);
                
                int buttonlen = buttons.length;
                int pos = rand.nextInt(buttonlen);
               
                for(int i=0;i<buttons.length;i++){
                        String explanition = "";
                        String [] ss;
                        int idx;
                        if(i==pos){
                                ss = s;
                                idx = currentWordIndex;
                         }else{
                                idx = currentWordIndex+i-pos;
                                if(idx<0)idx=listlen+idx;
                                if(idx>=listlen)idx=idx-listlen;
                                ss = wordlist.get(idx).split("=");
                        }
                        for(int j=1;j<ss.length;j++)explanition+=ss[j];
                        buttons[i].setText(explanition);
                        buttons[i].setActionCommand(""+idx);
                        buttons[i].setSelected(false);
                }
                
        }
        /** Listens to the radio buttons. */
        public void actionPerformed(ActionEvent e) {
            String selected = e.getActionCommand();
            String current = ""+currentWordIndex;
            if(selected.equals(current)){
                    Random rand = new Random();
                    String picName = picNames[rand.nextInt(picNames.length)];
                    picture.setVisible(true);
                    picture.setIcon(createImageIcon("images/"
                                             + picName
                                             + ".gif"));
            }
        }

        /** Returns an ImageIcon, or null if the path was invalid. */
        protected static ImageIcon createImageIcon(String path) {
            java.net.URL imgURL = VocaBuild.class.getResource(path);
            if (imgURL != null) {
                return new ImageIcon(imgURL);
            } else {
                System.err.println("Couldn't find file: " + path);
                return null;
            }
        }

        /**
         * Create the GUI and show it.  For thread safety,
         * this method should be invoked from the
         * event-dispatching thread.
         */
        private static void createAndShowGUI() {
            //Create and set up the window.
            JFrame frame = new JFrame("VocaBuild");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            //Create and set up the content pane.
            JComponent newContentPane = new VocaBuild();
            newContentPane.setOpaque(true); //content panes must be opaque
            frame.setContentPane(newContentPane);

            //Display the window.
            frame.pack();
            frame.setVisible(true);
        }
        
        private java.util.List<String> wordlist = new ArrayList<String>();//to store words
        private void readfile() {
            String line = null;
            try {
                BufferedReader input = new BufferedReader(new InputStreamReader(
                            this.getClass().getClassLoader().getResourceAsStream("vocabulary.txt")));
                while ((line = input.readLine()) != null) {
                        wordlist.add(line);
                    //System.out.println(line);
                }
            } catch (Exception ex) {
                System.err.format("IOException: %s%n", ex);
            }


        }

        public static void main(String[] args) {
            //Schedule a job for the event-dispatching thread:
            //creating and showing this application's GUI.
            javax.swing.SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    createAndShowGUI();
                }
            }
                                                  );
        }
}
