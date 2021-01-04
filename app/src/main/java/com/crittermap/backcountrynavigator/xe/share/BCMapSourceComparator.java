package com.crittermap.backcountrynavigator.xe.share;

import android.text.TextUtils;

import com.crittermap.backcountrynavigator.xe.data.model.map.BCMap;
import com.crittermap.backcountrynavigator.xe.service.map.BCLocation;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class BCMapSourceComparator implements Comparator<BCMap> {

    private String state;
    private String country;

    public BCMapSourceComparator(String state, String country) {
        this.state = state;
        this.country = country;
    }

    @Override
    public int compare(BCMap map1, BCMap map2) {
        if (map1.equals(map2)) return 0;

        if (map1.getShortName().equals("wwbcnvector")) return -1;

        if (map2.getShortName().equals("wwbcnvector")) return 1;

        Gson gson = new Gson();
        BCLocation location1 = gson.fromJson(map1.getLocation(), BCLocation.class);
        BCLocation location2 = gson.fromJson(map2.getLocation(), BCLocation.class);

        if (TextUtils.isEmpty(state) && TextUtils.isEmpty(country)) {
            return compareWW(getLocationWW(location1), getLocationWW(location2));
        }

        if (!TextUtils.isEmpty(state)) {
            int compareState = compareState(getLocationState(location1), getLocationState(location2));
            if (compareState != 0) return compareState;
        }

        if (!TextUtils.isEmpty(country)) {
            int compareCountry = compareCountry(getLocationCountry(location1), getLocationCountry(location2));
            if (compareCountry != 0) return compareCountry;
        }


        return compareWW(getLocationWW(location1), getLocationWW(location2));
    }

    private String getLocationWW(BCLocation location) {
        return location == null ? "" : location.ww;
    }

    private Object getLocationState(BCLocation location) {
        return location == null ? "" : location.state;
    }

    private Object getLocationCountry(BCLocation location) {
        return location == null ? "" : location.country;
    }


    private int compareWW(String ww1, String ww2) {
        return -ww1.compareTo(ww2);
    }

    private int compareState(Object state1, Object state2) {
        return -compareValue(state1, state2, state);
    }

    private int compareCountry(Object country1, Object country2) {
        return -compareValue(country1, country2, country);
    }

    private int compareValue(Object location1, Object location2, String value) {
        List<String> states1 = null;
        List<String> states2 = null;
        if (location1 instanceof ArrayList) {
            states1 = (List<String>) location1;
        }

        if (location2 instanceof ArrayList) {
            states2 = (List<String>) location2;
        }

        if (isEmpty(states1) && isEmpty(states2))
            return 0;


        if (isEmpty(states1) && states2.contains(value))
            return -1;

        if (isEmpty(states2) && states1.contains(value)) {
            return 1;
        }

        if (!isEmpty(states1) && !isEmpty(states2)) {
            if (states1.contains(value) && !states2.contains(value)) {
                return 1;
            }
            if (!states1.contains(value) && states2.contains(value)) {
                return -1;
            }
        }

        return 0;
    }

    public boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }
}
