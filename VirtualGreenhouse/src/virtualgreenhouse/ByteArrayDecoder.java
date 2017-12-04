/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package virtualgreenhouse;

import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 *
 * @author mads
 */
public class ByteArrayDecoder implements IMessage, ICommands {

    byte[] byteArray = null;
    GreenHouse gh = new GreenHouse();
    private int returnData;

    protected byte[] answer = new byte[125];

    public ByteArrayDecoder(byte[] byteArray) throws SocketException {
        this.byteArray = byteArray;
        gh = GreenHouse.getInstance();
    }

    public byte[] getResult() {
        return byteArray;
    }

    /**
     * Get result data as raw bytes
     *
     * @return data embedded in answer
     */
    public int getResultData() {
        int data;
        byte[] result = this.getResult();
        int dataSize = result[SIZE];
        int intValue = 0;
        /*
        data = new byte[dataSize];
        for (int i = 0; i < dataSize; i++) {
            data = result[DATA_START + i];
        }

        for (int i = 0; i <= 4; i++) {
            intValue = (intValue << 8) - Byte.MIN_VALUE + (int) data[i];
        }
        */
        data = result[DATA_START];
        if (dataSize == 0) {
            return 0;
        } else {
            //return intValue; //Integer.parseInt(data.toString());
            return data;
        }
    }

    public byte[] decoder() {
            if ((byteArray[COMMAND] > 8 && byteArray[COMMAND] < 15) || byteArray[COMMAND] == 17) {
                return this.readDecoder();
            } else {
                return this.setDecoder();
            }

        }

    private byte[] setDecoder() {
        switch (byteArray[COMMAND]) {
            case (NO_CMD):
                break;
            case (TEMP_SETPOINT):
                gh.SetTemperature(this.getResultData());
                break;
            case (MOIST_SETPOINT):
                gh.SetMoisture(this.getResultData());
                break;
            case (REDLIGHT_SETPOINT):
                gh.SetRedLight(this.getResultData());
                break;
            case (BLUELIGHT_SETPOINT):
                gh.SetBlueLight(this.getResultData());
                break;
            case (ADDWATER):
                gh.AddWater(this.getResultData());
                break;
            case (ADDFERTILISER):
                gh.AddFertiliser(this.getResultData());
                break;
            case (ADDCO2):
                gh.AddCO2(this.getResultData());
                break;
            case (SET_FAN_SPEED):
                gh.SetFanSpeed(this.getResultData());
                break;
        }
        byteArray[COMMAND] += 64;
        byteArray[DIRECTION] = 1;
        return byteArray;
    }

    private byte[] readDecoder() {
        System.out.println(byteArray[COMMAND]);
        switch (byteArray[COMMAND]) {
            case (READ_GREENHOUSE_TEMP):
                returnData = (int) gh.ReadTemp1();
                System.out.println("Decoder temp" + returnData);
                break;
            case (READ_OUTDOOR_TEMP):
                returnData = (int) gh.ReadTemp2();
                break;
            case (READ_MOISTURE):
                returnData = (int) gh.ReadMoist();
                System.out.println("Decoder moisture: " + returnData);
                break;
            case (READ_PLANT_HEIGHT):
                returnData = (int) gh.ReadPlantHeight();
                break;
            case (GET_STATUS):
                gh.GetStatus();
                break;
        }
        byteArray[COMMAND] += 64;
        byteArray[DIRECTION] = 1;
        byteArray[DATA_START] += returnData;
        return byteArray;
    }
}
