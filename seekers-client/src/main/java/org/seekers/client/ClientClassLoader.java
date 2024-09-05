package org.seekers.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class ClientClassLoader extends ClassLoader {
    
    private static final Logger logger = LoggerFactory.getLogger(ClientClassLoader.class);
    
    @Nonnull
    public SeekersImpl getSeekersImpl(@Nonnull String name) throws ClientLoaderException {
        Class<?> loaded = findClass(name);
        if (loaded != null) {
            if (loaded.isInstance(SeekersImpl.class)) {
                try {
                    Object created = loaded.newInstance();
                    return (SeekersImpl) created;
                } catch (InstantiationException|IllegalAccessException ex) {
                    throw new ClientLoaderException(ex);
                }
            }
            throw new ClientLoaderException("Class is not an instance of org.seekers.client.SeekersImpl");
        }
        throw new ClientLoaderException("Could not find class");
    }
    
    @CheckForNull
    @Override
    public Class<?> findClass(String name) {
        try {
            byte[] b = loadClassFromFile(name);
            return defineClass(name, b, 0, b.length);
        } catch (ClientLoaderException ex) {
            logger.error(ex.getMessage(), ex);
        }
        return null;
    }

    @Nonnull
    private byte[] loadClassFromFile(String fileName) throws ClientLoaderException {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(
                fileName.replace('.', File.separatorChar) + ".class");) {
            byte[] buffer;
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            int nextValue = 0;
            
            while ( (nextValue = inputStream.read()) != -1 ) {
                byteStream.write(nextValue);
            }
            buffer = byteStream.toByteArray();
            return buffer;
        } catch (IOException ex) {
            throw new ClientLoaderException(ex);
        }
    }
}