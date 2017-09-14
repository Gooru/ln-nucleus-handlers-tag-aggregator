package org.gooru.nucleus.handlers.tag.aggregator.processors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.eventbus.Message;

/**
 * @author szgooru Created On: 08-Sep-2017
 */
public final class ProcessorBuilder {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessorBuilder.class);
    
    private ProcessorBuilder() {
        throw new AssertionError();
    }

    public static Processor build(Message<Object> message) {
        LOGGER.debug("building processor");
        return new MessageProcessor(message);
    }
}
