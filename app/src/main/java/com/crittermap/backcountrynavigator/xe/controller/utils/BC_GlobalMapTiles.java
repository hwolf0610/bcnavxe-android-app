package com.crittermap.backcountrynavigator.xe.controller.utils;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.crittermap.backcountrynavigator.xe.controller.database.BC_DatabaseHelper;
import com.crittermap.backcountrynavigator.xe.controller.database.metadata;
import com.esri.arcgisruntime.arcgisservices.LevelOfDetail;
import com.esri.arcgisruntime.arcgisservices.TileInfo;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nhat@saveondev.com on 12/22/17.
 */

public class BC_GlobalMapTiles {
    private static final int TILESIZE = 256;
    private static final double INITIALRESOLUTION = 2 * Math.PI * 6378137 / TILESIZE;//156543.03392804062;
    private static final double ORIGINSHIFT = 20037508.342789244; //Math.PI * 6378137
    private static final double ORIGIN_SHIFT = Math.PI * 6378137;

    /*
     *
     *  def Resolution(self, zoom ):
        "Resolution (meters/pixel) for given zoom level (measured at Equator)"

        # return (2 * math.pi * 6378137) / (self.tileSize * 2**zoom)
        return self.initialResolution / (2**zoom)
     */
    static public double resolution(int zoom) {
        return INITIALRESOLUTION / Math.pow(2, zoom);
    }


	/*
     *     def TileBounds(self, tx, ty, zoom):
        "Returns bounds of the given tile in EPSG:900913 coordinates"

        minx, miny = self.PixelsToMeters( tx*self.tileSize, ty*self.tileSize, zoom )
        maxx, maxy = self.PixelsToMeters( (tx+1)*self.tileSize, (ty+1)*self.tileSize, zoom )
        return ( minx, miny, maxx, maxy )
	 */

    static CoordinateBoundingBox tileBounds(TileID tid) {

        double minx = pixelToMeters(tid.x * TILESIZE, tid.level);

        double miny = -pixelToMeters((tid.y + 1) * TILESIZE, tid.level);
        double maxx = pixelToMeters((tid.x + 1) * TILESIZE, tid.level);
        double maxy = -pixelToMeters(tid.y * TILESIZE, tid.level);
        return new CoordinateBoundingBox(minx, miny, maxx, maxy);

        //return tileToBBox(tid.level,tid.x,tid.y);
    }

    public static Envelope tileBoundsAsEnvelop(TileID tid) {

        double minx = pixelToMeters(tid.x * TILESIZE, tid.level);
        double miny = -pixelToMeters((tid.y + 1) * TILESIZE, tid.level);
        double maxx = pixelToMeters((tid.x + 1) * TILESIZE, tid.level);
        double maxy = -pixelToMeters(tid.y * TILESIZE, tid.level);
        return new Envelope(minx, miny, maxx, maxy, SpatialReferences.getWebMercator());

        //return tileToBBox(tid.level,tid.x,tid.y);
    }
    /*
	 *     def PixelsToMeters(self, px, py, zoom):
        "Converts pixel coordinates in given zoom level of pyramid to EPSG:900913"

        res = self.Resolution( zoom )
        mx = px * res - self.originShift
        my = py * res - self.originShift
        return mx, my
	 */

    private static double pixelToMeters(int pix, int zoom) {
        double res = resolution(zoom);
        return (res * pix - ORIGINSHIFT);
    }


    public static long[] tileToMeters(int zoom, int px, int py) {
        // "Converts pixel coordinates in given zoom level of pyramid to EPSG:900913"

        //2 * math.pi * 6378137 / self.tileSize
        double res = 2 * Math.PI * 6378137 / (2 << zoom);

        System.out.println(res);

        double mx = px * res - ORIGINSHIFT;
        double mx2 = (px + 1) * res - ORIGINSHIFT;
        double my = py * res - ORIGINSHIFT;
        double my2 = (py + 1) * res - ORIGINSHIFT;

        return new long[]{(long) mx, (long) my, (long) mx2, (long) my2};
    }

    public static CoordinateBoundingBox tileToBBox(int zoom, int px, int py) {
        // "Converts pixel coordinates in given zoom level of pyramid to EPSG:900913"

        //2 * math.pi * 6378137 / self.tileSize
        double res = 2 * Math.PI * 6378137 / (2 << zoom);

        System.out.println(res);

        double mx = px * res - ORIGIN_SHIFT;
        double mx2 = (px + 1) * res - ORIGIN_SHIFT;
        double my = py * res - ORIGIN_SHIFT;
        double my2 = (py + 1) * res - ORIGIN_SHIFT;

        return new CoordinateBoundingBox(mx, my, mx2, my2);
    }

