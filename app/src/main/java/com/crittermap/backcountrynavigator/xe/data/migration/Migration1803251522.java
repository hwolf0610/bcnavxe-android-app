package com.crittermap.backcountrynavigator.xe.data.migration;

import com.crittermap.backcountrynavigator.xe.data.BCDatabase;
import com.crittermap.backcountrynavigator.xe.data.model.map.BCMap;
import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.SQLiteType;
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration;

@Migration(database = BCDatabase.class, version = 1803251522)
public class Migration1803251522 extends AlterTableMigration<BCMap> {

    public Migration1803251522(Class<BCMap> table) {
        super(table);
    }

    @Override
    public void onPreMigrate() {
        super.onPreMigrate();
        addColumn(SQLiteType.TEXT, "mapType");
    }
}
