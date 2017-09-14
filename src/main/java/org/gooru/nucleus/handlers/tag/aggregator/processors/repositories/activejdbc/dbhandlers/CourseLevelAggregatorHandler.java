package org.gooru.nucleus.handlers.tag.aggregator.processors.repositories.activejdbc.dbhandlers;

import org.gooru.nucleus.handlers.tag.aggregator.processors.ProcessorContext;
import org.gooru.nucleus.handlers.tag.aggregator.processors.responses.ExecutionResult;
import org.gooru.nucleus.handlers.tag.aggregator.processors.responses.MessageResponse;

/**
 * @author szgooru Created On: 12-Sep-2017
 */
public class CourseLevelAggregatorHandler implements DBHandler {

    public CourseLevelAggregatorHandler(ProcessorContext context) {
    }

    @Override
    public ExecutionResult<MessageResponse> checkSanity() {
        return null;
    }

    @Override
    public ExecutionResult<MessageResponse> validateRequest() {
        return null;
    }

    @Override
    public ExecutionResult<MessageResponse> executeRequest() {
        return null;
    }

    @Override
    public boolean handlerReadOnly() {
        return false;
    }

}
