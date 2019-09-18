package org.n3r.idworker;

@SuppressWarnings("serial")
public class InvalidSystemClock extends RuntimeException {
    public InvalidSystemClock(String message) {
        super(message);
    }
}
