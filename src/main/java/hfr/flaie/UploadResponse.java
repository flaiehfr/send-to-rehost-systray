package hfr.flaie;

import java.util.List;
import java.util.stream.Collectors;

public class UploadResponse {
    public int multiple;
    public String thumbURL;
    public String resizedURL;
    public String picURL;
    public String thumbBB;
    public String picBB;
    public String thumbBBLink;
    public String resizedBBLink;
    public List<String> multipleResults;
    public boolean isGIF;

    public boolean isMultiple() {
        return multiple == 1;
    }

    public String toString(String format) {
        if (isGIF) {
            if (format.contains("BBCode")) {
                return picBB;
            }
            return picURL;
        }
        switch (format) {
            case "URL de l'image pleine":
                return picURL;
            case "BBCode de l'image pleine":
                return picBB;
            case "URL de l'image réduite":
                return resizedURL;
            case "URL de l'image miniature":
                return thumbURL;
            case "BBCode de l'image miniature":
                return thumbBB;
            case "BBCode de l'image miniature avec lien":
                return thumbBBLink;
            default:
                return resizedBBLink;
        }
    }

    public List<UploadResponse> getMultipleResults() {
        return multipleResults.stream()
                .map(s -> {
                    String[] p = s.split("[|]");
                    var resp = new UploadResponse();
                    var i = 4;
                    resp.thumbURL = p[i++];
                    resp.resizedURL = p[i++];
                    resp.picURL = p[i++];
                    resp.thumbBB = p[i++];
                    resp.picBB = p[i++];
                    resp.thumbBBLink = p[i++];
                    resp.resizedBBLink = p[i];
                    resp.isGIF = Boolean.parseBoolean(p[14].toLowerCase());
                    return resp;
                }).collect(Collectors.toList());
    }
}
