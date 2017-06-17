package org.datasyslab.shapefilebuild;

import com.vividsolutions.jts.geom.*;

import java.util.HashMap;

/**
 * Created by zongsizhang on 6/14/17.
 */
public class PolygonGenerator extends ShapeGenerator{

    public PolygonGenerator(){
        generatorConf = new HashMap<String, Object>(){
            {
                put("max_x", 180);
                put("min_x", -180);
                put("max_y", 90);
                put("min_y", -90);
                /** range of value z */
                put("min_z", 1);
                put("max_z", 20);
                /** point num range of each part */
                put("min_point_num", 5);
                put("max_point_num", 15);
                /** part num range */
                put("min_polygon_num", 1);
                put("max_polygon_num", 4);
                /** range of holes of each ring */
                put("min_hole_num", 0);
                put("max_hole_num", 3);
                /** range of holes of each ring */
                put("min_radius", 0.0001);
                put("max_radius", 1);
            }
        };
        shapeClass = MultiPolygon.class;
    }

    /**
     * generate a MultiPolygon with random number of random polygons
     * @param geometryFactory
     * @return
     */
    protected Geometry generateRandomGeometry(GeometryFactory geometryFactory) {
        int[] polygonNumBound = new int[]{
                new Integer(generatorConf.get("min_polygon_num").toString()),
                new Integer(generatorConf.get("max_polygon_num").toString())
        };
        int polygonNum = GenerateUtils.generateRangeInt(polygonNumBound[0], polygonNumBound[1]);
        Polygon[] polygons = new Polygon[polygonNum];
        for(int i = 0;i < polygonNum; ++i){
            polygons[i] = generateRandomPolygon(geometryFactory, 2);
        }
        return geometryFactory.createMultiPolygon(polygons);
    }

    /**
     * create a polygon with random number of random holes
     * @param geometryFactory
     * @param dimension
     * @return
     */
    protected Polygon generateRandomPolygon(GeometryFactory geometryFactory, int dimension){
        // generate hole number
        int[] holeNumBound = new int[]{
                new Integer(generatorConf.get("min_hole_num").toString()),
                new Integer(generatorConf.get("max_hole_num").toString())
        };
        int holeNum = GenerateUtils.generateRangeInt(holeNumBound[0], holeNumBound[1]);
        /*
         * generate outRing
        */
        double[] xBound = new double[]{
                new Double(generatorConf.get("min_x").toString()),
                new Double(generatorConf.get("max_x").toString())
        };
        double[] yBound = new double[]{
                new Double(generatorConf.get("min_y").toString()),
                new Double(generatorConf.get("max_y").toString())
        };
        double[] rBound = new double[]{
                new Double(generatorConf.get("min_radius").toString()),
                new Double(generatorConf.get("max_radius").toString())
        };
        int[] pointNumBound = new int[]{
                new Integer(generatorConf.get("min_point_num").toString()),
                new Integer(generatorConf.get("max_point_num").toString())
        };
        // generate random radius
        double outRingRadius = GenerateUtils.generateRangeDouble(rBound[0], rBound[1]);
        int pointNum = GenerateUtils.generateRangeInt(pointNumBound[0], pointNumBound[1]);
        // make a random center that is valid
        double centerX = GenerateUtils.generateRangeDouble(xBound[0]+outRingRadius, xBound[1]-outRingRadius);
        double centerY = GenerateUtils.generateRangeDouble(yBound[0]+outRingRadius, yBound[1]-outRingRadius);
        Coordinate center = new Coordinate(centerX, centerY);
        LinearRing shell = generateRandomRegularRing(center, pointNum, dimension, outRingRadius, false, geometryFactory);
        /*
        generate holes
         */
        // first calculate radius of inner circle
        double innerAngle = Math.PI / pointNum;
        double innerRadius = outRingRadius * Math.cos(innerAngle);
        LinearRing[] holes = new LinearRing[holeNum];
        System.out.println("==" + outRingRadius);
        for(int i = 0;i < holeNum; ++i){
            // create a random radius
            double holeRadius = GenerateUtils.generateRangeDouble(rBound[0], innerRadius);
            double reversRadius = (innerRadius - holeRadius) * 0.9; // distance between hole center to ring center
            // create random angle
            double detachAngle = GenerateUtils.generateRangeDouble(-2 * Math.PI, 2 * Math.PI);
            Coordinate holeCenter= new Coordinate(
                    Math.cos(detachAngle) * reversRadius + centerX,
                     Math.sin(detachAngle * reversRadius + centerY));
            holeCenter.x = centerX;
            holeCenter.y = centerY;
            //prepare number of point in hole
            int holePointNum = GenerateUtils.generateRangeInt(pointNumBound[0], pointNumBound[1]);
            holes[i] = generateRandomRegularRing(holeCenter, holePointNum, 2, holeRadius, true, geometryFactory);
        }
        return geometryFactory.createPolygon(shell, holes);
    }

    /**
     * generate a ring of regular polygon of random vertexes
     * @param center
     * @param pointNum
     * @param dimension
     * @param radius
     * @param isCCW
     * @param geometryFactory
     * @return
     */
    protected LinearRing generateRandomRegularRing(Coordinate center, int pointNum, int dimension, double radius, boolean isCCW ,final GeometryFactory geometryFactory){
        // create new sequence, end point counted
        CoordinateSequence coordinateSequence = geometryFactory.getCoordinateSequenceFactory().create(pointNum + 1, dimension);
        // prepare factors
        double innerAngle = 2 * Math.PI / pointNum;
        // generate 2d ordinates, start with points at (radius + centerx, 0 + centery);
        for(int i = 0;i < pointNum; i++){
            double curAngle = innerAngle * (isCCW?i:(0-i));
            double x = radius * Math.cos(curAngle) + center.x;
            double y = radius * Math.sin(curAngle) + center.y;
            coordinateSequence.setOrdinate(i, 0, x);
            coordinateSequence.setOrdinate(i, 1, y);
        }
        // if dimension is 3, assign z value
        if(dimension == 3){
            double[] zBound = new double[]{
                    new Double(generatorConf.get("min_z").toString()),
                    new Double(generatorConf.get("max_z").toString())
            };
            for(int i = 0;i < pointNum; ++i){
                double zValue = GenerateUtils.generateRangeDouble(zBound[0], zBound[1]);
                coordinateSequence.setOrdinate(i, 2, zValue);
            }
        }
        // copy start point to end point
        coordinateSequence.setOrdinate(pointNum, 0, coordinateSequence.getOrdinate(0, 0));
        coordinateSequence.setOrdinate(pointNum, 1, coordinateSequence.getOrdinate(0, 1));
        coordinateSequence.setOrdinate(pointNum, 2, coordinateSequence.getOrdinate(0, 2));

        return geometryFactory.createLinearRing(coordinateSequence);

    }
}
