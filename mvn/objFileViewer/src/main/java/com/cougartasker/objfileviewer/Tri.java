package com.cougartasker.objfileviewer;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

public class Tri {
  private List<Point> points;
  private Color fill = Color.WHITE;
  private double ambientlevel = 0.2;
  public static Vect light = Vect.Z;

  public Color getFill(double amount) {
    amount = amount * (1 - ambientlevel) + ambientlevel;
    int red = (int) (fill.getRed() * amount);
    int green = (int) (fill.getGreen() * amount);
    int blue = (int) (fill.getBlue() * amount);
    return new Color(red, green, blue);
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

  public Tri() {
    // Random r = new Random();
    // this.fill = new Color(r.nextFloat(),r.nextFloat(),r.nextFloat());

  }

  public Tri(Point a, Point b, Point c) {
    this();
    ArrayList<Point> points = new ArrayList<>();
    points.add(a);
    points.add(b);
    points.add(c);
    setPoints(points);
  }

  public Vect norm() {
    Vect ab = points.get(1).sub(points.get(0));
    Vect ac = points.get(2).sub(points.get(0));
    return ac.cross(ab).unit();
  }

  public double area() {
    double a = Math.abs(points.get(0).sub(points.get(1)).mag());
    double b = Math.abs(points.get(1).sub(points.get(2)).mag());
    double c = Math.abs(points.get(0).sub(points.get(2)).mag());
    double p = (a + b + c) / 2;
    return Math.sqrt(p * (p - a) * (p - b) * (p - c));
  }

  private void draw(DoubleDepthBuffer out, Vect lightDirection, Point start, Point a, Point b) {
    int miny = (int) Math.round(Math.max(Math.min(Math.min(a.getY(), b.getY()), start.getY()), 0));
    int maxy = (int) Math.round(Math.min(Math.max(Math.max(a.getY(), b.getY()), //
        start.getY()), out.getHeight()));
    int startx = (int) Math.round(start.getX());
    int starty = (int) Math.round(start.getY());
    if (miny == maxy || (int) Math.round(a.getX()) == (int) Math.round(b.getX())) {
      if (startx >= 0 && startx < out.getWidth() && starty >= 0 && starty < out.getHeight()) {
        // out.setPixel(startx, starty, 0.0000001, Color.GREEN);
        out.setPixel(startx, starty, start.getZ(), //
            this.getFill(start.getLightingFactor(lightDirection)));
      }
    }
    for (int y = miny; y <= maxy; y++) {
      Point ls = start.lineY(a, y + 0.5);
      Point le = start.lineY(b, y + 0.5);
      double stopax = ls.getX();
      double stopbx = le.getX();
      int minx = (int) Math.round(Math.max(Math.min(stopax, stopbx), 0));
      int maxx = (int) Math.round(Math.min(Math.max(stopax, stopbx), out.getWidth()));
      for (int x = minx; x <= maxx; x++) {
        Point p = ls.lineX(le, x);
        out.setPixel(x, y, p.getZ(), this.getFill(p.getLightingFactor(lightDirection)));
      }
    }
  }

  public void draw(Cam c) {
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
        // c.getCanvas().setPixel((int) out.getX(),(int) out.getY(),0,Color.WHITE);
      }
    });

    for (Vect v : topBottom) {
      if (c.behind(v)) {
        return;
      }
    }

    topBottom.sort(new Comparator<Vect>() {
      @Override
      public int compare(Vect o1, Vect o2) {
        return (int) (o1.getY() - o2.getY());
      }
    });
    Point top = topBottom.get(0);
    Point mid = topBottom.get(1);
    Point bot = topBottom.get(2);

    if (mid.getY() - top.getY() < 1) {
      // if the top tri has no height then just draw the bottom
      draw(c.getCanvas(), light, bot, top, mid);
      return;
    } else if (bot.getY() - mid.getY() < 1) {
      // if the bottom tri has no height then just draw the top
      draw(c.getCanvas(), light, top, mid, bot);
      return;
    }
    // if there is both a top and bottom triangle then separate them
    Point altmid = top.lineY(bot, mid.getY());
    draw(c.getCanvas(), light, top, altmid, mid);
    draw(c.getCanvas(), light, bot, altmid, mid);

  }
}