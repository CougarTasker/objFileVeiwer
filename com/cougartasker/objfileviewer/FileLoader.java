package com.cougartasker.objfileviewer;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FileLoader{
    private Component parent;
    FileLoader(Component parent){
        this.parent = parent;
    }
    public File getData() {

            JFileChooser chooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("obj files","obj");
            chooser.setFileFilter(filter);
            if(chooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION){
                return chooser.getSelectedFile();
            }else{
                return null;
            }
    }

    public List<Tri> getTri(){
        return getTri(getData());
    }
    public List<Point> getPoints(File f){
        if (f == null){
            return new ArrayList<Point>();
        }
        Scanner file = null;
        try {
            file = new Scanner(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        List<Point> points = new ArrayList<>();
        while (file.hasNext()) {
            if (file.next().equals("v")) {
                Point point = new Point(file.nextDouble(), file.nextDouble(), file.nextDouble());
                if (points.contains(point)) {
                    //if there are duplicates then remove them because there will be a seam
                    points.add(points.get(points.indexOf(point)));
                } else {
                    points.add(point);
                }
            }
        }
        return points;
    }
    public List<Tri> getTri(File f){
        if (f == null){
            return new ArrayList<Tri>();
        }
        try {
            Scanner file = new Scanner(f);
            List<Point> points = new ArrayList<>();
            List<Tri> out = new ArrayList<Tri>();
            while (file.hasNext()){
                switch (file.next()){
                    case "v":
                        Point point = new Point(file.nextDouble(),file.nextDouble(),file.nextDouble());
                        if(points.contains(point)){
                            //if there are duplicates then remove them because there will be a seam
                            points.add(points.get(points.indexOf(point)));
                        }else{
                            points.add(point);
                        }

                    break;
                    case "f":
                        int a = Integer.parseInt(file.next().split("/")[0]);
                        int b = Integer.parseInt(file.next().split("/")[0]);
                        int c = Integer.parseInt(file.next().split("/")[0]);
                        Tri face = new Tri( points.get(a-1),
                                            points.get(b-1),
                                            points.get(c-1));

                        out.add(face);
                    break;
                    default:
                        //System.out.println("other");
                    break;
                }
            }
            return out;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}