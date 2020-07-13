package GamePackage;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class Controller implements KeyListener, MouseListener {
    Action action;

    public Controller(){ action = new Action(); }

    public Action getAction() { return action; }

    public void noAction() {action.noAction();}

    boolean getTheAnyButton(){ return action.theAnyButton; }

    boolean checkForSpacePress() { return action.checkForSpacePress(); }
    //version of action.checkForSpacePress(), called only by TitleScreen

    @Override
    public void keyTyped(KeyEvent e) { }

    @Override
    public void keyPressed(KeyEvent e) {
        action.theAnyButton = true;
        int key = e.getKeyCode();
        switch (key){
            /*
            case KeyEvent.VK_UP:
                action.yInput = -1;
                break;
            case KeyEvent.VK_DOWN:
                action.yInput = 1;
                break;
            case KeyEvent.VK_LEFT:
                action.xInput = -1;
                break;
            case KeyEvent.VK_RIGHT:
                action.xInput = 1;
                break;
            */
            case KeyEvent.VK_SPACE:
                action.space = true;
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        action.theAnyButton = false;
        int key = e.getKeyCode();
        switch (key){
            /*
            case KeyEvent.VK_UP:
            case KeyEvent.VK_DOWN:
                action.yInput = 0;
                break;
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_RIGHT:
                action.xInput = 0;
                break;
            */
            case KeyEvent.VK_SPACE:
                action.space = false;
                break;
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        action.clicked = false;
        //doesn't count unless the click was in the GameFrame
        if (e.getSource() instanceof GameFrame){
            action.clicked = true;
            action.clickLocation = e.getPoint();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) { }

    @Override
    public void mouseReleased(MouseEvent e) { }

    @Override
    public void mouseEntered(MouseEvent e) { }

    @Override
    public void mouseExited(MouseEvent e) { }
}
