package org.gooru.nucleus.handlers.tag.aggregator.processors.repositories.activejdbc;

import org.gooru.nucleus.handlers.tag.aggregator.processors.ProcessorContext;
import org.gooru.nucleus.handlers.tag.aggregator.processors.repositories.CourseRepo;
import org.gooru.nucleus.handlers.tag.aggregator.processors.repositories.LessonRepo;
import org.gooru.nucleus.handlers.tag.aggregator.processors.repositories.UnitRepo;

/**
 * @author szgooru Created On: 12-Sep-2017
 */
public final class AJRepoBuilder {
    
   private AJRepoBuilder() {
       throw new AssertionError();
   }
   
   public static LessonRepo buildLessonRepo(ProcessorContext context) {
       return new AJLessonRepo(context);
   }
   
   public static UnitRepo buildUnitRepo(ProcessorContext context) {
       return new AJUnitRepo(context);
   }
   
   public static CourseRepo buildCourseRepo(ProcessorContext context) {
       return new AJCourseRepo(context);
   }
   
}
