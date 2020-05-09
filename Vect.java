import java.util.Objects;

public class Vect{
    private double x;
    private double y;
    private double z;
    public static final Vect X = new Vect(1,0,0);
    public static final Vect Y = new Vect(0,1,0);
    public static final Vect Z = new Vect(0,0,1);
    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    @Override
    protected Vect clone() {
        return new Vect(x,y,z);
    }

    public void setZ(double z) {
        this.z = z;
    }
    public Vect add(Vect b){
        return new Vect(x+b.getX(),y+b.getY(),z+b.getZ());
    }
    public double dot(Vect b){
        return x*b.getX()+y*b.getY()+z*b.getZ();
    }
    public Vect cross(Vect b){
        return  new Vect(
                y*b.getZ()-z*b.getY(),
                z*b.getX()-x*b.getZ(),
                x*b.getY()-y*b.getX()
        );
    }
    public double mag(){
        return Math.pow(x*x+y*y+z*z,0.5);
    }
    public double ang(Vect b){
        double out = Math.acos(dot(b)/(mag()*b.mag()));
        if(out > Math.PI/2){
            return Math.PI - out;
        }
        return out;
    }
    public Vect mul(double d){
        return new Vect(x*d,y*d,z*d);
    }
    public Vect unit(){
        if(mag() != 0){
            return mul(1/mag());
        }
        return null;
    }
    public Vect project(Vect direction){
        Vect out = direction.unit();
        return out.mul(dot(out));
    }
    public Vect sub(Vect b){
        return new Vect(x-b.getX(),y-b.getY(),z-b.getZ());
    }
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
    public Vect(double x,double y, double z){
        this.x = x;
        this.y = y;
        this.z = z;
    }
    public Vect(Vect v){
        this(v.getX(),v.getY(),v.getZ());
    }

    public static void main(String[] args) {
        Vect rot = new Vect(Math.PI,-Math.PI/2,Math.PI/2);
        assert X.rotate(rot).rotate(rot.mul(-1)).equals(X);

        System.out.println(X.rotate(rot));
        System.out.println(X.rotate(rot).rotate(rot.mul(-1)));
    }
    public void set(Vect v){
        setX(v.getX());
        setY(v.getY());
        setZ(v.getZ());
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