package Json;

import Structures.TabRowDataContainersBrief;
import Structures.TabRowDataImages;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Vector;

public class JsonUtils {

    /*-------------------------------------------------------------------------

    ------------------------------------------------------------------------ */
    public static Vector<TabRowDataImages> GetImageList(String jsonInput) {
        Vector<TabRowDataImages> data = new Vector<>();

        try {
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(jsonInput);
            JSONArray jsa = (JSONArray) obj;
            Iterator iter = jsa.iterator();
            while (iter.hasNext()) {
                JSONObject obj1 = (JSONObject) iter.next();  // image
                String sId = ((String) obj1.get("Id")).substring(7, 19);
                long ut = (long) obj1.get("Created");
                String sDate = ConvertTime1(ut);
                long size = (long) obj1.get("Size");
                String sSize = String.format("%d MB", size / (1024 * 1024));
                JSONArray tags = (JSONArray) obj1.get("RepoTags");
                Iterator iter_tagiter = tags.iterator();
                String spom = (String) iter_tagiter.next();
                data.add(new TabRowDataImages(sId, spom, sSize, sDate));
            }
            return (data);
        } catch (Exception ex) {
            ex.printStackTrace();
            return (null);
        }
    }
    /*-------------------------------------------------------------------------

    ------------------------------------------------------------------------ */
    public static Vector<TabRowDataContainersBrief> GetContainerList(String jsonInput) {
        Vector<TabRowDataContainersBrief> data = new Vector<>();

        try {
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(jsonInput);
            JSONArray jsa = (JSONArray) obj;
            Iterator iter = jsa.iterator();
            while (iter.hasNext()) {
                JSONObject obj1 = (JSONObject) iter.next();  // CONTAINER
                String sId = ((String) obj1.get("Id")).substring(0, 12);
                String sImage = (String) obj1.get("Image");
                String sStatus = (String) obj1.get("Status");

                JSONArray names = (JSONArray) obj1.get("Names");
                Iterator iter_tagiter = names.iterator();
                // names begin with '/' char

                JSONObject netSet = (JSONObject) obj1.get("NetworkSettings");
                String sIpAddr = "";
                JSONObject netWorks = (JSONObject) netSet.get("Networks");
                for(Object ob : netWorks.values()) {
                    JSONObject jso = (JSONObject) ob;
                    if(jso.containsKey("IPAddress")) {
                        sIpAddr = (String) jso.get("IPAddress");
                    }
                    int n = 0;
                }
                String spom = ((String) iter_tagiter.next()).substring(1);
                data.add(new TabRowDataContainersBrief(sId, sImage, sStatus, spom, sIpAddr));
            }
            return (data);
        } catch (Exception ex) {
            ex.printStackTrace();
            return (null);
        }
    }


    /*-------------------------------------------------------------------------

    ------------------------------------------------------------------------ */
    public static String ConvertTime1(long unixTime) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(1000 * unixTime);
        String s = String.format("%04d-%02d-%02d %02d:%02d:%02d",
                c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH),
                c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), c.get(Calendar.SECOND));
        return (s);
    }

    /*-------------------------------------------------------------------------

    ------------------------------------------------------------------------ */
    public static Vector<String> GetReistryRepoList(String jsonInput) {
        Vector<String> data = new Vector<>();

        try {
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(jsonInput);
            JSONObject jso = (JSONObject)obj;
            JSONArray jsa = (JSONArray) jso.get("repositories");
            Iterator iter = jsa.iterator();
            while (iter.hasNext()) {
                String repo = (String) iter.next();
                data.add(repo);
            }
            return (data);
        } catch (Exception ex) {
            ex.printStackTrace();
            return (null);
        }
    }

    /*-------------------------------------------------------------------------
        JSON response:
        {
           "name": "rtl-can",
            "tags": [
                "1.0.0",
                "latest"
            ]
        }
    ------------------------------------------------------------------------ */
    public static Vector<String> GetImagesForRepo(String jsonInput) {
        Vector<String> images = new Vector<>();

        try {
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(jsonInput);
            JSONObject jso = (JSONObject)obj;
            JSONArray jsa = (JSONArray) jso.get("tags");
            Iterator iter = jsa.iterator();
            while (iter.hasNext()) {
                String repo = (String) iter.next();
                images.add(repo);
            }
            return (images);
        } catch (Exception ex) {
            ex.printStackTrace();
            return (null);
        }
    }
}
