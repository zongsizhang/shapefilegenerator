package org.datasyslab.shapefilebuild;

import com.vividsolutions.jts.geom.*;
import org.geotools.feature.SchemaException;
import org.opengis.feature.simple.SimpleFeatureType;

import java.io.IOException;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by zongsizhang on 6/14/17.
 */
public class MultiPointGenerator extends ShapeGenerator{

    public MultiPointGenerator(){
        generatorConf = new HashMap<String, Object>(){
            {
                put("max_x", 180);
                put("min_x", -180);
                put("max_y", 90);
                put("min_y", -90);
                /**  point number of each part */
                put("max_point_num", 10);
                put("min_point_num", 5);
                put("max_part_num", 10);
                put("min_part_num", 3);
            }
        };
        shapeClass = MultiPoint.class;
    }

    protected Geometry generateRandomGeometry(GeometryFactory geometryFactory) {
        double[] xBound = new double[]{
                new Double(generatorConf.get("min_x").toString()),
                new Double(generatorConf.get("max_x").toString())
        };

        double[] yBound = new double[]{
                new Double(generatorConf.get("min_y").toString()),
                new Double(generatorConf.get("max_y").toString())
        };
        int[] pointNumBound = new int[]{
                new Integer(generatorConf.get("min_point_num").toString()),
                new Integer(generatorConf.get("max_point_num").toString())
        };
        int pointNum = new Random().nextInt(pointNumBound[1] - pointNumBound[0] + 1) + pointNumBound[0];
        Point[] points = new Point[pointNum];
        for(int i = 0;i < pointNum; ++i){
            double x = GenerateUtils.generateRangeDouble(xBound[0], xBound[1]);
            double y = GenerateUtils.generateRangeDouble(yBound[0], yBound[1]);
            points[i] = geometryFactory.createPoint(new Coordinate(x,y));
        }
        return geometryFactory.createMultiPoint(points);
    }
}
