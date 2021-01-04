package com.crittermap.backcountrynavigator.xe.service.geocoder;


import java.util.List;

public class ElevationResponse {
    private List<ElevationResult> results;
    private String status;

    public List<ElevationResult> getResults() {
        return results;
    }

    public void setResults(List<ElevationResult> results) {
        this.results = results;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
