package com.alfred.casetrace.editor;

public class AlfredPart {

    private int start;
    private int end;

    public AlfredPart(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public boolean isValid() {
        return start < end;
    }

}
