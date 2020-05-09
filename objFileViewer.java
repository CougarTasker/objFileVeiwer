import javax.swing.*;
import java.awt.*;
import java.util.List;

public class objFileViewer extends JFrame{
    private List<Tri> face = null;
    private Cam view = null;
    objFileViewer(){
        super();
        FileLoader fl = new FileLoader();
        this.face = fl.getTri();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        view = new Cam(face);
        add(view);
        setVisible(true);
        view.requestFocusInWindow();

    }
    public static void main(String[] args) {
        objFileViewer t = new objFileViewer();
    }
}
