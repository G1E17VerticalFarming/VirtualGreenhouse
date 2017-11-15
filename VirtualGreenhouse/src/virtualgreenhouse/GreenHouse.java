/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package virtualgreenhouse;

import API.IGreenhouse;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.BitSet;
import java.util.Date;
import java.util.Enumeration;


/**
 * @author chris
 */
public class GreenHouse implements IGreenhouse, ActionListener, PropertyChangeListener {

    private double temp1 = 17;
    private double temp2 = 26;
    private double moisture = 72;
    private double waterLevel = 0;
    private int co2Level = 0;
    private double plantHeight = 0.0;
    private int fanSpeed = 0;
    private int fertiliser = 0;
    private int port = 0;
    private String ip;

    // light level
    private int blueLightLevel = 0;
    private int redLightLevel = 0;

    private Date dateChecker;

    private static GreenHouse greenhouse = new GreenHouse();


    public static GreenHouse getInstance() throws SocketException {
        greenhouse.setIp();
        return greenhouse;
    }

    private void GrowthRate() {
        // timeStart checks time at start of execution
        long timeStart = dateChecker.getTime();
        // timeSinceExe is used to only execute at specific times later than timeStart
        long timeSinceExe = dateChecker.getTime();

        while (plantHeight < 30) {
            if (timeSinceExe >= timeSinceExe && fertiliser < 1) {
                timeSinceExe += 60000;
                plantHeight++;
            } else if (timeSinceExe >= timeSinceExe && fertiliser >= 50) {
                timeSinceExe += 30000;
                plantHeight++;
                fertiliser--;
            }
        }
    }

    @Override
    public boolean SetTemperature(int kelvin) {
        // timeTemp keeps track of time needed for execution
        long timeTemp;

        boolean increaseTemp = true;

        // Check if kelvin is higher or lower than current temp
        if (kelvin > temp1) {
            timeTemp = ((int) kelvin - (int) temp1);
        } else {
            timeTemp = ((int) temp1 - (int) kelvin);
            increaseTemp = false;
        }

        // timeStart checks time at start of execution
        long timeStart = dateChecker.getTime();
        // timeSinceExe is used to only execute at specific times later than timeStart
        long timeSinceExe = dateChecker.getTime();
        // finishTime is when the execution should be done
        long finishTime = timeStart + (timeTemp * 1000);

        // While timeStart is lower than finishTime, execute and increase/decrease
        while (timeStart <= finishTime) {
            if (increaseTemp && dateChecker.getTime() >= timeSinceExe) {
                timeSinceExe += 1000;
                temp1++;
            } else if (!increaseTemp && dateChecker.getTime() >= timeSinceExe) {
                timeSinceExe += 1000;
                temp1--;
            }
        }
        return true;
    }

    @Override
    public boolean SetMoisture(int moist) {
        // timeTemp keeps track of time needed for execution
        long timeTemp;

        boolean increaseMoisture = true;

        // Check if moist is higher or lower than current moisture
        if (moist > moisture) {
            timeTemp = ((int) moist - (int) moisture);
        } else {
            timeTemp = ((int) moisture - (int) moist);
            increaseMoisture = false;
        }

        // timeStart checks time at start of execution
        long timeStart = dateChecker.getTime();
        // timeSinceExe is used to only execute at specific times later than timeStart
        long timeSinceExe = dateChecker.getTime();
        // finishTime is when the execution should be done
        long finishTime = timeStart + (timeTemp * 1000);

        // While timeStart is lower than finishTime, execute and increase/decrease
        while (timeStart <= finishTime) {
            if (increaseMoisture && dateChecker.getTime() >= timeSinceExe) {
                timeSinceExe += 1000;
                moisture++;
            } else if (!increaseMoisture && dateChecker.getTime() >= timeSinceExe) {
                timeSinceExe += 1000;
                moisture--;
            }
        }
        return true;
    }

    @Override
    public boolean SetRedLight(int level) {
        if (level < 100 || level > 0) {
            return false;
        } else {
            this.redLightLevel = level;
            return true;
        }
    }

    @Override
    public boolean SetBlueLight(int level) {
        if (level < 100 || level > 0) {
            return false;
        } else {
            this.blueLightLevel = level;
            return true;
        }
    }

    @Override
    public boolean AddWater(int sec) {
        // timeStart checks time at start of execution
        long timeStart = dateChecker.getTime();
        // timeSinceExe is used to only execute at specific times later than timeStart
        long timeSinceExe = dateChecker.getTime();
        // finishTime is when the execution should be done
        long finishTime = timeStart + (sec * 1000);

        while (timeStart <= finishTime) {
            if (dateChecker.getTime() >= timeSinceExe) {
                timeSinceExe += 1000;
                waterLevel++;
            }
        }
        return true;
    }

    @Override
    public boolean AddFertiliser(int sec) {
        // timeStart checks time at start of execution
        long timeStart = dateChecker.getTime();
        // timeSinceExe is used to only execute at specific times later than timeStart
        long timeSinceExe = dateChecker.getTime();
        // finishTime is when the execution should be done
        long finishTime = timeStart + (sec * 1000);

        while (timeStart <= finishTime) {
            if (dateChecker.getTime() >= timeSinceExe) {
                timeSinceExe += 1000;
                fertiliser++;
            }
        }
        return true;
    }

    @Override
    public boolean AddCO2(int sec) {
        // timeStart checks time at start of execution
        long timeStart = dateChecker.getTime();
        // timeSinceExe is used to only execute at specific times later than timeStart
        long timeSinceExe = dateChecker.getTime();
        // finishTime is when the execution should be done
        long finishTime = timeStart + (sec * 1000);

        while (timeStart <= finishTime) {
            if (dateChecker.getTime() >= timeSinceExe) {
                timeSinceExe += 1000;
                if (co2Level < 100) {
                    co2Level++;
                }
            }
        }
        return true;
    }

    @Override
    public double ReadTemp1() {
        return temp1;
    }

    @Override
    public double ReadTemp2() {
        return temp2;
    }

    @Override
    public double ReadMoist() {
        return moisture;
    }

    @Override
    public double ReadWaterLevel() {
        return waterLevel;
    }

    @Override
    public double ReadPlantHeight() {
        return plantHeight;
    }

    @Override
    public BitSet ReadErrors() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean ResetError(int errorNum) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public byte[] GetStatus() {
        return new byte[1];
    }

    @Override
    public boolean SetFanSpeed(int speed) {
        if (speed < 100 || speed > 0) {
            return false;
        } else {
            this.fanSpeed = speed;
            return true;
        }
    }


    public void askForPort() {
        String prompt;
        JFrame frame = new JFrame("InputDialog Example #1");
        prompt = JOptionPane.showInputDialog(frame, "Which port do you wish to connect to?");

        if ((Integer.parseInt(prompt) < 1024 || Integer.parseInt(prompt) > 65536)) {
            System.out.println("Something whent wrong, please try again, and this time input a valid port");
            askForPort();
        }
        this.port = Integer.parseInt(prompt);

    }


    public int getPort() {
        return this.port;
    }

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

}	
