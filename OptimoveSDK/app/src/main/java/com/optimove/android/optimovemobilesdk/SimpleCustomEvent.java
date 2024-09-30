package com.optimove.android.optimovemobilesdk;

import com.optimove.android.main.events.OptimoveEvent;

import java.util.HashMap;
import java.util.Map;

public class SimpleCustomEvent extends OptimoveEvent {

    public SimpleCustomEvent() {
    }

    @Override
    public String getName() {
        return "Simple cUSTOM_Event     ";
    }

    @Override
    public Map<String, Object> getParameters() {
        HashMap<String, Object> result = new HashMap<>();
        String val = "  some_string  ";
        result.put("strinG_param", val);
        result.put("number_param", 42);
        return result;
    }
}
