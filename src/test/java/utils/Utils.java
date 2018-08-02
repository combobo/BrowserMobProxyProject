package utils;

import net.lightbody.bmp.core.har.Har;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Utils {
    public static void writeHarToFile(Har har){
        try {
            File file = new File("target\\Test.har");
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            try {
                har.writeTo(fileOutputStream);
            }
            catch (IOException e) {
                System.out.println(e.getMessage());
            }
            finally {
                fileOutputStream.close();
            }
        }
        catch (IOException e){
            System.out.println(e.getMessage());
        }
    }
}
