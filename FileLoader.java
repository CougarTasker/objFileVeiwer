import javax.swing.JFrame;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FileLoader extends JFrame{
    public static void main(String[] args) {
        FileLoader a = new FileLoader();
    }
    private File data = null;
    FileLoader(){
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
    private File getData() {
        if (data == null){
            JFileChooser chooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("obj files","obj");
            chooser.setFileFilter(filter);
            if(chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION){
                this.data = chooser.getSelectedFile();
            }
        }
        return data;
    }

    public List<Tri> getTri(){
        try {
            Scanner file = new Scanner(getData());
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