package org.gooru.nucleus.handlers.tag.aggregator.processors;

import org.gooru.nucleus.handlers.tag.aggregator.processors.responses.MessageResponse;

/**
 * @author szgooru Created On: 08-Sep-2017
 */
public interface Processor {
    MessageResponse process();
}
