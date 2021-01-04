package com.crittermap.backcountrynavigator.xe.data.model;

import com.crittermap.backcountrynavigator.xe.data.BCDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by henry on 5/17/2018.
 */

@Table(database = BCDatabase.class, cachingEnabled = true)
public class BCBillTransaction extends BaseModel {
    @Column
    @PrimaryKey
    private String id;
    @Column
    private String billHistoryId;
    @Column
    private String dateOfTransaction;
    @Column
    private Integer cost;
    @Column
    private String mapId;
    @Column
    private String mapName;

    public BCBillTransaction() {
    }

    public BCBillTransaction(String id, String billHistoryId, String dateOfTransaction, Integer cost, String mapId, String mapName) {
        this.id = id;
        this.billHistoryId = billHistoryId;
        this.dateOfTransaction = dateOfTransaction;
        this.cost = cost;
        this.mapId = mapId;
        this.mapName = mapName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBillHistoryId() {
        return billHistoryId;
    }

    public void setBillHistoryId(String billHistoryId) {
        this.billHistoryId = billHistoryId;
    }

    public String getDateOfTransaction() {
        return dateOfTransaction;
    }

    public void setDateOfTransaction(String dateOfTransaction) {
        this.dateOfTransaction = dateOfTransaction;
    }

    public Integer getCost() {
        return cost;
    }

    public void setCost(Integer cost) {
        this.cost = cost;
    }

    public String getMapId() {
        return mapId;
    }

    public void setMapId(String mapId) {
        this.mapId = mapId;
    }

    public String getMapName() {
        return mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }
}
