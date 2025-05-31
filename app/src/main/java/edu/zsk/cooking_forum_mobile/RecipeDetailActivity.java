package edu.zsk.cooking_forum_mobile;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class RecipeDetailActivity extends AppCompatActivity {
    private TextView tvTitle, tvAuthor, tvDescription, tvIngredients, tvInstructions, tvCategory;
    private Button btnLike;
    private DatabaseHelper databaseHelper;
    private Recipe recipe;
    private boolean isLiked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        databaseHelper = new DatabaseHelper(this);

        int recipeId = getIntent().getIntExtra("recipe_id", -1);
        if (recipeId == -1) {
            finish();
            return;
        }

        recipe = databaseHelper.getRecipeById(recipeId);
        if (recipe == null) {
            finish();
            return;
        }

        int userId = SessionManager.getInstance(this).getUserId();
        isLiked = databaseHelper.isRecipeLiked(userId, recipeId);
        int likeCount = databaseHelper.getLikeCount(recipeId);

        recipe.setLiked(isLiked);
        recipe.setLikeCount(likeCount);

        tvTitle = findViewById(R.id.tvTitle);
        tvAuthor = findViewById(R.id.tvAuthor);
        tvDescription = findViewById(R.id.tvDescription);
        tvIngredients = findViewById(R.id.tvIngredients);
        tvInstructions = findViewById(R.id.tvInstructions);
        tvCategory = findViewById(R.id.tvCategory);
        btnLike = findViewById(R.id.btnLike);

        tvTitle.setText(recipe.getTitle());
        tvAuthor.setText("By: " + recipe.getUsername());
        tvDescription.setText(recipe.getDescription());
        tvIngredients.setText(recipe.getIngredients());
        tvInstructions.setText(recipe.getInstructions());
        tvCategory.setText("Category: " + recipe.getCategory());

        updateLikeButton();

        btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleLike();
            }
        });
    }

    private void toggleLike() {
        int userId = SessionManager.getInstance(this).getUserId();

        if (isLiked) {
            databaseHelper.removeLike(userId, recipe.getId());
            isLiked = false;
            recipe.setLikeCount(recipe.getLikeCount() - 1);
        } else {
            databaseHelper.addLike(userId, recipe.getId());
            isLiked = true;
            recipe.setLikeCount(recipe.getLikeCount() + 1);
        }

        updateLikeButton();
    }

    private void updateLikeButton() {
        if (isLiked) {
            btnLike.setText(getString(R.string.liked) + " (" + recipe.getLikeCount() + ")");
        } else {
            btnLike.setText(getString(R.string.like) + " (" + recipe.getLikeCount() + ")");
        }
    }
}