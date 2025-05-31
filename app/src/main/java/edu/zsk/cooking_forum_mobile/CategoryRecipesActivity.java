package edu.zsk.cooking_forum_mobile;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CategoryRecipesActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecipeAdapter recipeAdapter;
    private List<Recipe> recipeList;
    private DatabaseHelper databaseHelper;
    private TextView tvCategoryTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_recipes);

        String category = getIntent().getStringExtra("category");
        if (category == null) {
            finish();
            return;
        }

        databaseHelper = new DatabaseHelper(this);
        recipeList = category.equals(getString(R.string.all_categories)) ?
                databaseHelper.getAllRecipes() :
                databaseHelper.getRecipesByCategory(category);

        tvCategoryTitle = findViewById(R.id.tvCategoryTitle);
        tvCategoryTitle.setText(category);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recipeAdapter = new RecipeAdapter(this, recipeList);
        recyclerView.setAdapter(recipeAdapter);
    }
}