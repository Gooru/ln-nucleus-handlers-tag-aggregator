package org.gooru.nucleus.handlers.tag.aggregator.processors.repositories.activejdbc;

import org.gooru.nucleus.handlers.tag.aggregator.processors.ProcessorContext;
import org.gooru.nucleus.handlers.tag.aggregator.processors.repositories.CourseRepo;
import org.gooru.nucleus.handlers.tag.aggregator.processors.repositories.activejdbc.dbhandlers.DBHandlerBuilder;
import org.gooru.nucleus.handlers.tag.aggregator.processors.repositories.activejdbc.transactions.TransactionExecutor;
import org.gooru.nucleus.handlers.tag.aggregator.processors.responses.MessageResponse;

/**
 * @author szgooru Created On: 12-Sep-2017
 */
public class AJCourseRepo implements CourseRepo {

    private ProcessorContext context;
    
    public AJCourseRepo(ProcessorContext context) {
        this.context = context;
    }

    @Override
    public MessageResponse aggregate() {
        return TransactionExecutor.executeTransaction(DBHandlerBuilder.buildCourseLevelAggragatorHandler(context));
    }

}
