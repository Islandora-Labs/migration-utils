package org.fcrepo.migration.routes;

import org.apache.camel.builder.RouteBuilder;

/**
 * Recursively crawls the objectStore in a read-only fashion and sends an empty
 * message to trigger shutdown when all files have been read.
 *
 * @author Daniel Lamb
 */
public class FileCrawler extends RouteBuilder {

    @Override
    public void configure() {
        from("file:{{objectStore.path}}?noop=true&recursive=true")
            .choice()
                .when().simple("${body} != null")
                    .to("seda:foxml?blockWhenFull=true")
                .otherwise()
                    //.process(new ShutdownProcessor());
                    .log("FINISHED");
    }
}
