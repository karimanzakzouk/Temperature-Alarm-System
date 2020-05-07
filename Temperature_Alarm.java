package test;

import eu.hansolo.medusa.Gauge;
import eu.hansolo.medusa.TickLabelOrientation;
import eu.hansolo.medusa.skins.ModernSkin;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPortEventListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import jssc.SerialPort;
import static jssc.SerialPort.MASK_RXCHAR;
import jssc.SerialPortEvent;
import jssc.SerialPortException;
import jssc.SerialPortList;

public class TestJSSC2 extends Application {

    public float value = 0;
    public static Thread th;
    public float threshold = 35;
    Gauge gauge = new Gauge();
    public static int x = 0;
    SerialPort arduinoPort = null;
    ObservableList<String> portList;
    public static Alert alert;

    String path = "C:\\Users\\Martha\\Desktop\\alarm\\fire.mp3";  //2
    //Instantiating Media class  
    Media media = new Media(new File(path).toURI().toString());
    //Instantiating MediaPlayer class   
    MediaPlayer mediaPlayer = new MediaPlayer(media);

    private void detectPort() {

        portList = FXCollections.observableArrayList();

        String[] serialPortNames = SerialPortList.getPortNames();
        for (String name : serialPortNames) {
            System.out.println(name);
            portList.add(name);
        }
    }

