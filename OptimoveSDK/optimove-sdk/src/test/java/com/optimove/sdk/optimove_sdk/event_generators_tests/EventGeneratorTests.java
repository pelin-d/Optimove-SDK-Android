package com.optimove.sdk.optimove_sdk.event_generators_tests;

import android.content.Context;
import android.content.SharedPreferences;

import com.optimove.sdk.optimove_sdk.main.EventHandlerProvider;
import com.optimove.sdk.optimove_sdk.main.TenantInfo;
import com.optimove.sdk.optimove_sdk.main.UserInfo;
import com.optimove.sdk.optimove_sdk.main.event_generators.EventGenerator;
import com.optimove.sdk.optimove_sdk.main.event_handlers.EventHandler;
import com.optimove.sdk.optimove_sdk.main.events.core_events.OptipushOptIn;
import com.optimove.sdk.optimove_sdk.main.events.core_events.OptipushOptOut;
import com.optimove.sdk.optimove_sdk.main.events.core_events.SdkMetadataEvent;
import com.optimove.sdk.optimove_sdk.main.events.core_events.SetAdvertisingIdEvent;
import com.optimove.sdk.optimove_sdk.main.events.core_events.UserAgentHeaderEvent;
import com.optimove.sdk.optimove_sdk.main.tools.RequirementProvider;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static com.optimove.sdk.optimove_sdk.optitrack.OptitrackConstants.LAST_OPT_REPORTED_KEY;
import static com.optimove.sdk.optimove_sdk.optitrack.OptitrackConstants.LAST_REPORTED_OPT_IN;
import static com.optimove.sdk.optimove_sdk.optitrack.OptitrackConstants.LAST_REPORTED_OPT_OUT;
import static com.optimove.sdk.optimove_sdk.optitrack.OptitrackConstants.OPTITRACK_SP_NAME;
import static info.solidsoft.mockito.java8.AssertionMatcher.assertArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

public class EventGeneratorTests {


    @Mock
    SharedPreferences optitrackPreferences;
    @Mock
    SharedPreferences.Editor editor;
    @Mock
    UserInfo userInfo;


    private String packageName = "package_name";
    private String encryptedDeviceId = "some_encrypted_device_id";
    @Mock
    private RequirementProvider requirementProvider;
    @Mock
    private TenantInfo tenantInfo;
    @Mock
    private EventHandlerProvider eventHandlerProvider;
    @Mock
    private EventHandler eventHandler;
    @Mock
    private Context context;


    private EventGenerator eventGenerator;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        //context
        when(context.getSharedPreferences(OPTITRACK_SP_NAME, Context.MODE_PRIVATE)).thenReturn(optitrackPreferences);
        //shared prefs
        when(optitrackPreferences.edit()).thenReturn(editor);
        when(optitrackPreferences.getInt(eq(LAST_OPT_REPORTED_KEY), anyInt())).thenReturn(-1);
        when(editor.putLong(anyString(), anyLong())).thenReturn(editor);
        when(editor.putString(anyString(), any())).thenReturn(editor);
        when(editor.putInt(anyString(), anyInt())).thenReturn(editor);

        when(userInfo.getAdvertisingId()).thenReturn("asdgsdfg");

        when(eventHandlerProvider.getEventHandler()).thenReturn(eventHandler);


