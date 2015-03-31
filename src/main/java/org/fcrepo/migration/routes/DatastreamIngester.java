package org.fcrepo.migration.routes;

import org.apache.camel.ShutdownRoute;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.builder.xml.Namespaces;

/**
 * Ingests a datastream from Fedora 3 foxml as a NonRdfSourceDescription in
 * Fedora 4.
 *
 * @author Daniel Lamb
 */
public class DatastreamIngester extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        // Namespaces
        final Namespaces ns = new Namespaces("foxml", "info:fedora/fedora-system:def/foxml#");
        ns.add("audit", "info:fedora/fedora-system:def/audit#");

        from("direct:datastream")
            .id("datastreamIngester")
            .shutdownRoute(ShutdownRoute.Defer)

            // Split on datastream version.
            // Use xtokenize to stream each piece without reading everything
            // into memory.
            .split().xtokenize("/foxml:datastream/foxml:datastreamVersion", 'i', ns).streaming()
            .to("direct:datastreamVersion");
    }
}
