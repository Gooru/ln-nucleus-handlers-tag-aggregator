package org.gooru.nucleus.handlers.tag.aggregator.processors.events;

import io.vertx.core.json.JsonObject;

/**
 * @author szgooru Created On: 13-Sep-2017
 */
public class EventBuilderFactory {

    private static final String EVT_COURSE_UPDATE = "event.course.update";
    private static final String EVT_UNIT_UPDATE = "event.unit.update";
    private static final String EVT_LESSON_UPDATE = "event.lesson.update";

    private static final String EVENT_NAME = "event.name";
    private static final String EVENT_BODY = "event.body";
    private static final String ID = "id";

    public static EventBuilder getUpdateCourseEventBuilder(String courseId) {
        return () -> new JsonObject().put(EVENT_NAME, EVT_COURSE_UPDATE).put(EVENT_BODY,
            new JsonObject().put(ID, courseId));
    }

    public static EventBuilder getUpdateLessonEventBuilder(String lessonId) {
        return () -> new JsonObject().put(EVENT_NAME, EVT_LESSON_UPDATE).put(EVENT_BODY,
            new JsonObject().put(ID, lessonId));
    }

    public static EventBuilder getUpdateUnitEventBuilder(String unitId) {
        return () -> new JsonObject().put(EVENT_NAME, EVT_UNIT_UPDATE).put(EVENT_BODY,
            new JsonObject().put(ID, unitId));
    }

}
