package ac.id.ubpkarawang.sigeoo.Model.Informasi;

import java.util.List;

public class JamKerjaItem {
    List<JamKerja> data;
    boolean state;

    public JamKerjaItem() {
    }

    public List<JamKerja> getData() {
        return data;
    }

    public void setData(List<JamKerja> data) {
        this.data = data;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }
}
