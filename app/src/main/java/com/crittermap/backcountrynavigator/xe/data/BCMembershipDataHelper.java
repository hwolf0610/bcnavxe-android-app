package com.crittermap.backcountrynavigator.xe.data;

import com.crittermap.backcountrynavigator.xe.data.model.BCDatabaseHelper;
import com.crittermap.backcountrynavigator.xe.data.model.BCMembership;
import com.crittermap.backcountrynavigator.xe.data.model.BCMembership_Table;
import com.raizlabs.android.dbflow.sql.language.Select;

public class BCMembershipDataHelper extends BCDatabaseHelper {
    public static BCMembership findbyUserId(String userId) {
        return new Select()
                .from(BCMembership.class)
                .where(BCMembership_Table.userId.eq(userId))
                .querySingle();
    }
}
