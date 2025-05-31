package edu.zsk.cooking_forum_mobile;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {
    @GET("categories.php")
    Call<CategoriesResponse> getCategories();
}

class CategoriesResponse {
    private List<Category> categories;

    public List<Category> getCategories() {
        return categories;
    }
}