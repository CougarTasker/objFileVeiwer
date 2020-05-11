import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

public class Cam extends JComponent implements KeyListener, Runnable {
    private final DoubleDepthBuffer canvas;
    private final int[] resalution = new int[]{600,400};
    private final double fov = Math.PI *0.7;
    private final double size = 1;//width in the 3d space
    private Vect pos;
    private Vect rot;
    private List<Tri> scene = null;
    private Thread render = null;
    boolean drawCanvas = false;
    Cam(List<Tri> scene){
        super();
        canvas = new DoubleDepthBuffer(resalution[0],resalution[1]);
        addKeyListener(this);
        pos=new Vect(0,0,0);
        rot=new Vect(0,0,0);
        this.scene = scene;
        this.requestFocusInWindow();
        setSize(resalution[0],resalution[1]);
        render=new Thread(this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    repaint();
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public void setPos(Vect pos) {
        this.pos = pos;
    }
    public void setPos(List<Point> pos){
        Vect bot = Vect.Y.mul(Double.POSITIVE_INFINITY);
        Vect top = Vect.Y.mul(Double.NEGATIVE_INFINITY);
        Vect sum = new Vect(0,0,0);
        int count = 0;
        for(Point v: pos){
            count +=1;
            sum = v.add(sum);
            if(v.getY() > top.getY()){
                top = v;
            }
            if(v.getY() < bot.getY()){
                bot = v;
            }
        }
        double objSize = top.sub(bot).mag();
        Vect centre = sum.div(count);
        double d = size *Math.sqrt(1/(2-2*Math.cos(fov)));
        double h = size*resalution[1]/resalution[0];

        double back = 2*objSize*d/h;
        this.pos = centre.sub(Vect.Z.mul(back));

    }


    public void setScene(List<Tri> scene) {
        this.scene = scene;
    }

    public DoubleDepthBuffer getCanvas() {
        return canvas;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
//        this.canvas.clear();
//        Tri test = new Tri(new Vect(0,0.1,3), new Vect(0,-0.2,3), new Vect(0.4,0,3));
//        test.draw(this);
//        canvas.paint(g);

        if(drawCanvas){
            if(!this.render.isAlive()){
                render=new Thread(this);
                render.start();
            }
            canvas.paint(g);
            canvas.doneReading();
        }else{
            int offsetx = (g.getClipBounds().width - resalution[0])/2;
            int offsety = (g.getClipBounds().height - resalution[1])/2;

            g.drawRoundRect(offsetx,offsety,resalution[0],resalution[1],10,10);
            List<Tri> cll = cull(scene);
            for(Tri face:cll){
                List<Point> points = face.getPoints();
                for (int i = 0; i < points.size(); i++) {
                    Vect to = project(points.get(i));

                    Vect from = project(points.get((i + 1) % points.size()));
                    double d = size *Math.sqrt(1/(2-2*Math.cos(fov)));
                    if(to.getZ() >-d && from.getZ()>-d) {
                        g.drawLine(
                                offsetx + (int) to.getX()   ,offsety+(int) to.getY(),
                                offsetx + (int) from.getX() ,offsety+(int) from.getY()
                        );
                    }
                }
            }
        }
    }
    public boolean behind(Vect v){
        double d = size *Math.sqrt(1/(2-2*Math.cos(fov)));
        return v.getZ() < -d;
    }
    public List<Tri> cull(List<Tri> scene){
        if(scene == null){
            return new ArrayList<Tri>();
        }
        List<Tri> out = new ArrayList<Tri>();
        for(Tri face : scene){
            if(face.norm().dot(face.getPoints().get(0).sub(pos))>0){
                out.add(face);
            }
        }
        return out;
    }
    public void run(){
        this.canvas.clear();
        List<Tri> cll = cull(scene);
        for(Tri face : cll){
            face.draw(this);
        }
        this.canvas.doneWriting();
    }
    public Vect project(Vect p){
        Vect out = p.sub(this.pos);
        out = out.rotate(this.rot);
        double d = size *Math.sqrt(1/(2-2*Math.cos(fov)));
        out = out.sub(Vect.Z.mul(d));
        out.setX(out.getX()*d/(out.getZ()+d));
        out.setY(out.getY()*d/(out.getZ()+d));

        double h =  (double) (resalution[1])/(double)(resalution[0])*size;
        out = out.add(new Vect(size/2,-h/2,0));//move point into depth buffer space
        out = out.mul((double) (resalution[0])/size);//scale
        out.setY(out.getY()*-1);
        return out;
    }

    /**
     * Invoked when a key has been typed.
     * See the class description for {@link KeyEvent} for a definition of
     * a key typed event.
     *
     * @param e the event to be processed
     */
    @Override
    public void keyTyped(KeyEvent e) {

    }

    /**
     * Invoked when a key has been pressed.
     * See the class description for {@link KeyEvent} for a definition of
     * a key pressed event.
     *
     * @param e the event to be processed
     */
    @Override
    public void keyPressed(KeyEvent e) {
        switch (KeyEvent.getKeyText(e.getKeyCode())){
            case "A":
                rot = this.rot.add(Vect.Y.mul(Math.PI/100));
            break;
            case "D":
                rot = this.rot.add(Vect.Y.mul(-Math.PI/100));
            break;
            case "W":
                rot = this.rot.add(Vect.X.mul(Math.PI/100));
            break;
            case "S":
                rot = this.rot.add(Vect.X.mul(-Math.PI/100));
            break;
            case "E":
                rot = this.rot.add(Vect.Z.mul(-Math.PI/100));
                break;
            case "Q":
                rot = this.rot.add(Vect.Z.mul(Math.PI/100));
                break;
            case "I":
            case "Up":
                pos = this.pos.add(Vect.Z.antiRotate(rot).mul(size/7));
                break;
            case "K":
            case "Down":
                pos = this.pos.sub(Vect.Z.antiRotate(rot).mul(size/7));
                break;
            case "L":
            case "Right":
                pos = this.pos.add(Vect.X.antiRotate(rot).mul(size/7));
                break;
            case "J":
            case "Left":
                pos = this.pos.sub(Vect.X.antiRotate(rot).mul(size/7));
                break;
            case "O":
            case "Shift":
                pos = this.pos.add(Vect.Y.antiRotate(rot).mul(size/7));
                break;
            case "U":
                pos = this.pos.sub(Vect.Y.antiRotate(rot).mul(size/7));
                break;
            case "C":
                drawCanvas = !drawCanvas;
                //resalution[0]+=1;
                break;
        }
        repaint();
        System.out.println(KeyEvent.getKeyText(e.getKeyCode()));
    }

    public Vect getRot() {
        return rot;
    }

    /**
     * Invoked when a key has been released.
     * See the class description for {@link KeyEvent} for a definition of
     * a key released event.
     *
     * @param e the event to be processed
     */
    @Override
    public void keyReleased(KeyEvent e) {

    }
}
