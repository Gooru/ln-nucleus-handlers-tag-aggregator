package org.gooru.nucleus.handlers.tag.aggregator.bootstrap;

import org.gooru.nucleus.handlers.tag.aggregator.bootstrap.shutdown.Finalizer;
import org.gooru.nucleus.handlers.tag.aggregator.bootstrap.shutdown.Finalizers;
import org.gooru.nucleus.handlers.tag.aggregator.bootstrap.startup.Initializer;
import org.gooru.nucleus.handlers.tag.aggregator.bootstrap.startup.Initializers;
import org.gooru.nucleus.handlers.tag.aggregator.constants.MessageBusEndpoints;
import org.gooru.nucleus.handlers.tag.aggregator.processors.ProcessorBuilder;
import org.gooru.nucleus.handlers.tag.aggregator.processors.responses.MessageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.EventBus;

/**
 * @author szgooru Created On: 08-Sep-2017
 */
public class TagAggregatorVerticle extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(TagAggregatorVerticle.class);
    
    @Override
    public void start(Future<Void> startFuture) throws Exception {
        EventBus eb = vertx.eventBus();

        vertx.executeBlocking(blockingFuture -> {
            startApplication();
            blockingFuture.complete();
        }, startApplicationFuture -> {
            if (startApplicationFuture.succeeded()) {
                eb.consumer(MessageBusEndpoints.MBEP_TAG_AGGREGATOR, message -> {
                    LOGGER.debug("Received message: '{}'", message.body());
                    vertx.executeBlocking(future -> {
                        MessageResponse result = ProcessorBuilder.build(message).process();
                        future.complete(result);
                    }, res -> {
                        MessageResponse result = (MessageResponse) res.result();
                        LOGGER.debug("Sending response: '{}'", result.reply());
                        message.reply(result.reply(), result.deliveryOptions());
                    });
                }).completionHandler(result -> {
                    if (result.succeeded()) {
                        LOGGER.info("Tag aggregator end point ready to listen");
                        startFuture.complete();
                    } else {
                        LOGGER.error("Error registering the tag aggregator handler. Halting the tag aggregator machinery");
                        startFuture.fail(result.cause());
                        Runtime.getRuntime().halt(1);
                    }
                });
            } else {
                startFuture.fail("Not able to initialize the tag aggregator machinery properly");
            }
        });
    }

    @Override
    public void stop(Future<Void> stopFuture) throws Exception {
        shutDownApplication();
    }

    private void startApplication() {
        Initializers initializers = new Initializers();
        try {
            for (Initializer initializer : initializers) {
                initializer.initializeComponent(vertx, config());
            }
        } catch (IllegalStateException ise) {
            LOGGER.error("Error initializing application", ise);
            Runtime.getRuntime().halt(1);
        }
    }

    private void shutDownApplication() {
        Finalizers finalizers = new Finalizers();
        for (Finalizer finalizer : finalizers) {
            finalizer.finalizeComponent();
        }
    }
}
