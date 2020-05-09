/**
 * class for the points that make up the shape of the object as they also need to contain a normal for smooth shading
 */
public class Point extends Vect{
    private int normalcount = 0;
    private Vect normal;

    /**
     * each connected surface can add their normal and this will average them out
     * @param v the normal of this surface
     */
    public void addNormal(Vect v){
        v = v.unit();
        normal = normal.mul(normalcount++).add(v).div(normalcount);
    }

    /**
     * constructs a point from three position components
     * @param x the x component of the point
     * @param y the y component of the point
     * @param z the z component of the point
     */
    public Point(double x, double y, double z) {
        super(x, y, z);
        normal = new Vect(0,0,0);
        normalcount=0;
    }

    /**
     * creates a point from the values of another point
     * @param p the point to copy from
     */
    public Point(Point p) {
        super(p);
        setNormal(p.getNormal());
    }
    public void setNormal(Vect normal) {
        this.normal = normal;
        normalcount =1;
    }

    public Vect getNormal() {
        return normal;
    }
    public  Point(Vect position,Vect normal){
        super(position);
        this.normal = normal;
        normalcount = 1;
    }
    public Point interpolate(Point end, double factor) {
        Vect p  = super.interpolate(end, factor);
        Vect n = normal.interpolate(end.getNormal(),factor);
        return new Point(p,n);
    }
    public Point lineX(Point end, double x){
            double fact = (x-this.getX())/(end.getX()-this.getX());
            fact = Math.min(Math.max(fact,0),1);
            return interpolate(end,fact);
    }
    public Point lineY(Point end,int y){
        return  lineY(end,(double) y);
    }
    public Point lineX(Point end,int y){
        return  lineX(end,(double) y);
    }
    public Point lineY(Point end, double y){
        double fact = (y-this.getY())/(end.getY()-this.getY());
        fact = Math.min(Math.max(fact,0),1);
        return interpolate(end,fact);
    }
    public double getLightingFactor(Vect direction){
        return Math.max(0,normal.dot(direction)/(normal.mag()*direction.mag()));
    }
}
