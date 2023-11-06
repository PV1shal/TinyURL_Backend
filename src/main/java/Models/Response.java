package Models;

public class Response {
    String header;
    String message;
    String additionalInfo;

    public Response(String header, String message, String additionalInfo) {
        this.header = header;
        this.message = message;
        this.additionalInfo = additionalInfo;
    }

    public Response(String header, String message) {
        this.header = header;
        this.message = message;
    }
}
