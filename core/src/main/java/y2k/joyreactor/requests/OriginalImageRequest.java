package y2k.joyreactor.requests;

import rx.Observable;
import y2k.joyreactor.Platform;
import y2k.joyreactor.common.ObservableUtils;
import y2k.joyreactor.http.HttpClient;
import y2k.joyreactor.images.ImageThumbnailUrlBuilder;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by y2k on 16/10/15.
 */
public class OriginalImageRequest {

    private String imageUrl;
    private String format;

    public OriginalImageRequest(String imageUrl) {
        this(imageUrl, null);
    }

    public OriginalImageRequest(String imageUrl, String format) {
        this.imageUrl = imageUrl;
        this.format = format;
    }

    public Observable<File> request() {
        return ObservableUtils.create(() -> {

            File file = new File(Platform.Instance.getCurrentDirectory(), "" + imageUrl.hashCode() + "." + getExtension());
            if (file.exists()) return file;

            try {
                HttpClient.getInstance().downloadToFile(getImageUrl(), file);
            } catch (Exception e) {
                file.delete();
                throw e;
            }

            return file;
        });
    }

    private String getImageUrl() {
        ImageThumbnailUrlBuilder urlBuilder = new ImageThumbnailUrlBuilder();
        urlBuilder.url = imageUrl;
        urlBuilder.format = format;
        return urlBuilder.buildString();
    }

    private String getExtension() {
        if (format != null) return format;

        Matcher m = Pattern.compile("\\.([^\\.]+)$").matcher(imageUrl);
        if (!m.find()) throw new IllegalStateException("can't find extension from url " + imageUrl);
        return m.group(1);
    }
}