package view;

import controller.IController;
import model.Element;
import org.jdesktop.swingx.JXImagePanel;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

public class View extends JFrame implements Observer {
    private JButton button1;
    private JPanel panel1;
    private JPanel graphique;
    private JPanel gestion;
    private JLabel temp;
    private JSlider slider1;
    private JPanel visuel;
    private JLabel hum;
    private JLabel cons;
    private JXImagePanel image;
    private JProgressBar progressBar1;
    private IController controller;

    public View(final IController controller) {
        this.controller = controller;
        JFrame frame = new JFrame("Frydge Controller");
        frame.setContentPane(panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //System.out.println(new File("/home/tristan/IdeaProjects/PympMyFridge/src/main/resources/frigo_ferme.png").exists());
        File img = new File("/home/tristan/IdeaProjects/PympMyFridge/src/main/resources/frigo_ferme.png");
        System.out.println(img.exists());
        try {
            image.setImage(ImageIO.read(img));
        } catch (IOException e) {
            e.printStackTrace();
        }
        frame.pack();
        frame.setVisible(true);

        button1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                controller.setConsigne(slider1.getValue());
            }
        });
    }

    public void update(Observable observable, Object o) {
        JSONObject message = new JSONObject((String)o);
        temp.setText(message.getString(Element.TEMPERATURE.toString()));
        hum.setText(message.getString(Element.HUMIDITE.toString()));
        cons.setText(message.getString(Element.CONSIGNE.toString()));
    }
}
