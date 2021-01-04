package com.crittermap.backcountrynavigator.xe.data.model.map;

import com.crittermap.backcountrynavigator.xe.data.model.BCDatabaseHelper;
import com.raizlabs.android.dbflow.sql.language.Select;

public class BCMapDBHelper extends BCDatabaseHelper {
    public static BCMap getByShortName(String shortName) {
        return new Select().from(BCMap.class)
                .where(BCMap_Table.shortName.eq(shortName))
                .querySingle();
    }

    public static BCMap getById(String id) {
        return new Select().from(BCMap.class)
                .where(BCMap_Table.id.eq(id))
                .querySingle();
    }
}
