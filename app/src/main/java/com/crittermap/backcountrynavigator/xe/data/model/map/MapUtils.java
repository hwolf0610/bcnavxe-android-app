package com.crittermap.backcountrynavigator.xe.data.model.map;

import android.util.Log;

import com.crittermap.backcountrynavigator.xe.controller.layer.BC_CoverageGridLayer;
import com.crittermap.backcountrynavigator.xe.controller.layer.BC_OfflineVectorLoader;
import com.crittermap.backcountrynavigator.xe.controller.layer.BC_ShellLayer;
import com.crittermap.backcountrynavigator.xe.controller.layer.BC_WebTiledLayer;
import com.crittermap.backcountrynavigator.xe.controller.utils.BC_ConverterUtils;
import com.crittermap.backcountrynavigator.xe.controller.utils.BC_Helper;
import com.crittermap.backcountrynavigator.xe.data.model.BCDatabaseHelper;
import com.crittermap.backcountrynavigator.xe.data.model.BCUser;
import com.crittermap.backcountrynavigator.xe.service.map.BCMapLayer;
import com.crittermap.backcountrynavigator.xe.service.map.BCMapSource;
import com.crittermap.backcountrynavigator.xe.share.BCUtils;
import com.crittermap.backcountrynavigator.xe.share.MAP_STATUS;
import com.crittermap.backcountrynavigator.xe.ui.mapSource.fragments.BCSuggestMapFragment;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.ArcGISVectorTiledLayer;
import com.esri.arcgisruntime.layers.ServiceImageTiledLayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.ArrayList;
import java.util.List;

import static com.crittermap.backcountrynavigator.xe.controller.utils.BC_Helper.getScaleByZoomLevel;
import static com.crittermap.backcountrynavigator.xe.share.BCConstant.AVIATION_FILTER;
import static com.crittermap.backcountrynavigator.xe.share.BCConstant.HYBRID_FILTER;
import static com.crittermap.backcountrynavigator.xe.share.BCConstant.MARINE_FILTER;
import static com.crittermap.backcountrynavigator.xe.share.BCConstant.SATELLITE_FILTER;
import static com.crittermap.backcountrynavigator.xe.share.BCConstant.STREET_FILTER;
import static com.crittermap.backcountrynavigator.xe.share.BCConstant.TOPO_FILTER;

/**
 * Created by henry on 4/21/2018.
 */

public class MapUtils {

    public static BCMap map(BCMapSource mapSource) {
        ArrayList<String> userFavoriteMap = new ArrayList<>();
        BCUser user = BCUtils.getCurrentUser();
        if (user != null) {
            userFavoriteMap = user.getFavoriteBasemap();
        }

        Gson gson = new Gson();
        BCMap map = new BCMap();
        Viewpoint viewpoint = new Viewpoint(new Envelope(-20037508.342789244, -20037508.342789244, 20037508.342789244, 20037508.342789244, SpatialReferences.getWebMercator()));
        try {
            viewpoint = new Viewpoint(new Point(Double.parseDouble(mapSource.getLon()), Double.parseDouble(mapSource.getLat()), SpatialReferences.getWgs84()), BC_Helper.getScaleByZoomLevel(Integer.parseInt(mapSource.getZoom())));
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("MapUtils", "MapUtils map mapSource from server:" + e.getMessage());
        }

        map.setId(mapSource.getId());
        map.setMapName(mapSource.getMapName());
        map.setMapTag(mapSource.getMapSubjectValue());
        map.setMapStatus(MAP_STATUS.NOT_DOWNLOAD);
        if (userFavoriteMap.contains(mapSource.getShortname())) {
            map.setFavoriteMap(true);
        } else {
            map.setFavoriteMap(false);
        }
        map.setPinned(false);
        map.setClassType(mapSource.getClasstype());
        map.setBaseUrl(mapSource.getBaseurl());
        map.setCopyRight(mapSource.getCopyright());
        map.setCopyRightUrl(mapSource.getCopyrighturl());
        map.setDesc(mapSource.getDesc());
        map.setImage(mapSource.getImage());
        map.setLat(mapSource.getLat());
        map.setLon(mapSource.getLon());
        map.setZoom(BC_ConverterUtils.tryParseInt(mapSource.getZoom(), 7));
        map.setMinZoom(BC_ConverterUtils.tryParseInt(mapSource.getMinzoom(), 0));
        map.setMaxZoom(BC_ConverterUtils.tryParseInt(mapSource.getMaxzoom(), 19));
        map.setViewPointJson(viewpoint.toJson());
        map.setLocation(gson.toJson(mapSource.getLocation()));
        map.setTileResolverType(mapSource.getTileresolvertype());
        map.setShortName(mapSource.getShortname());
        map.setVisibility(mapSource.getVisibility());
        map.setMapType(mapSource.getMaptype());
        map.setMapLayers(gson.toJson(mapSource.getMaplayer()));
        map.setMembershipType(mapSource.getMembershipType());
        BCMap exist = MapUtils.findMapById(map.getId());
        if (exist != null) {
            map.setPinned(exist.isPinned());
            map.setMapStatus(exist.getMapStatus());
            map.setLastUsedTime(exist.getLastUsedTime());
        }

        BCDatabaseHelper.save(map);
        return map;
    }

