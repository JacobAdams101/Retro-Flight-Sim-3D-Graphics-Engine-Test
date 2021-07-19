
package flightsim;

/**
 *
 * @author jacob
 */
public class Vec3D
{
    public float X, Y, Z;
    public Vec3D(float x, float y, float z)
    {
        this.X = x;
        this.Y = y;
        this.Z = z;
    }

    public Vec3D translate(Vec3D t)
    {
        return new Vec3D(this.X+t.X, this.Y+t.Y, this.Z+t.Z);
    }
    
    public Vec3D scale(float s)
    {
        return new Vec3D(this.X*s, this.Y*s, this.Z*s);
    }
    
    public float mag()
    {
        return (float)Math.sqrt((this.X*this.X)+(this.Y*this.Y)+(this.Z*this.Z));
    }
    
    public float magSquared()
    {
        return (this.X*this.X)+(this.Y*this.Y)+(this.Z*this.Z);
    }

    public Vec3D deepCopy()
    {
        return new Vec3D(this.X, this.Y, this.Z);
    }
}
