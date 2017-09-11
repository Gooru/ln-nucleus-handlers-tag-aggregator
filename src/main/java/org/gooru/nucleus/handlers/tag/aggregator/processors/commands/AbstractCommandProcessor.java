package org.gooru.nucleus.handlers.tag.aggregator.processors.commands;

import java.util.ArrayList;
import java.util.List;

import org.gooru.nucleus.handlers.tag.aggregator.processors.Processor;
import org.gooru.nucleus.handlers.tag.aggregator.processors.ProcessorContext;
import org.gooru.nucleus.handlers.tag.aggregator.processors.responses.MessageResponse;

/**
 * @author szgooru Created On: 08-Sep-2017
 */
public abstract class AbstractCommandProcessor implements Processor {

    protected final List<String> deprecatedVersions = new ArrayList<>();
    protected final ProcessorContext context;
    protected String version;

    protected AbstractCommandProcessor(ProcessorContext context) {
        this.context = context;
    }

    @Override
    public MessageResponse process() {
        setDeprecatedVersions();
        return processCommand();
    }

    protected abstract void setDeprecatedVersions();

    protected abstract MessageResponse processCommand();
}
