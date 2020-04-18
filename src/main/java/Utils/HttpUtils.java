package Utils;

import Structures.HttpResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;



//#################################################################################
public class HttpUtils {

    private static final Logger log = LoggerFactory.getLogger(HttpUtils.class);

    /*----------------------------------------------------------------------

    ------------------------------------------------------------------------*/
    public static HttpResult SendHttpGet(String sUrl) {
        BufferedReader in;
        String resString = "";

        try {
            URL url = new URL(sUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setConnectTimeout(5000);
            con.setRequestMethod("GET");


            int status = con.getResponseCode();
            if (status == 200) {
                in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuilder content = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                resString = content.toString();
                in.close();
            }
            con.disconnect();
            return (new HttpResult(status, resString, ""));

        } catch (Exception ex) {
            ex.printStackTrace();
            return (new HttpResult(-1, ex.getMessage(), ""));
        }

    }


    /*----------------------------------------------------------------------

    ------------------------------------------------------------------------*/
    public static HttpResult SendHttpDelete(String sUrl) {
        BufferedReader in;
        String resString = "";

        log.info("SendHttpDelete: URL=" + sUrl);
        try {
            URL url = new URL(sUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setConnectTimeout(5000);
            con.setRequestMethod("DELETE");
            int status = con.getResponseCode();


            /*
            StringBuilder content = new StringBuilder();
            if(con.getInputStream() != null) {
                in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();
            }
            if(con.getErrorStream() != null) {
                in = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();
            }
            resString = content.toString();
            */


            resString = con.getResponseMessage();
            con.disconnect();
            return (new HttpResult(status, resString, ""));

        } catch (Exception ex) {
            ex.printStackTrace();
            return (new HttpResult(-1, ex.getMessage(), ""));
        }

    }

    /*----------------------------------------------------------------------

    ------------------------------------------------------------------------*/
    public static HttpResult SendHttpPost(String sUrl) {
        BufferedReader in;
        String resString = "";

        log.info("SendHttpPost: URL=" + sUrl);
        try {
            URL url = new URL(sUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setConnectTimeout(5000);
            con.setRequestMethod("POST");
            int status = con.getResponseCode();
            resString = con.getResponseMessage();
            con.disconnect();
            log.info(String.format("RES=%d  STATUS=%s", status, resString));
            return (new HttpResult(status, resString, ""));

        } catch (Exception ex) {
            ex.printStackTrace();
            return (new HttpResult(-1, ex.getMessage(), ""));
        }
    }


    /*----------------------------------------------------------------------

    ------------------------------------------------------------------------*/
    public static HttpResult SendHttpPost2(String sUrl) {

        BufferedReader in;
        String resString = "", sErrString = "";

        log.info("SendHttpPost: URL=" + sUrl);
        try {
            URL url = new URL(sUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setConnectTimeout(5000);
            con.setRequestMethod("POST");

            con.setDoInput(true);
            con.setDoOutput(true);

            con.connect();


            int status = con.getResponseCode();

            StringBuilder content = new StringBuilder();
            if(con.getInputStream() != null) {
                in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();
                resString = content.toString();
            }

            if(con.getErrorStream() != null) {
                content = new StringBuilder();
                in = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();
                sErrString = content.toString();
            }



            con.disconnect();
            log.info("RES: " + status + "  RESP: " + resString + "  ERR: " + sErrString );
            return (new HttpResult(status, resString, sErrString));

        }
        catch (Exception e) {
            System.out.println("Exception" + e);
            return(null);
        }
    }
}
