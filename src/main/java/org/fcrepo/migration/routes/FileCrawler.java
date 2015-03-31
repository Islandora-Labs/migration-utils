package org.fcrepo.migration.routes;

import org.apache.camel.builder.RouteBuilder;
import org.fcrepo.migration.filters.FoxmlQueueFinishedFilter;
import org.fcrepo.migration.processors.ShutdownProcessor;

/**
 * Recursively crawls the objectStore in a read-only fashion and sends an empty
 * message to trigger shutdown when all files have been processed.
 *
 * @author Daniel Lamb
 */
public class FileCrawler extends RouteBuilder {

    @Override
    public void configure() {
        from("file:{{objectStore.path}}?noop=true&recursive=true&sendEmptyMessageWhenIdle=true")
            .id("fileCrawler")
            .choice()
                .when().simple("${body} != null")
                    .to("seda:foxml?blockWhenFull=true")
                .otherwise()
                    .filter().method(FoxmlQueueFinishedFilter.class, "queueIsFinished")
                        .process(new ShutdownProcessor());
    }
}
