package org.gooru.nucleus.handlers.tag.aggregator.processors.commands;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import org.gooru.nucleus.handlers.tag.aggregator.constants.CommonConstants;
import org.gooru.nucleus.handlers.tag.aggregator.processors.Processor;
import org.gooru.nucleus.handlers.tag.aggregator.processors.ProcessorContext;
import org.gooru.nucleus.handlers.tag.aggregator.processors.responses.MessageResponseFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author szgooru Created On: 08-Sep-2017
 */
public enum CommandProcessorBuilder {

    DEFAULT("default") {
        private final Logger LOGGER = LoggerFactory.getLogger(CommandProcessorBuilder.class);
        private final ResourceBundle MESSAGES = ResourceBundle.getBundle(CommonConstants.RESOURCE_BUNDLE);

        @Override
        public Processor build(ProcessorContext context) {
            return () -> {
                LOGGER.error("Invalid operation type passed in, not able to handle");
                return MessageResponseFactory.createInvalidRequestResponse(MESSAGES.getString("invalid.operation"));
            };
        }
    };

    private String name;

    CommandProcessorBuilder(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    private static final Map<String, CommandProcessorBuilder> LOOKUP = new HashMap<>();

    static {
        for (CommandProcessorBuilder builder : values()) {
            LOOKUP.put(builder.name, builder);
        }
    }

    public static CommandProcessorBuilder lookupBuilder(String name) {
        CommandProcessorBuilder builder = LOOKUP.get(name);
        if (builder == null) {
            return DEFAULT;
        }
        return builder;
    }

    public abstract Processor build(ProcessorContext context);
}
