package com.crittermap.backcountrynavigator.xe.controller;

import android.content.Context;
import android.database.MatrixCursor;
import android.provider.BaseColumns;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.widget.SearchView;

import com.crittermap.backcountrynavigator.xe.R;
import com.crittermap.backcountrynavigator.xe.share.Logger;
import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.tasks.geocode.GeocodeParameters;
import com.esri.arcgisruntime.tasks.geocode.GeocodeResult;
import com.esri.arcgisruntime.tasks.geocode.LocatorTask;
import com.esri.arcgisruntime.tasks.geocode.SuggestResult;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by nhat@saveondev on 3/13/2018.
 */

public class BC_SearchWidget {
    private static final String TAG = BC_SearchWidget.class.getSimpleName();
    private final String URI_GEOCODE_SERVER = "http://geocode.arcgis.com/arcgis/rest/services/World/GeocodeServer";
    private final String COLUMN_NAME_ADDRESS = "address";
    private final String[] mColumnNames = {BaseColumns._ID, COLUMN_NAME_ADDRESS};
    private SearchView mAddressSearchView;
    private LocatorTask mLocatorTask;
    private GeocodeParameters mAddressGeocodeParameters;
    private Context mContext;
    private DisplaySearchResultListener mDisplaySearchResultListener;

    public BC_SearchWidget(SearchView addressSearchView, Context context, DisplaySearchResultListener displaySearchResultListener) {
        mAddressSearchView = addressSearchView;
        mContext = context;
        // create a LocatorTask from an online service
        mLocatorTask = new LocatorTask(URI_GEOCODE_SERVER);
        mDisplaySearchResultListener = displaySearchResultListener;
        setupAddressSearchView();
    }

    private void setupAddressSearchView() {

        mAddressGeocodeParameters = new GeocodeParameters();
        // get place name and address attributes
        mAddressGeocodeParameters.getResultAttributeNames().add("PlaceName");
        mAddressGeocodeParameters.setMaxResults(1);
        mAddressSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String address) {
                geoCodeTypedAddress(address);
                // clear focus from search views
                mAddressSearchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!newText.equals("")) {
                    final ListenableFuture<List<SuggestResult>> task = mLocatorTask.suggestAsync(newText);
                    task.addDoneListener(new Runnable() {

                        @Override
                        public void run() {
                            try {
                                // get the results of the async operation
                                List<SuggestResult> suggestResults = task.get();
                                MatrixCursor suggestionsCursor = new MatrixCursor(mColumnNames);
                                int key = 0;
                                // add each address suggestion to a new row
                                for (SuggestResult result : suggestResults) {
                                    suggestionsCursor.addRow(new Object[]{key++, result.getLabel()});
                                }
                                // define SimpleCursorAdapter
                                String[] cols = new String[]{COLUMN_NAME_ADDRESS};
                                int[] to = new int[]{R.id.suggestion_address};
                                final SimpleCursorAdapter suggestionAdapter = new SimpleCursorAdapter(mContext,
                                        R.layout.layout_suggestion, suggestionsCursor, cols, to, 0);
                                mAddressSearchView.setSuggestionsAdapter(suggestionAdapter);
                                // handle an address suggestion being chosen
                                mAddressSearchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
                                    @Override
                                    public boolean onSuggestionSelect(int position) {
                                        return false;
                                    }

                                    @Override
                                    public boolean onSuggestionClick(int position) {
                                        // get the selected row
                                        MatrixCursor selectedRow = (MatrixCursor) suggestionAdapter.getItem(position);
                                        // get the row's index
                                        int selectedCursorIndex = selectedRow.getColumnIndex(COLUMN_NAME_ADDRESS);
                                        // get the string from the row at index
                                        String address = selectedRow.getString(selectedCursorIndex);
                                        // use clicked suggestion as query
                                        mAddressSearchView.setQuery(address, true);
                                        return true;
                                    }
                                });
                            } catch (Exception e) {
                                Logger.e(TAG, e.getMessage());
                            }
                        }
                    });
                }
                return true;
            }
        });
    }

    private void geoCodeTypedAddress(final String address) {
        if (address != null) {
            // Execute async task to find the address
            mLocatorTask.addDoneLoadingListener(new Runnable() {
                @Override
                public void run() {
                    if (mLocatorTask.getLoadStatus() == LoadStatus.LOADED) {
                        // Call geocodeAsync passing in an address
                        final ListenableFuture<List<GeocodeResult>> geocodeResultListenableFuture = mLocatorTask
                                .geocodeAsync(address, mAddressGeocodeParameters);
                        geocodeResultListenableFuture.addDoneListener(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    // Get the results of the async operation
                                    List<GeocodeResult> geocodeResults = geocodeResultListenableFuture.get();
                                    if (geocodeResults.size() > 0) {
                                        mDisplaySearchResultListener.displaySearchResult(geocodeResults.get(0));
                                    } else {
                                        //TODO location not found handling @Trang
                                    }
                                } catch (InterruptedException | ExecutionException e) {
                                    //TODO Exception handling @Trang
                                }
                            }
                        });
                    } else {
                        mLocatorTask.retryLoadAsync();
                    }
                }
            });
            mLocatorTask.loadAsync();
        }
    }

    public interface DisplaySearchResultListener {
        void displaySearchResult(GeocodeResult result);
    }
}
