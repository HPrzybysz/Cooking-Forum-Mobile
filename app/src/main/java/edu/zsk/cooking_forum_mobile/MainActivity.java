package edu.zsk.cooking_forum_mobile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView, categoriesRecyclerView;
    private RecipeAdapter recipeAdapter;
    private List<Recipe> recipeList;
    private DatabaseHelper databaseHelper;
    private Button btnCreateRecipe, btnCategories;
    private CategoriesAdapter categoriesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (SessionManager.getInstance(getApplicationContext()).getUserId() == -1) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar == null) {
            throw new RuntimeException("Toolbar not found in layout");
        }
        setSupportActionBar(toolbar);

        databaseHelper = new DatabaseHelper(this);

        recyclerView = findViewById(R.id.recyclerView);
        if (recyclerView == null) {
            throw new RuntimeException("recyclerView not found in layout");
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recipeList = new ArrayList<>();
        recipeAdapter = new RecipeAdapter(this, recipeList);
        recyclerView.setAdapter(recipeAdapter);

        RecyclerView categoriesRecyclerView = findViewById(R.id.categoriesRecyclerView);
        if (categoriesRecyclerView == null) {
            throw new RuntimeException("categoriesRecyclerView not found in layout");
        }
        categoriesRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        categoriesAdapter = new CategoriesAdapter(this, new ArrayList<>());
        categoriesRecyclerView.setAdapter(categoriesAdapter);

        btnCreateRecipe = findViewById(R.id.btnCreateRecipe);
        btnCategories = findViewById(R.id.btnCategories);

        if (btnCreateRecipe == null || btnCategories == null) {
            throw new RuntimeException("Buttons not found in layout");
        }

        btnCreateRecipe.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CreateRecipeActivity.class);
            startActivity(intent);
        });

        btnCategories.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CategoriesActivity.class);
            startActivity(intent);
        });

        loadRecipes();
        fetchCategoriesFromApi();
    }

    private void loadRecipes() {
        recipeList.clear();
        recipeList.addAll(databaseHelper.getAllRecipes());
        recipeAdapter.notifyDataSetChanged();
    }

    private void fetchCategoriesFromApi() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<CategoriesResponse> call = apiService.getCategories();

        call.enqueue(new Callback<CategoriesResponse>() {
            @Override
            public void onResponse(Call<CategoriesResponse> call, Response<CategoriesResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Category> categories = response.body().getCategories();
                    updateCategoriesInUI(categories);
                } else {
                    Log.e("API Error", "Failed to fetch categories");
                }
            }

            @Override
            public void onFailure(Call<CategoriesResponse> call, Throwable t) {
                Log.e("API Failure", "Error: " + t.getMessage());
                Toast.makeText(MainActivity.this, "Failed to load categories", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateCategoriesInUI(List<Category> categories) {
        runOnUiThread(() -> {
            if (categoriesAdapter != null) {
                categoriesAdapter.updateCategories(categories);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_profile) {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRecipes();
    }
}