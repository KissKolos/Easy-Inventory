package com.easyinventory.client.ui;

public class FormattedException extends RuntimeException {
    public FormattedException(String message,Exception cause) {
        super(message,cause);
    }

}
