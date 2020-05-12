package app.quranhub.mushaf.network.api;

import app.quranhub.mushaf.network.model.BooksResponse;
import io.reactivex.Single;
import retrofit2.http.GET;

public interface BooksApi {

    @GET("/api/user/get-books")
    Single<BooksResponse> getAllBooks();

}
