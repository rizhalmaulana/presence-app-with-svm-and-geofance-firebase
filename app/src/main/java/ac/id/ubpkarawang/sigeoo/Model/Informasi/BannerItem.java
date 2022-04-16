package ac.id.ubpkarawang.sigeoo.Model.Informasi;

import java.util.List;

public class BannerItem {

    List<Banner> dataBanner;
    boolean state;

    public BannerItem() {
    }

    public List<Banner> getDataBanner() {
        return dataBanner;
    }

    public void setDataBanner(List<Banner> dataBanner) {
        this.dataBanner = dataBanner;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

}
