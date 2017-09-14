package org.gooru.nucleus.handlers.tag.aggregator.processors.repositories;

import org.gooru.nucleus.handlers.tag.aggregator.processors.ProcessorContext;
import org.gooru.nucleus.handlers.tag.aggregator.processors.repositories.activejdbc.AJRepoBuilder;

/**
 * @author szgooru Created On: 12-Sep-2017
 */
public final class RepoBuilder {

    private RepoBuilder() {
        throw new AssertionError();
    }

    public static LessonRepo buildLessonRepo(ProcessorContext context) {
        return AJRepoBuilder.buildLessonRepo(context);
    }
    
    public static UnitRepo buildUnitRepo(ProcessorContext context) {
        return AJRepoBuilder.buildUnitRepo(context);
    }
    
    public static CourseRepo buildCourseRepo(ProcessorContext context) {
        return AJRepoBuilder.buildCourseRepo(context);
    }

}
