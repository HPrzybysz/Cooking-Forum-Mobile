package edu.zsk.cooking_forum_mobile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class CategoriesActivity extends AppCompatActivity {
    private ListView listView;
    private DatabaseHelper databaseHelper;
    private List<String> categories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        databaseHelper = new DatabaseHelper(this);
        categories = new ArrayList<>();
        categories.add(getString(R.string.all_categories));

        for (Recipe recipe : databaseHelper.getAllRecipes()) {
            if (!categories.contains(recipe.getCategory())) {
                categories.add(recipe.getCategory());
            }
        }

        listView = findViewById(R.id.listView);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, categories);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedCategory = categories.get(position);
                Intent intent = new Intent(CategoriesActivity.this, CategoryRecipesActivity.class);
                intent.putExtra("category", selectedCategory);
                startActivity(intent);
            }
        });
    }
}