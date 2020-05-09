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
            List<Vect> points = new ArrayList<Vect>();
            List<Tri> out = new ArrayList<Tri>();
            while (file.hasNext()){
                switch (file.next()){
                    case "v":
                        Vect point = new Vect(file.nextDouble(),file.nextDouble(),file.nextDouble());
                        points.add(point);
                    break;
                    case "f":
                        Tri face = new Tri( points.get(file.nextInt()-1),
                                            points.get(file.nextInt()-1),
                                            points.get(file.nextInt()-1));

                        out.add(face);
                    break;
                    default:
                        System.out.println("other");
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