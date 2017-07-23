/*
set the source file encode to be GB2312 to use Chinese
*/
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.nio.file.Files;
import java.nio.file.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.io.IOException;
import java.util.*;
import java.util.Random;
import java.lang.Thread.*;


public class VocaBuild extends JPanel
            implements ActionListener {
                    
        static final int FPS = 30;
        static long delay = (long)(1000/FPS);
        static long period = delay;
        static int count = 150;
        
        JLabel question,picture,progress;
        JRadioButton[] buttons;
        int currentWordIndex;
        boolean goodanswer = false;
        int trytimes = 0;
        //Group the radio buttons.
        ButtonGroup group = new ButtonGroup();
        String [] picNames ={"question","Bird","Cat","Dog","Rabbit","Pig"};
        static String finishlistfile="finished.dat";
        static String newwordsfile="newwords.txt";

        public VocaBuild() {
            super(new BorderLayout());
            readfile();
            //Set up the picture label.
            question = new JLabel("words",SwingConstants.CENTER);

            //The preferred size is hard-coded to be the width of the
            //widest image and the height of the tallest image.
            //A real program would compute this.
            question.setPreferredSize(new Dimension(177, 120));
            question.setFont (question.getFont ().deriveFont (22.0f));

            //Set up the picture label.
            picture = new JLabel(new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("images/"
                                               + picNames[0]
                                               + ".gif"))));
            picture.setHorizontalTextPosition(JLabel.CENTER);
            picture.setFont ( picture.getFont ().deriveFont (16.0f));
            picture.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (goodanswer) {
                        fillwords();

                    }
                }

            }
                                    );
            progress = new JLabel("number",SwingConstants.LEFT);
            //The preferred size is hard-coded to be the width of the
            //widest image and the height of the tallest image.
            //A real program would compute this.
            picture.setPreferredSize(new Dimension(125, 125));

            //Put the radio buttons in a column in a panel.
            JPanel radioPanel = new JPanel(new GridLayout(0, 1));
            radioPanel.setPreferredSize(new Dimension(350, 120));
            int n = 5;
            buttons= new JRadioButton[n];
            for (int i=0;i<n;i++) {
                buttons[i]=new JRadioButton("");
                buttons[i].setFont (question.getFont ().deriveFont (16.0f));
                buttons[i].addActionListener(this);
                group.add(buttons[i]);
                radioPanel.add(buttons[i]);
            }
            add(progress, BorderLayout.NORTH);
            add(radioPanel, BorderLayout.EAST);
            add(picture, BorderLayout.LINE_START);
            add(question, BorderLayout.CENTER);
            setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

            fillwords();
            
            java.util.Timer timer = new java.util.Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    int flag = count;
                    if(count>0){
                        picture.setText(""+count);
                        count--;
                        if(flag>0 && count==0){//do it only once
                                picture.setText("New Word!");
                                picture.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("images/"
                                        + "questionfail"
                                        + ".gif"))));
                                writenewwordfile(wordlist.get(currentWordIndex)+"\r\n");
                        }
                    }
                }
            }
            , delay, period);
        }
        private void fillwords() {
            if(finishlist.size()<wordlist.size()){
            count = 150;
            goodanswer=false;
            trytimes = 0;//mark as not tried
            group.clearSelection();
            Random rand = new Random();
            int listlen = wordlist.size();
            do {
                currentWordIndex = rand.nextInt(listlen);
            } while (finishlist.contains(currentWordIndex));

            String [] s = wordlist.get(currentWordIndex).split("=");
            question.setText(s[0]);
            //question.setSize(new Dimension(22*s[0].length(), 120) );
            //this.revalidate();
            //this.repaint();
            int buttonlen = buttons.length;
            int pos = rand.nextInt(buttonlen);

            for (int i=0;i<buttons.length;i++) {
                String explanition = "";
                String [] ss;
                int idx;
                if (i==pos) {
                    ss = s;
                    idx = currentWordIndex;
                } else {
                    idx = currentWordIndex+i-pos;
                    if (idx<0)idx=listlen+idx;
                    if (idx>=listlen)idx=idx-listlen;
                    ss = wordlist.get(idx).split("=");
                }
                for (int j=1;j<ss.length;j++)explanition+=ss[j];
                buttons[i].setText(explanition);
                buttons[i].setActionCommand(""+idx);
                buttons[i].setSelected(false);
            }
            ImageIcon iic = new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("images/"
                                          + picNames[0]
                                          + ".gif")));
            picture.setIcon(iic);
            int remain = wordlist.size()-finishlist.size();
            progress.setText("还有"+remain+"个，共有"+wordlist.size()+"个");
            }else{
                    progress.setText("congratulations! all finished :)");
            }
        }
        
        private void doanimation(String typename){
                count = 0; //freeze counter
                String [] picnames = {};
                String [] NG_group = {"NG0","NG1","NG2","NG0","NG2","NG1","NG0"};
                String [] good_group = {"thumb0","thumb2","thumb4","thumb2","thumb0","thumb2","thumb4"};
                if(typename.equals("NG")){
                        picnames = new String[NG_group.length];
                        System.arraycopy(NG_group,0,picnames,0,NG_group.length);
                }
                if(typename.equals("good")){
                        picnames = new String[good_group.length];
                        System.arraycopy(good_group,0,picnames,0,good_group.length);
                }
                
                for(int i=0;i<picnames.length;i++){
                        String s = picnames[i];
                ImageIcon iic = new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("images/"
                                + s
                                + ".gif")));
                picture.setIcon(iic); 
                picture.setText("");
                update(getGraphics());  
                try {
                    Thread.sleep(150);
                } catch (InterruptedException ie) {
                    //Handle exception
                }                
                }
        }
        /** Listens to the radio buttons. */
        public void actionPerformed(ActionEvent e) {
            String selected = e.getActionCommand();
            String current = ""+currentWordIndex;
            String picName;
            ImageIcon iic;
            if (selected.equals(current)) {
                Random rand = new Random();
                picName = picNames[rand.nextInt(picNames.length-1)+1];
                goodanswer = true;
                doanimation("good");
                if (trytimes==0) {
                    finishlist.add(currentWordIndex);
                }
            } else {
                doanimation("NG");
                picName = picNames[0];
                goodanswer = false;
                writenewwordfile(wordlist.get(currentWordIndex)+"\r\n");

                trytimes++;

            }
            iic = new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("images/"
                                + picName
                                + ".gif")));
            picture.setIcon(iic);
            picture.setText("?");

            if (trytimes==0) {
                fillwords();//if success in 1st try, jump to next question
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
            frame.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    writeDataFile();
                    System.exit(0);
                }
            }
                                   );

            //Create and set up the content pane.
            JComponent newContentPane = new VocaBuild();
            newContentPane.setOpaque(true); //content panes must be opaque
            frame.setContentPane(newContentPane);

            //Display the window.
            frame.pack();
            frame.setVisible(true);
            

        }

        private java.util.List<String> wordlist = new ArrayList<String>();//to store words
        private java.util.List<String> newwordlist = new ArrayList<String>();//new word list, to avoid multiple items added
        private static java.util.List<Integer> finishlist = new ArrayList<Integer>();
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

            readDataFile();
            readnewwordfile();
        }
        private void readnewwordfile() {
            String line = null;
            try (BufferedReader input = new BufferedReader(new InputStreamReader(new FileInputStream(newwordsfile)))){
                while ((line = input.readLine()) != null) {
                    newwordlist.add(line);
                    //System.out.println(line);
                }

            } catch (Exception ex) {
                System.err.format("IOException: %s%n", ex);
            }
        }
        private void writenewwordfile(String s) {
            if(newwordlist.contains(s))return;
            Charset charset = Charset.forName("GB2312");
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                            new FileOutputStream(newwordsfile, true), "GB2312"))) {
                writer.write(s, 0, s.length());
                newwordlist.add(s);
            } catch (IOException x) {
                System.err.format("IOException: %s%n", x);
            }
        }
        private static void writeDataFile() {
            DataOutputStream output=null;
            try {
                output = new DataOutputStream(new FileOutputStream(finishlistfile));
                for (int i = 0; i < finishlist.size(); i++) {
                    output.writeInt(finishlist.get(i));
                }
                output.flush();
                output.close();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            finally {
                try {
                    output.close();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }

        private static void readDataFile() {
            DataInputStream input=null;
            try {
                input = new DataInputStream(new FileInputStream(finishlistfile));
                while (input.available() > 0) {
                    int number = input.readInt();
                    finishlist.add(number);
                    //System.out.println(" number =" + number);
                }
                input.close();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            finally {
                try {
                    input.close();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
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
