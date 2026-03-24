import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;

public class KeyManager implements KeyListener {
    private HashMap<Integer, KeyState> keyStates = new HashMap<>();

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
            this.keyStates.put(e.getKeyCode(), KeyState.Pressed);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        this.keyStates.put(e.getKeyCode(), KeyState.Released);
    }

    //pressed -> down, released -> up
    //remember to call every frame
    public void update() {
        this.keyStates.replaceAll((keyCode_, state) -> {
            if (state == KeyState.Pressed) {
                return KeyState.Down;
            } else if (state == KeyState.Released) {
                return KeyState.Up;
            } else {
                return state;
            }
        });
    }

    //pressed counts as down
    public boolean isDown(int keycode){
        KeyState state = this.keyStates.getOrDefault(keycode, KeyState.Up);
        return state == KeyState.Down || state == KeyState.Pressed;
    }

    //released counts as up
    public boolean isUp(int keycode){
        KeyState state = this.keyStates.getOrDefault(keycode, KeyState.Up);
        return state == KeyState.Up || state == KeyState.Released;
    }

    public boolean isPressed(int keycode){
        KeyState state = this.keyStates.getOrDefault(keycode, KeyState.Up);
        return state == KeyState.Pressed;
    }

    public boolean isReleased(int keycode){
        KeyState state = this.keyStates.getOrDefault(keycode, KeyState.Up);
        return state == KeyState.Released;
    }
}
