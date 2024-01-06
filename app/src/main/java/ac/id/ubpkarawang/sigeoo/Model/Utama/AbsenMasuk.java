package ac.id.ubpkarawang.sigeoo.Model.Utama;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AbsenMasuk {

    public AbsenMasuk() {
    }

    @Expose
    @SerializedName("id_absen")
    private String id_absen;

    @Expose
    @SerializedName("user_uid")
    private String user_uid;

    @Expose
    @SerializedName("tgl_absen")
    private String tgl_absen;

    @Expose
    @SerializedName("jam_absen")
    private String jam_absen;

    @Expose
    @SerializedName("lat_absen")
    private String lat_absen;

    @Expose
    @SerializedName("long_absen")
    private String long_absen;

    @Expose
    @SerializedName("akurasi_absen")
    private String akurasi_absen;

    @Expose
    @SerializedName("keterangan_absen")
    private String keterangan_absen;

    @Expose
    @SerializedName("status_absen")
    private String status_absen;

    public String getId_absen() {
        return id_absen;
    }

    public void setId_absen(String id_absen) {
        this.id_absen = id_absen;
    }

    public String getUser_uid() {
        return user_uid;
    }

    public void setUser_uid(String user_uid) {
        this.user_uid = user_uid;
    }

    public String getTgl_absen() {
        return tgl_absen;
    }

    public void setTgl_absen(String tgl_absen) {
        this.tgl_absen = tgl_absen;
    }

    public String getJam_absen() {
        return jam_absen;
    }

    public void setJam_absen(String jam_absen) {
        this.jam_absen = jam_absen;
    }

    public String getLat_absen() {
        return lat_absen;
    }

    public void setLat_absen(String lat_absen) {
        this.lat_absen = lat_absen;
    }

    public String getLong_absen() {
        return long_absen;
    }

    public void setLong_absen(String long_absen) {
        this.long_absen = long_absen;
    }

    public String getAkurasi_absen() {
        return akurasi_absen;
    }

    public void setAkurasi_absen(String akurasi_absen) {
        this.akurasi_absen = akurasi_absen;
    }

    public String getKeterangan_absen() {
        return keterangan_absen;
    }

    public void setKeterangan_absen(String keterangan_absen) {
        this.keterangan_absen = keterangan_absen;
    }

    public String getStatus_absen() {
        return status_absen;
    }

    public void setStatus_absen(String status_absen) {
        this.status_absen = status_absen;
    }
}
