package com.company;

public class FIFO extends Instance {
    public boolean full;
    public boolean enq;
    public boolean deq;
    public boolean empty;

    public FIFO(String name, String bit, String intf) {
        super(name, bit, intf);
    }
    public FIFO(){super(); };


}
