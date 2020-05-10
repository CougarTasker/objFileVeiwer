
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static java.awt.image.ImageConsumer.STATICIMAGEDONE;

public class DepthBuffer{
    private int width;
    private int height;
    private Color[][] img;
    private double[][] depth;
    DepthBuffer(int width, int height){
        super();
        this.width = width;
        this.height = height;
        img = new Color[width][height];
        depth = new double[width][height];
    }

    public void clear(){
       img = new Color[width][height];
    }
    synchronized void setPixel(int x,int y,double z,Color c){
        if(x<0 || x>= width || y<0 || y>=height){
            return;
        }
        if(depth[x][y] > z && z >0|| img[x][y] == null){
            depth[x][y] = z;
            img[x][y] = c;
        }
    }
    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public void paint(Graphics g) {
        int sf = 1;
        if(g.getClipBounds().getHeight()/g.getClipBounds().getWidth() < width/height){
            sf = (int) Math.floor(g.getClipBounds().getHeight()/height);
        }else{
            sf = (int) Math.floor(g.getClipBounds().getWidth()/width);
        }
        if(sf <= 0){
            sf=1;
        }
        int offsetx = (int)(g.getClipBounds().getWidth()-sf*width)/2;
        int offsety = (int)(g.getClipBounds().getHeight()-sf*height)/2;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if(img[x][y]!= null){
                    g.setColor(img[x][y]);
                }else{
                    g.setColor(Color.BLACK);
                }
                g.fillRect(offsetx+x*sf,offsety+y*sf,sf,sf);
            }
        }
    }
}
