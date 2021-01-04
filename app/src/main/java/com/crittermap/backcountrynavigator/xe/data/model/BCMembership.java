package com.crittermap.backcountrynavigator.xe.data.model;

import com.crittermap.backcountrynavigator.xe.data.BCDatabase;
import com.crittermap.backcountrynavigator.xe.service.membership.BCMembershipData;
import com.crittermap.backcountrynavigator.xe.share.BCUtils;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

@Table(database = BCDatabase.class, cachingEnabled = true)
public class BCMembership extends BaseModel {
    @Column
    @PrimaryKey
    private int membershipId;
    @Column
    private String userId;
    @Column
    private String membershipType;
    @Column
    private long validFrom;
    @Column
    private long validTo;
    @Column
    private String membershipStatus;

    public BCMembership() {
    }

    public BCMembership(BCMembershipData response) {
        this.membershipType = response.getMembershipType();
        this.validFrom = BCUtils.parseDate(response.getValidFrom());
        this.validTo = BCUtils.parseDate(response.getValidTo());
        this.membershipStatus = response.getStatus();
    }

    public int getMembershipId() {
        return membershipId;
    }

    public void setMembershipId(int membershipId) {
        this.membershipId = membershipId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMembershipType() {
        return membershipType;
    }

    public void setMembershipType(String membershipType) {
        this.membershipType = membershipType;
    }

    public long getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(long validFrom) {
        this.validFrom = validFrom;
    }

    public long getValidTo() {
        return validTo;
    }

    public void setValidTo(long validTo) {
        this.validTo = validTo;
    }

    public String getMembershipStatus() {
        return membershipStatus;
    }

    public void setMembershipStatus(String membershipStatus) {
        this.membershipStatus = membershipStatus;
    }
}
