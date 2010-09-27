package com.googlecode.onevre.ag.agserver;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.Vector;

public class MulticastAddressAllocator {
    public static String RANDOM = "random";
    public static String INTERVAL = "interval";
    public static String SDR_BASE_ADDRESS = "224.2.128.0";
    public static int SDR_MASK_SIZE = 17;

    private static Vector<String> allocatedAddresses = new Vector<String>();

    private static final int makeIntFromByte4(byte[] b) {
        return b[0]<<24 | (b[1]&0xff)<<16 | (b[2]&0xff)<<8 | (b[3]&0xff);
    }

    private static final byte[] makeByte4FromInt(int i) {
        return new byte[] { (byte)(i>>24), (byte)(i>>16), (byte)(i>>8), (byte)i };
    }

    private static byte[] randomAddress(String base, int maskSize) throws UnknownHostException{
        int mask=Integer.MAX_VALUE;
        mask =  mask<<(32-maskSize);
        int negMask = ~mask;
        int randomNumber = ((int) (Math.random() * Integer.MAX_VALUE))&negMask;
        int iaddr = (makeIntFromByte4(Inet4Address.getByName(base).getAddress())&mask)|randomNumber;
        return makeByte4FromInt(iaddr);
    }

    public static String allocateAddress(){
        try {
            String addressString="";
            do{
                addressString=Inet4Address.getByAddress(randomAddress(SDR_BASE_ADDRESS, SDR_MASK_SIZE)).getHostAddress();
            } while (allocatedAddresses.contains(addressString));
                allocatedAddresses.add(addressString);
                return addressString;
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String recycleAddress(String address){
        if (allocatedAddresses.remove(address))
            return address;
        return null;
    }

}
