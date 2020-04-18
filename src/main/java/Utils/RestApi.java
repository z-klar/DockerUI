package Utils;

import Structures.HttpResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;


public class RestApi {


    private static final Logger log = LoggerFactory.getLogger(RestApi.class);


    /*------------------------------------------------------------------------

     ------------------------------------------------------------------------*/
    public static HttpResult DeleteImage(String imgName) {

        log.info("Remove image: " + imgName);
        String sUrl = "http://localhost:2375/images/" + imgName ;
        return(HttpUtils.SendHttpDelete(sUrl));
    }

    /*------------------------------------------------------------------------

     ------------------------------------------------------------------------*/
    public static HttpResult InspectImage(String imgName) {

        log.info("Inspect image: " + imgName);
        String sUrl = "http://localhost:2375/images/" + imgName + "/json" ;
        return(HttpUtils.SendHttpGet(sUrl));
    }

    /*------------------------------------------------------------------------

     ------------------------------------------------------------------------*/
    public static HttpResult DeleteContainer(String contName) {

        log.info("Remove container: " + contName);
        String sUrl = "http://localhost:2375/containers/" + contName ;
        return(HttpUtils.SendHttpDelete(sUrl));
    }

    /*------------------------------------------------------------------------

     ------------------------------------------------------------------------*/
    public static HttpResult StartContainer(String contId) {

        log.info("Start container: " + contId);
        String sUrl = "http://localhost:2375/containers/" + contId + "/start" ;
        return(HttpUtils.SendHttpPost(sUrl));
    }

    /*------------------------------------------------------------------------

     ------------------------------------------------------------------------*/
    public static HttpResult StopContainer(String contId) {

        log.info("Stop container: " + contId);
        String sUrl = "http://localhost:2375/containers/" + contId + "/stop" ;
        return(HttpUtils.SendHttpPost(sUrl));
    }

    /*------------------------------------------------------------------------

     ------------------------------------------------------------------------*/
    public static HttpResult InspectContainer(String contName) {

        log.info("Inspect container: " + contName);
        String sUrl = "http://localhost:2375/containers/" + contName + "/json" ;
        return(HttpUtils.SendHttpGet(sUrl));
    }

    /*------------------------------------------------------------------------

     ------------------------------------------------------------------------*/
    public static HttpResult BuildImage(String name, String dockerFilePath) {

        log.info("Build image: " + name + "  DockerFilePath: " + dockerFilePath);
        System.setProperty("user.dir", dockerFilePath);  // change CD

        String sUrl = "http://localhost:2375/build?t=" + name ;
//        if(dockerFilePath.length() > 0) {
  //          sUrl += "&dockerfile=" + dockerFilePath;
    //    }
        return(HttpUtils.SendHttpPost(sUrl));
    }

}
