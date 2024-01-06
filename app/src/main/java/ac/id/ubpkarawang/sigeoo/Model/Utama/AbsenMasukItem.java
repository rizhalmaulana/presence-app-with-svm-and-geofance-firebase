package ac.id.ubpkarawang.sigeoo.Model.Utama;

import java.util.List;

public class AbsenMasukItem {
    List<AbsenMasuk> data;
    boolean state;
    String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public AbsenMasukItem() {}

    public List<AbsenMasuk> getData() {
        return data;
    }

    public void setData(List<AbsenMasuk> data) {
        this.data = data;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }
}
