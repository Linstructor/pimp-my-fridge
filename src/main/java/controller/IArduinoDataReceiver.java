package controller;

import org.json.JSONObject;

public interface IArduinoDataReceiver {
    void onValueChange(JSONObject message);
}