    public static List<BCMap> mapAll(List<BCMapSource> mapSources) {
        List<BCMap> maps = new ArrayList<>();
        for (BCMapSource mapSource : mapSources) {
            maps.add(map(mapSource));
        }
        return maps;
    }


    public static boolean isMapLocalExist() {
        return new Select().from(BCMap.class).hasData();
    }

    public static List<BCMap> getAllMaps() {
        return new Select().from(BCMap.class).where(BCMap_Table.visibility.notEq("layer")).queryList();
    }

    public static List<BCMap> getAllDownloadedMaps() {
        return new Select().from(BCMap.class).where(BCMap_Table.mapStatus.eq(MAP_STATUS.DOWNLOADED)).and(BCMap_Table.visibility.notEq("layer")).queryList();
    }

    public static List<BCMap> getFavouriteMaps() {
        return new Select().from(BCMap.class).where(BCMap_Table.isFavoriteMap.eq(true)).and(BCMap_Table.visibility.notEq("layer")).queryList();
    }

    public static List<BCMap> getPinnedMaps() {
        return new Select().from(BCMap.class).where(BCMap_Table.isPinned.eq(true))
                .orderBy(BCMap_Table.lastUsedTime, true).queryList();
    }

    public static BCMap findMapById(String mapId) {
        return new Select().from(BCMap.class).where(BCMap_Table.id.eq(mapId)).querySingle();
    }

    public static List<BCMap> findMapByMapName(String searchQuery, boolean isFavourite, MAP_STATUS map_status) {
        if (searchQuery == null) {
            searchQuery = "";
        }
        if (isFavourite) {
            return new Select().from(BCMap.class).where(BCMap_Table.mapName.like("%" + searchQuery + "%"))
                    .and(BCMap_Table.isFavoriteMap.eq(true)).and(BCMap_Table.visibility.notEq("layer")).queryList();
        } else if (map_status.equals(MAP_STATUS.DOWNLOADED)) {
            return new Select().from(BCMap.class).where(BCMap_Table.mapName.like("%" + searchQuery + "%"))
                    .and(BCMap_Table.mapStatus.eq(map_status)).and(BCMap_Table.visibility.notEq("layer")).queryList();
        } else {
            return new Select().from(BCMap.class).where(BCMap_Table.mapName.like("%" + searchQuery + "%")).and(BCMap_Table.visibility.notEq("layer")).queryList();
        }
    }

