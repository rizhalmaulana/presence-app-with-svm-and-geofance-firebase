package ac.id.ubpkarawang.sigeoo.Utils;

import java.util.List;

import ac.id.ubpkarawang.sigeoo.Model.DataIndonesia;
import retrofit2.Call;
import retrofit2.http.GET;

public interface CovidService {
    @GET("indonesia")
    Call<List<DataIndonesia>> getKasus();
}
