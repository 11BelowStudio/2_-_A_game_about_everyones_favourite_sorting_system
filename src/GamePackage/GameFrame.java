package GamePackage;

import javax.swing.*;
import java.awt.*;

public class GameFrame extends JFrame {

    //yeah, it's pretty much the JEasyFrame thing but the view is added via a method
    //which was intended for some sort of optional fullscreen support
    //but this idea was not implemented in time.

    private Component comp;

    public GameFrame() {
        this.setTitle("The Button Factory");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    public void addView(View v){
        comp = v;
        getContentPane().add(BorderLayout.CENTER, comp);
        this.setVisible(true);
        pack();
    }

    public Dimension getDimensions(){ return comp.getPreferredSize(); }


}