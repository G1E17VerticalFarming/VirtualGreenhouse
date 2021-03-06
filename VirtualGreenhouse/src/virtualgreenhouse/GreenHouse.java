package virtualgreenhouse;

import interfaces.IGreenhouse;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.BitSet;
import java.util.Enumeration;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * @author chris
 */
public class GreenHouse implements IGreenhouse, ActionListener, PropertyChangeListener {

    private double temp1 = 11;
    private double temp2 = 21;
    private AtomicInteger desiredTemp = new AtomicInteger();
    private double moisture = 45;
    private double waterLevel = 0;
    private double plantHeight = 0.0;
    private int fanSpeed = 0;
    private int fertiliser = 0;
    private boolean heatingElement;
    private int blueLightLevel = 0;
    private int redLightLevel = 0;
    private int port = 0;
    private String ip;

    public GreenHouse() {
        initialize();
    }

    private static GreenHouse greenhouse = new GreenHouse();

    public static GreenHouse getInstance() throws SocketException {
        greenhouse.setIp();
        return greenhouse;
    }

    /**
     * The initialize method schedules the timers that should always be active.
     * The natureTimer is responsible for the simulated natural decay. All variables can be changed to match the wanted simulation, speed or experiment.
     * tempTimer adjusts the temperature to match the desired temperature. This should always be on, since the real PLC would also try to adjust its temperature to the last desired temperature.
     */
    private void initialize() {

        desiredTemp.set((int) temp2); // Sets the initial wanted temperature to match the outdoor temperature, until a command to set the temperature is received.
        Timer natureTimer = new Timer();
        natureTimer.scheduleAtFixedRate(
                new TimerTask() {
                    @Override
                    public void run() {
                        if (fertiliser < 3) {
                            plantHeight++;
                        } else {
                            plantHeight += 2;
                        }
                        if (temp1 > temp2) { // If the indoor temperature is higher than the outdoor temperature the temperature drops
                            temp1 -= 0.5;
                        } else if (temp1 < temp2) {
                            temp1 += 0.5;
                        }
                        fertiliser--; // Natural decay of fertilizer
                        if (moisture > 0) {
                            if (temp1 < 25) { // Natural decay of moisture depending on temperature
                                moisture--;
                                waterLevel -= 0.1;
                            } else {
                                moisture -= 2;
                                waterLevel -= 0.2;
                            }
                        }
                        if (waterLevel > 3 && moisture < 100) {
                            moisture += waterLevel / 4;
                        }
                        if (heatingElement) { // If the heating element is on, the temperature indoors will increase.
                            temp1 += 2;
                        }
                        if (fanSpeed == 1) { // The faster the fan-speed, the faster the temperature will drop.
                            temp1--;
                        } else if (fanSpeed == 2) {
                            temp1 -= 2;
                        }
                    }
                }, 10000, 10000
        );
        Timer tempTimer = new Timer();
        tempTimer.scheduleAtFixedRate(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        if (temp1 < desiredTemp.intValue()) {
                            SetHeatingElement(true);
                            SetFanSpeed(0);
                        } else if (temp1 > desiredTemp.intValue()) {
                            SetFanSpeed(1);
                            SetHeatingElement(false);
                        }
                    }
                },
                5000, 5000
        );
    }

    /**
     * SetTemperature takes kelvin input, and sets the desired temperature
     * desiredTemp is an AtomicInterger, which is read by the tempTimer
     *
     * @param kelvin
     * @return bool representing whether the command was executed or not
     */
    @Override
    public boolean SetTemperature(int kelvin) {
        desiredTemp.set(kelvin);
        return true;
    }

    /**
     * @param level
     * @return bool representing whether the command was executed or not
     */
    @Override
    public boolean SetRedLight(int level) {
        if (level < 100 || level > 0) {
            return false;
        } else {
            redLightLevel = level;
            return true;
        }
    }

    /**
     * @param level
     * @return bool representing whether the command was executed or not
     */
    @Override
    public boolean SetBlueLight(int level) {
        if (level < 100 || level > 0) {
            return false;
        } else {
            blueLightLevel = level;
            return true;
        }
    }

    /**
     * The timer in AddWater and AddFertilizer should not always be running, since a new scheduled timer
     * has to be started every time the methods are called.
     *
     * @param sec
     * @return bool representing whether the command was executed or not
     */
    @Override
    public boolean AddWater(int sec) {
        AtomicInteger counter = new AtomicInteger();
        counter.set(0);
        new java.util.Timer().scheduleAtFixedRate(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        if (counter.intValue() <= sec) {
                            waterLevel++;
                            counter.set(counter.intValue() + 1);
                        } else {
                            this.cancel();
                        }
                    }

                },
                1000, 1000
        );
        return true;
    }

    /**
     * See AddWater()
     *
     * @param sec
     * @return bool representing whether the command was executed or not
     */
    @Override
    public boolean AddFertiliser(int sec) {
        int[] counter = new int[0];
        new java.util.Timer().scheduleAtFixedRate(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        if (counter[0] <= sec) {
                            fertiliser++;
                            counter[0] += 1;
                        } else {
                            this.cancel();
                        }
                    }

                },
                1000, 1000
        );
        return true;
    }

    /**
     * @param speed
     * @return bool representing whether the command was executed or not
     */
    @Override
    public boolean SetFanSpeed(int speed) {
        if (speed < 0 || speed > 2) {
            return false;
        } else {
            fanSpeed = speed;
            return true;
        }
    }

    /**
     * Turns on or off the heating element
     *
     * @param b
     */
    private void SetHeatingElement(boolean b) {
        heatingElement = b;
    }

    /**
     * Returns indoor temperature
     *
     * @return temp1
     */
    @Override
    public synchronized double ReadTemp1() {
        return temp1;
    }

    /**
     * Returns outdoor temperature
     *
     * @return temp2
     */
    @Override
    public synchronized double ReadTemp2() {
        return temp2;
    }

    /**
     * Returns moisture
     *
     * @return moisture
     */
    @Override
    public synchronized double ReadMoist() {
        return moisture;
    }

    /**
     * Returns water level
     *
     * @return waterLevel
     */
    @Override
    public synchronized double ReadWaterLevel() {
        return waterLevel;
    }

    /**
     * Returns plant height
     *
     * @return plantHeight
     */
    @Override
    public synchronized double ReadPlantHeight() {
        return plantHeight;
    }

    /**
     * method for prompting the user for a port. Used to set the port of the Greenhouse.
     */
    public void askForPort() {
        String prompt;
        JFrame frame = new JFrame("InputDialog Example #1");
        prompt = JOptionPane.showInputDialog(frame, "Which port do you wish to connect to?", 5000);

        if ((Integer.parseInt(prompt) < 1024 || Integer.parseInt(prompt) > 65536)) {
            System.out.println("Something went wrong, please try again, and this time input a valid port");
            askForPort();
        }
        this.port = Integer.parseInt(prompt);

    }

    /**
     * @returns the port of the current Greenhouse as an int
     */
    public int getPort() {
        return this.port;
    }

    /**
     * This method sets the IP address of the Greenhouse to the currently active network interface of the computer it
     * runs on. It filters out all bad or unusable addresses and only returns IPv4 addresses (excluding localhost)
     */
    private void setIp() throws SocketException {
        Enumeration e = NetworkInterface.getNetworkInterfaces();
        while (e.hasMoreElements()) {
            NetworkInterface n = (NetworkInterface) e.nextElement();
            Enumeration ee = n.getInetAddresses();
            while (ee.hasMoreElements()) {
                InetAddress i = (InetAddress) ee.nextElement();
                if (i.isSiteLocalAddress()) {
                    ip = i.getHostAddress();
                }
            }
        }
    }

    /**
     * @returns the IP address of the current Greenhouse as a string
     */
    public String getIp() {
        return ip;
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean SetMoisture(int i) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public BitSet ReadErrors() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean ResetError(int i) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public byte[] GetStatus() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean AddCO2(int sec) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}	
