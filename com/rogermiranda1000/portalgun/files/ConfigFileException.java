package com.rogermiranda1000.portalgun.files;

import com.rogermiranda1000.helper.SoftCriticalException;

public class ConfigFileException extends SoftCriticalException {
    public ConfigFileException(String err) {
        super(err);
    }

    public ConfigFileException(Exception ex) {
        super(ex);
    }
}
