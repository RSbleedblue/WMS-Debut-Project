package org.wms.utils;

import java.util.HashMap;

public class MapQuality {
    private HashMap<Integer, String> qualitiesByNumber = new HashMap<>();
    private HashMap<String, Integer> qualitiesByName = new HashMap<>();
    public String getQualitiesName(int n) {
        qualitiesByNumber.put(1, "basic");
        qualitiesByNumber.put(2, "fair");
        qualitiesByNumber.put(3, "average");
        qualitiesByNumber.put(4, "good");
        qualitiesByNumber.put(5, "best");
        return qualitiesByNumber.get(n).toUpperCase();
    }
    public int getQualitiesValues(String quality){
        qualitiesByName.put("basic", 1);
        qualitiesByName.put("fair", 2);
        qualitiesByName.put("average", 3);
        qualitiesByName.put("good", 4);
        qualitiesByName.put("best", 5);
        return qualitiesByName.get(quality);
    }
}
