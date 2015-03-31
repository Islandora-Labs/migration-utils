package org.fcrepo.migration.filters;

import org.apache.camel.CamelContext;
import org.apache.camel.component.seda.SedaEndpoint;

public class FoxmlQueueFinishedFilter {

    public boolean queueIsFinished(CamelContext context) {
        SedaEndpoint seda = context.getEndpoint("seda:foxml", SedaEndpoint.class);
        return seda.getExchanges().size() == 0;
    }
}
