/*
 * Copyright (C) 2022  Seekers Contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.seekers.server;

import org.apiguardian.api.API;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.WillClose;
import java.io.File;
import java.io.IOException;

/**
 * A seekers driver runs a script that starts a client for a specified AI file.
 * A seekers client is a local network client that runs a single AI file. It communicates between the AI script file and
 * the seekers' server. It is created by a language loader, which creates different types of clients for different
 * file extensions. After a client is created, it hosts a file, runs the script until the match is finished and finally
 * closes all open resources.
 *
 * @author karlz
 */
@API(since = "0.1.0", status = API.Status.EXPERIMENTAL)
public class SeekersDriver implements AutoCloseable {
    private static final @Nonnull Logger logger = LoggerFactory.getLogger(SeekersDriver.class);

    private final @Nullable Process process;

    /**
     * Creates a
     *
     * @param file the name of the file
     * @param exec the execution command template
     */
    public SeekersDriver(@Nonnull String file, @Nonnull String exec) {
        if (!exec.contains("{file}")) throw new IllegalArgumentException(
                "Execution template should contain a reference to the executed file");
        ProcessBuilder builder = new ProcessBuilder(exec.replace("{file}", file).split(" "));

        logger.debug("Redirect output to log file");
        File log = new File(file + ".log");
        if (!log.exists()) {
            try {
                if (!log.getParentFile().exists()) {
                    logger.warn("Parent folder is missing, this may be an error");
                    if (log.getParentFile().mkdirs())
                        logger.error("Could not create parent folder for file {}!", file);
                }
                if (log.createNewFile()) {
                    logger.debug("Logfile was created");
                } else if (!log.exists()) {
                    logger.error("Could not create log file for file {}!", file);
                }
            } catch (IOException e) {
                logger.error("Creation failed for an unknown reason, please check the logs above", e);
            }
        }
        builder.redirectError(log);
        builder.redirectOutput(log);

        logger.debug("Start driver process");
        Process process = null;
        try {
            process = builder.start();
        } catch (IOException e) {
            logger.error("Could not start process", e);
        }
        this.process = process;
    }

    /**
     * Closes the hosted file and any related gRPC network resources.
     */
    @WillClose
    public void close() {
        logger.info("Close process");
        if (process != null) {
            if (process.exitValue() != 0)
                logger.warn("Process finished with exit code {}", process.exitValue());
            process.destroy();
        }
    }
}
