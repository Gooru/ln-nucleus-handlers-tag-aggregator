package org.gooru.nucleus.handlers.tag.aggregator.constants;

/**
 * @author szgooru Created On: 08-Sep-2017
 */
public final class MessageBusEndpoints {

    public static final String MBEP_TAG_AGGREGATOR = "org.gooru.nucleus.message.bus.tag.aggregator";
    public static final String MBEP_EVENT = "org.gooru.nucleus.message.bus.publisher.event";

    private MessageBusEndpoints() {
        throw new AssertionError();
    }
}
