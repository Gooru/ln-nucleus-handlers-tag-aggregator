package org.gooru.nucleus.handlers.tag.aggregator.processors;

import java.util.UUID;

import org.gooru.nucleus.handlers.tag.aggregator.constants.MessageConstants;

/**
 * Created by ashish on 20/3/16.
 */
final class ProcessorContextHelper {

    static boolean validateUser(String userId) {
        return !(userId == null || userId.isEmpty())
            && (userId.equalsIgnoreCase(MessageConstants.MSG_USER_ANONYMOUS) || validateId(userId));
    }

    private ProcessorContextHelper() {
        throw new AssertionError("Should not instantiate");
    }

    private static boolean validateId(String id) {
        return !(id == null || id.isEmpty()) && validateUuid(id);
    }

    private static boolean validateUuid(String uuidString) {
        try {
            UUID.fromString(uuidString);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
