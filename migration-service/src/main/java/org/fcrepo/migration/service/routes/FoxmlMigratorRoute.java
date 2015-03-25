package org.fcrepo.migration.service.routes;

import org.apache.camel.builder.RouteBuilder;

/**
 * Sets up REST endpoints.
 *
 * @author Daniel Lamb
 */
public class FoxmlMigratorRoute extends RouteBuilder {

    @Override
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
