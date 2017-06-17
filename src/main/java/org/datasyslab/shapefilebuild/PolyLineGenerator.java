package org.datasyslab.shapefilebuild;

import com.vividsolutions.jts.geom.*;

import java.util.HashMap;
import java.util.Random;

/**
 * Created by zongsizhang on 6/14/17.
 */
public class PolyLineGenerator extends ShapeGenerator{

    public PolyLineGenerator(){
        generatorConf = new HashMap<String, Object>(){
            {
                put("max_x", 180);
                put("min_x", -180);
                put("max_y", 90);
                put("min_y", -90);
                put("min_point_num", 10);
                put("max_point_num", 20);
            }
        };
        shapeClass = MultiLineString.class;
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
        int[] partNumBound = new int[]{
                new Integer(generatorConf.get("min_point_num").toString()),
                new Integer(generatorConf.get("max_point_num").toString())
        };
        int partNum = new Random().nextInt(partNumBound[1] - partNumBound[0] + 1) + partNumBound[0];
        LineString[] lineStrings = new LineString[partNum];
        for(int i = 0;i < partNum; ++i){
            int pointNum = new Random().nextInt(pointNumBound[1] - pointNumBound[0] + 1) + pointNumBound[0];
            Coordinate[] coordinates = new Coordinate[pointNum];
            for(int j = 0;j < pointNum; ++j){
                double x = GenerateUtils.generateRangeDouble(xBound[0], xBound[1]);
                double y = GenerateUtils.generateRangeDouble(yBound[0], yBound[1]);
                coordinates[j] = new Coordinate(x,y);
            }
            lineStrings[i] = geometryFactory.createLineString(coordinates);
        }
        return geometryFactory.createMultiLineString(lineStrings);
    }
}
