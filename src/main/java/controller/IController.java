package controller;

import java.io.IOException;

public interface IController {
    void setConsigne(int consigne);
    void quit();
    void retryArduinoConnection();
    void connectToArduino();
    void setGoal(double goal);
}
