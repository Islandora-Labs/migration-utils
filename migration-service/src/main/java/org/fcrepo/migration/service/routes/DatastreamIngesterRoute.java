package org.fcrepo.migration.service.routes;

import org.apache.camel.builder.RouteBuilder;

public class FoxmlMigratorRoute extends RouteBuilder {
    public void configure() throws Exception {
        // Configure rest servlet
        restConfiguration().component("servlet");

        // Set up endpoints
        rest("/migrate")
            .description("Migrates an object to Fedora4 from a FOXML file.")

            .post("/foxml")
                .to("direct:ingestContainer");
    }
}
