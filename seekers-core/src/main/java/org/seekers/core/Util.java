package org.seekers.core;

import java.io.*;

final class Util {
    private Util() {
        throw new IllegalStateException("Utility class");
    }

    public static int checkPositive(int number) {
        if (number > 0) return number;
        throw new IllegalArgumentException("Negative or zero number: " + number);
    }

    public static double checkPositive(double number) {
        if (number > 0) return number;
        throw new IllegalArgumentException("Negative or zero number: " + number);
    }

    public static int checkNotNegative(int number) {
        if (number < 0) throw new IllegalArgumentException("Negative number: " + number);
        return number;
    }

    private static final String CONFIG_NAME = "config.ini";

    public static void copyIfNotExists() {
        File file = new File(CONFIG_NAME);
        if (!file.exists()) {
            try (FileOutputStream output = new FileOutputStream(file);
                 InputStream input = Util.class.getResourceAsStream(CONFIG_NAME)) {
                if (input == null) throw new FileNotFoundException(CONFIG_NAME);
                output.write(input.readAllBytes());
            } catch (IOException e) {
                throw new InternalError(e);
            }
        }
    }
}
