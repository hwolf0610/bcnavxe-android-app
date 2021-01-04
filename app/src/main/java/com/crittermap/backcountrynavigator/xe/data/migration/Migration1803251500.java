package com.crittermap.backcountrynavigator.xe.data.migration;

import com.crittermap.backcountrynavigator.xe.data.BCDatabase;
import com.crittermap.backcountrynavigator.xe.service.trip.BCTripResponse;
import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.SQLiteType;
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration;

/**
 * Created by henry on 3/25/2018.
 */

@Migration(database = BCDatabase.class, version = 1803251500)
public class Migration1803251500 extends AlterTableMigration<BCTripResponse> {

    public Migration1803251500(Class<BCTripResponse> table) {
        super(table);
    }

    @Override
    public void onPreMigrate() {
        super.onPreMigrate();
        addColumn(SQLiteType.TEXT, "status");
    }
}
