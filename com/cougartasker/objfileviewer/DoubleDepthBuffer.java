package com.cougartasker.objfileviewer;
import java.awt.*;

public class DoubleDepthBuffer{
    private DepthBuffer a;
    boolean aDone = false;
    private DepthBuffer b;
    boolean bDone = false;
    private boolean writinga = true;
    public boolean depthMode = false;
    DoubleDepthBuffer(int width, int height){
        a = new DepthBuffer(width,height);
        b = new DepthBuffer(width,height);
    }
    synchronized void setPixel(int x, int y, double z, Color c){
        start(false);
        if(writinga){
            a.setPixel(x,y,z,c);
        }else{
            b.setPixel(x,y,z,c);
        }
    }
    public void clear(){
        start(false);
        if(writinga){
            a.clear();
        }else{
            b.clear();
        }
    }
    private void start(boolean reading){
        if(writinga ^ reading){
            while (aDone){
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }else{
            while(bDone){
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }
    public void paint(Graphics g){
        start(true);
        if(writinga){
            b.paint(g,depthMode);
        }else{
            a.paint(g,depthMode);
        }
    }
    public int getHeight() {
        return a.getHeight();
    }
    public int getWidth() {
        return a.getWidth();
    }
    public void doneReading(){
        if(writinga){
            bDone=true;
        }else{
            aDone=true;
        }
        done();
    }
    public void doneWriting(){
        if(writinga){
            aDone=true;
        }else{
            bDone=true;
        }
        done();
    }

    private void done(){
        if(aDone && bDone){
            writinga = !writinga;
            aDone = false;
            bDone = false;
        }

    }
}
