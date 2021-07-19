
package flightsim;


import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;

/*
Author(s): Jacob Adams
*/
public class KeyInput
{

    private KeyboardFocusManager keyManager;

    public int lastKeyCode;
    
    public KeyInput()
    {

        keyManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        keyManager.addKeyEventDispatcher(new KeyEventDispatcher() {
            @Override
            public boolean dispatchKeyEvent(KeyEvent e)
            {
                if(e.getID()==KeyEvent.KEY_PRESSED)
                {

                    lastKeyCode = e.getKeyCode();
                    onKey(e.getKeyCode());
                    return true;
                }
                if(e.getID()==KeyEvent.KEY_RELEASED)
                {
                    lastKeyCode = e.getKeyCode();
                    offKey(e.getKeyCode());
                    return true;
                }
                return false;
              }
            });   
    }
    
    private void onKey(int key)
    {
        
        InputKey currentlyPressed; //Used to temporarily store the value of any key inputs
        
        currentlyPressed = KeyBindings.getInputFromKeyEvent(key); //Load the requested input from the key bindings stored
        
        if (currentlyPressed != null) { //If the inputed key has a use and is not null
            press(currentlyPressed); //Update the user input
        }

    }
    
    private void offKey(int key)
    {
        InputKey currentlyReleased; //Used to temporarily store the value of any key inputs
        
        currentlyReleased = KeyBindings.getInputFromKeyEvent(key); //Load the requested input from the key bindings stored
        
        if (currentlyReleased != null) { //If the inputed key has a use and is not null
            release(currentlyReleased); //Update the user input
        }

    }
    
    private boolean[] isPressed = new boolean [InputKey.values().length];
    
    public void press(InputKey k)
    {
        isPressed[k.ordinal()] = true;
    }
    public void release(InputKey k)
    {
        isPressed[k.ordinal()] = false;
    }
    public boolean isPressed(InputKey k)
    {
        return isPressed[k.ordinal()];
    }
    
    static public enum InputKey
    {
        UP,
        DOWN,
        LEFT,
        RIGHT,
        THROTTLEUP,
        THROTTLEDOWN,
        FIRSTPERSON,
        THIRDPERSON,
        ESCAPE,
    }
    
    public static class KeyBindings
    {
        
        public static class InputBind
        {
            public int inputBindedTo;
            public int alternativeBind;

            public InputBind(int key1, int key2)
            {
                inputBindedTo = key1;
                alternativeBind = key2;
            }

            public boolean isActive(int keyEvent)
            {
                return keyEvent == inputBindedTo || keyEvent == alternativeBind;
            }

        }
        
        public static InputBind bindedKey [];

        public static final String SAVENAME = "Settings/Keybindings.txt";

        

        static
        {
            bindedKey = new InputBind [9];
            resetKeyBinds();
        }



        public static void resetKeyBinds()
        {
            bindedKey [0] = new InputBind(KeyEvent.VK_W, KeyEvent.VK_UP);
            bindedKey [1] = new InputBind(KeyEvent.VK_S, KeyEvent.VK_DOWN);
            bindedKey [2] = new InputBind(KeyEvent.VK_A, KeyEvent.VK_LEFT);
            bindedKey [3] = new InputBind(KeyEvent.VK_D, KeyEvent.VK_RIGHT);
            bindedKey [4] = new InputBind(KeyEvent.VK_SHIFT, KeyEvent.VK_X);
            bindedKey [5] = new InputBind(KeyEvent.VK_CONTROL, KeyEvent.VK_C);
            bindedKey [6] = new InputBind(KeyEvent.VK_E, KeyEvent.VK_E);
            bindedKey [7] = new InputBind(KeyEvent.VK_Q, KeyEvent.VK_Q);
            bindedKey [8] = new InputBind(KeyEvent.VK_ESCAPE, KeyEvent.VK_ESCAPE);
        }

        public static InputKey getInputFromKeyEvent(int key)
        {
            int i; //Declare int 'i' for looping

            for (i = 0; i < bindedKey.length; i++)
            {
                if (bindedKey [i].isActive(key))
                {
                    return InputKey.values() [i];
                }
            }
            return null;
        }

    }
}