    /*
    def MetersToPixels(self, mx, my, zoom):
		"Converts EPSG:900913 to pyramid pixel coordinates in given zoom level"

		res = self.Resolution( zoom )
		px = (mx + self.originShift) / res
		py = (my + self.originShift) / res
		return px, py
     */
    private static int metersToPixel(double mx, int zoom) {
        double res = resolution(zoom);
        return (int) ((mx + ORIGIN_SHIFT) / res);
    }

    /*
    def PixelsToTile(self, px, py):
		"Returns a tile covering region in given pixel coordinates"

		tx = int( math.ceil( px / float(self.tileSize) ) - 1 )
		ty = int( math.ceil( py / float(self.tileSize) ) - 1 )
		return tx, ty
     */
    private static TileID pixelsToTile(int px, int py, int zoom) {
        int tx = (int) (Math.ceil(px / TILESIZE));
        int ty = (int) (Math.ceil(py / TILESIZE));
        return new TileID(zoom, tx, ty);
    }

    /*
        def MetersToTile(self, mx, my, zoom):
		"Returns tile for given mercator coordinates"

		px, py = self.MetersToPixels( mx, my, zoom)
		return self.PixelsToTile( px, py)
     */
    public static TileID metersToTmsTile(double mx, double my, int zoom) {
        int px = metersToPixel(mx, zoom);
        int py = metersToPixel(my, zoom);
        return pixelsToTile(px, py, zoom);
    }

    /*
    def LatLonToMeters(self, lat, lon ):
		"Converts given lat/lon in WGS84 Datum to XY in Spherical Mercator EPSG:900913"

		mx = lon * self.originShift / 180.0
		my = math.log( math.tan((90 + lat) * math.pi / 360.0 )) / (math.pi / 180.0)

		my = my * self.originShift / 180.0
		return mx, my
     */
    public static Point wgs84ToWebMercator(double lat, double lon) {
        double mx = lon * ORIGIN_SHIFT / 180.0;
        double my = Math.log(Math.tan((90 + lat) * Math.PI / 360.0)) / (Math.PI / 180.0);

        my = my * ORIGIN_SHIFT / 180.0;
        return new Point(mx, my, SpatialReferences.getWebMercator());
    }

    public static TileID metersToTmsTile(Point point, int zoomLevel) {
        return metersToTmsTile(point.getX(), point.getY(), zoomLevel);
    }

    public static TileInfo CreateTileInfo(String databasePath, String dbName, metadata _metadata) {
        BC_DatabaseHelper databaseHelper = BC_DatabaseHelper.createInstance(databasePath, dbName, _metadata);
        SQLiteDatabase database = databaseHelper.getSqLiteDatabase();
        int minZoom = 0;
        int maxZoom = 0;
        Cursor minZoomC = database.rawQuery("SELECT value FROM metadata WHERE name = 'minzoom'", null);

        if (minZoomC.moveToFirst()) {
            minZoom = Integer.parseInt(minZoomC.getString(0));
        }
        Cursor maxZoomC = database.rawQuery("SELECT value FROM metadata WHERE name = 'maxzoom'", null);
        if (maxZoomC.moveToFirst()) {
            maxZoom = Integer.parseInt(maxZoomC.getString(0));
        }
        int dpi = 96;
        ArrayList<LevelOfDetail> levels = new ArrayList<>();
        for (int i = minZoom; i <= maxZoom; i++) {
            double resolution = BC_GlobalMapTiles.resolution(i);
            double scale = resolution * dpi * 39.37;
            LevelOfDetail l = new LevelOfDetail(i, resolution, scale);
            levels.add(l);
        }

        database.close();
        minZoomC.close();
        maxZoomC.close();

        Point origin = new Point(-20037508.342789244, 20037508.342789244, SpatialReferences.getWebMercator());
        return new TileInfo(dpi, TileInfo.ImageFormat.PNG, levels, origin, SpatialReferences.getWebMercator(), 256, 256);
    }

