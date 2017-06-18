# shapefilegenerator
tools for generating shapefile.

## Introduction
This generator works based on libraries from geotools. Offering utils for generating shapefiles for test.

Currently support function:
generate random Geometries(.shp file)
generate random attributes(.dbf file)

currently support geometry type:
- Point
- MultiPoint
- Polyline
- Polygon

## Tutorial

Generate a random shapefile with number of shapes as shape_num
```java
ShapeGenerator generator = new PolygonGenerator();
        try {
            generator.buildRandomShapeFile(shape_num, "map");
        }catch(Exception e){
            e.printStackTrace();
        }
```
This example creates a shpae file with shape type = Polygon. To create file with other types, Instantiate **generator** with other types.


