package arduino.serialport;

import arduino.entidades.Mensaje;
import arduino.eventos.RespuestaEventListener;
import arduino.logs.Debugger;
import arduino.serialport.exception.PuertosVaciosException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.comm.*;
import java.io.*;
import java.util.*;
import javax.comm.SerialPortEventListener;

public class Rs232 implements SerialPortEventListener {

    private static CommPortIdentifier portId;
    private InputStream inputStream;
    private OutputStream outputStream;
    private SerialPort serialPort;
    public String Puerto;
    protected static HashMap map = new HashMap();
    private ArrayList comList = new ArrayList();
    private boolean portOpen = false;
    private static Rs232 instancia = null;
    private RespuestaEventListener respuesta_event_listener;

    protected Rs232() {
        
    }

    public static Rs232 instancia() {
        if (instancia == null) {
            instancia = new Rs232();

        }
        return instancia;
    }

    public void ini() throws NoSuchPortException, PortInUseException, TooManyListenersException, IOException, UnsupportedCommOperationException, PuertosVaciosException {
        comList = this.getPortsName();
        if (comList.isEmpty()) {
            throw new PuertosVaciosException();
        }
        portId = CommPortIdentifier.getPortIdentifier(comList.get(0).toString());
        Debugger.debug("------abre puerto " + comList.get(0).toString() + "------------");
        serialPort = (SerialPort) portId.open("ComControl", 2000);
        serialPort.addEventListener(this);
        inputStream = serialPort.getInputStream();
        outputStream = serialPort.getOutputStream();
        serialPort.notifyOnDataAvailable(true);
        serialPort.notifyOnCTS(true);
        serialPort.notifyOnDSR(true);
        serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.FLOWCONTROL_RTSCTS_IN, SerialPort.PARITY_NONE);
        portOpen = true;

    }

    @SuppressWarnings("static-access")
    public void serialEvent(SerialPortEvent event) {
        switch (event.getEventType()) {
            case SerialPortEvent.BI:
            case SerialPortEvent.OE:
            case SerialPortEvent.FE:
            case SerialPortEvent.PE:
            case SerialPortEvent.CD:
            case SerialPortEvent.CTS:
            case SerialPortEvent.DSR:
            case SerialPortEvent.RI:
            case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
            case SerialPortEvent.DATA_AVAILABLE:
                if(portOpen){
                try {
                    while ((portOpen)&&(inputStream.available() > 0)) {
                        Mensaje.getInstancia().getMensaje((byte) inputStream.read());
                    }
                } catch (IOException ex) {
                    Logger.getLogger(Rs232.class.getName()).log(Level.SEVERE, null, ex);
                }
                }
        }

    }

    private ArrayList getPortsName() {
        ArrayList Serie = new ArrayList();
        Enumeration pList = CommPortIdentifier.getPortIdentifiers();
        while (pList.hasMoreElements()) {
            CommPortIdentifier cpi = (CommPortIdentifier) pList.nextElement();
            map.put(cpi.getName(), cpi);
            if (cpi.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                Serie.add(cpi.getName());
            }
        }

        return Serie;

    }

    public void close() throws IOException {
        portOpen = false;
        inputStream.close();
        outputStream.close();
        serialPort.close();
        
        
        Debugger.debug("------cierra puerto " + comList.get(0).toString() + "------------");
    }

    public boolean isPortOpen() {
        return portOpen;
    }

    public RespuestaEventListener getRespuesta_event_listener() {
        return respuesta_event_listener;
    }

    public void setRespuesta_event_listener(RespuestaEventListener _respuesta_event_listener) {
        this.respuesta_event_listener = _respuesta_event_listener;
        Mensaje.getInstancia().setRespuestaEventListener(_respuesta_event_listener);
    }
}
