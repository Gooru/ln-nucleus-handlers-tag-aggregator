package org.gooru.nucleus.handlers.tag.aggregator.processors.repositories.activejdbc.dbhandlers;

import org.gooru.nucleus.handlers.tag.aggregator.processors.responses.ExecutionResult;
import org.gooru.nucleus.handlers.tag.aggregator.processors.responses.MessageResponse;

/**
 * @author szgooru Created On: 12-Sep-2017
 */
public interface DBHandler {
    ExecutionResult<MessageResponse> checkSanity();

    ExecutionResult<MessageResponse> validateRequest();

    ExecutionResult<MessageResponse> executeRequest();

    boolean handlerReadOnly();
}
