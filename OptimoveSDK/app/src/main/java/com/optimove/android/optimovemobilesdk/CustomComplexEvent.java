package com.optimove.android.optimovemobilesdk;

import com.optimove.android.main.events.OptimoveEvent;

import java.util.HashMap;
import java.util.Map;

public class CustomComplexEvent extends OptimoveEvent {

    public CustomComplexEvent() {
    }

    @Override
    public String getName() {
        return "custom_complex_event";
    }

    @Override
    public Map<String, Object> getParameters() {
        HashMap<String, Object> result = new HashMap<>();
        String val = "some_string";
        result.put("string_param", val);
        result.put("number_param", 42);
        return result;
    }
}
