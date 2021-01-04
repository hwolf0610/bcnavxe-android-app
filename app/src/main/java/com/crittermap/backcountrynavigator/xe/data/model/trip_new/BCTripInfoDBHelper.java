package com.crittermap.backcountrynavigator.xe.data.model.trip_new;

import com.crittermap.backcountrynavigator.xe.data.model.BCDatabaseHelper;
import com.crittermap.backcountrynavigator.xe.share.TripStatus;
import com.raizlabs.android.dbflow.StringUtils;
import com.raizlabs.android.dbflow.sql.language.OrderBy;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class BCTripInfoDBHelper extends BCDatabaseHelper {

    public static Map<String, BCTripInfo> loadAll() {
        List<BCTripInfo> queryList = new Select().from(BCTripInfo.class).queryList();

        return TripUtils.toHashMap(queryList);
    }

    public static List<BCTripInfo> loadAllTrips() {
        return new Select().from(BCTripInfo.class).queryList();
    }


    public static List<BCTripInfo> loadTripsByStatus(TripStatus downloaded) {
        return new Select().from(BCTripInfo.class)
                .where(BCTripInfo_Table.tripStatus.eq(TripStatus.PRISTINE))
                .queryList();
    }

    public static List<BCTripInfo> loadPinnedTrip() {
        return new Select().from(BCTripInfo.class)
                .where(BCTripInfo_Table.isPinned.eq(true))
                .queryList();
    }

    public static List<String> getFolders() {
        List<BCTripInfo> trips = new Select()
                .from(BCTripInfo.class)
                .groupBy(BCTripInfo_Table.trekFolder).queryList();
        List<String> folders = new ArrayList<>();
        for (BCTripInfo tripInfo : trips) {
            String folder = tripInfo.getTrekFolder();
            if (StringUtils.isNotNullOrEmpty(folder)) {
                folders.add(tripInfo.getTrekFolder());
            }
        }
        return folders;
    }

    public static BCTripInfo get(String tripId) {
        return new Select().from(BCTripInfo.class)
                .where(BCTripInfo_Table.id.eq(tripId))
                .querySingle();
    }

    public static Map<String, List<BCTripInfo>> getLocalTrips(String userId) {

        List<BCTripInfo> tripInfos = new Select().from(BCTripInfo.class)
                .where(BCTripInfo_Table.tripStatus.notEq(TripStatus.NOT_DOWNLOADED))
                .and(BCTripInfo_Table.ownerId.eq(userId))
                .queryList();

        Map<String, List<BCTripInfo>> map = new HashMap<>();
        for (BCTripInfo tripInfo : tripInfos) {
            if (!map.containsKey(tripInfo.getTrekFolder())) {
                map.put(tripInfo.getTrekFolder(), new ArrayList<>(Arrays.asList(tripInfo)));
            } else {
                map.get(tripInfo.getTrekFolder()).add(tripInfo);
            }
        }

        return map;
    }

    public static BCTripInfo getLastEditedTrip(String userId) {
        return new Select().from(BCTripInfo.class)
                .where(BCTripInfo_Table.ownerId.eq(userId))
                .orderBy(OrderBy.fromProperty(BCTripInfo_Table.timestamp).descending())
                .querySingle();
    }
}
