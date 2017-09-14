package org.gooru.nucleus.handlers.tag.aggregator.processors;

import java.util.ResourceBundle;

import org.gooru.nucleus.handlers.tag.aggregator.constants.CommonConstants;
import org.gooru.nucleus.handlers.tag.aggregator.constants.MessageConstants;
import org.gooru.nucleus.handlers.tag.aggregator.processors.repositories.RepoBuilder;
import org.gooru.nucleus.handlers.tag.aggregator.processors.responses.ExecutionResult;
import org.gooru.nucleus.handlers.tag.aggregator.processors.responses.MessageResponse;
import org.gooru.nucleus.handlers.tag.aggregator.processors.responses.MessageResponseFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.MultiMap;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

/**
 * @author szgooru Created On: 08-Sep-2017
 */
public class MessageProcessor implements Processor {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageProcessor.class);
    private static final ResourceBundle MESSAGES = ResourceBundle.getBundle(CommonConstants.RESOURCE_BUNDLE);

    private final Message<Object> message;
    private JsonObject session;
    private JsonObject request;
    private String entityId;

    public MessageProcessor(Message<Object> message) {
        this.message = message;
    }

    @Override
    public MessageResponse process() {
        MessageResponse result = null;
        try {
            ExecutionResult<MessageResponse> validateResult = validateAndInitialize();
            if (validateResult.isCompleted()) {
                return validateResult.result();
            }

            final String entityType = request.getString(MessageConstants.REQ_ENTITY_TYPE);
            switch (entityType) {
            case CommonConstants.ENTITY_LESSON:
                result = processLessonLevelTagAggregation();
                break;
            case CommonConstants.ENTITY_UNIT:
                result = processUnitLevelTagAggregation();
                break;
            case CommonConstants.ENTITY_COURSE:
                result = processCourseLevelTagAggregation();
                break;

            default:
                LOGGER.error("invalid entity type passed in, not able to handle");
                MessageResponseFactory.createInternalErrorResponse(MESSAGES.getString("invalid.entity.type"));
            }

            return result;
        } catch (Throwable e) {
            LOGGER.error("Unhandled exception in processing", e);
            return MessageResponseFactory.createInternalErrorResponse(MESSAGES.getString("unexpected.error"));
        }
    }

    private MessageResponse processLessonLevelTagAggregation() {
        ProcessorContext context = createContext();
        return RepoBuilder.buildLessonRepo(context).aggregate();
    }

    private MessageResponse processUnitLevelTagAggregation() {
        ProcessorContext context = createContext();
        return RepoBuilder.buildUnitRepo(context).aggregate();
    }

    private MessageResponse processCourseLevelTagAggregation() {
        ProcessorContext context = createContext();
        return RepoBuilder.buildCourseRepo(context).aggregate();
    }

    private ProcessorContext createContext() {
        MultiMap headers = message.headers();
        return new ProcessorContext.ProcessorContextBuilder(session, request, entityId, headers).build();
    }

    private ExecutionResult<MessageResponse> validateAndInitialize() {
        if (message == null || !(message.body() instanceof JsonObject)) {
            LOGGER.error("Invalid message received, either null or body of message is not JsonObject ");
            return new ExecutionResult<>(
                MessageResponseFactory.createInvalidRequestResponse(MESSAGES.getString("invalid.payload")),
                ExecutionResult.ExecutionStatus.FAILED);
        }

        request = ((JsonObject) message.body());
        if (request == null) {
            LOGGER.error("Invalid JSON payload on Message Bus");
            return new ExecutionResult<>(
                MessageResponseFactory.createInvalidRequestResponse(MESSAGES.getString("invalid.payload")),
                ExecutionResult.ExecutionStatus.FAILED);
        }

        session = request.getJsonObject(MessageConstants.MSG_KEY_SESSION);
        if (session == null || session.isEmpty()) {
            LOGGER.error("Invalid session token obtained, probably not authorized properly");
            return new ExecutionResult<>(
                MessageResponseFactory.createForbiddenResponse(MESSAGES.getString("invalid.session")),
                ExecutionResult.ExecutionStatus.FAILED);
        }

        entityId = request.getString(MessageConstants.REQ_ENTITY_ID);
        if (!ProcessorContextHelper.validateId(entityId)) {
            LOGGER.error("Invalid entity id provided");
            return new ExecutionResult<>(
                MessageResponseFactory.createInvalidRequestResponse(MESSAGES.getString("invalid.entity.id")),
                ExecutionResult.ExecutionStatus.FAILED);
        }

        // All is well, continue processing
        return new ExecutionResult<>(null, ExecutionResult.ExecutionStatus.CONTINUE_PROCESSING);
    }

}
