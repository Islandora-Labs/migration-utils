package org.fcrepo.migration.processors;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.spring.Main;

/**
 * Shuts down the application.
 *
 * @author Daniel Lamb
 */
public class ShutdownProcessor implements Processor {

    private Thread stop;

    @Override
    public void process(final Exchange exchange) throws Exception {
        if (stop == null) {
            stop = new Thread() {
                @Override
                public void run() {
                    try {
                        Main.getInstance().shutdown();
                    }
                    catch (Exception e) {
                        // ignore
                    }
                }
            };
        }

        stop.start();
    }
}
