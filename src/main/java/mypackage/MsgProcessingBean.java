/*
 * Copyright (c)  2020 Oracle and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package mypackage;

import java.util.concurrent.SubmissionPublisher;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.sse.OutboundSseEvent;
import javax.ws.rs.sse.SseBroadcaster;
import javax.ws.rs.sse.SseEventSink;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.eclipse.microprofile.reactive.streams.operators.ProcessorBuilder;
import org.eclipse.microprofile.reactive.streams.operators.ReactiveStreams;
import org.glassfish.jersey.internal.jsr166.Flow;
import org.glassfish.jersey.media.sse.OutboundEvent;
import org.reactivestreams.FlowAdapters;
import org.reactivestreams.Publisher;

/**
 * Bean for message processing.
 */
@ApplicationScoped
public class MsgProcessingBean {
    private final SubmissionPublisher<String> emitter = new SubmissionPublisher<>();
    private final SubmissionPublisher<String> broadcaster = new SubmissionPublisher<>();

    /**
     * Create a publisher for the emitter.
     *
     * @return A Publisher from the emitter
     */
    @Outgoing("multiplyVariants")
    public Publisher<String> preparePublisher() {
        // Create new publisher for emitting to by this::process
        return ReactiveStreams
                .fromPublisher(FlowAdapters.toPublisher(emitter))
                .buildRs();
    }

    /**
     * Returns a builder for a processor that maps a string into three variants.
     *
     * @return ProcessorBuilder
     */
    @Incoming("multiplyVariants")
    @Outgoing("wrapSseEvent")
    public ProcessorBuilder<String, String> multiply() {
        // Multiply to 3 variants of same message
        return ReactiveStreams.<String>builder()
                .flatMap(o ->
                        ReactiveStreams.of(
                                // upper case variant
                                o.toUpperCase(),
                                // repeat twice variant
                                o.repeat(2),
                                // reverse chars 'tnairav'
                                new StringBuilder(o).reverse().toString())
                );
    }


    @Incoming("wrapSseEvent")
    public void wrapSseEvent(String msg) {
        System.out.println("broadcasting "+ msg);
        this.broadcaster.submit(msg);
    }

    /**
     * Consumes events.
     *
     * @param eventSink event sink
     * @param sse event
     */
    public void addSink(final SseEventSink eventSink) {
        broadcaster.subscribe(JerseySubscriber.create(eventSink));
    }

    /**
     * Emit a message.
     *
     * @param msg message to emit
     */
    public void process(final String msg) {
        emitter.submit(msg);
    }
}
