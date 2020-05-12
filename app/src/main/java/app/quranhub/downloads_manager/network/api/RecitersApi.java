package app.quranhub.downloads_manager.network.api;

import app.quranhub.downloads_manager.network.model.RecitersResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RecitersApi {

    @GET("/api/user/get-quran-reciters")
    Call<RecitersResponse> getQuranReciters(@Query("recitation") int recitationId);
}
