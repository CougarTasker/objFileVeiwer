import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

public class Tri{
    private List<Vect> points;
    private Color fill= Color.BLUE;
    private double ambientlevel = 0.1;
    public Color getFill(double amount) {
        amount = amount*(1-ambientlevel)+ambientlevel;
        return new Color((int)(fill.getRed()*amount),(int)(fill.getGreen()*amount), (int)(fill.getBlue()*amount));
    }
    public void setFill(Color fill) {
        this.fill = fill;
    }
    public List<Vect> getPoints() {
        return points;
    }
    public void setPoints(List<Vect> points) {
        this.points = points;
    }
    public Tri(){
//        Random r = new Random();
//        this.fill = new Color(r.nextFloat(),r.nextFloat(),r.nextFloat());

    }
    public Tri(Vect a,Vect b,Vect c){
        this();
        ArrayList<Vect> points = new ArrayList<Vect>();
        points.add(a);
        points.add(b);
        points.add(c);
        setPoints(points);
    }
    public Vect norm(){
        Vect ab =  points.get(1).sub(points.get(0));
        Vect ac = points.get(2).sub(points.get(0));
        return ac.cross(ab).unit();
    }
    public double area(){
        double a = Math.abs(points.get(0).sub(points.get(1)).mag());
        double b = Math.abs(points.get(1).sub(points.get(2)).mag());
        double c = Math.abs(points.get(0).sub(points.get(2)).mag());
        double p = (a+b+c)/2;
        return Math.sqrt(p*(p-a)*(p-b)*(p-c));
    }
    private void draw(DepthBuffer out,Vect lightDirection,Vect start,Vect a, Vect b){
        int stopay = (int) a.getY();
        int stopby = (int) start.getY();
        int miny = (int) Math.max(Math.min(stopay,stopby),0);
        int maxy = (int) Math.min(Math.max(stopay,stopby),out.getHeight());
        double light = Math.abs(1-lightDirection.ang(this.norm())/Math.PI);
        for (int y = miny; y < maxy; y++) {
            Vect ls = lineY(start,a,y);
            Vect le = lineY(start,b,y);
            int stopax = (int) ls.getX();
            int stopbx = (int) le.getX();
            int minx = (int) Math.max(Math.min(stopax,stopbx),0);
            int maxx = (int) Math.min(Math.max(stopax,stopbx),out.getWidth());
            for (int x = minx; x < maxx; x++) {
                Vect p = lineX(ls,le,x);
                out.setPixel(x,y,p.getZ(),this.getFill(light));
            }
        }
    }
    private Vect lineY(Vect start,Vect end,int y){
        double fact = (y-start.getY())/(end.getY()-start.getY());
        fact = Math.min(Math.max(fact,0),1);
        return start.add(end.sub(start).mul(fact));
    }
    private Vect lineX(Vect start,Vect end,int x){
        double fact = (x-start.getX())/(end.getX()-start.getX());
        fact = Math.min(Math.max(fact,0),1);
        return start.add(end.sub(start).mul(fact));
    }
    public void draw(Cam c){
        List<Vect> topBottom = new ArrayList<Vect>();
        points.forEach(new Consumer<Vect>() {
            @Override
            public void accept(Vect v) {
                topBottom.add(new Vect(v));
            }
        });


        topBottom.forEach(new Consumer<Vect>() {
            @Override
            public void accept(Vect in) {
                Vect out = c.project(in);
                in.set(out);
            }
        });

        for(Vect v : topBottom){
            if(c.behind(v)){
                return;
            }
        }


        topBottom.sort(new Comparator<Vect>() {
            @Override
            public int compare(Vect o1, Vect o2) {
                return (int) (o1.getY()-o2.getY());
            }
        });
        Vect top = topBottom.get(0);
        Vect mid = topBottom.get(1);
        Vect bot = topBottom.get(2);
        Vect sh = mid.sub(top);
        Vect lo = bot.sub(top);
        if(lo.getY() <1){//if the height is zero don't draw this.
            return;
        }
        Vect light = Vect.Z.rotate(c.getRot());
        Vect altmid = lo.mul(sh.getY()/lo.getY()).add(top);
        draw(c.getCanvas(),light,top,altmid,mid);
        draw(c.getCanvas(),light,bot,altmid,mid);

    }
}