    public static List<BCMap> findMapByMapNameAndFilter(String searchQuery, List<BCSuggestMapFragment.ListCheckBox> mListCheckBox) {
        boolean isTopoChecked = false;
        boolean isMarineChecked = false;
        boolean isHyBirdChecked = false;
        boolean isSatelliteChecked = false;
        boolean isStreetChecked = false;
        boolean isAviationChecked = false;
        boolean isAllChecked = false;

        for (BCSuggestMapFragment.ListCheckBox checkbox : mListCheckBox) {
            if (checkbox.getCheckBoxName().equals(TOPO_FILTER)) {
                isTopoChecked = checkbox.isChecked();
            }
            if (checkbox.getCheckBoxName().equals(MARINE_FILTER)) {
                isMarineChecked = checkbox.isChecked();
            }
            if (checkbox.getCheckBoxName().equals(HYBRID_FILTER)) {
                isHyBirdChecked = checkbox.isChecked();
            }
            if (checkbox.getCheckBoxName().equals(SATELLITE_FILTER)) {
                isSatelliteChecked = checkbox.isChecked();
            }
            if (checkbox.getCheckBoxName().equals(STREET_FILTER)) {
                isStreetChecked = checkbox.isChecked();
            }
            if (checkbox.getCheckBoxName().equals(AVIATION_FILTER)) {
                isAviationChecked = checkbox.isChecked();
            }
        }

        if (isTopoChecked &&
                isMarineChecked &&
                isHyBirdChecked &&
                isSatelliteChecked &&
                isStreetChecked &&
                isAviationChecked) {
            isAllChecked = true;
        }

        if (isAllChecked) {
            if (searchQuery != null && searchQuery.length() > 0) {
                return new Select().from(BCMap.class).where(BCMap_Table.mapName.like("%" + searchQuery + "%")).and(BCMap_Table.visibility.notEq("layer")).queryList();
            } else {
                return getAllMaps();
            }
        } else {
            List<BCMap> mapResults;
            if (searchQuery != null && searchQuery.length() > 0) {
                mapResults = new Select().from(BCMap.class).where(BCMap_Table.mapName.like("%" + searchQuery + "%")).and(BCMap_Table.visibility.notEq("layer")).queryList();
            } else {
                mapResults = getAllMaps();
            }
            List<BCMap> filterResults = new ArrayList<>();

            for (BCMap map : mapResults) {
                if (map.getMapTag() == null) {
                    continue;
                }
                String[] mapTags = map.getMapTag().split(",");
                if (mapTags.length > 0) {
                    for (String mapTag : mapTags) {
                        if (isTopoChecked && mapTag.equals(TOPO_FILTER)) {
                            if (!filterResults.contains(map)) {
                                filterResults.add(map);
                            }
                        }
                        if (isMarineChecked && mapTag.equals(MARINE_FILTER)) {
                            if (!filterResults.contains(map)) {
                                filterResults.add(map);
                            }
                        }
                        if (isHyBirdChecked && mapTag.equals(HYBRID_FILTER)) {
                            if (!filterResults.contains(map)) {
                                filterResults.add(map);
                            }
                        }
                        if (isSatelliteChecked && mapTag.equals(SATELLITE_FILTER)) {
                            if (!filterResults.contains(map)) {
                                filterResults.add(map);
                            }
                        }
                        if (isStreetChecked && mapTag.equals(STREET_FILTER)) {
                            if (!filterResults.contains(map)) {
                                filterResults.add(map);
                            }
                        }
                        if (isAviationChecked && mapTag.equals(AVIATION_FILTER)) {
                            if (!filterResults.contains(map)) {
                                filterResults.add(map);
                            }
                        }
                    }

                }
            }
            return filterResults;
        }
    }

    public static BCMap getDefaultOrLastMap() {
        BCMap lastMap = BCUtils.getLastMapPref();
        if (lastMap == null) {
            lastMap = new BCMap();
            lastMap.setMinZoom(0);
            lastMap.setMaxZoom(17);
            lastMap.setTileResolverType("GMT");
            lastMap.setViewPointJson(new Viewpoint(new Point(-95.712891, 37.09024, SpatialReferences.getWgs84()), getScaleByZoomLevel(4)).toJson());
            lastMap.setZoom(4);
            lastMap.setLon("37.09024");
            lastMap.setLat("-95.712891");
            lastMap.setBaseUrl("https://services.arcgisonline.com/ArcGIS/rest/services/World_Topo_Map/MapServer/tile/{l}/{y}/{x}");
            lastMap.setId("5a225c7762cac11c844e4bab");
            lastMap.setPinned(true);
            lastMap.setMapStatus(MAP_STATUS.NOT_DOWNLOAD);
            lastMap.setMapTag("topo,street");
            lastMap.setMapName("World Topographic Map from ArcGIS");
            lastMap.setShortName("arcgiswwtopo");
        } else {
            lastMap.setPinned(true);
        }
        BCUtils.saveLastMapPref(lastMap);
        return lastMap;
    }

    public static void getOfflineMultilayerMap(MapView mMapView, ArcGISMap map, BCMap bcMap, boolean isFirstLoad) {
        Gson gson = new Gson();
        //make sure we arent using offline vector loader
        BC_OfflineVectorLoader.getInstance().clear();
        map.setBasemap(new Basemap());
        List<BCMapLayer> layerList = gson.fromJson(bcMap.getMapLayers(), new TypeToken<List<BCMapLayer>>() {
        }.getType());
        for (BCMapLayer mapLayer : layerList) {
            BCMap subMap = BCMapDBHelper.getByShortName(mapLayer.getLayer());
            if (subMap != null) {
                BC_CoverageGridLayer layer = new BC_CoverageGridLayer(subMap);
                layer.setOpacity(Float.parseFloat(mapLayer.getAlpha()));
                map.getBasemap().getBaseLayers().add(layer);
            }
        }

        if (map.getOperationalLayers().size() > 0) {
            BC_CoverageGridLayer layer = (BC_CoverageGridLayer) map.getOperationalLayers().get(0);
            setViewpoint(mMapView, map, isFirstLoad, layer.getInitialViewpoint());
        }
    }

