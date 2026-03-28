package src;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.HashSet;

public class KeyManager implements KeyListener {
    private final HashSet<Integer> down = new HashSet<>();
    private final HashSet<Integer> pressed = new HashSet<>();
    private final HashSet<Integer> released = new HashSet<>();


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
}
