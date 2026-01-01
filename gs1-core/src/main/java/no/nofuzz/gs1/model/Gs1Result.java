package no.nofuzz.gs1.model;

import java.util.Map;

public class Gs1Result {

    private final Map<String, Gs1Element> map;

    public Gs1Result(Map<String, Gs1Element> map) {
        this.map = Map.copyOf(map);
    }

    public Object get(String ai) {
        return map.get(ai).value();
    }

    public Map<String, Gs1Element> asMap() {
        return map;
    }
}