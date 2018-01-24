package org.gooru.nucleus.handlers.tag.aggregator.bootstrap.startup;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

/**
 * @author szgooru Created On: 08-Sep-2017
 */
public interface Initializer {
    void initializeComponent(Vertx vertx, JsonObject config);
}
