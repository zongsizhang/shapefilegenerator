package org.datasyslab.shapefilebuild;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
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
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.referencing.crs.DefaultGeographicCRS;
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
public abstract class ShapeGenerator {

    protected abstract Geometry generateRandomGeometry(GeometryFactory geometryFactory);

    /** configuration map of shapefile builder */
    protected Map<String, Object> generatorConf = null;

    /** current class of shape */
    protected Class shapeClass;

    public Map<String, Object> getGeneratorConf() {
        return generatorConf;
    }

    public void setGeneratorConf(Map<String, Object> generatorConf) {
        this.generatorConf = generatorConf;
    }

    public void setAttribute(String name, Object value){
        if(generatorConf.containsKey(name)){
            generatorConf.put(name, value);
        }
    }

    public void buildRandomShapeFile(int shapecount, String path) throws SchemaException, IOException {
        //create descriptor of shpaes
        final SimpleFeatureType TYPE = createFeatureType(shapeClass);
        //create feature builder and collection
        List<SimpleFeature> features = new ArrayList<SimpleFeature>();
        GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory(null);
        SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(TYPE);
        //create random point and add to features.
        for(int i = 0;i < shapecount; ++i){
            Geometry geometry = generateRandomGeometry(geometryFactory);
            System.out.println(geometry.toText());
            featureBuilder.add(geometry);
            SimpleFeature feature = featureBuilder.buildFeature(null);
            features.add(feature);
        }
        outputFile(TYPE, features, path);
    }

    protected void outputFile(final SimpleFeatureType TYPE, List<SimpleFeature> features, String path) throws IOException {
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

    protected static SimpleFeatureType createFeatureType(Class geometryClass) {

        SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
        builder.setName("Location");
        builder.setCRS(DefaultGeographicCRS.WGS84); // <- Coordinate reference system

        // add attributes in order
        builder.add("the_geom", geometryClass);
        //builder.length(15).add("Name", String.class); // <- 15 chars width for name field
        //builder.add("number",Integer.class);

        // build the type
        final SimpleFeatureType LOCATION = builder.buildFeatureType();

        return LOCATION;
    }



}
