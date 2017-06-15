package org.datasyslab.shapefilebuild;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import org.geotools.data.DataUtilities;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.Transaction;
import org.geotools.data.collection.ListFeatureCollection;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        final SimpleFeatureType TYPE = DataUtilities.createType("Location",
                "location:Point:srid=4326"
        );
        //create feature builder and collection
        List<SimpleFeature> features = new ArrayList<SimpleFeature>();
        GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory(null);
        SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(TYPE);
        //create random point and add to features.
        for(int i = 0;i < shapecount; ++i){
            featureBuilder.add(generateRandomPoint(geometryFactory));
            SimpleFeature feature = featureBuilder.buildFeature(null);
            features.add(feature);
        }

        //prepare shapefile
        File shapefile = GenerateUtils.getNewShapeFile(new File(path));
        ShapefileDataStoreFactory dataStoreFactory = new ShapefileDataStoreFactory();
        Map<String, Serializable> params = new HashMap<String, Serializable>();
        params.put("url", shapefile.toURI().toURL()); // this throw MalformedURLException
        params.put("create spatial index", Boolean.TRUE);
        ShapefileDataStore newDataStore = (ShapefileDataStore) dataStoreFactory.createNewDataStore(params);// throw IOException
        newDataStore.createSchema(TYPE);

        //write features to shapefile
        Transaction transaction = new DefaultTransaction("create");

        String typeName = newDataStore.getTypeNames()[0];
        SimpleFeatureSource featureSource = newDataStore.getFeatureSource(typeName);
        SimpleFeatureType SHAPE_TYPE = featureSource.getSchema();

        System.out.println("SHAPE:"+SHAPE_TYPE);

        if (featureSource instanceof SimpleFeatureStore) {
            SimpleFeatureStore featureStore = (SimpleFeatureStore) featureSource;
            /*
             * SimpleFeatureStore has a method to add features from a
             * SimpleFeatureCollection object, so we use the ListFeatureCollection
             * class to wrap our list of features.
             */
            SimpleFeatureCollection collection = new ListFeatureCollection(TYPE, features);
            featureStore.setTransaction(transaction);
            try {
                featureStore.addFeatures(collection);
                transaction.commit();
            } catch (Exception problem) {
                problem.printStackTrace();
                transaction.rollback();
            } finally {
                transaction.close();
            }
            System.exit(0); // success!
        } else {
            System.out.println(typeName + " does not support read/write access");
            System.exit(1);
        }
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
