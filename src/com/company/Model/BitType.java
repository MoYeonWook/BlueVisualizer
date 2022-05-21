package com.company.Model;

import java.math.BigInteger;

public enum BitType {
    BIN, HEX, DEC;


    public static String convertBin(String bin, BitType bitType) {
        int idx = -1;
        for (int i = 0; i < bin.length(); i++) { //get position after the leading zero
            if (bin.charAt(i) == '0') idx = i;
            else break;
        }
        if (idx == bin.length() - 1) bin = "0";
        else bin = bin.substring(idx + 1);


        if (bitType == BitType.BIN) return bin;
        else if (bitType == BitType.HEX) return new BigInteger(bin, 2).toString(16);
        else return new BigInteger(bin, 2).toString(10);
    }
}