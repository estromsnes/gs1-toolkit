package no.nofuzz.gs1.exception;

public class Gs1ParseException extends RuntimeException {

    private final Gs1ErrorCode code;
    private final int position;

    public Gs1ParseException(Gs1ErrorCode code, String msg, int pos) {
        super(msg);
        this.code = code;
        this.position = pos;
    }

    public Gs1ErrorCode getCode() {
        return code;
    }

    public int getPosition() {
        return position;
    }
}