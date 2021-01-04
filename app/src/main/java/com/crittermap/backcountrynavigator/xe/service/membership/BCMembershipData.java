package com.crittermap.backcountrynavigator.xe.service.membership;


public class BCMembershipData {
    private String membershipType;
    private String validFrom;
    private String validTo;
    private String status;

    public BCMembershipData() {
    }

    public BCMembershipData(String membershipType, String validFrom, String validTo, String status) {
        this.membershipType = membershipType;
        this.validFrom = validFrom;
        this.validTo = validTo;
        this.status = status;
    }

    public String getMembershipType() {
        return membershipType;
    }

    public void setMembershipType(String membershipType) {
        this.membershipType = membershipType;
    }

    public String getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(String validFrom) {
        this.validFrom = validFrom;
    }

    public String getValidTo() {
        return validTo;
    }

    public void setValidTo(String validTo) {
        this.validTo = validTo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