        eventGenerator =
                EventGenerator.builder()
                        .withUserInfo(userInfo)
                        .withPackageName(packageName)
                        .withEncryptedDeviceId(encryptedDeviceId)
                        .withRequirementProvider(requirementProvider)
                        .withTenantInfo(tenantInfo)
                        .withEventHandlerProvider(eventHandlerProvider)
                        .withContext(context)
                        .build();
    }

    @Test
    public void userAgentEventShouldBeSentWhenInitialized() {
        eventGenerator.generateStartEvents(false);
        verify(eventHandler,timeout(300)).reportEvent(assertArg(arg -> Assert.assertEquals(arg.getOptimoveEvent()
                .getName(), UserAgentHeaderEvent.EVENT_NAME)));
    }
    @Test
    public void sdkMetadataEventShouldBeSentWhenInitialized() {
        eventGenerator.generateStartEvents(false);
        verify(eventHandler,timeout(300)).reportEvent(assertArg(arg -> Assert.assertEquals(arg.getOptimoveEvent()
                .getName(), SdkMetadataEvent.EVENT_NAME)));
    }

    @Test
    public void adIdShouldBeReportedIfAdIdAllowedAndCanReportAdAndAdvertisingIdIsNotNull() {
        when(requirementProvider.canReportAdId()).thenReturn(true);
        when(userInfo.getAdvertisingId()).thenReturn("sdfgdsf");
        eventGenerator.generateStartEvents(true);
        verify(eventHandler,timeout(300)).reportEvent(assertArg(arg -> Assert.assertEquals(arg.getOptimoveEvent()
                .getName(), SetAdvertisingIdEvent.EVENT_NAME)));
    }
    @Test
    public void adIdShouldntBeReportedIfAdvertisingIdIsntAllowed() {
        eventGenerator.generateStartEvents(false);
        verify(eventHandler, times(0)).reportEvent(assertArg(arg -> Assert.assertEquals(arg.getOptimoveEvent()
                .getName(), SetAdvertisingIdEvent.EVENT_NAME)));
    }
    @Test
    public void adIdShouldntBeReportedIfCantReportAdId() {
        when(requirementProvider.canReportAdId()).thenReturn(false);
        eventGenerator.generateStartEvents(true);
        verify(eventHandler, times(0)).reportEvent(assertArg(arg -> Assert.assertEquals(arg.getOptimoveEvent()
                .getName(), SetAdvertisingIdEvent.EVENT_NAME)));
    }
    @Test
    public void adIdShouldntBeReportedIfAdvertisingIdIsNull() {
        when(requirementProvider.canReportAdId()).thenReturn(true);
        when(userInfo.getAdvertisingId()).thenReturn(null);
        eventGenerator.generateStartEvents(true);
        verify(eventHandler, times(0)).reportEvent(assertArg(arg -> Assert.assertEquals(arg.getOptimoveEvent()
                .getName(), SetAdvertisingIdEvent.EVENT_NAME)));
    }
    @Test
    public void neitherOptInNorOptOutShouldBeSentWhenWasOptinAndCurrentlyOptIn() {
        when(optitrackPreferences.getInt(eq(LAST_OPT_REPORTED_KEY), anyInt())).thenReturn(LAST_REPORTED_OPT_IN);
        when(requirementProvider.notificaionsAreEnabled()).thenReturn(true);
        eventGenerator.generateStartEvents(false);
        verifyZeroInteractions(editor);
    }
    @Test
    public void optInShouldBeSentWhenWasntOptinAndCurrentlyIs() {
        when(requirementProvider.notificaionsAreEnabled()).thenReturn(true);
        when(optitrackPreferences.getInt(eq(LAST_OPT_REPORTED_KEY), anyInt())).thenReturn(LAST_REPORTED_OPT_OUT);
        eventGenerator.generateStartEvents(false);

        verify(eventHandler, timeout(300)).reportEvent(assertArg(arg -> Assert.assertEquals(arg.getOptimoveEvent()
                .getName(), OptipushOptIn.EVENT_NAME)));
        verify(eventHandler,timeout(300)).reportEvent(assertArg(arg -> Assert.assertTrue(arg.getExecutionTimeout() > 0)));

        InOrder inOrder = inOrder(editor);
        inOrder.verify(editor)
                .putInt(LAST_OPT_REPORTED_KEY, LAST_REPORTED_OPT_IN);
        inOrder.verify(editor)
                .apply();
    }

    @Test
    public void optOutShouldBeSentWhenWasOptinAndCurrentlyIsnt() {
        when(requirementProvider.notificaionsAreEnabled()).thenReturn(false);
        when(optitrackPreferences.getInt(eq(LAST_OPT_REPORTED_KEY), anyInt())).thenReturn(LAST_REPORTED_OPT_IN);

        eventGenerator.generateStartEvents(false);

        verify(eventHandler,timeout(300)).reportEvent(assertArg(arg -> Assert.assertEquals(arg.getOptimoveEvent()
                .getName(), OptipushOptOut.EVENT_NAME)));
        verify(eventHandler,timeout(300)).reportEvent(assertArg(arg -> Assert.assertTrue(arg.getExecutionTimeout() > 0)));

        InOrder inOrder = inOrder(editor);
        inOrder.verify(editor)
                .putInt(LAST_OPT_REPORTED_KEY, LAST_REPORTED_OPT_OUT);
        inOrder.verify(editor)
                .apply();
    }

}