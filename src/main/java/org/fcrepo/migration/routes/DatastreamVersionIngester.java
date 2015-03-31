package org.fcrepo.migration.routes;

import org.apache.camel.Exchange;
import org.apache.camel.ShutdownRoute;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.builder.xml.Namespaces;
import org.fcrepo.camel.FcrepoHeaders;
import org.fcrepo.camel.HttpMethods;

/**
 * Ingests a datastream from Fedora 3 foxml as a NonRdfSourceDescription in
 * Fedora 4.
 *
 * @author Daniel Lamb
 */
public class DatastreamVersionIngester extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        // Namespaces
        final Namespaces ns = new Namespaces("foxml", "info:fedora/fedora-system:def/foxml#");
        ns.add("audit", "info:fedora/fedora-system:def/audit#");

        from("direct:datastreamVersion")
            .id("DatastreamVersionIngester")
            .shutdownRoute(ShutdownRoute.Defer)
            // Get the mimetype
            .setProperty("mimetype").xpath("/foxml:datastreamVersion/@MIMETYPE", ns)
            .log("MIMETYPE ${property.mimetype}")

            // Set headers
            .setHeader(FcrepoHeaders.FCREPO_IDENTIFIER).simple("${property.container}")
            .setHeader(Exchange.CONTENT_TYPE).simple("${property.mimetype}")
            .setHeader(Exchange.HTTP_METHOD, constant(HttpMethods.POST))

            // Switch statement on mimetype and xml/binary
            .choice()
                // Add RDF properties here
                .when().simple("${property.mimetype} == 'application/rdf+xml'")
                    .log("You should add RDF properties to the parent container here!")
                // Add xml content as NonRDFSourceDescription
                .when().xpath("/foxml:datastreamVersion/foxml:xmlContent", ns)
                    .setBody().xpath("/foxml:datastreamVersion/foxml:xmlContent", ns)
                    .to("fcrepo:{{fcrepo.baseurl}}")
                // Add binary content as NonRDFSourceDescription
                .when().xpath("/foxml:datastreamVersion/foxml:binaryContent", ns)
                    .setBody().xpath("/foxml:datastreamVersion/foxml:binaryContent/text()", ns)
                    .unmarshal().base64()
                    .to("fcrepo:{{fcrepo.baseurl}}");
    }
}
