package org.seekers.client;

import javax.annotation.Nonnull;

public class ClientLoaderException extends Exception {
    
    public ClientLoaderException(@Nonnull Throwable throwable) {
        super(throwable);
    }
    
    public ClientLoaderException(@Nonnull String msg) {
        super(msg);
    }
}