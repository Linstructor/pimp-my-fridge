package view;

import com.jfoenix.controls.*;
import controller.IController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Element;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.nio.file.Paths;
import java.util.Observable;
import java.util.Observer;

public class UI extends Application implements Observer{

    private Parent rootLayout;
    private IController adapter;

    @FXML
    private Pane affichage;
    @FXML
    private JFXToggleButton on_off;
    @FXML
    private JFXSlider slider;
    @FXML
    private Label temp , hum, cons, loading_pourcentage, arduino_etat;
    @FXML
    private LineChart<String, Number> tempChart, humChart;
    @FXML
    private StackPane fullstack;
    @FXML
    private JFXButton send, reconnect_button;
    @FXML
    private Pane condensation_panel;
    @FXML
    private ImageView frigo;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Circle etat_round;
    private XYChart.Series tempSeries = new XYChart.Series();
    private XYChart.Series humSeries = new XYChart.Series();
    private Image photo_ouverte, photo_ferme;

    private double goal;
    private double temperature;

    private JFXDialog dialog;


    public UI(IController adapter) {
        this.adapter = adapter;
        File img = new File("src/main/resources/frigo_ferme.png");
        try {
            photo_ferme = new Image(img.toURL().toString());
            img = new File("src/main/resources/frigo_ouvert.png");
            photo_ouverte = new Image(img.toURL().toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage primaryStage) {
        initUI(primaryStage);
        adapter.connectToArduino();

    }

    private void initUI(Stage primaryStage){
        FXMLLoader loader = new FXMLLoader();
        try {
            loader.setLocation(Paths.get("src/main/java/view/UI.fxml").toUri().toURL());
            loader.setController(this);
            rootLayout = loader.load();
            primaryStage.setTitle("PimpMyFridge");
            primaryStage.setScene(new Scene(rootLayout));
            primaryStage.setResizable(false);
            primaryStage.show();
            primaryStage.setOnCloseRequest(e -> {
                try {
                    stop();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            });
            reconnect_button.setOnAction(event -> {
                adapter.retryArduinoConnection();
            });
            frigo.setImage(photo_ferme);
            tempChart.getData().add(tempSeries);
            tempChart.getYAxis().setAutoRanging(false);
            humChart.getData().add(humSeries);
            tempChart.getXAxis().setTickLabelsVisible(false);
            tempChart.getXAxis().setTickLength(0.0);
            humChart.getXAxis().setTickLabelsVisible(false);
            humChart.getXAxis().setTickLength(0.0);
            for (int i = 0; i<25; i++){
                tempSeries.getData().add(new XYChart.Data(""+i,0));
                humSeries.getData().add(new XYChart.Data(""+i,0));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    int count = 25;
    private void updateChart(double value, XYChart.Series series){
        series.getData().add(new XYChart.Data(""+count++,value));
        series.getData().remove(0);
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        System.out.println("Fermeture de la fenetre");
        adapter.quit();
    }

    @FXML
    private void onSendCLick() {
        adapter.setConsigne((int) slider.getValue() + 1);
        progressBar.setProgress(0.0);
        goal = temperature;
    }

    @Override
    public void update(Observable o, Object arg) {
        JSONObject message = new JSONObject((String)arg);
        Platform.runLater(()->{
            if (!message.getBoolean(Element.ETAT.toString())){
                temp.setText("--");
                hum.setText("--");
                cons.setText("--");
                etat_round.setFill(Paint.valueOf("#f80000"));
                arduino_etat.setText("Arduino non connecté");
                arduino_etat.setTextFill(Paint.valueOf("#f80000"));
                send.setDisable(true);
                createDialog();
                condensation_panel.setVisible(true);
            }else{
                refreshUI(message);
            }
            if (message.getBoolean(Element.PORTE.toString())){
                frigo.setImage(photo_ouverte);
            }else{
                frigo.setImage(photo_ferme);
            }
            if (progressBar.getProgress() <= 1){
                loading_pourcentage.setText(new BigDecimal(progressBar.getProgress()*100).setScale(0, BigDecimal.ROUND_CEILING).doubleValue()+" %");
            }
        });
    }

    private void refreshUI(JSONObject message) {
        temperature = message.getDouble(Element.TEMPERATURE.toString());
        temperature = new BigDecimal(temperature).setScale(2, BigDecimal.ROUND_DOWN).doubleValue();
        progressBar.setProgress(Math.abs(goal - temperature) / Math.abs(goal - message.getDouble(Element.CONSIGNE.toString())));
        temp.setText(temperature + " °C");
        hum.setText(message.getString(Element.HUMIDITE.toString())+ " %");
        cons.setText(message.getString(Element.CONSIGNE.toString())+" °C");
        updateChart(message.getDouble(Element.TEMPERATURE.toString()), tempSeries);
        updateChart(message.getDouble(Element.HUMIDITE.toString()), humSeries);
        etat_round.setFill(Paint.valueOf("#11ab3f"));
        arduino_etat.setText("Arduino connecté");
        arduino_etat.setTextFill(Paint.valueOf("#11ab3f"));
        reconnect_button.setVisible(false);
        send.setDisable(false);
        if (message.getDouble(Element.TEMPERATURE.toString()) < message.getDouble(Element.ROSEE.toString())){
            condensation_panel.setVisible(true);
        }else{
            condensation_panel.setVisible(false);
        }
    }

    private void createDialog() {
        dialog = new JFXDialog();
        JFXDialogLayout dialogLayout = new JFXDialogLayout();
        dialog = new JFXDialog(fullstack, dialogLayout, JFXDialog.DialogTransition.CENTER);
        dialogLayout.setHeading(new Text("Erreur de connection"));
        dialogLayout.setBody(new Text("Impossible de se connecter à l'arduino, veuillez vérifier si la carte arduino est connectée à l'ordinateur"));
        JFXButton close = new JFXButton("Annuler");
        close.setOnAction(event -> {
            reconnect_button.setVisible(true);
            dialog.close();
        });
        JFXButton reconnect = new JFXButton("Recommencer");
        reconnect.setOnAction(event -> {
            dialog.close();
            adapter.retryArduinoConnection();
        });
        dialogLayout.setActions(reconnect, close);
        dialog.show();
    }
}
