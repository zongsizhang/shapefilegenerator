package org.datasyslab.shapefilebuild;

import org.geotools.feature.SchemaException;

import java.io.IOException;
import java.util.Map;

/**
 * Created by zongsizhang on 6/14/17.
 */
public abstract class ShapeGenerator {

    public abstract void buildRandomShapeFile(int shapecount, String path) throws SchemaException, IOException;

    /** configuration map of shapefile builder */
    protected Map<String, Object> generatorConf = null;

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


}
