package org.datasyslab.shapefilebuild;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import org.geotools.data.DataUtilities;
import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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


    public void buildRandomShapeFile(int shapecount, String path) throws SchemaException, IOException {
        //create descriptor of shpaes
        final SimpleFeatureType TYPE = createFeatureType(Point.class);
        //create feature builder and collection
        List<SimpleFeature> features = new ArrayList<SimpleFeature>();
        GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory(null);
        SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(TYPE);
        //create random point and add to features.
        for(int i = 0;i < shapecount; ++i){
            Point point = generateRandomPoint(geometryFactory);
            System.out.println(point.toText());
            featureBuilder.add(point);
            SimpleFeature feature = featureBuilder.buildFeature(null);
            features.add(feature);
        }
        outputFile(TYPE, features, path);
    }

    public Point generateRandomPoint(GeometryFactory geometryFactory){
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
