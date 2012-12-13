/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package arduino.entidades;

import java.util.ArrayList;

/**
 * 
 * @author Fabian Nonino <<fabian.nonino@gmail.com>>
 */
public class MByte {

    private static final char[] kDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a',
        'b', 'c', 'd', 'e', 'f'};

    public static byte[] hexToBytes(char[] hex) {
        int tamaño = hex.length / 2;
        byte[] reto = new byte[tamaño];
        for (int i = 0; i < tamaño; i++) {
            int high = Character.digit(hex[i * 2], 16);
            int low = Character.digit(hex[i * 2 + 1], 16);
            int valor = (high << 4) | low;
            if (valor > 127) {
                valor -= 256;
            }
            reto[i] = (byte) valor;
        }
        return reto;
    }

    public static ArrayList<Byte> hexToBytesArray(char[] hex) {
        int tamaño = hex.length / 2;
        ArrayList<Byte> reto = new ArrayList<Byte>();

        for (int i = 0; i < tamaño; i++) {
            int high = Character.digit(hex[i * 2], 16);
            int low = Character.digit(hex[i * 2 + 1], 16);
            int valor = (high << 4) | low;
            if (valor > 127) {
                valor -= 256;
            }
            reto.add((byte) valor);
        }
        return reto;
    }

    public static char[] bytesToHex(byte[] _byte) {
        int length = _byte.length;

        char[] hex = new char[length * 2];
        for (int i = 0; i < length; i++) {
            int valor = (_byte[i] + 256) % 256;
            int mas_sig = valor >> 4;
            int menos_sig = valor & 0x0f;
            hex[i * 2 + 0] = kDigits[mas_sig];
            hex[i * 2 + 1] = kDigits[menos_sig];
        }
        return hex;
    }

    public static int ByteToInt(byte x) {
        int i = 0, result = 0;
        for (i = 0; i < 8; i++) {
            result = result + (x & (int) Math.pow(2, i));
        }
        return result;
    }

    public static int BytesAnd(byte d, int i) {
        int andd;
        andd = d & (int) Math.pow(2, i);
        if (andd == 0) {
            return 0;
        } else {

            return 1;

        }

    }

    public static boolean BytesAndBool(byte d, int i) {
        int andd;
        andd = d & (int) Math.pow(2, i);
        if (andd == 0) {
            return false;
        } else {

            return true;

        }

    }

    public static boolean IntAndBool(int d, int i) {
        int andd;
        andd = d & (int) Math.pow(2, i);
        if (andd == 0) {
            return false;
        } else {

            return true;

        }

    }

    public static int intToBCDINT(int convert) {
        int sub1;
        int sub2;
        sub1 = (int) convert / 16;
        sub2 = convert - (sub1 * 16);
        return (sub2 + (sub1 * 10));
    }

    public static String intToHex(int convert) {
        if (convert < 16) {
            return "0" + Integer.toHexString(convert);

        } else {
            return Integer.toHexString(convert);
        }
    }

    public static String stringToHex(String base) {
        char[] reto;
        byte[] bytes = base.getBytes();
        reto = bytesToHex(bytes);
        return String.valueOf(reto);
    }

    public static byte[] hexToBytes(String hex) {
        return hexToBytes(hex.toCharArray());
    }

    public static char byteToChar(byte _byte) {
        char reto;
        reto = (char) Integer.parseInt(Integer.toHexString(MByte.ByteToInt(_byte)), 16);
        return reto;
    }

    public static char intToAscii(int _entero) {
        return (char) _entero;
    }
}
