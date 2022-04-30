package ac.id.ubpkarawang.sigeoo.Model.Informasi;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Berita implements Parcelable {
    @SerializedName("urlToImage")
    private String urlToImage;
    @SerializedName("title")
    private String title;
    @SerializedName("description")
    private String description;
    @SerializedName("content")
    private String content;
    @SerializedName("publishedAt")
    private String publishedAt;

    public Berita() {

    }

    protected Berita(Parcel in) {
        urlToImage = in.readString();
        title = in.readString();
        description = in.readString();
        content = in.readString();
    }

    public static final Creator<Berita> CREATOR = new Creator<Berita>() {
        @Override
        public Berita createFromParcel(Parcel in) {
            return new Berita(in);
        }

        @Override
        public Berita[] newArray(int size) {
            return new Berita[size];
        }
    };

    public String getUrlToImage() {
        return urlToImage;
    }

    public void setUrlToImage(String urlToImage) {
        this.urlToImage = urlToImage;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(urlToImage);
        parcel.writeString(title);
        parcel.writeString(description);
        parcel.writeString(content);
    }
}
