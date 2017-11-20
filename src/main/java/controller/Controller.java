package controller;

import model.Element;
import model.IModel;
import org.json.JSONObject;

import java.io.IOException;

public class Controller implements IController, IArduinoDataReceiver {

    private IModel model;
    private ArduinoManager arduinoManager = new ArduinoManager();

    public Controller(IModel model) {
        this.model = model;
    }

    private void changeValeur(JSONObject message){
        double temperature = message.getDouble(Element.TEMPERATURE.toString());
        //double temperature_ext = message.getDouble(Element.TEMPERATURE_EXT.toString());
        double temperature_ext = 0.0;
        double humidite = message.getDouble(Element.HUMIDITE.toString());
        double roseePoint = message.getDouble(Element.ROSEE.toString());
        model.setNewValeur(temperature, humidite,roseePoint, temperature_ext);
    }

    public void setConsigne(int consigne) {
        if (model.isArduinoConnected()){
            model.setConsigne(consigne);
            sendDataArduino(consigne);
        }
    }

    public void connectToArduino(){
        System.out.println("Try connection to arduino");
        arduinoManager.addDataListener(this);
        try {
            arduinoManager.initialize();
            model.setArduinoConnectionState(true);
        } catch (NotFoundException e) {
            model.setArduinoConnectionState(false);
            arduinoManager.clearListener();
            System.err.println("Aucune connection avec une arduino");
        }
    }

    @Override
    public void setGoal(double goal) {
        model.setGoal(goal);
    }

    @Override
    public void quit() {
        arduinoManager.close();
    }

    @Override
    public void retryArduinoConnection() {
        connectToArduino();
    }

    private void setPortState(boolean state) {
        model.setPorteState(state);
    }

    private void sendDataArduino(int consigne){
        try {
            arduinoManager.sendConsigneToArduino(consigne);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onValueChange(JSONObject message) {
        System.out.println("New value");
        boolean porteStatus = message.getInt(Element.PORTE.toString()) == 1;
        setPortState(porteStatus);
        changeValeur(message);
    }
}
