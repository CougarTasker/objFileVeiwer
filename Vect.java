import java.util.Objects;

/**
 * This is a 3d vector class it contains all the methods for vector manipulation.
 */
public class Vect{
    private double x;
    private double y;
    private double z;
    public static final Vect X = new Vect(1,0,0);
    public static final Vect Y = new Vect(0,1,0);
    public static final Vect Z = new Vect(0,0,1);

    /**
     * @return the x component of the the vector
     */
    public double getX() {
        return x;
    }
    /**
     * @return the y component of the the vector
     */
    public double getY() {
        return y;
    }
    /**
     * @return the z component of the the vector
     */
    public double getZ() {
        return z;
    }

    /**
     * set the x component of the vector
     * @param x the x component
     */
    public void setX(double x) {
        this.x = x;
    }
    /**
     * set the y component of the vector
     * @param y the y component
     */
    public void setY(double y) {
        this.y = y;
    }
    /**
     * set the z component of the vector
     * @param z the z component
     */
    public void setZ(double z) {
        this.z = z;
    }

    /**
     * set all of the components
     * @param v the values
     */
    public void set(Vect v){
        setX(v.getX());
        setY(v.getY());
        setZ(v.getZ());
    }

    /**
     * get a copy of this vector it is a diffrent object with with the same values
     * @return the copy of the this vector
     */
    @Override
    protected Vect clone() {
        return new Vect(x,y,z);
    }


    /**
     * adds two vectors and returns the sum
     *
     * @return the sum of the two vectors
     */
    public Vect add(Vect b){
        return new Vect(x+b.getX(),y+b.getY(),z+b.getZ());
    }

    /**
     * this subtracts the parameter from the vector and returns theresultt
     * @param b the vector to subtract
     * @return the difference of the two vectors
     */
    public Vect sub(Vect b){
        return new Vect(x-b.getX(),y-b.getY(),z-b.getZ());
    }
    /**
     * get the dot product of two vectors
     *
     * @return the dot product
     */
    public double dot(Vect b){
        return x*b.getX()+y*b.getY()+z*b.getZ();
    }

    /**
     * calculate the cross product of two vectors
     * @return the cross product
     */
    public Vect cross(Vect b){
        return  new Vect(
                y*b.getZ()-z*b.getY(),
                z*b.getX()-x*b.getZ(),
                x*b.getY()-y*b.getX()
        );
    }

    /**
     * get the magnitude of the vector also known as the absolute
     * @return the magnatude this is &gt;= 0
     */
    public double mag(){
        return Math.pow(x*x+y*y+z*z,0.5);
    }

    /**
     * get the shortest angle between two vectors
     *
     * @return the angle in radians it is in the range -pi/2 to pi/2
     */
    public double ang(Vect b){
        double out = Math.acos(dot(b)/(mag()*b.mag()));
        if(out > Math.PI/2){
            return Math.PI - out;
        }
        return out;
    }

    /**
     * multiply a vector by a scalar
     * @param d the scalar
     * @return the result of the multiplication
     */
    public Vect mul(double d){
        return new Vect(x*d,y*d,z*d);
    }

    /**
     * get the scalar division of this vector
     * @param d the scalar to divide by
     * @return the scaled vector
     */
    public Vect div(double d){
        return  mul(1/d);
    }

    /**
     * get a unit vector from this vector if the vector has direction. returns null otherwise
     * @return the unit vector, it has magnitude 1 and is in the same direction as the original
     */
    public Vect unit(){
        if(mag() != 0){
            return div(mag());
        }
        return null;
    }

    /**
     * calculates the vector that is interpolated between this and the end.
     * @param end this is where the interpolation is scaling towards
     * @param factor this is how far along the vector this has moved
     * @return a vector that is a factor multiple between the original vector and the end
     */
    public Vect interpolate(Vect end, double factor){
        return add(end.sub(this).mul(factor));
    }

    /**
     * this method projects a vector along another the result has the length the shadow
     * the vector casts onto the direction vector
     * @param direction the direction that the vector should be projected onto
     * @return the projected vector its the direction the same as the input and the magnitude &lt;= the original
     */
    public Vect project(Vect direction){
        Vect out = direction.unit();
        return out.mul(dot(out));
    }

    /**
     * this rotates a vector around an axis
     * @param axis what the vector will revolve around
     * @param angle how much the vector should revolve around this axis in radians
     * @return the rotated vector
     */
    public Vect rotate(Vect axis, double angle){
        Vect along = project(axis);
        Vect rotatecomp = sub(along);
        if(rotatecomp.mag()== 0 || axis.mag()==0){
            return new Vect(x,y,z);
        }
        Vect altAxis = axis.cross(rotatecomp).unit().mul(rotatecomp.mag());
        rotatecomp = rotatecomp.mul(Math.cos(angle)).add(altAxis.mul(Math.sin(angle)));
        return along.add(rotatecomp);
    }

    /**
     * rotates a vector by euler angles
     * @param euclid the euler angles stored as a vector
     * @return the rotated vector
     */
    public Vect rotate(Vect euclid){
        Vect  x = X.clone();
        Vect y = Y.clone();
        Vect z = Z.clone();
        Vect out = this.rotate(X,euclid.getX());
        y = y.rotate(X,euclid.getX());
        z = z.rotate(X,euclid.getX());
        out = out.rotate(y,euclid.getY());
        z = z.rotate(y,euclid.getY());
        return out.rotate(z,euclid.getZ());
    }
    /**
     * undoes a vector that has been rotated by euler angles
     * @param euclid the euler angles stored as a vector
     * @return the rotated vector
     */
    public Vect antiRotate(Vect euclid){
        euclid = euclid.mul(-1);
        Vect  x = X.clone();
        Vect y = Y.clone();
        Vect z = Z.clone();
        Vect out = this.rotate(Z,euclid.getZ());
        y = y.rotate(Z,euclid.getZ());
        x = x.rotate(Z,euclid.getZ());
        out = out.rotate(y,euclid.getY());
        x = x.rotate(y,euclid.getY());
        return out.rotate(x,euclid.getX());
    }

    /**
     * constructs a vector from three compoants
     * @param x the x component of the vector
     * @param y the y component of the vector
     * @param z the z component of the vector
     */
    public Vect(double x,double y, double z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * constructs a vector from the values of another
     * @param v the vector to copy from
     */
    public Vect(Vect v){
        this(v.getX(),v.getY(),v.getZ());
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vect vect = (Vect) o;
        return Double.compare(vect.x, x) == 0 &&
                Double.compare(vect.y, y) == 0 &&
                Double.compare(vect.z, z) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }

    @Override
    public String toString() {
        return "{x:"+ x +",y:"+ y +",z:"+ z +"}";
    }
}