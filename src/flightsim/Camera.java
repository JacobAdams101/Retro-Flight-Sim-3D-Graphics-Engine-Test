
package flightsim;

/**
 *
 * @author jacob
 */
public class Camera
{
    public Vec3D pos;

    public float yRot;
    public float xRot;

    public Camera(Vec3D p)
    {
        this.pos = p;
        this.yRot = 0;
        this.xRot = 0;
        calc();
    }

    public Camera(float x, float y, float z)
    {
        this(new Vec3D(x, y, z));
    }

    public void setPosition(float x, float y, float z)
    {
        this.pos.X = x;
        this.pos.Y = y;
        this.pos.Z = z;
    }

    public float cosy;
    public float siny;
    public float cosx;
    public float sinx;

    public void calc()
    {
        cosy = (float)Math.cos(yRot);
        siny = (float)Math.sin(yRot);

        cosx = (float)Math.cos(xRot);
        sinx = (float)Math.sin(xRot);
    }
    
    public Vec3D getCenteredOffset(Vec3D offset)
    {
        float x, y, z;
        float X, Y, Z;
        
        float cosy = (float)Math.cos(-yRot);
        float siny = (float)Math.sin(-yRot);

        float cosx = (float)Math.cos(-xRot);
        float sinx = (float)Math.sin(-xRot);

        x = offset.X;
        y = offset.Y;
        z = offset.Z;
        
        
        X = (cosy*x) + (siny*sinx*y)+(siny*cosx*z);
        Y = (cosx*y) - (sinx*z);
        Z = (cosy*cosx*z) + (cosy*sinx*y) - (siny*x);

        return new Vec3D(X, Y, Z);
    }

    public void rotate(float xRotChange, float yRotChange)
    {
        this.xRot += xRotChange;
        this.yRot += yRotChange;
    }

}
