package org.sikuli.script.proxy;

import java.io.InputStream;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProcessReader extends Thread {
    private final Logger logger;
    private final Process process;
    private final InputStream input;

    public ProcessReader(Process process, InputStream input) {
        super();
        setName(getClass().getSimpleName()+"-"+getId());
        setDaemon(true);
        this.process = process;
        this.input = input;
        this.logger = Logger.getLogger(getClass().getName());
    }

    @Override
    public void run() {
        try {
            Scanner sc = new Scanner(input);
            while (!Thread.currentThread().isInterrupted() && process.isAlive()) {
                try {
                    logger.log(Level.INFO, sc.nextLine());
                } catch (Exception ex) {
                    if (!process.isAlive())
                        break;
                    throw ex;
                }
            }
        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
        } finally {
            try {
                if (input != null) {
                    input.close();
                }
            } catch (Exception exc) {
                logger.log(Level.SEVERE, exc.getMessage(), exc);
            }
        }
    }
}
