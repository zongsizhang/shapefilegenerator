package org.datasyslab.shapefilebuild;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

import java.util.HashMap;

/**
 * Created by zongsizhang on 6/14/17.
 */
public class PointGenerator extends ShapeGenerator{

    public PointGenerator(){
        generatorConf = new HashMap<String, Object>(){
            {
                put("max_x", 180);
                put("min_x", -180);
                put("max_y", 90);
                put("min_y", -90);
            }
        };
    }

    public Geometry generateRandomGeometry(GeometryFactory geometryFactory){
        double[] xBound = new double[]{
                new Double(generatorConf.get("min_x").toString()),
                new Double(generatorConf.get("max_x").toString())
        };

        double[] yBound = new double[]{
                new Double(generatorConf.get("min_y").toString()),
                new Double(generatorConf.get("max_y").toString())
        };
        double x = GenerateUtils.generateRangeDouble(xBound[0], xBound[1]);
        double y = GenerateUtils.generateRangeDouble(yBound[0], yBound[1]);
        return geometryFactory.createPoint(new Coordinate(x,y));
    }
}
