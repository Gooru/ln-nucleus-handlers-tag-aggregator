package org.gooru.nucleus.handlers.tag.aggregator.processors.events;

import io.vertx.core.json.JsonObject;

/**
 * @author szgooru Created On: 13-Sep-2017
 */
public interface TagAggregatorRequestBuilder {

    JsonObject build();
}
