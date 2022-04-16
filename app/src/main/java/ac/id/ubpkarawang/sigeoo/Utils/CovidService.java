package ac.id.ubpkarawang.sigeoo.Utils;

import ac.id.ubpkarawang.sigeoo.Model.Covid.DataIndonesia;
import retrofit2.Call;
import retrofit2.http.GET;

public interface CovidService {
    @GET("indonesia")
    Call<DataIndonesia> getKasus();
}
