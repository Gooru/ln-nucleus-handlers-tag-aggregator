package org.gooru.nucleus.handlers.tag.aggregator.processors.repositories.activejdbc.dbhandlers;

import org.gooru.nucleus.handlers.tag.aggregator.processors.ProcessorContext;

/**
 * @author szgooru Created On: 12-Sep-2017
 */
public final class DBHandlerBuilder {

    private DBHandlerBuilder() {
        throw new AssertionError();
    }
    
    public static DBHandler buildLessonLevelAggregatorHandler(ProcessorContext context) {
        return new LessonLevelAggregatorHandler(context);
    }
    
    public static DBHandler buildUnitLevelAggregatorHandler(ProcessorContext context) {
        return new UnitLevelAggregatorHandler(context);
    }

    public static DBHandler buildCourseLevelAggragatorHandler(ProcessorContext context) {
        return new CourseLevelAggregatorHandler(context);
    }
}
