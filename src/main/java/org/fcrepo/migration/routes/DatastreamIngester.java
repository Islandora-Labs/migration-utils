package org.fcrepo.migration.routes;

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

        from("seda:datastream")
            .split().xpath("/foxml:datastream/foxml:datastreamVersion", ns)
            .to("seda:datastreamVersion?blockWhenFull=true");
    }
}
