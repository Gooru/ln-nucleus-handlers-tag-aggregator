package org.gooru.nucleus.handlers.tag.aggregator.processors;

import io.vertx.core.MultiMap;
import io.vertx.core.json.JsonObject;

/**
 * @author szgooru Created On: 26-May-2017
 */
public final class ProcessorContext {
    private final String userId;
    private final JsonObject session;
    private final JsonObject request;
    private final String libraryId;
    private final MultiMap requestHeaders;
    private final TenantContext tenantContext;

    private ProcessorContext(String userId, JsonObject session, JsonObject request, String libraryId, MultiMap headers) {
        if (session == null || userId == null || session.isEmpty() || headers == null || headers.isEmpty()) {
            throw new IllegalStateException("Processor Context creation failed because of invalid values");
        }

        this.userId = userId;
        this.session = session.copy();
        this.request = request != null ? request.copy() : null;
        this.libraryId = libraryId;
        this.requestHeaders = headers;
        this.tenantContext = new TenantContext(session);
    }

    public String userId() {
        return this.userId;
    }

    public JsonObject session() {
        return this.session.copy();
    }

    public JsonObject request() {
        return this.request;
    }
    
    public String libraryId() {
        return this.libraryId;
    }

    public MultiMap requestHeaders() {
        return this.requestHeaders;
    }
    
    public String tenant() {
        return this.tenantContext.tenant();
    }

    public String tenantRoot() {
        return this.tenantContext.tenantRoot();
    }

    public static class ProcessorContextBuilder {
        private final String userId;
        private final JsonObject session;
        private final JsonObject request;
        private final String libraryId;
        private final MultiMap requestHeaders;
        private boolean built = false;

        ProcessorContextBuilder(String userId, JsonObject session, JsonObject request, String libraryId, MultiMap headers) {
            if (session == null || userId == null || session.isEmpty() || headers == null || headers.isEmpty()) {
                throw new IllegalStateException("Processor Context creation failed because of invalid values");
            }
            this.userId = userId;
            this.session = session.copy();
            this.request = request != null ? request.copy() : null;
            this.libraryId = libraryId;
            this.requestHeaders = headers;
        }

        ProcessorContext build() {
            if (this.built) {
                throw new IllegalStateException("Tried to build again");
            } else {
                this.built = true;
                return new ProcessorContext(userId, session, request, libraryId, requestHeaders);
            }
        }
    }
    
    private static class TenantContext {
        private static final String TENANT = "tenant";
        private static final String TENANT_ID = "tenant_id";
        private static final String TENANT_ROOT = "tenant_root";

        private final String tenantId;
        private final String tenantRoot;

        TenantContext(JsonObject session) {
            JsonObject tenantJson = session.getJsonObject(TENANT);
            if (tenantJson == null || tenantJson.isEmpty()) {
                throw new IllegalStateException("Tenant Context invalid");
            }
            this.tenantId = tenantJson.getString(TENANT_ID);
            if (tenantId == null || tenantId.isEmpty()) {
                throw new IllegalStateException("Tenant Context with invalid tenant");
            }
            this.tenantRoot = tenantJson.getString(TENANT_ROOT);
        }

        public String tenant() {
            return this.tenantId;
        }

        public String tenantRoot() {
            return this.tenantRoot;
        }
    }
}
