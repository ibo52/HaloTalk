package org.halosoft.gui.models.net.utils;

import java.util.Arrays;

import org.json.JSONObject;

/**
 * Stands for Simple Transmission Protocol
 * 
 * Simple format to transmit messages. Currently implemented for Broadcaster and its Clients
 * Let it addressed respectively from 0 as by definition;
 * 8 byte: field for data type
 * remaining: data
 */
public class STP {

    public static enum DataType{

        STRING,
        JSON,
        BYTE,
    }

    //lengths of fileds in size of bytes
    public static int LEN_DATA_FIELD=DataType.values().length/256 + 1;//auto calculate needed byte size to comply with size of datatype

    public static byte[] format(byte[] data, DataType dataType){

        byte[] newData=new byte[data.length+ LEN_DATA_FIELD];

        int transmissionType = 1+dataType.ordinal();
        //write protocol data byte by byte
        for (int i = 0; i < LEN_DATA_FIELD; i++) {

            newData[i]= (byte)( 0xff & transmissionType >> (8*(LEN_DATA_FIELD-1-i)) );
        }

        System.arraycopy(data, 0, newData, LEN_DATA_FIELD, data.length);

        return newData;
    }

    public static DataType getDataType(byte[] data){

        int transmissionType = 0;
        //get protocol data byte by byte
        for (int i = 0; i < LEN_DATA_FIELD; i++) {

            transmissionType = transmissionType | data[i]<<(8*(LEN_DATA_FIELD-1-i));
        }

        return DataType.values()[transmissionType-1];
    }

    public static JSONObject deformat(byte[] data){

        DataType dType=STP.getDataType(data);

        data=Arrays.copyOfRange(data, LEN_DATA_FIELD, data.length);


        JSONObject json=new JSONObject();
        json.putOpt("dataType", dType);

        switch (dType) {
            case JSON:
                System.out.println("to json:"+data.toString());
                json.putOpt("data",  new JSONObject( new String(data, 0,data.length) ) );
                break;

            case STRING:
                json.putOpt("data", new String(data, 0,data.length) );
                break;

            case BYTE:
            default:
                json.putOpt("data", data );
                break;
        }
        return json;
    }

}
