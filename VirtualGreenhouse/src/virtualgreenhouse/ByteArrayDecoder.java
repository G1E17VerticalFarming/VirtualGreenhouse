/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package virtualgreenhouse;

import java.net.SocketException;

/**
 *
 * @author mads
 */
public class ByteArrayDecoder implements IMessage, ICommands {

    byte[] byteArray = null;
    GreenHouse gh = new GreenHouse();

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
        byte[] data;
        byte[] result = this.getResult();
        int dataSize = result[SIZE];

        data = new byte[dataSize];
        for (int i = 0; i < dataSize; i++) {
            data[i] = result[DATA_START + i];
        }
        if (dataSize == 0) {
            return 0;
        } else {
            return Integer.parseInt(data.toString());
        }
    }

    public int decoder() {
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
            case (READ_GREENHOUSE_TEMP):
                return (int) gh.ReadTemp1();
            case (READ_OUTDOOR_TEMP):
                return (int) gh.ReadTemp2();
            case (READ_MOISTURE):
                return (int) gh.ReadMoist();
            case (READ_PLANT_HEIGHT):
                return (int) gh.ReadPlantHeight();
            case (GET_STATUS):
                gh.GetStatus();
                break;
            case (SET_FAN_SPEED):
                gh.SetFanSpeed(this.getResultData());
                break;
        }
        return 0;
    }
}
