
package flightsim;

import java.awt.AWTException;
import java.awt.Insets;
import java.awt.Robot;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jacob
 */
public class Game
{
    public World world;
    public Window window;
    public MouseInput mouse;

    public KeyInput keyInput; //Used to get the keyboard input of the user

    //public float fps = 0;

    final long MAXFPS = 30;
    final long MINFRAMETIME = 1000000000l/MAXFPS;

    final float MOUSESENSITIVITY = 1/1000f;

    private Robot ROBOT;

    public Game()
    {
        world = new World();
        final int SCALEDOWN = 4;
        window = new Window("Flight Simulator", 1920/SCALEDOWN, 1080/SCALEDOWN);
        mouse = new MouseInput(window.window);

        keyInput = new KeyInput(); //Setup the key listener

        ROBOT = null;
        try
        {
            ROBOT = new Robot();
        }
        catch (AWTException ex)
        {
            Logger.getLogger(FlightSim.class.getName()).log(Level.SEVERE, null, ex);
        }

        window.assignObjectToDraw(world);
    }

    public synchronized void run()
    {
        long start, end;
        float durationInSec = 0;
        while (true)
        {

            start = System.nanoTime();
            world.update(durationInSec);

            window.render();

            if (keyInput.isPressed(KeyInput.InputKey.UP))
            {
                world.plane.pitchUp(durationInSec);
            }
            if (keyInput.isPressed(KeyInput.InputKey.DOWN))
            {
                world.plane.pitchDown(durationInSec);
            }
            if (keyInput.isPressed(KeyInput.InputKey.LEFT))
            {
                world.plane.yawLeft(durationInSec);
            }
            if (keyInput.isPressed(KeyInput.InputKey.RIGHT))
            {
                world.plane.yawRight(durationInSec);
            }
            if (keyInput.isPressed(KeyInput.InputKey.THROTTLEUP))
            {
                world.plane.thrustUp(durationInSec);
            }
            if (keyInput.isPressed(KeyInput.InputKey.THROTTLEDOWN))
            {
                world.plane.thrustDown(durationInSec);
            }

            if (keyInput.isPressed(KeyInput.InputKey.THIRDPERSON))
            {
                world.firstPerson = false;
            }
            if (keyInput.isPressed(KeyInput.InputKey.FIRSTPERSON))
            {
                world.firstPerson = true;
            }
            if (keyInput.isPressed(KeyInput.InputKey.ESCAPE))
            {
                System.exit(0);
            }
            updateCameraRotation();
            world.cam.calc();
            do
            {
                end = System.nanoTime();
            }
            while(end < start+MINFRAMETIME);
            durationInSec = ((float)(end-start)) / 1000000000f;
        }

    }

    private void updateCameraRotation()
    {
        float shiftX, shiftY;
        shiftX = ((float)mouse.getMouseX() - window.halfWidth)*MOUSESENSITIVITY;
        shiftY = ((float)mouse.getMouseY() - window.halfHeight)*MOUSESENSITIVITY;

        world.cam.rotate(shiftY, -shiftX); //As a shift in the x screen is a rotation about the y axis
        centerMouse(); //Recenter the mouse
    }
    public void centerMouse()
    {
        Insets inset = window.window.getInsets();
        ROBOT.mouseMove((int)window.halfWidth + window.getX() + inset.left, (int)window.halfHeight + window.getY() + inset.top);
    }
}
