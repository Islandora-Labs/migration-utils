package org.fcrepo.migration.routes;

import org.apache.camel.builder.RouteBuilder;
import org.fcrepo.migration.processors.ShutdownProcessor;

/**
 * Recursively crawls the objectStore in a read-only fashion and sends an empty
 * message to trigger shutdown when all files have been read.
 *
 * @author Daniel Lamb
 */
public class FileCrawler extends RouteBuilder {

    @Override
    public void configure() {
        from("file:{{objectStorePath}}?noop=true&recursive=true&sendEmptyMessageWhenIdle=true")
            .choice()
                .when().simple("${body} != null")
                    .to("seda:foxml")
                .otherwise()
                    .process(new ShutdownProcessor());
    }
}
