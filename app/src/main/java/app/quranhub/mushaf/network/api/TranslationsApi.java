package app.quranhub.mushaf.network.api;

import retrofit2.Call;
import retrofit2.http.GET;
import app.quranhub.mushaf.network.model.TranslationsResponse;

public interface TranslationsApi {

    @GET("/api/user/get-translations")
    Call<TranslationsResponse> getAllTranslations();

}
