package com.company;

public class FIFO extends Instance {
    public boolean full;
    public boolean enq;
    public boolean deq;
    public boolean empty = true;
    public String asm = "empty"; // assembly code

    public FIFO(String name, String bit, String intf) {
        super(name, bit, intf);
    }
    public FIFO(){super(); }


}
