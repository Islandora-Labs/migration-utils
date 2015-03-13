package org.fcrepo.migration.service.routes;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.builder.xml.Namespaces;
import org.apache.camel.component.http.HttpMethods;

public class ContainerIngesterRoute extends RouteBuilder {
    public void configure() throws Exception {
        // Namespaces
        Namespaces ns = new Namespaces("foxml", "info:fedora/fedora-system:def/foxml#");
        ns.add("audit", "info:fedora/fedora-system:def/audit#");

        from("direct:ingestContainer")
            // Set foxml as property on exchange so it persists
            .setProperty("foxml").simple("${body}")

            // Make a container in Fedora
            .removeHeaders("*")
            .setHeader(Exchange.HTTP_METHOD, constant(HttpMethods.POST))
            .transform(simple("${null}"))
            .to("fcrepo:localhost:8080/fcrepo/rest")

            // Set parent path on the exchange so it persists
            .setHeader("container", body(String.class))
            .setProperty("container").javaScript("request.headers.get('container').split('rest')[1]")

            // Reset back to foxml
            .removeHeaders("*")
            .transform(simple("${property.foxml}"))

            // And split it on datastreams and ingest each
            .split().xpath("/foxml:digitalObject/foxml:datastream", ns)
            .to("direct:ingestDatastream");
    }
}
