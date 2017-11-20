package controller;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import model.Element;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;

public class ArduinoManager implements SerialPortEventListener {
    private List<IArduinoDataReceiver> listeners = new ArrayList<IArduinoDataReceiver>();
    private SerialPort serialPort;
    /** The port we're normally going to use. */
    private static final String PORT_NAMES[] = {
            "/dev/tty.usbserial-A9007UX1", // Mac OS X
            "/dev/ttyACM0", // Raspberry Pi
            "/dev/ttyUSB0", // Linux
            "COM3", // Windows
    };

    private BufferedReader input;
    private OutputStream output;
    private static final int TIME_OUT = 2000;
    private static final int DATA_RATE = 9600;

    public void initialize() throws NotFoundException {
        System.setProperty("gnu.io.rxtx.SerialPorts", "/dev/ttyACM0");
        CommPortIdentifier portId = null;
        Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();

        //First, Find an instance of serial port as set in PORT_NAMES.
        while (portEnum.hasMoreElements()) {
            CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
            for (String portName : PORT_NAMES) {
                if (currPortId.getName().equals(portName)) {
                    portId = currPortId;
                    break;
                }
            }
        }
        if (portId == null) {
            System.out.println("Could not find COM port.");
            throw new NotFoundException();
        }

        try {
            // open serial port, and use class name for the appName.
            serialPort = (SerialPort) portId.open(this.getClass().getName(),
                    TIME_OUT);

            // set port parameters
            serialPort.setSerialPortParams(DATA_RATE,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);

            // open the streams
            input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
            output = serialPort.getOutputStream();

            // add event listeners
            serialPort.addEventListener(this);
            serialPort.notifyOnDataAvailable(true);
        } catch (Exception e) {
            System.err.println(e.toString());
        }
    }

    public synchronized void close() {
        if (serialPort != null) {
            serialPort.removeEventListener();
            serialPort.close();
            System.out.println("SerialPort as been closed");
        }
    }

    public synchronized void serialEvent(SerialPortEvent oEvent)  {
        if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
            try {
                String inputLine=input.readLine();
                System.out.println("Retour arduino: "+inputLine);
                dataReceived(inputLine);
            } catch (Exception e) {
                System.err.println(e.toString());
            }
        }
    }

    public synchronized void sendConsigneToArduino(int consigne) throws IOException {
        JSONObject consigneJSON = new JSONObject();
        consigneJSON.put(Element.CONSIGNE.toString(), consigne);
        output.write(consigneJSON.toString().getBytes());
        output.flush();
        System.out.println("New consigne send "+consigneJSON.toString());
    }

    public void addDataListener(IArduinoDataReceiver listener){
        listeners.add(listener);
    }

    private void dataReceived(String message){
        JSONObject messageJSON = new JSONObject(message);
        listeners.forEach(listener -> listener.onValueChange(messageJSON));
    }

    public void clearListener(){
        listeners.clear();
    }
}
