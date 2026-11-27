package com.fincore.ledgerservice.exception;

public class MissMatchIdempotencyKeyException extends RuntimeException {
    public MissMatchIdempotencyKeyException(String message) {
        super(message);
    }
}
