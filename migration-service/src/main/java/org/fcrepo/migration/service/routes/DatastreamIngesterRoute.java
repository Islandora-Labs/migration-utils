package org.fcrepo.migration.service.routes;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.builder.xml.Namespaces;
import org.apache.camel.component.http.HttpMethods;
import org.fcrepo.camel.FcrepoHeaders;

/**
 * Ingests a datastream from Fedora 3 as a NonRdfSourceDescription in Fedora 4.
 *
 * @author Daniel Lamb
 */
public class DatastreamIngesterRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        // Namespaces
        final Namespaces ns = new Namespaces("foxml", "info:fedora/fedora-system:def/foxml#");
        ns.add("audit", "info:fedora/fedora-system:def/audit#");

        from("direct:ingestDatastream")
            //TODO: Handle each version of this particular datastream

            // Get the mimetype
            .setProperty("mimetype").xpath("/foxml:datastream/foxml:datastreamVersion/@MIMETYPE", ns)
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
                .when().xpath("/foxml:datastream/foxml:datastreamVersion/foxml:xmlContent", ns)
                    .setBody().xpath("/foxml:datastream/foxml:datastreamVersion/foxml:xmlContent", ns)
                    .to("fcrepo:{{fcrepo.baseurl}}")
                // Add binary content as NonRDFSourceDescription
                .when().xpath("/foxml:datastream/foxml:datastreamVersion/foxml:binaryContent", ns)
                    .setBody().xpath("/foxml:datastream/foxml:datastreamVersion/foxml:binaryContent/text()", ns)
                    .unmarshal().base64()
                    .to("fcrepo:{{fcrepo.baseurl}}");
    }
}
