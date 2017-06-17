package org.datasyslab.shapefilebuild;

/**
 * Created by zongsizhang on 6/14/17.
 */
public class Main {
    public static void main(String[] args){
        ShapeGenerator generator = new PolygonGenerator();
        try {
            generator.buildRandomShapeFile(10000, "map");
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