    public static TileInfo createTileInfo() {
        int dpi = 96;
        ArrayList<LevelOfDetail> levels = new ArrayList<>();
        for (int i = 1; i <= 22; i++) {
            double resolution = BC_GlobalMapTiles.resolution(i);
            double scale = resolution * dpi * 39.37;
            LevelOfDetail l = new LevelOfDetail(i, resolution, scale);
            levels.add(l);
        }

        Point origin = new Point(-20037508.342789244, 20037508.342789244, SpatialReferences.getWebMercator());
        return new TileInfo(dpi, TileInfo.ImageFormat.PNG, levels, origin, SpatialReferences.getWebMercator(), 256, 256);
    }

    public static Envelope getWorldWebExtent() {
        return new Envelope(-20037508.342789244, -20037508.342789244, 20037508.342789244, 20037508.342789244, SpatialReferences.getWebMercator());
    }


    public static TileID pointToGoogleTileID(Point center, int zoom) {
        //make sure the point is in Mercator
        Point cp = (Point) GeometryEngine.project(center, SpatialReferences.getWebMercator());
        TileID tmstileid = metersToTmsTile(cp, zoom);
        Log.i("BC_GlobalMapTiles", "center = " + center.toJson() + "cp = " + cp.toJson() + " tmstileid=" + tmstileid.getTileIdString());
        return tmsToGoole(tmstileid);

    }

    public static TileID tmsToGoole(TileID tileID) {
        int level = tileID.getLevel();
        int col = tileID.getX();
        int row = tileID.getY();
        // need to flip origin
        int nRows = (1 << level); // Num rows = 2^level
        int tmsRow = nRows - 1 - row;
        return new TileID(level, col, tmsRow);
    }

    public static TileIDForService tmsToGoole(TileIDForService tileID) {
        int level = tileID.getLevel();
        int col = tileID.getX();
        int row = tileID.getY();
        // need to flip origin
        int nRows = (1 << level); // Num rows = 2^level
        int tmsRow = nRows - 1 - row;
        return new TileIDForService(level, col, tmsRow);
    }

    public static List<Envelope> getTmsTilesByZoom(Envelope gridEnvelope, int zoomLevel) {
        ArrayList<Envelope> r = new ArrayList<>();
        gridEnvelope = (Envelope) GeometryEngine.project(gridEnvelope, SpatialReferences.getWebMercator());
        Point upperLeft = new Point(gridEnvelope.getXMin(), gridEnvelope.getYMax(), gridEnvelope.getSpatialReference());
        Point bottomLeft = new Point(gridEnvelope.getXMin(), gridEnvelope.getYMin(), gridEnvelope.getSpatialReference());
        Point bottomRight = new Point(gridEnvelope.getXMax(), gridEnvelope.getYMin(), gridEnvelope.getSpatialReference());
        TileID upperLeftTile = metersToTmsTile(upperLeft, zoomLevel);
        TileID bottomLeftTile = metersToTmsTile(bottomLeft, zoomLevel);
        TileID bottomRightTile = metersToTmsTile(bottomRight, zoomLevel);

        for (int row = bottomLeftTile.getY(); row <= upperLeftTile.getY(); row++) {
            for (int col = bottomLeftTile.getX(); col <= bottomRightTile.getX(); col++) {
                TileID tileID = new TileID(zoomLevel, col, row);
                if (BC_Helper.isTileValid(tileID)) {
                    TileID gTileID = tmsToGoole(tileID);
                    Envelope envelope = tileBoundsAsEnvelop(gTileID);
                    r.add(envelope);
                }
            }
        }
        return r;
    }

    public static List<Envelope> getGoogleTilesByZoom(Envelope gridEnvelope, int zoomLevel) {
        return getGoogleTilesByZoom(gridEnvelope, zoomLevel, 0);
    }

    public static List<Envelope> getGoogleTilesByZoom(Envelope gridEnvelope, int zoomLevel, int buff) {
        ArrayList<Envelope> r = new ArrayList<>();
        gridEnvelope = (Envelope) GeometryEngine.project(gridEnvelope, SpatialReferences.getWebMercator());
        Point upperLeft = new Point(gridEnvelope.getXMin(), gridEnvelope.getYMax(), gridEnvelope.getSpatialReference());
        Point bottomLeft = new Point(gridEnvelope.getXMin(), gridEnvelope.getYMin(), gridEnvelope.getSpatialReference());
        Point bottomRight = new Point(gridEnvelope.getXMax(), gridEnvelope.getYMin(), gridEnvelope.getSpatialReference());
        TileID upperLeftTile = tmsToGoole(metersToTmsTile(upperLeft, zoomLevel));
        TileID bottomLeftTile = tmsToGoole(metersToTmsTile(bottomLeft, zoomLevel));
        TileID bottomRightTile = tmsToGoole(metersToTmsTile(bottomRight, zoomLevel));

        for (int row = upperLeftTile.getY() - buff; row <= bottomLeftTile.getY() + buff; row++) {
            for (int col = bottomLeftTile.getX() - buff; col <= bottomRightTile.getX() + buff; col++) {
                TileID tileID = new TileID(zoomLevel, col, row);
                if (BC_Helper.isTileValid(tileID)) {
                    Envelope envelope = tileBoundsAsEnvelop(tileID);
                    r.add(envelope);
                }
            }
        }
        return r;
    }

