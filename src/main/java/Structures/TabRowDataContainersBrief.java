package Structures;

public class TabRowDataContainersBrief {
    public String Id;
    public String Image;
    public String Status;
    public String Name;
    public String IPAddress;

    public TabRowDataContainersBrief(String sid, String simg,
                            String ssts, String sname, String ip) {
        Id = sid;
        Name = sname;
        Image = simg;
        Status = ssts;
        IPAddress = ip;
    }
}
