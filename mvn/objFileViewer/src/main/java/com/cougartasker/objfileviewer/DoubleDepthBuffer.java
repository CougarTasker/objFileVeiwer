package com.cougartasker.objfileviewer;

import java.awt.Color;
import java.awt.Graphics;

public class DoubleDepthBuffer {
  private DepthBuffer bufferA;
  boolean bufferADone = false;
  private DepthBuffer bufferB;
  boolean bufferBDone = false;
  private boolean writinga = true;
  public boolean depthMode = false;

  DoubleDepthBuffer(int width, int height) {
    bufferA = new DepthBuffer(width, height);
    bufferB = new DepthBuffer(width, height);
  }

  /**
   * Set a pixel in the buffer that is currently being written too.
   * 
   * @param x the x position of the pixel
   * @param y the y position of the pixel
   * @param z the depth of the pizel the less the better it must be bigger than
   *          zero
   * @param c the color of the pizel.
   */
  synchronized void setPixel(int x, int y, double z, Color c) {
    start(false);
    if (writinga) {
      bufferA.setPixel(x, y, z, c);
    } else {
      bufferB.setPixel(x, y, z, c);
    }
  }

  /**
   * Clear the buffer being currently written too.
   */
  public void clear() {
    start(false);
    if (writinga) {
      bufferA.clear();
    } else {
      bufferB.clear();
    }
  }

  /**
   * get the lock for reading or writing to the buffer.
   * 
   * @param reading wether you request the reading or writing lock.
   */
  private void start(boolean reading) {
    if (writinga ^ reading) {
      while (bufferADone) {
        try {
          Thread.sleep(1);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    } else {
      while (bufferBDone) {
        try {
          Thread.sleep(1);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }

  }

  /**
   * Paint the buffer that is complete.
   * 
   * @param g the grapics to paint with
   */
  public void paint(Graphics g) {
    start(true);
    if (writinga) {
      bufferB.paint(g, depthMode);
    } else {
      bufferA.paint(g, depthMode);
    }
  }

  /**
   * get the height of the buffer.
   * 
   * @return int the height
   */
  public int getHeight() {
    return bufferA.getHeight();
  }

  /**
   * get the width of the buffer.
   * 
   * @return int the width.
   */
  public int getWidth() {
    return bufferA.getWidth();
  }

  /**
   * relases the reading lock.
   */
  public void doneReading() {
    if (writinga) {
      bufferBDone = true;
    } else {
      bufferADone = true;
    }
    done();
  }

  /**
   * release the writing lock.
   */
  public void doneWriting() {
    if (writinga) {
      bufferADone = true;
    } else {
      bufferBDone = true;
    }
    done();
  }

  private void done() {
    if (bufferADone && bufferBDone) {
      writinga = !writinga;
      bufferADone = false;
      bufferBDone = false;
    }

  }
}
