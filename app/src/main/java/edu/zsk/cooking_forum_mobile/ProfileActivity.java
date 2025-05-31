package edu.zsk.cooking_forum_mobile;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

public class ProfileActivity extends AppCompatActivity {
    private TextView tvUsername;
    private Button btnChangeUsername, btnMyRecipes, btnLikedRecipes, btnLogout;
    private RecyclerView recyclerView;
    private RecipeAdapter recipeAdapter;
    private List<Recipe> recipeList;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        databaseHelper = new DatabaseHelper(this);
        int userId = SessionManager.getInstance(this).getUserId();
        String username = databaseHelper.getUsername(userId);

        tvUsername = findViewById(R.id.tvUsername);
        tvUsername.setText(username);

        btnChangeUsername = findViewById(R.id.btnChangeUsername);
        btnMyRecipes = findViewById(R.id.btnMyRecipes);
        btnLikedRecipes = findViewById(R.id.btnLikedRecipes);
        btnLogout = findViewById(R.id.btnLogout);
        recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recipeList = databaseHelper.getUserRecipes(userId);
        recipeAdapter = new RecipeAdapter(this, recipeList);
        recyclerView.setAdapter(recipeAdapter);

        btnChangeUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangeUsernameDialog();
            }
        });

        btnMyRecipes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recipeList.clear();
                recipeList.addAll(databaseHelper.getUserRecipes(userId));
                recipeAdapter.notifyDataSetChanged();
            }
        });

        btnLikedRecipes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recipeList.clear();
                recipeList.addAll(databaseHelper.getLikedRecipes(userId));
                recipeAdapter.notifyDataSetChanged();
            }
        });

        btnLogout.setOnClickListener(v -> {
            SessionManager.getInstance(getApplicationContext()).clearSession();
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void showChangeUsernameDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);

        builder.setTitle(getString(R.string.change_username));

        final View customLayout = getLayoutInflater().inflate(R.layout.dialog_change_username, null);
        builder.setView(customLayout);

        builder.setPositiveButton(getString(R.string.update), (dialog, which) -> {
            EditText etNewUsername = customLayout.findViewById(R.id.etNewUsername);
            String newUsername = etNewUsername.getText().toString().trim();

            if (newUsername.isEmpty()) {
                Toast.makeText(this, getString(R.string.error_empty_fields), Toast.LENGTH_SHORT).show();
                return;
            }

            int userId = SessionManager.getInstance(this).getUserId();
            if (databaseHelper.updateUsername(userId, newUsername)) {
                tvUsername.setText(newUsername);
                Toast.makeText(this, getString(R.string.username_updated), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.error_occurred), Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton(getString(android.R.string.cancel), null);
        builder.show();
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}