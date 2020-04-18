package Structures;

public class HttpResult {
    public int ResultCode;
    public String Response;
    public String Body;

    public HttpResult(int code, String res, String  b) {
        ResultCode = code;
        Response = res;
        Body = b;
    }
}
