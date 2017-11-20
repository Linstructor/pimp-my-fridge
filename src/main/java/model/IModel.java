package model;

public interface IModel {
    void setNewValeur(double temperature, double humidite, double roseePointAct, double temperature_ext);
    boolean isArduinoConnected();
    void setArduinoConnectionState(boolean etat);
    void setConsigne(int consigne);
    void setPorteState(boolean etat);
    void setGoal(double goal);

}