    public static void getOfflineMap(MapView mMapView, ArcGISMap map, BCMap bcMap, boolean isFirstLoad) {
        BC_CoverageGridLayer layer = new BC_CoverageGridLayer(bcMap);
        //make sure we arent using offline vector loader
        BC_OfflineVectorLoader.getInstance().clear();
        map.setBasemap(new Basemap(layer));
        setViewpoint(mMapView, map, isFirstLoad, layer.getInitialViewpoint());
    }

    public static void getOnlineMultilayerMap(MapView mMapView, ArcGISMap map, BCMap bcMap, boolean isFirstLoad) {
        Gson gson = new Gson();
        //make sure we arent using offline vector loader
        BC_OfflineVectorLoader.getInstance().clear();
        map.getBasemap().getBaseLayers().clear();
        List<BCMapLayer> layerList = gson.fromJson(bcMap.getMapLayers(), new TypeToken<List<BCMapLayer>>() {
        }.getType());
        for (BCMapLayer mapLayer : layerList) {
            BCMap subMap = BCMapDBHelper.getByShortName(mapLayer.getLayer());
            if (subMap != null) {
                ServiceImageTiledLayer onlineMapLayer = BC_WebTiledLayer.create(subMap, mMapView);
                onlineMapLayer.setOpacity(Float.parseFloat(mapLayer.getAlpha()));
                map.getBasemap().getBaseLayers().add(onlineMapLayer);
            }
        }

        Viewpoint viewpoint = Viewpoint.fromJson(bcMap.getViewPointJson());
        setViewpoint(mMapView, map, isFirstLoad, viewpoint);
    }

    public static void getOnlineVectorMap(MapView mMapView, ArcGISMap map, BCMap bcMap, boolean isFirstLoad) {
        Gson gson = new Gson();

        //make sure we arent using offline vector loader
        BC_OfflineVectorLoader.getInstance().clear();
        map.getBasemap().getBaseLayers().clear();
        List<BCMapLayer> layerList = gson.fromJson(bcMap.getMapLayers(), new TypeToken<List<BCMapLayer>>() {
        }.getType());
        if (layerList != null) {
            for (BCMapLayer maplayer : layerList) {
                String url = maplayer.getLayer();
                ArcGISVectorTiledLayer vtlayer = new ArcGISVectorTiledLayer(url);
                map.getBasemap().getBaseLayers().add(vtlayer);
            }
        }

        map.setMinScale(BC_Helper.getScaleByZoomLevel(2));
        //create some kind of default viewpoint
        Viewpoint defaultViewpoint = new Viewpoint(40, -100, BC_Helper.getScaleByZoomLevel(6));
        setViewpoint(mMapView, map, isFirstLoad, defaultViewpoint);

    }

    public static void getOfflineVectorMap(MapView mMapView, ArcGISMap map, BCMap bcMap, boolean isFirstLoad) {
        BC_OfflineVectorLoader.getInstance().clear();
        //get instance of OfflineVectorLoader
        BC_OfflineVectorLoader loader = BC_OfflineVectorLoader.getInstance();
        //set the map source to be used.
        loader.clear();

        map.getBasemap().getBaseLayers().clear();

        Log.v("OfflineVectorMap", "Baselayers count=" + map.getBasemap().getBaseLayers().size() + " after clearing");
        map.getBasemap().getBaseLayers().add(new BC_ShellLayer());
        loader.setMap(bcMap, mMapView.getContext());


        //give the initial viewpoint.
        //create some kind of default viewpoint
        Viewpoint defaultViewpoint = new Viewpoint(40, -100, BC_Helper.getScaleByZoomLevel(6));
        setViewpoint(mMapView, map, isFirstLoad, defaultViewpoint);
        //loader.setViewPoints(mMapView);

    }




    public static void getOnlineMap(MapView mMapView, ArcGISMap map, BCMap bcMap, boolean isFirstLoad) {
        ServiceImageTiledLayer layer = BC_WebTiledLayer.create(bcMap, mMapView);
        map.setBasemap(new Basemap(layer));
        Viewpoint viewpoint = Viewpoint.fromJson(bcMap.getViewPointJson());
        setViewpoint(mMapView, map, isFirstLoad, viewpoint);
    }

    private static void setViewpoint(MapView mMapView, ArcGISMap map, boolean isFirstLoad, Viewpoint defaultViewpoint) {
        if (isFirstLoad ) {
            Envelope envelope = BCUtils.getLastLocation();
            if (envelope != null) {
                map.setInitialViewpoint(new Viewpoint(envelope));
            } else {
                map.setInitialViewpoint(defaultViewpoint);
            }
        }
    }
}
