package src;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.HashSet;

public class KeyManager implements KeyListener, FocusListener {
    private final HashSet<Integer> down = new HashSet<>();
    private final HashSet<Integer> pressed = new HashSet<>();
    private final HashSet<Integer> released = new HashSet<>();

    //we need this to handle loosing focus when we have a key down
    @Override
    public void focusGained(FocusEvent e) {
        //nothing needs to be done
    }

    //release all keys when we loose focus
    @Override
    public void focusLost(FocusEvent e) {
        released.addAll(down);
        down.clear();
        pressed.clear();
    }


    public enum KeyState {
        Pressed, Released, Down, Up,
    }

    //we will not be using this because this is for text input not key presses
    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        //java will send multiple keyPressed events if the key is held down, we don't want this behavior
        if (!isDown(e.getKeyCode())) {
            this.pressed.add(e.getKeyCode());
            this.down.add(e.getKeyCode());
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        this.released.add(e.getKeyCode());
        this.down.remove(e.getKeyCode());
    }

    //pressed -> down, released -> up
    //remember to call every frame
    public void update() {
        this.pressed.clear();
        this.released.clear();
    }

    //pressed counts as down
    public boolean isDown(int keycode){
        return down.contains(keycode);
    }

    //released counts as up
    public boolean isUp(int keycode){
        return !down.contains(keycode);
    }

    public boolean isPressed(int keycode){
        return pressed.contains(keycode);
    }

    public boolean isReleased(int keycode){
        return released.contains(keycode);
    }

    public boolean isAnyDown(){
        return !down.isEmpty();
    }
}
