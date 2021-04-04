package com.cougartasker.objfileviewer;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;

public class Cam extends JComponent implements KeyListener, Runnable {

  private static final long serialVersionUID = 1809920819789073150L;

  private final DoubleDepthBuffer canvas;
  private final int[] resalution = new int[] { 600, 400 };
  private final double fov = Math.PI * 0.7;
  private final double size = 1;// width in the 3d space
  private Vect pos;
  private Vect rot;
  private List<Tri> scene = null;
  private Thread render = null;
  boolean drawCanvas = false;

  Cam(final List<Tri> scene) {
    super();
    canvas = new DoubleDepthBuffer(resalution[0], resalution[1]);
    addKeyListener(this);
    pos = new Vect(0, 0, 0);
    rot = new Vect(0, 0, 0);
    this.scene = scene;
    this.requestFocusInWindow();
    setSize(resalution[0], resalution[1]);
    render = new Thread(this);
    new Thread(new Runnable() {
      @Override
      public void run() {
        while (true) {
          repaint();
          try {
            Thread.sleep(1000 / 60);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
      }
    }).start();
  }

  public final void setPos(final Vect pos) {
    this.pos = pos;
  }

  public final void setPos(final List<Point> pos) {
    Vect bot = Vect.Y.mul(Double.POSITIVE_INFINITY);
    Vect top = Vect.Y.mul(Double.NEGATIVE_INFINITY);
    Vect sum = new Vect(0, 0, 0);
    int count = 0;
    for (Point v : pos) {
      count += 1;
      sum = v.add(sum);
      if (v.getY() > top.getY()) {
        top = v;
      }
      if (v.getY() < bot.getY()) {
        bot = v;
      }
    }
    double objSize = top.sub(bot).mag();
    Vect centre = sum.div(count);
    double d = size * Math.sqrt(1 / (2 - 2 * Math.cos(fov)));
    double h = size * resalution[1] / resalution[0];

    double back = 2 * objSize * d / h;
    this.pos = centre.sub(Vect.Z.mul(back));

  }

  public final void setScene(final List<Tri> scene) {
    this.scene = scene;
  }

  public final DoubleDepthBuffer getCanvas() {
    return canvas;
  }

  @Override
  public final void paint(final Graphics g) {
    super.paint(g);
    // this.canvas.clear();
    // Tri test = new Tri(new Vect(0,0.1,3), new Vect(0,-0.2,3), new Vect(0.4,0,3));
    // test.draw(this);
    // canvas.paint(g);
    int width = resalution[0];
    int height = resalution[1];
    if (drawCanvas) {
      if (!this.render.isAlive()) {
        render = new Thread(this);
        render.start();
      }
      canvas.paint(g);
      canvas.doneReading();
    } else {
      int sf = (int) Math.min(Math.floor(g.getClipBounds().getWidth() / width),
          Math.floor(g.getClipBounds().getHeight() / height));
      if (sf <= 0) {
        sf = 1;
      }
      int offsetx = (int) (g.getClipBounds().getWidth() - sf * width) / 2;
      int offsety = (int) (g.getClipBounds().getHeight() - sf * height) / 2;
      g.drawRoundRect(offsetx, offsety, resalution[0] * sf, resalution[1] * sf, 10, 10);
      List<Tri> cll = cull(scene);
      for (Tri face : cll) {
        List<Point> points = face.getPoints();
        for (int i = 0; i < points.size(); i++) {
          Vect to = project(points.get(i));

          Vect from = project(points.get((i + 1) % points.size()));
          double d = size * Math.sqrt(1 / (2 - 2 * Math.cos(fov)));
          if (to.getZ() > -d && from.getZ() > -d) {
            g.drawLine(offsetx + (int) to.getX() * sf, offsety + (int) to.getY() * sf, offsetx + (int) from.getX() * sf,
                offsety + (int) from.getY() * sf);
          }
        }
      }
    }
  }

  public final boolean behind(final Vect v) {
    double d = size * Math.sqrt(1 / (2 - 2 * Math.cos(fov)));
    return v.getZ() < -d;
  }

  public final List<Tri> cull(final List<Tri> scene) {
    if (scene == null) {
      return new ArrayList<Tri>();
    }
    List<Tri> out = new ArrayList<Tri>();
    for (Tri face : scene) {
      if (face.norm().dot(face.getPoints().get(0).sub(pos)) > 0) {
        out.add(face);
      }
    }
    return out;
  }

  public final void run() {
    this.canvas.clear();
    List<Tri> cll = cull(scene);
    Tri.light = Tri.light.rotate(Vect.Y, 0.1);
    for (Tri face : cll) {
      face.draw(this);
    }
    this.canvas.doneWriting();
  }

  /**
   * Project a vocotor to camera space.
   * 
   * @param p the vecotor in world space.
   * @return Vect the vecotor in camera space
   */
  public final Vect project(final Vect p) {
    Vect out = p.sub(this.pos);
    out = out.rotate(this.rot);
    double d = size * Math.sqrt(1 / (2 - 2 * Math.cos(fov)));
    out = out.sub(Vect.Z.mul(d));
    out.setX(out.getX() * d / (out.getZ() + d));
    out.setY(out.getY() * d / (out.getZ() + d));

    double h = (double) (resalution[1]) / (double) (resalution[0]) * size;
    out = out.add(new Vect(size / 2, -h / 2, 0));// move point into depth buffer space
    out = out.mul((double) (resalution[0]) / size);// scale
    out.setY(out.getY() * -1);
    return out;
  }

  /**
   * Invoked when a key has been typed. See the class description for
   * {@link KeyEvent} for a definition of a key typed event.
   *
   * @param e the event to be processed
   */
  @Override
  public void keyTyped(final KeyEvent e) {

  }

  /**
   * Invoked when a key has been pressed. See the class description for
   * {@link KeyEvent} for a definition of a key pressed event.
   *
   * @param e the event to be processed
   */
  @Override
  public void keyPressed(final KeyEvent e) {
    switch (KeyEvent.getKeyText(e.getKeyCode())) {
      case "A":
        rot = this.rot.add(Vect.Y.mul(Math.PI / 100));
        break;
      case "D":
        rot = this.rot.add(Vect.Y.mul(-Math.PI / 100));
        break;
      case "W":
        rot = this.rot.add(Vect.X.mul(Math.PI / 100));
        break;
      case "S":
        rot = this.rot.add(Vect.X.mul(-Math.PI / 100));
        break;
      case "E":
        rot = this.rot.add(Vect.Z.mul(-Math.PI / 100));
        break;
      case "Q":
        rot = this.rot.add(Vect.Z.mul(Math.PI / 100));
        break;
      case "I":
      case "Up":
        pos = this.pos.add(Vect.Z.antiRotate(rot).mul(size / 7));
        break;
      case "K":
      case "Down":
        pos = this.pos.sub(Vect.Z.antiRotate(rot).mul(size / 7));
        break;
      case "L":
      case "Right":
        pos = this.pos.add(Vect.X.antiRotate(rot).mul(size / 7));
        break;
      case "J":
      case "Left":
        pos = this.pos.sub(Vect.X.antiRotate(rot).mul(size / 7));
        break;
      case "O":
      case "Shift":
        pos = this.pos.add(Vect.Y.antiRotate(rot).mul(size / 7));
        break;
      case "U":
        pos = this.pos.sub(Vect.Y.antiRotate(rot).mul(size / 7));
        break;
      case "C":
        drawCanvas = !drawCanvas;
        // resalution[0]+=1;
        break;
      case "X":
        canvas.depthMode = !canvas.depthMode;
        // resalution[0]+=1;
        break;
      default:
        break;
    }
    repaint();
    System.out.println(KeyEvent.getKeyText(e.getKeyCode()));
  }

  /**
   * Get the current rotation of the camera.
   * 
   * @return Vect the rotation vector.
   */
  public final Vect getRot() {
    return rot;
  }

  @Override
  public void keyReleased(KeyEvent arg0) {
    // TODO Auto-generated method stub
    
  }

}
