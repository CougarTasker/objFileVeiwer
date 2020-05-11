package com.cougartasker.objfileviewer;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import com.formdev.flatlaf.FlatDarkLaf;

public class objFileViewer extends JFrame implements ActionListener {
    private Cam view;
    objFileViewer(){
        super();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(10,10,10,10);
        c.anchor = GridBagConstraints.LINE_START;
        c.weightx =1;
        c.weighty =0;

        JLabel title = new JLabel();
        title.setText("3D File Viewer");
        Font f = new Font(title.getFont().getName(),Font.BOLD,20);
        title.setFont(f);
        add(title,c);

        c.anchor = GridBagConstraints.LINE_END;
        c.gridx = 1;
        JButton select = new JButton();
        select.setText("select file");
        select.addActionListener(this);
        add(select,c);

        c.anchor = GridBagConstraints.LINE_START;
        c.fill = GridBagConstraints.BOTH;

        view = new Cam(new ArrayList<Tri>());
        c.gridx=0;
        c.gridy=1;
        c.gridwidth = 2;
        c.weighty =1;
        add(view,c);

        c.weighty =0;
        c.gridy=2;
        c.gridx =0;
        JLabel instructions = new JLabel();
        instructions.setText(
                "<html><p style=\"padding:0;margin:0;\">\n" +
                "this is a simple .obj 3d file viewer with smooth shading. to rotate the camera use the wasd keys and to pan(move) the camera press the arrow keys.\n" +
                "</p></html>"
        );
        add(instructions,c);

        JLabel message = new JLabel();
        message.setText("Cougar Tasker 2020");
        c.gridy=3;
        add(message,c);
        setSize(650,600);
        setVisible(true);
        view.requestFocusInWindow();

    }
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel( new FlatDarkLaf() );
        } catch( Exception ex ) {
            System.err.println( "Failed to initialize LaF" );
        }
        objFileViewer t = new objFileViewer();
    }

    /**
     * Invoked when an action occurs.
     *
     * @param e the event to be processed
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        FileLoader fl = new FileLoader(this);
        File f = fl.getData();
        view.setScene(fl.getTri(f));
        view.setPos(fl.getPoints(f));
        view.requestFocusInWindow();
    }
}
