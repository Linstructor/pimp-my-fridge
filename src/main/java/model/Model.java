package model;

import org.json.JSONObject;

import java.util.Observable;

public class Model extends Observable implements IModel {
    private int consigne = 15;
    private double temperature = 0.0;
    private double humidite = 0.0;
    private double roseePointAct = 0.0;
    private double temperature_ext = 0.0;
    private double roseePointPrev = 0.0;
    private boolean porte = false;
    private double goal = 0.0;

    private boolean arduinoConnectionState;

    public boolean isArduinoConnected(){
        return arduinoConnectionState;
    }

    @Override
    public void setArduinoConnectionState(boolean etat) {
        this.arduinoConnectionState = etat;
        changeUI();
    }

    public void setNewValeur(double temperature, double humidite, double roseePointAct, double temperature_ext){
        this.temperature = temperature;
        this.humidite = humidite;
        this.roseePointAct = roseePointAct;
        this.temperature_ext = temperature_ext;
        changeUI();
    }

    public void setConsigne(int valeur){
        consigne = valeur;
        roseePointPrev = calculRosee();
        changeUI();
    }

    private Double calculRosee(){
        //TODO faire le calcul du point de rosée
        return 1.1;
    }

    public void setPorteState(boolean state){
        porte = state;
        changeUI();
    }

    @Override
    public void setGoal(double goal) {
        this.goal = goal;
        changeUI();
    }

    private void changeUI(){
        this.setChanged();
        JSONObject modif = new JSONObject();
        modif.put(Element.ETAT.toString(), arduinoConnectionState);
        modif.put(Element.PORTE.toString(), porte);
        modif.put(Element.CONSIGNE.toString(), consigne+"");
        modif.put(Element.HUMIDITE.toString(), humidite+"");
        modif.put(Element.TEMPERATURE.toString(), temperature+"");
        modif.put(Element.ROSEE.toString(), roseePointAct+"");
        modif.put(Element.ETAT.toString(), arduinoConnectionState);
        modif.put(Element.TEMPERATURE_EXT.toString(), temperature_ext);

        this.notifyObservers(modif.toString());
    }

}
