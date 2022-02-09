package org.sikuli.script.proxy;

import org.sikuli.basics.Debug;

import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Scanner;

public class ProcessReader extends Thread {
    private final Process process;
    private final InputStream input;

    public ProcessReader(Process process, InputStream input) {
        super();
        setName(getClass().getSimpleName()+"-"+getId());
        setDaemon(true);
        this.process = process;
        this.input = input;
    }

    @Override
    public void run() {
        try {
            Scanner sc = new Scanner(input);
            while (!Thread.currentThread().isInterrupted() && process.isAlive()) {
                try {
                    Debug.info(sc.nextLine());
                } catch (Exception ex) {
                    if (!process.isAlive())
                        break;
                    throw ex;
                }
            }
        } catch (Exception ex) {
            StringWriter writer = new StringWriter();
            ex.printStackTrace(new PrintWriter(writer));
            Debug.error(writer.toString());
        } finally {
            try {
                if (input != null) {
                    input.close();
                }
            } catch (Exception exc) {
                StringWriter writer = new StringWriter();
                exc.printStackTrace(new PrintWriter(writer));
                Debug.error(writer.toString());
            }
        }
    }
}
