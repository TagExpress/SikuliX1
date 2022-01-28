package org.sikuli.script.proxy;

import java.io.InputStream;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProcessErrorReader extends Thread {
    private final Logger logger;
    private final Process process;
    private final InputStream error;

    public ProcessErrorReader(Process process, InputStream error) {
        super();
        setName("process-output-"+getId());
        setDaemon(true);
        this.process = process;
        this.error = error;
        this.logger = Logger.getLogger(getClass().getName());
    }

    @Override
    public void run() {
        try {
            Scanner sc = new Scanner(error);
            while (!Thread.currentThread().isInterrupted() && process.isAlive()) {
                logger.log(Level.INFO, sc.nextLine());
            }
        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
        } finally {
            try {
                if (error != null) {
                    error.close();
                }
            } catch (Exception exc) {
                logger.log(Level.SEVERE, exc.getMessage(), exc);
            }
        }
    }
}