    public static List<Envelope> getGoogleTilesToDrawGridByZoom(Envelope gridEnvelope, int zoomLevel, int buff) {
        ArrayList<Envelope> r = new ArrayList<>();
        gridEnvelope = (Envelope) GeometryEngine.project(gridEnvelope, SpatialReferences.getWebMercator());
        Point upperLeft = new Point(gridEnvelope.getXMin(), gridEnvelope.getYMax(), gridEnvelope.getSpatialReference());
        Point bottomLeft = new Point(gridEnvelope.getXMin(), gridEnvelope.getYMin(), gridEnvelope.getSpatialReference());
        Point bottomRight = new Point(gridEnvelope.getXMax(), gridEnvelope.getYMin(), gridEnvelope.getSpatialReference());
        TileID upperLeftTile = tmsToGoole(metersToTmsTile(upperLeft, zoomLevel));
        TileID bottomLeftTile = tmsToGoole(metersToTmsTile(bottomLeft, zoomLevel));
        TileID bottomRightTile = tmsToGoole(metersToTmsTile(bottomRight, zoomLevel));

        for (int row = upperLeftTile.getY() - buff; row <= bottomLeftTile.getY() + buff; row++) {
            Envelope rowTile = null;
            for (int col = bottomLeftTile.getX() - buff; col <= bottomRightTile.getX() + buff; col++) {
                TileID tileID = new TileID(zoomLevel, col, row);
                if (BC_Helper.isTileValid(tileID)) {
                    Envelope envelope = tileBoundsAsEnvelop(tileID);
                    if (rowTile == null) {
                        rowTile = envelope;
                    } else {
                        rowTile = GeometryEngine.combineExtents(rowTile, envelope);
                    }
                }
            }
            r.add(rowTile);
        }

        for (int col = bottomLeftTile.getX() - buff; col <= bottomRightTile.getX() + buff; col++) {
            Envelope colTile = null;
            for (int row = upperLeftTile.getY() - buff; row <= bottomLeftTile.getY() + buff; row++) {
                TileID tileID = new TileID(zoomLevel, col, row);
                if (BC_Helper.isTileValid(tileID)) {
                    Envelope envelope = tileBoundsAsEnvelop(tileID);
                    if (colTile == null) {
                        colTile = envelope;
                    } else {
                        colTile = GeometryEngine.combineExtents(colTile, envelope);
                    }
                }
            }
            r.add(colTile);
        }
        return r;
    }

    public static List<TileID> getGoogleTilesByZoomAndTile(TileID tile, int zoomLevel) {
        ArrayList<TileID> r = new ArrayList<>();
        if (zoomLevel < tile.level) {
            r.add(new TileID(zoomLevel, tile.x / 2, tile.y / 2));
            return r;
        } else if (zoomLevel == tile.level) {
            r.add(new TileID(tile.level, tile.x, tile.y));
        } else {
            int range = zoomLevel - tile.level;
            int minX = (int) (tile.x * Math.pow(2, range));
            int maxX = (int) (minX + Math.pow(2, range) - 1);
            int minY = (int) (tile.y * Math.pow(2, range));
            int maxY = (int) (minY + Math.pow(2, range) - 1);
            for (int row = minY; row <= maxY; row++) {
                for (int col = minX; col <= maxX; col++) {
                    TileID tileID = new TileID(zoomLevel, col, row);
                    if (BC_Helper.isTileValid(tileID)) {
                        r.add(tileID);
                    }
                }
            }
        }


        return r;
    }

    public static ArrayList<TileID> getGoogleTilesToDownload(TileID googleTile, int minZoom, int maxZoom) {
        ArrayList<TileID> list = new ArrayList<>();
        for (int i = minZoom; i <= maxZoom; i++) {
            list.addAll(getGoogleTilesByZoomAndTile(googleTile, i));
        }
        return list;
    }
}
