package edu.zsk.cooking_forum_mobile;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {
    @GET("categories.php")
    Call<CategoriesResponse> getCategories();
}

class CategoriesResponse {
    @SerializedName("categories")
    private List<Category> categories;

    public List<Category> getCategories() {
        return categories;
    }
}