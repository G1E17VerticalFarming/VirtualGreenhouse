package virtualgreenhouse;

import interfaces.ICommands;
import interfaces.IMessage;

import java.net.SocketException;

public class ByteArrayDecoder implements IMessage, ICommands {

    byte[] byteArray = null;
    GreenHouse gh = new GreenHouse();
    private int returnData;

    /**
     * The constructor takes the parameteriezed bytearray and adds this as the current byteArray
     * @param byteArray
     * @throws SocketException
     */
    public ByteArrayDecoder(byte[] byteArray) throws SocketException {
        this.byteArray = byteArray;
        gh = GreenHouse.getInstance();
    }

    /**
     *
     * @return byteArray
     */
    private byte[] getResult() {
        return byteArray;
    }

    /**
     * Get result data as raw bytes
     * @return data embedded in answer
     */
    public int getResultData() {
        int data;
        byte[] result = this.getResult();
        int dataSize = result[SIZE];
        data = result[DATA_START];
        if (dataSize == 0) {
            return 0;
        } else {
            return data;
        }
    }

    /**
     * The method decoder determines whether the incoming byteArray contains a set-command or read-command
     * Thereafter the appropriate method is called.
     * @return the bytearray that is to be returned from either the readDecoder or setDecoder
     */
    public byte[] decoder() {
        if ((byteArray[COMMAND] > 8 && byteArray[COMMAND] < 15) || byteArray[COMMAND] == 17) {
            return this.readDecoder();
        } else {
            return this.setDecoder();
        }

    }

    /**
     * This method is called when the incoming byteArray contains a set-command.
     * The method contains a swtich-statement that checks which command the bytearray's command field is equal to.
     * Before returning 64 is called to the command-field of the bytearray, to signal that the command was executed.
     * The direction is changed to 1, to signal that the message is coming from PLC towards Scada.
     * @return returning byteArray
     */
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

    /**
     * This method is called when the incoming byteArray contains a read-command.
     * The method contains a swtich-statement that checks which command the bytearray's command field is equal to.
     * Before returning 64 is called to the command-field of the bytearray, to signal that the command was executed.
     * The direction is changed to 1, to signal that the message is coming from PLC towards Scada.
     * The returnData from the greenhouse-class is added to the data-field of the byteArray
     * @return
     */
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
