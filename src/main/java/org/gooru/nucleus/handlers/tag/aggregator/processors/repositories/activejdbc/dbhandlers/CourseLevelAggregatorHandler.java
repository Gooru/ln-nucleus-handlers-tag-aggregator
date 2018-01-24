package org.gooru.nucleus.handlers.tag.aggregator.processors.repositories.activejdbc.dbhandlers;

import java.util.Map;

import org.gooru.nucleus.handlers.tag.aggregator.constants.MessageConstants;
import org.gooru.nucleus.handlers.tag.aggregator.processors.ProcessorContext;
import org.gooru.nucleus.handlers.tag.aggregator.processors.events.EventBuilderFactory;
import org.gooru.nucleus.handlers.tag.aggregator.processors.repositories.activejdbc.dbhelpers.GUTCodeLookupHelper;
import org.gooru.nucleus.handlers.tag.aggregator.processors.repositories.activejdbc.entities.AJEntityCourse;
import org.gooru.nucleus.handlers.tag.aggregator.processors.responses.ExecutionResult;
import org.gooru.nucleus.handlers.tag.aggregator.processors.responses.MessageResponse;
import org.gooru.nucleus.handlers.tag.aggregator.processors.responses.MessageResponseFactory;
import org.javalite.activejdbc.LazyList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.json.JsonObject;

/**
 * @author szgooru Created On: 12-Sep-2017
 */
public class CourseLevelAggregatorHandler implements DBHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(CourseLevelAggregatorHandler.class);
    private final ProcessorContext context;

    private AJEntityCourse course;
    private JsonObject tagsAdded;
    private JsonObject tagsRemoved;

    private JsonObject aggregatedGutCodes;
    private JsonObject aggregatedTaxonomy;

    public CourseLevelAggregatorHandler(ProcessorContext context) {
        this.context = context;
    }

    @Override
    public ExecutionResult<MessageResponse> checkSanity() {
        this.tagsAdded = context.request().getJsonObject(MessageConstants.REQ_TAGS_ADDED);
        this.tagsRemoved = context.request().getJsonObject(MessageConstants.REQ_TAGS_REMOVED);

        LOGGER.debug("checkSanity() OK");
        return new ExecutionResult<>(null, ExecutionResult.ExecutionStatus.CONTINUE_PROCESSING);
    }

    @Override
    public ExecutionResult<MessageResponse> validateRequest() {
        // Fetch course
        LazyList<AJEntityCourse> courses = AJEntityCourse.findBySQL(AJEntityCourse.SELECT_COURSE, context.entityId());
        if (courses.isEmpty()) {
            LOGGER.debug("course not found '{}'", context.entityId());
            return new ExecutionResult<>(MessageResponseFactory.createNotFoundResponse("course not found"),
                ExecutionResult.ExecutionStatus.FAILED);
        }

        this.course = courses.get(0);

        String existingAggregatedGutCodes = this.course.getString(AJEntityCourse.AGGREGATED_GUT_CODES);
        String existingAggregatedTaxonomy = this.course.getString(AJEntityCourse.AGGREGATED_TAXONOMY);

        this.aggregatedGutCodes = (existingAggregatedGutCodes != null && !existingAggregatedGutCodes.isEmpty())
            ? new JsonObject(existingAggregatedGutCodes) : new JsonObject();
        this.aggregatedTaxonomy = (existingAggregatedTaxonomy != null && !existingAggregatedTaxonomy.isEmpty())
            ? new JsonObject(existingAggregatedTaxonomy) : new JsonObject();

        if (this.tagsRemoved != null && !this.tagsRemoved.isEmpty()) {
            processTagRemoval();
        }

        if (this.tagsAdded != null && !this.tagsAdded.isEmpty()) {
            processTagAddition();
        }

        LOGGER.debug("validateRequest() OK");
        return new ExecutionResult<>(null, ExecutionResult.ExecutionStatus.CONTINUE_PROCESSING);
    }

    @Override
    public ExecutionResult<MessageResponse> executeRequest() {
        this.course.setAggregatedTaxonomy(this.aggregatedTaxonomy.toString());
        this.course.setAggregatedGutCodes(this.aggregatedGutCodes.toString());

        if (!this.course.save()) {
            LOGGER.debug("unable to save course '{}' after tag aggregation", this.course.getString(AJEntityCourse.ID));
            return new ExecutionResult<>(MessageResponseFactory.createInternalErrorResponse("unable to save"),
                ExecutionResult.ExecutionStatus.FAILED);
        }

        return new ExecutionResult<>(
            MessageResponseFactory.createNoContentResponse("updated",
                EventBuilderFactory.getAggregateCourseTagEventBuilder(this.course.getString(AJEntityCourse.ID))),
            ExecutionResult.ExecutionStatus.SUCCESSFUL);
    }

    @Override
    public boolean handlerReadOnly() {
        return false;
    }

    private void processTagRemoval() {

        Map<String, String> frameworkToGutCodeMapping =
            GUTCodeLookupHelper.populateGutCodesToTaxonomyMapping(this.tagsRemoved.fieldNames());

        frameworkToGutCodeMapping.keySet().forEach(gutCode -> {
            if (this.aggregatedGutCodes.containsKey(gutCode)) {
                int competencyCount = this.aggregatedGutCodes.getInteger(gutCode);
                // Competency count 1 means this competency is tagged only once
                // and across courses. Hence can be removed
                // Competency count greater than 1 means this competency is
                // tagged multiple times across course, so we will just reduce
                // the competency count
                if (competencyCount == 1) {
                    this.aggregatedGutCodes.remove(gutCode);
                    aggregatedTaxonomy.remove(frameworkToGutCodeMapping.get(gutCode));
                } else if (competencyCount > 1) {
                    this.aggregatedGutCodes.put(gutCode, (competencyCount - 1));
                }
            }

            // Do nothing of the gut code which is not present in existing
            // aggregated gut codes
        });
    }

    private void processTagAddition() {
        Map<String, String> frameworkToGutCodeMapping =
            GUTCodeLookupHelper.populateGutCodesToTaxonomyMapping(this.tagsAdded.fieldNames());

        frameworkToGutCodeMapping.keySet().forEach(gutCode -> {
            // If the gut code to be added is already exists in aggregated gut
            // codes, then increase competency count by 1
            // If it does not exists, then add new
            if (this.aggregatedGutCodes.containsKey(gutCode)) {
                int competencyCount = this.aggregatedGutCodes.getInteger(gutCode);
                this.aggregatedGutCodes.put(gutCode, (competencyCount + 1));
            } else {
                this.aggregatedGutCodes.put(gutCode, 1);
                this.aggregatedTaxonomy.put(frameworkToGutCodeMapping.get(gutCode),
                    this.tagsAdded.getJsonObject(frameworkToGutCodeMapping.get(gutCode)));
            }
        });
    }

}
