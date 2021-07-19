
package flightsim;

import java.awt.Insets;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import javax.swing.JFrame;

/*
Author(s): Jacob Adams
*/
public class MouseInput {
    
    private boolean mouse1Pressed;
    private boolean mouse2Pressed;
    
    private JFrame frame;
    
    public int mouseWheelMoved;
    
    
    public int getMouseX()
    {
        PointerInfo a = MouseInfo.getPointerInfo();
        Point b = a.getLocation();
        int mouseX = (int) b.getX();
        Insets inset = frame.getInsets();
        return mouseX - frame.getX() - inset.left;
    }
    public int getMouseY()
    {
        PointerInfo a = MouseInfo.getPointerInfo();
        Point b = a.getLocation();
        int mouseY = (int) b.getY();
        Insets inset = frame.getInsets();
        
        return mouseY - frame.getY() - inset.top;
    }
    public boolean getMouse1Pressed()
    {
        return mouse1Pressed;
    }
    public boolean getMouse2Pressed()
    {
        return mouse2Pressed;
    }

    public MouseInput(JFrame frame)
    {
        
        this.frame = frame;
        
        frame.addMouseListener(new MouseAdapter()
        {

                @Override
                public void mousePressed(MouseEvent e) {
                    if (e.getButton() == MouseEvent.BUTTON1)
                    {
                        mouse1Pressed = true;
                    }
                    if (e.getButton() == MouseEvent.BUTTON3)
                    {
                        mouse2Pressed = true;
                    }

                }

                @Override
                public void mouseReleased(MouseEvent e)
                {
                    if (e.getButton() == MouseEvent.BUTTON1)
                    {
                    mouse1Pressed = false;
                    }
                    if (e.getButton() == MouseEvent.BUTTON3)
                    {
                    mouse2Pressed = false;
                    }
                    
                }
            });
        
        frame.addMouseWheelListener(new MouseAdapter() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                mouseWheelMoved = e.getWheelRotation();
            }
        });
    }
    
   

}
