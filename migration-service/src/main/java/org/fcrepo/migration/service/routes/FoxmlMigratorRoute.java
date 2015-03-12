package org.fcrepo.migration.service.routes;

import org.apache.camel.builder.RouteBuilder;

public class FoxmlMigratorRoute extends RouteBuilder {
    public void configure() throws Exception {
        restConfiguration().component("servlet");

        rest("/migrate")
            .description("Migrates an object to Fedora4 from a FOXML file.")

            .post("/foxml")
                .to("direct:upgration");

        from("direct:upgration")
            .log("GOT A MESSAGE: ${body}");
    }
}
