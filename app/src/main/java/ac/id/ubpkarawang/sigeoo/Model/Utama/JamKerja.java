package ac.id.ubpkarawang.sigeoo.Model.Utama;

public class JamKerja {

    public JamKerja() {
    }

    String id_office_hour;
    String day_time;
    String start_time;
    String end_time;
    String updated_date;

    public String getId_office_hour() {
        return id_office_hour;
    }

    public void setId_office_hour(String id_office_hour) {
        this.id_office_hour = id_office_hour;
    }

    public String getDay_time() {
        return day_time;
    }

    public void setDay_time(String day_time) {
        this.day_time = day_time;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public String getUpdated_date() {
        return updated_date;
    }

    public void setUpdated_date(String updated_date) {
        this.updated_date = updated_date;
    }
}
