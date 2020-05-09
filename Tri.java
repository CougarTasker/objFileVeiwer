import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

public class Tri{
    private List<Point> points;
    private Color fill= Color.BLUE;
    private double ambientlevel = 0.4;
    public Color getFill(double amount) {
        amount = amount*(1-ambientlevel)+ambientlevel;
        return new Color((int)(fill.getRed()*amount),(int)(fill.getGreen()*amount), (int)(fill.getBlue()*amount));
    }
    public void setFill(Color fill) {
        this.fill = fill;
    }
    public List<Point> getPoints() {
        return points;
    }
    public void setPoints(List<Point> points) {
        this.points = points;
        points.forEach(new Consumer<Point>() {
            @Override
            public void accept(Point point) {
                point.addNormal(norm());
            }
        });
    }
    public Tri(){
        Random r = new Random();
        this.fill = new Color(r.nextFloat(),r.nextFloat(),r.nextFloat());

    }
    public Tri(Point a, Point b, Point c){
        this();
        ArrayList<Point> points = new ArrayList<>();
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
    private void draw(DepthBuffer out, Vect lightDirection, Point start, Point a, Point b){

        int miny = (int) Math.round(Math.max(Math.min(Math.min(a.getY(),b.getY()),start.getY()),0));
        int maxy = (int) Math.round(Math.min(Math.max(Math.max(a.getY(),b.getY()),start.getY()),out.getHeight()));
        for (int y = miny; y < maxy; y++) {
            Point ls = start.lineY(a,y);
            Point le = start.lineY(b,y);
            double stopax = ls.getX();
            double stopbx = le.getX();
            int minx = (int) Math.round(Math.max(Math.min(stopax,stopbx),0));
            int maxx = (int) Math.round(Math.min(Math.max(stopax,stopbx),out.getWidth()));
            for (int x = minx; x < maxx; x++) {
                Point p = ls.lineX(le,x);
                out.setPixel(x,y,p.getZ(),this.getFill(p.getLightingFactor(lightDirection)));
            }
        }
    }
    public void draw(Cam c){
        List<Point> topBottom = new ArrayList<Point>();
        points.forEach(new Consumer<Point>() {
            @Override
            public void accept(Point v) {
                topBottom.add(new Point(v));
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
        Point top = topBottom.get(0);
        Point mid = topBottom.get(1);
        Point bot = topBottom.get(2);

        if(bot.sub(top).getY()<1){//if the triangles height is less than a pixle dont draw it
            return;
        }
        Vect light = Vect.Z.rotate(c.getRot());
        if(mid.getY()-top.getY()<1){
            //if the top tri has no height then just draw the bottom
            draw(c.getCanvas(),light,bot,top,mid);
            return;
        }else if(bot.getY()-mid.getY()<1){
            //if the bottom tri has no height then just draw the top
            draw(c.getCanvas(),light,top,mid,bot);
            return;
        }
        //if there is both a top and bottom triangle then separate them
        Point altmid = top.lineY(bot,mid.getY());
        draw(c.getCanvas(),light,top,altmid,mid);
        draw(c.getCanvas(),light,bot,altmid,mid);

    }
}