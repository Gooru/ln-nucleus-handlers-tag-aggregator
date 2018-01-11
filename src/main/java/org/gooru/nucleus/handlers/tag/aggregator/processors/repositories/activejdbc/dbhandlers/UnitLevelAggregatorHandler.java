package org.gooru.nucleus.handlers.tag.aggregator.processors.repositories.activejdbc.dbhandlers;

import java.util.Map;

import org.gooru.nucleus.handlers.tag.aggregator.constants.MessageConstants;
import org.gooru.nucleus.handlers.tag.aggregator.processors.ProcessorContext;
import org.gooru.nucleus.handlers.tag.aggregator.processors.events.EventBuilderFactory;
import org.gooru.nucleus.handlers.tag.aggregator.processors.events.TagAggregatorRequestBuilderFactory;
import org.gooru.nucleus.handlers.tag.aggregator.processors.repositories.activejdbc.dbhelpers.GUTCodeLookupHelper;
import org.gooru.nucleus.handlers.tag.aggregator.processors.repositories.activejdbc.entities.AJEntityUnit;
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
public class UnitLevelAggregatorHandler implements DBHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(UnitLevelAggregatorHandler.class);
    private final ProcessorContext context;

    private AJEntityUnit unit;
    private JsonObject tagsAdded;
    private JsonObject tagsRemoved;

    private JsonObject aggregatedGutCodes;
    private JsonObject aggregatedTaxonomy;

    public UnitLevelAggregatorHandler(ProcessorContext context) {
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
        // Fetch unit
        LazyList<AJEntityUnit> units = AJEntityUnit.findBySQL(AJEntityUnit.SELECT_UNIT, context.entityId());
        if (units.isEmpty()) {
            LOGGER.debug("unit not found '{}'", context.entityId());
            return new ExecutionResult<>(MessageResponseFactory.createNotFoundResponse("unit not found"),
                ExecutionResult.ExecutionStatus.FAILED);
        }

        this.unit = units.get(0);

        String existingAggregatedGutCodes = this.unit.getString(AJEntityUnit.AGGREGATED_GUT_CODES);
        String existingAggregatedTaxonomy = this.unit.getString(AJEntityUnit.AGGREGATED_TAXONOMY);

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
        this.unit.setAggregatedTaxonomy(this.aggregatedTaxonomy.toString());
        this.unit.setAggregatedGutCodes(this.aggregatedGutCodes.toString());

        if (!this.unit.save()) {
            LOGGER.debug("unable to save unit '{}' after tag aggregation",
                this.unit.getString(AJEntityUnit.UNIT_ID));
            return new ExecutionResult<>(MessageResponseFactory.createInternalErrorResponse("unable to save"),
                ExecutionResult.ExecutionStatus.FAILED);
        }

        return new ExecutionResult<>(
            MessageResponseFactory.createNoContentResponse("updated",
                EventBuilderFactory.getAggregateUnitTagEventBuilder(this.unit.getString(AJEntityUnit.UNIT_ID)),
                TagAggregatorRequestBuilderFactory.getCourseTagAggregatorRequestBuilder(
                    this.unit.getString(AJEntityUnit.COURSE_ID), new JsonObject())),
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
                // and across units. Hence can be removed
                // Competency count greater than 1 means this competency is
                // tagged multiple times across unit, so we will just reduce
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
