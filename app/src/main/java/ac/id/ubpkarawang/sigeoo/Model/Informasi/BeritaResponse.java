package ac.id.ubpkarawang.sigeoo.Model.Informasi;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class BeritaResponse {
    @SerializedName("articles")
    private ArrayList<Berita> results;

    public BeritaResponse(ArrayList<Berita> results) {
        this.results = results;
    }

    @SerializedName("totalResults")
    public ArrayList<Berita> getResults() {
        return results;
    }
}
