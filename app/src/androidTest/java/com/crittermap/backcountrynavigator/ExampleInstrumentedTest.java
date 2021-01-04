package com.crittermap.backcountrynavigator;

import android.content.Context;
import android.graphics.Color;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.PointCollection;
import com.esri.arcgisruntime.geometry.Polyline;
import com.esri.arcgisruntime.geometry.SpatialReferences;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class) public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

//        assertEquals("com.crittermap.backcountrynavigator", appContext.getPackageName());

        Point point1 = new Point(0.1, 0.2, SpatialReferences.getWgs84());
        Point point2 = new Point(0.2, 0.3, SpatialReferences.getWgs84());
        Point point3 = new Point(0.3, 0.4, SpatialReferences.getWgs84());
        Point point4 = new Point(0.4, 0.5, SpatialReferences.getWgs84());
        Point point5 = new Point(0.5, 0.6, SpatialReferences.getWgs84());
        Point point6 = new Point(0.6, 0.7, SpatialReferences.getWgs84());
        Point point7 = new Point(0.7, 0.8, SpatialReferences.getWgs84());

        Point point8 = new Point(0.9, 1.0, SpatialReferences.getWgs84());
        Point point9 = new Point(1.0, 1.1, SpatialReferences.getWgs84());
        Point point10 = new Point(1.1, 1.2, SpatialReferences.getWgs84());
        Point point11 = new Point(1.2, 1.3, SpatialReferences.getWgs84());
        Point point12 = new Point(1.3, 1.4, SpatialReferences.getWgs84());
        PointCollection points = new PointCollection(SpatialReferences.getWgs84());
        points.add(point1);
        points.add(point2);
        Polyline polyline = new Polyline(points);
        polyline.getParts().getPartsAsPoints().iterator();
        Polyline polyline2 = new Polyline(points);
    }


    @Test
    public void testGeometry() {
        Point a = Point.createWithM(1, 2, 3, 4, SpatialReferences.getWgs84());
        Point b = Point.createWithM(5, 6, 7, 8, SpatialReferences.getWgs84());

        System.out.println(a.toJson());
        System.out.println(b.toJson());

        PointCollection points = new PointCollection(SpatialReferences.getWgs84());
        points.add(a);
        points.add(b);
        Polyline polyline = new Polyline(points);

        int color = Color.argb(1, 0, 0, 255);
        System.out.print(color);
        System.out.println(polyline.toJson());
    }
}
