
package com.campusfind.campusfind.exception;

// CUSTOM EXCEPTION: Jab requested item database mein na mile
public class LostItemNotFoundException extends RuntimeException {

    public LostItemNotFoundException(String message) {
        super(message);
    }
}
