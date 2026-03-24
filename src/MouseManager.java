package src;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

//stores the mouse position for use later
public class MouseManager implements MouseListener {
    private int mouse_x = 0;
    private int mouse_y = 0;

    @Override
    public void mouseClicked(MouseEvent e) {
        handleEvent(e);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        handleEvent(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        handleEvent(e);
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        handleEvent(e);
    }

    @Override
    public void mouseExited(MouseEvent e) {
        handleEvent(e);
    }

    private void handleEvent(MouseEvent e){
        mouse_x = e.getX();
        mouse_y = e.getY();
    }

    public int getMouse_y() {
        return mouse_y;
    }

    public int getMouse_x() {
        return mouse_x;
    }
}
