package org.fcrepo.migration.routes;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.builder.xml.Namespaces;
import org.fcrepo.camel.HttpMethods;

/**
 * Ingests an object into Fedora 4 using foxml from a Fedora 3 instance's
 * objectStore directory.
 *
 * @author Daniel Lamb
 */
public class ObjectFoxmlIngester extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        // Namespaces
        final Namespaces ns = new Namespaces("foxml", "info:fedora/fedora-system:def/foxml#");
        ns.add("audit", "info:fedora/fedora-system:def/audit#");

        from("seda:foxml")
            // Set foxml as property on exchange so it persists
            .setProperty("foxml").simple("${body}")

            // Make a container in Fedora
            .removeHeaders("*")
            .setHeader(Exchange.HTTP_METHOD, constant(HttpMethods.POST))
            .transform(simple("${null}"))
            .to("fcrepo:{{fcrepo.baseurl}}")

            // Set parent path on the exchange so it persists
            .setHeader("container", body(String.class))
            .setProperty("container").javaScript("request.headers.get('container').split('rest')[1]")

            // Reset back to foxml
            .removeHeaders("*")
            .transform(simple("${property.foxml}"))

            // TODO: Add object properties here:

            // And split it on datastreams and ingest each
            .split().xpath("/foxml:digitalObject/foxml:datastream", ns)
            .to("direct:datastream");
    }
}
