package tech.bfitzsimmons.instagramv3;

import android.graphics.Bitmap;

/**
 * Created by Brian on 7/4/2017.
 */

public class PhotoListItem {
    private String createdAt;
    private Bitmap imageBitmap;
    private String caption;

    public PhotoListItem(String createdAt, Bitmap imageBitmap, String caption) {
        this.createdAt = createdAt;
        this.imageBitmap = imageBitmap;
        this.caption = caption;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public Bitmap getImageBitmap() {
        return imageBitmap;
    }

    public String getCaption() {
        return caption;
    }
}
