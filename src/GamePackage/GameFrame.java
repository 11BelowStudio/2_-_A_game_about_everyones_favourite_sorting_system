package GamePackage;

import javax.swing.*;
import java.awt.*;

public class GameFrame extends JFrame {


    private Component comp;

    public GameFrame() {
        this.setTitle("2 - A Game About Everyone's Favourite Sorting System!");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    public void addView(View v){
        comp = v;
        getContentPane().add(BorderLayout.CENTER, comp);
        this.setVisible(true);
        this.setResizable(false); //no resizing.
        pack();
    }

    public Dimension getDimensions(){ return comp.getPreferredSize(); }


}