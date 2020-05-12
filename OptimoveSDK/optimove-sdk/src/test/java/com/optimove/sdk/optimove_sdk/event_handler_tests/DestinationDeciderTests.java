package com.optimove.sdk.optimove_sdk.event_handler_tests;

import com.optimove.sdk.optimove_sdk.main.OptistreamEventBuilder;
import com.optimove.sdk.optimove_sdk.main.event_handlers.DestinationDecider;
import com.optimove.sdk.optimove_sdk.main.event_handlers.OptistreamHandler;
import com.optimove.sdk.optimove_sdk.main.events.OptimoveEvent;
import com.optimove.sdk.optimove_sdk.main.events.SimpleCustomEvent;
import com.optimove.sdk.optimove_sdk.main.events.core_events.SetEmailEvent;
import com.optimove.sdk.optimove_sdk.main.events.core_events.SetUserIdEvent;
import com.optimove.sdk.optimove_sdk.main.events.core_events.UserAgentHeaderEvent;
import com.optimove.sdk.optimove_sdk.main.sdk_configs.reused_configs.EventConfigs;
import com.optimove.sdk.optimove_sdk.optitrack.OptistreamEvent;
import com.optimove.sdk.optimove_sdk.realtime.RealtimeManager;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static info.solidsoft.mockito.java8.AssertionMatcher.assertArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

public class DestinationDeciderTests {

    @Mock
    private Map<String, EventConfigs> eventConfigsMap;
    @Mock
    private OptistreamHandler optistreamHandler;
    @Mock
    private RealtimeManager realtimeManager;
    @Mock
    private OptistreamEventBuilder optistreamEventBuilder;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void eventShouldntBeReportedToRealtimeIfNotSupportedOnRealtime() {
        String eventName = "some_event_name";
        EventConfigs eventConfigs = mock(EventConfigs.class);
        when(eventConfigsMap.get(eventName)).thenReturn(eventConfigs);
        when(eventConfigs.isSupportedOnRealtime()).thenReturn(false);

        OptimoveEvent optimoveEvent = new SimpleCustomEvent(eventName, new HashMap<>());
        DestinationDecider destinationDecider = new DestinationDecider(eventConfigsMap, optistreamHandler,
                realtimeManager,
                optistreamEventBuilder, true, false);
        destinationDecider.reportEvent(Collections.singletonList(optimoveEvent));
        verifyZeroInteractions(realtimeManager);

    }

    @Test
    public void eventShouldntBeReportedToRealtimeIfRealtimeDisabled() {
        String eventName = "some_event_name";
        EventConfigs eventConfigs = mock(EventConfigs.class);
        when(eventConfigsMap.get(eventName)).thenReturn(eventConfigs);
        when(eventConfigs.isSupportedOnRealtime()).thenReturn(true);

        OptimoveEvent optimoveEvent = new SimpleCustomEvent(eventName, new HashMap<>());
        DestinationDecider destinationDecider = new DestinationDecider(eventConfigsMap, optistreamHandler,
                realtimeManager,
                optistreamEventBuilder, false, false);
        destinationDecider.reportEvent(Collections.singletonList(optimoveEvent));
        verifyZeroInteractions(realtimeManager);
    }
    @Test
    public void eventShouldntBeReportedToRealtimeIfRealtimeEnabledThroughOptistream() {
        String eventName = "some_event_name";
        EventConfigs eventConfigs = mock(EventConfigs.class);
        when(eventConfigsMap.get(eventName)).thenReturn(eventConfigs);
        when(eventConfigs.isSupportedOnRealtime()).thenReturn(true);

        OptimoveEvent optimoveEvent = new SimpleCustomEvent(eventName, new HashMap<>());
        DestinationDecider destinationDecider = new DestinationDecider(eventConfigsMap, optistreamHandler,
                realtimeManager,
                optistreamEventBuilder, true, true);
        destinationDecider.reportEvent(Collections.singletonList(optimoveEvent));
        verifyZeroInteractions(realtimeManager);
    }

    @Test
    public void eventShouldBeReportedToRealtimeIfRealtimeEnabledThroughOptistreamDisabled() {
        String eventName = "some_event_name";
        EventConfigs eventConfigs = mock(EventConfigs.class);
        OptimoveEvent optimoveEvent = new SimpleCustomEvent(eventName, new HashMap<>());

        when(eventConfigsMap.get(eventName)).thenReturn(eventConfigs);
        when(eventConfigs.isSupportedOnRealtime()).thenReturn(true);
        OptistreamEvent optistreamEvent = mock(OptistreamEvent.class);
        when(optistreamEventBuilder.convertOptimoveToOptistreamEvent(eq(optimoveEvent), anyBoolean())).thenReturn(optistreamEvent);

        when(optistreamEvent.getName()).thenReturn(eventName);

        DestinationDecider destinationDecider = new DestinationDecider(eventConfigsMap, optistreamHandler,
                realtimeManager,
                optistreamEventBuilder, true, false);
        destinationDecider.reportEvent(Collections.singletonList(optimoveEvent));
        verify(realtimeManager).reportEvents(assertArg(arg -> Assert.assertTrue(arg.get(0).getName().equals(
                eventName))));
    }
//    @Test
//    public void eventShouldBeReportedToOptitrackIfSupportedOnOptitrack() {
//        String eventName = "some_event_name";
//        EventConfigs eventConfigs = mock(EventConfigs.class);
//        when(eventConfigsMap.get(eventName)).thenReturn(eventConfigs);
//        when(eventConfigs.isSupportedOnOptitrack()).thenReturn(true);
//
//        OptimoveEvent optimoveEvent = new SimpleCustomEvent(eventName, new HashMap<>());
//        EventContext eventContext = new EventContext(optimoveEvent);
//        componentPool.reportEvent(eventContext);
//        verify(optitrackManager).reportEvent(optimoveEvent,eventConfigs);
//    }
//    @Test
//    public void eventShouldBeReportedToOptitrackAndDispatchedIfProcessingTimeout() {
//        String eventName = "some_event_name";
//        int timeout = 5;
//        EventConfigs eventConfigs = mock(EventConfigs.class);
//        when(eventConfigsMap.get(eventName)).thenReturn(eventConfigs);
//        when(eventConfigs.isSupportedOnOptitrack()).thenReturn(true);
//
//        OptimoveEvent optimoveEvent = new SimpleCustomEvent(eventName, new HashMap<>());
//        EventContext eventContext = new EventContext(optimoveEvent, timeout);
//        componentPool.reportEvent(eventContext);
//        InOrder inOrder = inOrder(optitrackManager);
//        inOrder.verify(optitrackManager).reportEvent(optimoveEvent,eventConfigs);
//        inOrder.verify(optitrackManager).setTimeout(timeout);
//        inOrder.verify(optitrackManager).sendAllEventsNow();
//    }
}