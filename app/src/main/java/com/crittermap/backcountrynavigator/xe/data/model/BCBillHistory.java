package com.crittermap.backcountrynavigator.xe.data.model;

import com.crittermap.backcountrynavigator.xe.data.BCDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.List;

/**
 * Created by henry on 5/17/2018.
 */

@Table(database = BCDatabase.class, cachingEnabled = true)
public class BCBillHistory extends BaseModel {
    @Column
    @PrimaryKey
    private String id;
    @Column
    private String userId;
    @Column
    private Integer currentBalance;
    private List<BCBillTransaction> billTransactionList;

    public BCBillHistory() {
    }

    public BCBillHistory(String id, String userId, Integer currentBalance, List<BCBillTransaction> billTransactionList) {
        this.id = id;
        this.userId = userId;
        this.currentBalance = currentBalance;
        this.billTransactionList = billTransactionList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(Integer currentBalance) {
        this.currentBalance = currentBalance;
    }

    public List<BCBillTransaction> getBillTransactionList() {
        return billTransactionList;
    }

    public void setBillTransactionList(List<BCBillTransaction> billTransactionList) {
        this.billTransactionList = billTransactionList;
    }
}
