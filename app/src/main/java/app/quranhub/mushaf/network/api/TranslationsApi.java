package app.quranhub.mushaf.network.api;

import app.quranhub.mushaf.network.model.TranslationsResponse;
import retrofit2.Call;
import retrofit2.http.GET;

public interface TranslationsApi {

    @GET("/api/user/get-translations")
    Call<TranslationsResponse> getAllTranslations();

}
