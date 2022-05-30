package ac.id.ubpkarawang.sigeoo.Model.Utama;

import java.util.List;

public class SvmItem {
    List<Svm> data;
    boolean state;

    public SvmItem() {
    }

    public List<Svm> getData() {
        return data;
    }

    public void setData(List<Svm> data) {
        this.data = data;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }
}
