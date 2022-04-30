package ac.id.ubpkarawang.sigeoo.Model.Informasi;

public class Banner {

    public Banner() {
    }

    private String id;
    private String foto_banner;
    private String deskripsi_banner;
    private String author_banner;
    private String created_at;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFoto_banner() {
        return foto_banner;
    }

    public void setFoto_banner(String foto_banner) {
        this.foto_banner = foto_banner;
    }

    public String getDeskripsi_banner() {
        return deskripsi_banner;
    }

    public void setDeskripsi_banner(String deskripsi_banner) {
        this.deskripsi_banner = deskripsi_banner;
    }

    public String getAuthor_banner() {
        return author_banner;
    }

    public void setAuthor_banner(String author_banner) {
        this.author_banner = author_banner;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

}
