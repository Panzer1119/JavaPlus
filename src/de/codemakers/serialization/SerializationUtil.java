/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.codemakers.serialization;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * SerializationUtil
 *
 * @author Paul Hagedorn
 */
public class SerializationUtil {

    /**
     * Converts an Object to an byte array using Javas ObjectOutputStream
     *
     * @param object Object to convert
     * @return byte array containing the object
     */
    public static final byte[] convertObjectToBytes(Object object) {
        try {
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream(); ObjectOutputStream oos = new ObjectOutputStream(baos)) {
                oos.writeObject(object);
                return baos.toByteArray();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Converts an byte array to an Object using Javas ObjectInputStream
     *
     * @param data Byte array to convert
     * @return Converted Object
     */
    public static final Object convertBytesToObject(byte[] data) {
        try {
            try (ByteArrayInputStream bais = new ByteArrayInputStream(data); ObjectInputStream ois = new ObjectInputStream(bais)) {
                return ois.readObject();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

}
