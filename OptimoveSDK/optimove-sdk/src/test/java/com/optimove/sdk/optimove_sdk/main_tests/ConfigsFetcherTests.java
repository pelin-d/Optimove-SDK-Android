package com.optimove.sdk.optimove_sdk.main_tests;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.collection.ArraySet;

import com.android.volley.ParseError;
import com.android.volley.Response;
import com.google.gson.Gson;
import com.optimove.sdk.optimove_sdk.fixtures.ConfigProvider;
import com.optimove.sdk.optimove_sdk.main.sdk_configs.ConfigsFetcher;
import com.optimove.sdk.optimove_sdk.main.sdk_configs.configs.Configs;
import com.optimove.sdk.optimove_sdk.main.sdk_configs.fetched_configs.FetchedGlobalConfig;
import com.optimove.sdk.optimove_sdk.main.sdk_configs.fetched_configs.FetchedTenantConfigs;
import com.optimove.sdk.optimove_sdk.main.tools.FileUtils;
import com.optimove.sdk.optimove_sdk.main.tools.networking.HttpClient;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Map;
import java.util.Set;

import static com.optimove.sdk.optimove_sdk.main.sdk_configs.ConfigsFetcher.GLOBAL_CONFIG_FILE_BASE_URL;
import static com.optimove.sdk.optimove_sdk.main.sdk_configs.ConfigsFetcher.TENANT_CONFIG_FILE_BASE_URL;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class ConfigsFetcherTests {

    @Mock
    private SharedPreferences localConfigKeysPreferences;
    @Mock
    private HttpClient httpClient;
    @Mock
    HttpClient.RequestBuilder<FetchedTenantConfigs> tenantBuilder;
    @Mock
    HttpClient.RequestBuilder<FetchedGlobalConfig> globalBuilder;
    @Mock
    private FileUtils fileUtils;
    @Mock
    private Context context;
    @Mock
    FileUtils.Reader reader;
    @Mock
    SharedPreferences.Editor editor;

    private String configName = "config_name";
    private String tenantToken = "tenant_info";
    private ConfigsFetcher regularConfigFetcher;
    private Configs configs;

    private Gson gson;


    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        gson = new Gson();
        Class<FetchedGlobalConfig> fetchedGlobalConfigClass = FetchedGlobalConfig.class;
        Class<FetchedTenantConfigs> fetchedTenantConfigsClass = FetchedTenantConfigs.class;


        when(httpClient.getObject(GLOBAL_CONFIG_FILE_BASE_URL, fetchedGlobalConfigClass)).thenReturn(globalBuilder);
        when(globalBuilder.destination(any(), any(), anyString())).thenReturn(globalBuilder);
        when(globalBuilder.destination(any(), any())).thenReturn(globalBuilder);
        when(globalBuilder.errorListener(any())).thenReturn(globalBuilder);

        when(httpClient.getObject(TENANT_CONFIG_FILE_BASE_URL, fetchedTenantConfigsClass)).thenReturn(tenantBuilder);
        when(tenantBuilder.destination(any(), any(), anyString())).thenReturn(tenantBuilder);
        when(tenantBuilder.destination(any(), any())).thenReturn(tenantBuilder);
        when(tenantBuilder.errorListener(any())).thenReturn(tenantBuilder);

        when(localConfigKeysPreferences.edit()).thenReturn(editor);
        when(editor.putBoolean(anyString(), anyBoolean())).thenReturn(editor);
        when(tenantBuilder.successListener(any())).thenReturn(tenantBuilder);
        when(globalBuilder.successListener(any())).thenReturn(globalBuilder);
        when(fileUtils.readFile(eq(context))).thenReturn(reader);
        when(reader.from(FileUtils.SourceDir.INTERNAL)).thenReturn(reader);
        when(reader.named(configName)).thenReturn(reader);

        String packageName = "some_package_name";
        when(context.getPackageName()).thenReturn(packageName);

        regularConfigFetcher = ConfigsFetcher.builder()
                .httpClient(httpClient)
                .tenantToken(tenantToken)
                .configName(configName)
                .sharedPrefs(localConfigKeysPreferences)
                .fileProvider(fileUtils)
                .context(context)
                .build();
        configs = ConfigProvider.getConfigs();

    }

    @Test(timeout = 1000)
    public void shouldReadFromFileIfNetworkErrorFromTenantConfigsFetch() {
        doAnswer(invocation -> {
            Response.ErrorListener errorListener =
                    (Response.ErrorListener) invocation.getArguments()[0];
            errorListener.onErrorResponse(mock(ParseError.class));
            return tenantBuilder;
        }).when(tenantBuilder)
                .errorListener(any());

        int randomTenantId = 56757;
        Gson gson = new Gson();
        when(localConfigKeysPreferences.getBoolean(eq(configName), anyBoolean())).thenReturn(true);
        Configs storedInFileConfig = ConfigProvider.getConfigs();
        storedInFileConfig.setTenantId(randomTenantId);
        when(reader.asString()).thenReturn(gson.toJson(storedInFileConfig));

        regularConfigFetcher.fetchConfigs(configs -> Assert.assertEquals(configs.getTenantId(), randomTenantId)
                , Assert::fail);
    }

    @Test(timeout = 1000)
    public void shouldReadFromFileIfNetworkErrorFromGlobalConfigsFetch() {
        doAnswer(invocation -> {
            Response.ErrorListener errorListener =
                    (Response.ErrorListener) invocation.getArguments()[0];
            errorListener.onErrorResponse(mock(ParseError.class));
            return globalBuilder;
        }).when(globalBuilder)
                .errorListener(any());

        int randomTenantId = 56757;
        Gson gson = new Gson();
        when(localConfigKeysPreferences.getBoolean(eq(configName), anyBoolean())).thenReturn(true);
        Configs storedInFileConfig = ConfigProvider.getConfigs();
        storedInFileConfig.setTenantId(randomTenantId);
        when(reader.asString()).thenReturn(gson.toJson(storedInFileConfig));

        regularConfigFetcher.fetchConfigs(configs -> Assert.assertEquals(configs.getTenantId(), randomTenantId)
                , Assert::fail);
    }

    @Test(timeout = 1000)
    public void shouldDeleteRedundantLocalConfigsIfTakenFromLocal() {
        String first = "first";
        String second = "second";
        Map savedFiles = mock(Map.class);
        Set<String> keySet = new ArraySet<>();
        keySet.add(first);
        keySet.add(second);

        when(localConfigKeysPreferences.getAll()).thenReturn(savedFiles);
        when(savedFiles.keySet()).thenReturn(keySet);
        applyParseErrorOnGlobalBuilder();
        FileUtils.Deleter deleter = mock(FileUtils.Deleter.class);
        when(fileUtils.deleteFile(context)).thenReturn(deleter);
        when(deleter.named(first)).thenReturn(deleter);
        when(deleter.named(second)).thenReturn(deleter);
        when(deleter.from(FileUtils.SourceDir.INTERNAL)).thenReturn(deleter);


        regularConfigFetcher.fetchConfigs(configs -> fail()
                , error -> {
                });
        InOrder inOrder = Mockito.inOrder(editor);
        int timeout = 1000;
        verify(editor, timeout(timeout)).remove(first);
        verify(editor, timeout(timeout)).remove(second);
        inOrder.verify(editor, timeout(timeout))
                .apply();
        inOrder.verifyNoMoreInteractions();

        verify(deleter, times(keySet.size())).now();
    }


    @Test
    public void shouldFetchConfigFileCorrectlyAndSaveIt() {
        applyPositiveConfigFetch();
        FileUtils.Writer writer = getMockFileWriter();
        regularConfigFetcher.fetchConfigs(configs ->
                        Assert.assertTrue(configsAreTheSame(configs, this.configs))
                , Assert::fail);

        InOrder inOrder = Mockito.inOrder(editor);
        inOrder.verify(editor, timeout(1000))
                .putBoolean(configName, true);
        inOrder.verify(editor, timeout(1000))
                .apply();
        verify(writer, timeout(1000)).now();
    }

    @Test(timeout = 1000)
    public void shouldFailConfigIfRemoteGlobalConfigMissesMBaaSEndpoint() {
        FetchedTenantConfigs fetchedTenantConfigs = gson.fromJson(ConfigProvider.getTenantConfigJsonString(),
                FetchedTenantConfigs.class);
        FetchedGlobalConfig fetchedGlobalConfig = gson.fromJson(ConfigProvider.getGlobalConfigJsonString(),
                FetchedGlobalConfig.class);
        fetchedGlobalConfig.fetchedOptipushConfigs.mbaasEndpoint = null;
        applyCustomConfigFetch(fetchedTenantConfigs, fetchedGlobalConfig);

        regularConfigFetcher.fetchConfigs(configs -> fail(), Assert::assertNotNull);
    }

    @Test
    public void shouldFailConfigIfLocalFileMissesMBaaSEndpoint() {
        int randomTenantId = 56757;
        Gson gson = new Gson();
        ConfigsFetcher.ConfigsErrorListener configsErrorListener = mock(ConfigsFetcher.ConfigsErrorListener.class);
        applyParseErrorOnGlobalBuilder();

        when(localConfigKeysPreferences.getBoolean(eq(configName), anyBoolean())).thenReturn(true);
        Configs storedInFileConfig = ConfigProvider.getConfigs();
        storedInFileConfig.setOptipushConfigs(null);
        storedInFileConfig.setTenantId(randomTenantId);
        when(reader.asString()).thenReturn(gson.toJson(storedInFileConfig));

        regularConfigFetcher.fetchConfigs(mock(ConfigsFetcher.ConfigsListener.class), configsErrorListener);
        verify(configsErrorListener, timeout(1000)).error(anyString());
    }

    @Test
    public void shouldFailConfigIfLocalFileMissesOptitrackConfigs() {
        int randomTenantId = 56757;
        Gson gson = new Gson();
        ConfigsFetcher.ConfigsErrorListener configsErrorListener = mock(ConfigsFetcher.ConfigsErrorListener.class);
        applyParseErrorOnGlobalBuilder();

        when(localConfigKeysPreferences.getBoolean(eq(configName), anyBoolean())).thenReturn(true);
        Configs storedInFileConfig = ConfigProvider.getConfigs();
        storedInFileConfig.setOptitrackConfigs(null);
        storedInFileConfig.setTenantId(randomTenantId);
        when(reader.asString()).thenReturn(gson.toJson(storedInFileConfig));

        regularConfigFetcher.fetchConfigs(mock(ConfigsFetcher.ConfigsListener.class), configsErrorListener);
        verify(configsErrorListener, timeout(1000)).error(anyString());
    }

    @Test
    public void shouldFailConfigIfLocalFileMissesRealtimeConfigs() {
        int randomTenantId = 56757;
        Gson gson = new Gson();
        ConfigsFetcher.ConfigsErrorListener configsErrorListener = mock(ConfigsFetcher.ConfigsErrorListener.class);
        applyParseErrorOnGlobalBuilder();

        when(localConfigKeysPreferences.getBoolean(eq(configName), anyBoolean())).thenReturn(true);
        Configs storedInFileConfig = ConfigProvider.getConfigs();
        storedInFileConfig.setRealtimeConfigs(null);
        storedInFileConfig.setTenantId(randomTenantId);
        when(reader.asString()).thenReturn(gson.toJson(storedInFileConfig));

        regularConfigFetcher.fetchConfigs(mock(ConfigsFetcher.ConfigsListener.class), configsErrorListener);
        verify(configsErrorListener, timeout(1000)).error(anyString());
    }

    private void applyParseErrorOnGlobalBuilder() {
        doAnswer(invocation -> {
            Response.ErrorListener errorListener =
                    (Response.ErrorListener) invocation.getArguments()[0];
            errorListener.onErrorResponse(mock(ParseError.class));
            return globalBuilder;
        }).when(globalBuilder)
                .errorListener(any());
    }

    private FileUtils.Writer getMockFileWriter() {
        FileUtils.Writer writer = mock(FileUtils.Writer.class);
        when(fileUtils.write(eq(context), eq(new Gson().toJson(this.configs)))).thenReturn(writer);
        when(writer.to(configName)).thenReturn(writer);
        when(writer.in(FileUtils.SourceDir.INTERNAL)).thenReturn(writer);
        return writer;
    }

    private void applyPositiveConfigFetch() {
        applyCustomConfigFetch(gson.fromJson(ConfigProvider.getTenantConfigJsonString(),
                FetchedTenantConfigs.class), gson.fromJson(ConfigProvider.getGlobalConfigJsonString(),
                FetchedGlobalConfig.class));
    }

    private void applyCustomConfigFetch(FetchedTenantConfigs fetchedTenantConfigs,
                                        FetchedGlobalConfig fetchedGlobalConfig) {
        doAnswer(invocation -> {
            Response.Listener<FetchedTenantConfigs> successListener =
                    (Response.Listener<FetchedTenantConfigs>) invocation.getArguments()[0];
            successListener.onResponse(fetchedTenantConfigs);
            return tenantBuilder;
        }).when(tenantBuilder)
                .successListener(any());
        doAnswer(invocation -> {
            Response.Listener<FetchedGlobalConfig> successListener =
                    (Response.Listener<FetchedGlobalConfig>) invocation.getArguments()[0];
            successListener.onResponse(fetchedGlobalConfig);
            return globalBuilder;
        }).when(globalBuilder)
                .successListener(any());
    }

    private boolean configsAreTheSame(Configs configsFirst, Configs configsSecond) {
        Gson gson = new Gson();
        return gson.toJson(configsFirst)
                .equals(gson.toJson(configsSecond));
    }


}
