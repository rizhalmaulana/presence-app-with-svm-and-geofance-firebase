package ac.id.ubpkarawang.sigeoo.Model.Informasi;

import java.util.List;

public class BannerItem {

    List<Banner> data;
    boolean state;

    public BannerItem(){

    }
    public List<Banner> getData() {
        return data;
    }

    public void setData(List<Banner> data) {
        this.data = data;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

}
