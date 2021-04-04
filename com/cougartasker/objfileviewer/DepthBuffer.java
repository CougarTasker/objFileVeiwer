package com.cougartasker.objfileviewer;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static java.awt.image.ImageConsumer.STATICIMAGEDONE;

public class DepthBuffer {
  private int width;
  private int height;
  private Color[][] img;
  private double[][] depth;

  DepthBuffer(int width, int height) {
    super();
    this.width = width;
    this.height = height;
    img = new Color[width][height];
    depth = new double[width][height];
  }

  public void clear() {
    img = new Color[width][height];
  }

  synchronized void setPixel(int x, int y, double z, Color c) {
    if (x < 0 || x >= width || y < 0 || y >= height) {
      return;
    }
    if (depth[x][y] > z && z > 0 || img[x][y] == null) {
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

  private double[] getMaxDepth() {
    double[] out = new double[] { Double.MAX_VALUE, 0 };
    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        if (img[x][y] != null) {
          if (out[0] > depth[x][y]) {
            out[0] = depth[x][y];
          } else if (out[1] < depth[x][y]) {
            out[1] = depth[x][y];
          }
        }

      }
    }
    return out;
  }
  
  public void paint(Graphics g) {
    paint(g, false);
  }
  public void paint(Graphics g,boolean depthMode) {
    int sf = (int) Math.min(Math.floor(g.getClipBounds().getWidth() / width),
        Math.floor(g.getClipBounds().getHeight() / height));
    if (sf <= 0) {
      sf = 1;
    }
    int offsetx = (int) (g.getClipBounds().getWidth() - sf * width) / 2;
    int offsety = (int) (g.getClipBounds().getHeight() - sf * height) / 2;
    if (depthMode) {
      double[] minmax = getMaxDepth();
      double range = 1/(minmax[1] - minmax[0]);
      double min = minmax[0];
      for (int x = 0; x < width; x++) {
        for (int y = 0; y < height; y++) {
          if (img[x][y] != null) {
            
            g.setColor(Color.getHSBColor((float) ((depth[x][y] - min) * range), 1, 1));
            g.fillRect(offsetx + x * sf, offsety + y * sf, sf, sf);
          }

        }
      }
    } else {
      for (int x = 0; x < width; x++) {
        for (int y = 0; y < height; y++) {
          if (img[x][y] != null) {
            g.setColor(img[x][y]);
            g.fillRect(offsetx + x * sf, offsety + y * sf, sf, sf);
          }

        }
      }
    }
    g.setColor(Color.BLUE);
    g.drawRoundRect(offsetx, offsety, sf * width, sf * height, 10, 10);
  }
}