    public static void callAlert() {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        alert = new Alert(AlertType.WARNING);//INFORMATION
                        alert.setTitle("Warning ");
                        alert.setContentText("FIRE");
                        alert.showAndWait();
                    }
                });
                return null;
            }
        };
        th = new Thread(task);
        th.setDaemon(true);
        th.start();
    }

    @Override
    public void start(Stage primaryStage) {
        detectPort();
        connectArduino("COM3");
        final ComboBox comboBoxPorts = new ComboBox(portList);
        comboBoxPorts.valueProperty()
                .addListener(new ChangeListener<String>() {
                    @Override
                    public void changed(ObservableValue<? extends String> observable,
                            String oldValue, String newValue) {

                        System.out.println(newValue);
                        disconnectArduino();
                        connectArduino(newValue);
                    }
                });

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(5);
        grid.setVgap(5);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Pane root = new Pane(grid);
        grid.setBackground(new Background(new BackgroundFill(Color.web("0x333333"), CornerRadii.EMPTY, Insets.EMPTY)));
        Scene scene = new Scene(root, 750, 500, Color.web("0x333333"));

        primaryStage.setScene(scene);
        primaryStage.show();

        primaryStage.setTitle("Fire ALarm System");
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();

        gauge.setSkin(new ModernSkin(gauge));
        gauge.setTitle("Current Temperature");
        gauge.setUnit("°C");
        gauge.setUnitColor(Color.WHITE);
        gauge.setDecimals(1);
        gauge.setAnimated(true);
        gauge.setValueColor(Color.WHITE);
        gauge.setTitleColor(Color.WHITE);
        gauge.setSubTitleColor(Color.WHITE);
        gauge.setBarColor(Color.rgb(0, 214, 215));
        gauge.setNeedleColor(Color.RED);
        gauge.setThresholdColor(Color.RED);
        gauge.setThreshold(threshold);
        gauge.setThresholdVisible(true);
        gauge.setTickLabelColor(Color.WHITE);
        gauge.setTickMarkColor(Color.WHITE);
        gauge.setTickLabelOrientation(TickLabelOrientation.ORTHOGONAL);
        gauge.setScaleX(1.5);
        gauge.setScaleY(1.5);
        grid.add(gauge, 30, 10);

        TextField text2 = new TextField();
        grid.add(text2, 5, 9);
        text2.setAlignment(Pos.CENTER);
        text2.setPrefWidth(50);

        TextField text1 = new TextField();
        grid.add(text1, 100, 200);

        Text t2 = new Text("THRESHOLD");
        t2.setFont(Font.font("Verdana", 20));
        t2.setFill(Color.WHITE);
        grid.add(t2, 5, 7);

        Button rect = new Button("Please Enter Threshold");
        rect.setStyle("    -fx-background-color: \n"
                + "        #ff4e00,\n"
                + "        //linear-gradient(#38424b 0%, #1f2429 20%, #191d22 100%),\n"
                + "        linear-gradient(#ff4e00 30%, #ec9f05 80%),\n"
                + "        radial-gradient(center 50% 0%, radius 100%, rgba(255,0,0,0.9), rgba(255,255,255,0));\n"
                + "    -fx-background-radius: 5,4,3,5;\n"
                + "    -fx-background-insets: 0,1,2,0;\n"
                + "    -fx-text-fill: white;\n"
                + "    -fx-effect: dropshadow( three-pass-box  , rgba(0,0,0,0.6) , 5, 0.0 , 0 , 1 );\n"
                + "    -fx-font-family: \"Impact\";\n"
                + "    -fx-font-wight: bold;\n"
                + "    -fx-text-fill: linear-gradient(white, #d0d0d0);\n"
                + "    -fx-font-size: 18px;\n"
                + "    -fx-padding: 15 30 15 30;");

        grid.add(rect, 5, 7);
        Button btn = new Button("SEND");
        btn.setStyle("    -fx-background-color: \n"
                + "        #090a0c,\n"
                + "        linear-gradient(#38424b 0%, #1f2429 20%, #191d22 100%),\n"
                + "        linear-gradient(#20262b, #191d22),\n"
                + "        radial-gradient(center 50% 0%, radius 100%, rgba(114,131,148,0.9), rgba(255,255,255,0));\n"
                + "    -fx-background-radius: 5,4,3,5;\n"
                + "    -fx-background-insets: 0,1,2,0;\n"
                + "    -fx-text-fill: white;\n"
                + "    -fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 5, 0.0 , 0 , 1 );\n"
                + "    -fx-font-family: \"Arial\";\n"
                + "    -fx-text-fill: linear-gradient(white, #d0d0d0);\n"
                + "    -fx-font-size: 12px;\n"
                + "    -fx-padding: 10 20 10 20;");

        grid.add(btn, 5, 10);

        btn.setPrefWidth(100);
        btn.setAlignment(Pos.CENTER);

        Button btn2 = new Button("STOP");
        btn2.setStyle("    -fx-background-color: \n"
                + "        #090a0c,\n"
                + "        linear-gradient(#38424b 0%, #1f2429 20%, #191d22 100%),\n"
                + "        linear-gradient(#20262b, #191d22),\n"
                + "        radial-gradient(center 50% 0%, radius 100%, rgba(114,131,148,0.9), rgba(255,255,255,0));\n"
                + "    -fx-background-radius: 5,4,3,5;\n"
                + "    -fx-background-insets: 0,1,2,0;\n"
                + "    -fx-text-fill: white;\n"
                + "    -fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 5, 0.0 , 0 , 1 );\n"
                + "    -fx-font-family: \"Arial\";\n"
                + "    -fx-text-fill: linear-gradient(white, #d0d0d0);\n"
                + "    -fx-font-size: 12px;\n"
                + "    -fx-padding: 10 20 10 20;");

        grid.add(btn2, 5, 11);
        btn2.setPrefWidth(100);
        btn2.setAlignment(Pos.CENTER);

        btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("send....");

                System.out.println(value);

                threshold = Float.parseFloat(text2.getText());
                gauge.setThreshold(threshold);
                System.out.println(threshold);

                if (value >= threshold) {
                    //by setting this property to true, the audio will be played   
                    mediaPlayer.play();

                } else {
                    mediaPlayer.stop();

                }
            }
        });

        btn2.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("STOP....");

                mediaPlayer.stop();
                threshold = 500;
            }
        });

    }

    public boolean connectArduino(String port) {

        System.out.println("connectArduino");

        boolean success = false;
        SerialPort serialPort = new SerialPort(port);
        try {
            serialPort.openPort();
            serialPort.setParams(
                    SerialPort.BAUDRATE_9600,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);
            serialPort.setEventsMask(MASK_RXCHAR);
            serialPort.addEventListener(new jssc.SerialPortEventListener() {

                public void serialEvent(SerialPortEvent serialPortEvent) {
                    if (serialPortEvent.isRXCHAR()) {
                        try {

                            String st = serialPort.readString(serialPortEvent.getEventValue());
                            System.out.println(st);

                            if (st.toCharArray()[0] == '#' && st.endsWith("\n")) {
                                StringTokenizer token = new StringTokenizer(st.substring(1, st.indexOf('\n')), ",");
                            }
                            value = Float.parseFloat(st);
                            if (value > 15 && value < 80) {
                                System.out.println(value);

                                gauge.setValue(value);
                                if (value >= threshold) {
                                    System.out.println("FIRE");
                                    mediaPlayer.play();
                                    if (x == 0) {
                                        x++;
                                        callAlert();
                                    }

                                } else {
                                    mediaPlayer.stop();

                                }
                            }
                            //Update label in ui thread
                            Platform.runLater(() -> {

                            });

                        } catch (SerialPortException ex) {
                            Logger.getLogger(TestJSSC2.class.getName())
                                    .log(Level.SEVERE, null, ex);
                        }

                    }
                }
            });

            arduinoPort = serialPort;
            success = true;
        } catch (SerialPortException ex) {
            Logger.getLogger(TestJSSC2.class.getName())
                    .log(Level.SEVERE, null, ex);
            System.out.println("SerialPortException: " + ex.toString());
        }

        return success;
    }

    public void disconnectArduino() {

        System.out.println("disconnectArduino()");
        if (arduinoPort != null) {
            try {
                arduinoPort.removeEventListener();

                if (arduinoPort.isOpened()) {
                    arduinoPort.closePort();
                }

            } catch (SerialPortException ex) {
                Logger.getLogger(TestJSSC2.class.getName())
                        .log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void stop() throws Exception {
        disconnectArduino();
        super.stop();
    }

    gnu.io.SerialPort serialPort;

    private final String PORT_NAMES[] = {"COM3"};

    private BufferedReader input;

    private OutputStream output;

    private static final int TIME_OUT = 2000;

    private static final int DATA_RATE = 9600;

    public void initialize() {

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
            return;
        }

        try {
            // open serial port, and use class name for the appName.
            serialPort = (gnu.io.SerialPort) portId.open(this.getClass().getName(),
                    TIME_OUT);

            // set port parameters
            serialPort.setSerialPortParams(DATA_RATE,
                    gnu.io.SerialPort.DATABITS_8,
                    gnu.io.SerialPort.STOPBITS_1,
                    gnu.io.SerialPort.PARITY_NONE);

            // open the streams
            input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
            output = serialPort.getOutputStream();

            // add event listeners
            serialPort.addEventListener((SerialPortEventListener) this);
            serialPort.notifyOnDataAvailable(true);
        } catch (Exception e) {
            System.err.println(e.toString());
        }
    }



    public static void main(String[] args) throws Exception {
        TestJSSC2 main2 = new TestJSSC2();
        launch(args);

        Thread t = new Thread() {
            public void run() {
                try {
                    Thread.sleep(1000000);
                } catch (InterruptedException ie) {

                }
            }
        };
        t.start();
        System.out.println("Started");

    }

}
