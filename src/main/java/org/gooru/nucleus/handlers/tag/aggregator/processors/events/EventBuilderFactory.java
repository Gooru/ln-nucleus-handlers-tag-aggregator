package org.gooru.nucleus.handlers.tag.aggregator.processors.events;

import io.vertx.core.json.JsonObject;

/**
 * @author szgooru Created On: 13-Sep-2017
 */
public class EventBuilderFactory {

    private static final String EVT_COURSE_TAG_AGGREGATE = "event.course.tag.aggregate";
    private static final String EVT_UNIT_TAG_AGGREGATE = "event.unit.tag.aggregate";
    private static final String EVT_LESSON_TAG_AGGREGATE = "event.lesson.tag.aggregate";

    private static final String EVENT_NAME = "event.name";
    private static final String EVENT_BODY = "event.body";
    private static final String ID = "id";

    public static EventBuilder getAggregateCourseTagEventBuilder(String courseId) {
        return () -> new JsonObject().put(EVENT_NAME, EVT_COURSE_TAG_AGGREGATE).put(EVENT_BODY,
            new JsonObject().put(ID, courseId));
    }

    public static EventBuilder getAggregateLessonTagEventBuilder(String lessonId) {
        return () -> new JsonObject().put(EVENT_NAME, EVT_LESSON_TAG_AGGREGATE).put(EVENT_BODY,
            new JsonObject().put(ID, lessonId));
    }

    public static EventBuilder getAggregateUnitTagEventBuilder(String unitId) {
        return () -> new JsonObject().put(EVENT_NAME, EVT_UNIT_TAG_AGGREGATE).put(EVENT_BODY,
            new JsonObject().put(ID, unitId));
    }

}
