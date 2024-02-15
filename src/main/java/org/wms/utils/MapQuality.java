package org.wms.utils;

import java.util.HashMap;

public class MapQuality {
    private HashMap<Integer, String> qualitiesByNumber = new HashMap<>();
    private HashMap<String, Integer> qualitiesByName = new HashMap<>();

    public String getQualitiesName(int n) {
        qualitiesByNumber.put(1, "poor");
        qualitiesByNumber.put(2, "bad");
        qualitiesByNumber.put(3, "average");
        qualitiesByNumber.put(4, "good");
        qualitiesByNumber.put(5, "best");
        return qualitiesByNumber.get(n);
    }
    public int getQualitiesValues(String quality){
        qualitiesByName.put("poor", 1);
        qualitiesByName.put("bad", 2);
        qualitiesByName.put("average", 3);
        qualitiesByName.put("good", 4);
        qualitiesByName.put("best", 5);
        return qualitiesByName.get(quality);
    }
}
