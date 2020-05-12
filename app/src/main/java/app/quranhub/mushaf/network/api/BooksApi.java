package app.quranhub.mushaf.network.api;

import io.reactivex.Single;
import retrofit2.http.GET;
import app.quranhub.mushaf.network.model.BooksResponse;

public interface BooksApi {

    @GET("/api/user/get-books")
    Single<BooksResponse> getAllBooks();

}
