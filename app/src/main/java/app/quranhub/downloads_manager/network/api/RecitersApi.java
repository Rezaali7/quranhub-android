package app.quranhub.downloads_manager.network.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import app.quranhub.downloads_manager.network.model.RecitersResponse;

public interface RecitersApi {

    @GET("/api/user/get-quran-reciters")
    Call<RecitersResponse> getQuranReciters(@Query("recitation") int recitationId);
}
