package org.datasyslab.shapefilebuild;

import org.geotools.swing.data.JFileDataStoreChooser;

import java.io.File;
import java.util.Random;

/**
 * Created by zongsizhang on 6/14/17.
 */
public class GenerateUtils {

    public static String generateGeographicCoordinate(){
        return "";
    }

    public static String generateCartesianCoordinate(){
        return "";
    }

    public static double generateRangeDouble(double lower, double upper){
        double random = new Random().nextDouble();
        return lower + (random * (upper - lower));
    }

    public static File getNewShapeFile(File csvFile) {
        String path = csvFile.getAbsolutePath();
        String newPath = path.substring(0, path.length() - 4) + ".shp";

        JFileDataStoreChooser chooser = new JFileDataStoreChooser("shp");
        chooser.setDialogTitle("Save shapefile");
        chooser.setSelectedFile(new File(newPath));

        int returnVal = chooser.showSaveDialog(null);

        if (returnVal != JFileDataStoreChooser.APPROVE_OPTION) {
            // the user cancelled the dialog
            System.exit(0);
        }

        File newFile = chooser.getSelectedFile();
        if (newFile.equals(csvFile)) {
            System.out.println("Error: cannot replace " + csvFile);
            System.exit(0);
        }

        return newFile;
    }
}
