package com.crittermap.backcountrynavigator.xe.service.geocoder;


import java.util.List;

public class GeocoderResponse {
    private List<GeocoderResult> results;
    private String status;

    public List<GeocoderResult> getResults() {
        return results;
    }

    public void setResults(List<GeocoderResult> results) {
        this.results = results;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
