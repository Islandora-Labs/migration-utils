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

        exchange.getContext().getInflightRepository().remove(exchange);

        if (stop == null) {
            stop = new Thread() {
                @Override
                public void run() {
                    try {
                        // Shutdown the public route, which also prevents
                        // this processor from getting executed twice.
                        exchange.getContext().stopRoute("fileCrawler");

                        // Stop the camel context.
                        exchange.getContext().stop();

                        // Stop the spring context.
                        Main.getInstance().stop();
                    } catch (Exception e) {
                        // Ignore
                    }
                }
            };
        }

        stop.start();
    }
}
