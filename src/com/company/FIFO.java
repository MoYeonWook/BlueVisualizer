package com.company;

public class FIFO extends Instance {
    public boolean full;
    public boolean enq;
    public boolean deq;
    public boolean empty = true;
    public FIFOStatusType statusType = FIFOStatusType.Default;
    public String asm = "empty"; // assembly code

    public FIFO(String name, String bit, String intf) {
        super(name, bit, intf);
    }
    public FIFO(){super(); }

    public boolean isFull() {
        return full;
    }

    public void setFull(boolean full) {
        this.full = full;
    }

    public boolean isEnq() {
        return enq;
    }

    public void setEnq(boolean enq) {
        this.enq = enq;
    }

    public boolean isDeq() {
        return deq;
    }

    public void setDeq(boolean deq) {
        this.deq = deq;
    }

    public boolean isEmpty() {
        return empty;
    }

    public void setEmpty(boolean empty) {
        this.empty = empty;
    }

    public FIFOStatusType getStatusType() {
        return statusType;
    }

    public void setStatusType(FIFOStatusType statusType) {
        this.statusType = statusType;
    }

    public String getAsm() {
        return asm;
    }

    public void setAsm(String asm) {
        this.asm = asm;
    }
}
