import java.awt.*;
import java.time.Instant;
import java.util.Date;

public class Game {
    /*
    Tick related stuff
     */
    public static final int TICKS_PER_SECOND = 60;
    public static final int MILLISECONDS_PER_TICK = 1000/TICKS_PER_SECOND;
    private int tick_counter = 0;

    public int getTick_counter() {
        return tick_counter;
    }

    /*
    Input related stuff
     */
    private KeyManager keyManager;
    private MouseManager mouseManager;


    public MouseManager getMouseManager() {
        return mouseManager;
    }

    public KeyManager getKeyManager() {
        return keyManager;
    }

    public Game(KeyManager keyManager, MouseManager mouseManager){
        this.keyManager = keyManager;
        this.mouseManager = mouseManager;
    }

    /*
    Game State related stuff
     */
    private enum PauseStates{
        NotPaused,
        CardSelect,
        WinScreen,
        LoseScreen,
    }
    private PauseStates paused = PauseStates.NotPaused;

    /*
    Main loop
     */
    public void start(Main main){
        Instant last_tick_time = Instant.now();
        //main loop start
        while (true){
            //wait for the tick to start
            if (last_tick_time.toEpochMilli() < MILLISECONDS_PER_TICK){
                try {
                    Thread.sleep(MILLISECONDS_PER_TICK - last_tick_time.toEpochMilli());
                } catch (InterruptedException e) {
                    continue; //if we cannot sleep, we will busy wait
                }
            }


            if (paused != PauseStates.NotPaused){ //if we are paused
                handle_ui_update();
            } else {
                handle_update_world();
            }

            main.render();

            keyManager.update();
        }
    }

    private void handle_ui_update(){
        //TODO: finish implementing
    }

    private void handle_update_world(){
        //TODO: finish implementing
    }

    /*
    Rendering utilities
     */
    //everything that we will render
    public void paint(Graphics2D g2D){
        g2D.setColor(Color.WHITE);
        g2D.drawString("centered", transform_x(0),transform_y(0));
    }

    //lets us go from the standard Cartesian coordinates( 0,0 at the center +x is right and +y is up) to screen space coordinates
    private int transform_x(int preimage){
        return preimage + Main.SCREEN_WIDTH/2;
    }

    private int transform_y(int preimage){
        return -preimage + Main.SCREEN_HEIGHT/2 + Main.TITLE_BAR_HEIGHT;
    }
}
