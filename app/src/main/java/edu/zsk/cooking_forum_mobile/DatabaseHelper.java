package edu.zsk.cooking_forum_mobile;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "cooking_forum.db";
    private static final int DATABASE_VERSION = 1;

    // User table
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_EMAIL = "email";

    // Recipe table
    private static final String TABLE_RECIPES = "recipes";
    private static final String COLUMN_RECIPE_ID = "recipe_id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_INGREDIENTS = "ingredients";
    private static final String COLUMN_INSTRUCTIONS = "instructions";
    private static final String COLUMN_CATEGORY = "category";
    private static final String COLUMN_USER_ID_FK = "user_id";

    // Likes table
    private static final String TABLE_LIKES = "likes";
    private static final String COLUMN_LIKE_ID = "like_id";
    private static final String COLUMN_RECIPE_ID_FK = "recipe_id";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create users table
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USERNAME + " TEXT UNIQUE,"
                + COLUMN_PASSWORD + " TEXT,"
                + COLUMN_EMAIL + " TEXT" + ")";
        db.execSQL(CREATE_USERS_TABLE);

        // Create recipes table
        String CREATE_RECIPES_TABLE = "CREATE TABLE " + TABLE_RECIPES + "("
                + COLUMN_RECIPE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_TITLE + " TEXT,"
                + COLUMN_DESCRIPTION + " TEXT,"
                + COLUMN_INGREDIENTS + " TEXT,"
                + COLUMN_INSTRUCTIONS + " TEXT,"
                + COLUMN_CATEGORY + " TEXT,"
                + COLUMN_USER_ID_FK + " INTEGER,"
                + "FOREIGN KEY(" + COLUMN_USER_ID_FK + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + ")" + ")";
        db.execSQL(CREATE_RECIPES_TABLE);

        // Create likes table
        String CREATE_LIKES_TABLE = "CREATE TABLE " + TABLE_LIKES + "("
                + COLUMN_LIKE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USER_ID_FK + " INTEGER,"
                + COLUMN_RECIPE_ID_FK + " INTEGER,"
                + "FOREIGN KEY(" + COLUMN_USER_ID_FK + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + "),"
                + "FOREIGN KEY(" + COLUMN_RECIPE_ID_FK + ") REFERENCES " + TABLE_RECIPES + "(" + COLUMN_RECIPE_ID + "),"
                + "UNIQUE(" + COLUMN_USER_ID_FK + ", " + COLUMN_RECIPE_ID_FK + ")" + ")";
        db.execSQL(CREATE_LIKES_TABLE);

        db.execSQL("CREATE INDEX idx_user_id ON " + TABLE_RECIPES + "(" + COLUMN_USER_ID_FK + ")");
        db.execSQL("CREATE INDEX idx_recipe_id ON " + TABLE_LIKES + "(" + COLUMN_RECIPE_ID_FK + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LIKES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    private int getColumnIndex(Cursor cursor, String columnName) {
        int index = cursor.getColumnIndex(columnName);
        if (index == -1) {
            throw new IllegalArgumentException("Column " + columnName + " doesn't exist in cursor");
        }
        return index;
    }

    public boolean addUser(String username, String password, String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, password);
        values.put(COLUMN_EMAIL, email);

        try {
            long result = db.insert(TABLE_USERS, null, values);
            return result != -1;
        } finally {
            db.close();
        }
    }

    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_USER_ID};
        String selection = COLUMN_USERNAME + " = ?" + " AND " + COLUMN_PASSWORD + " = ?";
        String[] selectionArgs = {username, password};

        try (Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null)) {
            return cursor.getCount() > 0;
        }
    }

    public boolean checkUsernameExists(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_USER_ID};
        String selection = COLUMN_USERNAME + " = ?";
        String[] selectionArgs = {username};

        try (Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null)) {
            return cursor.getCount() > 0;
        }
    }

    public int getUserId(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_USER_ID};
        String selection = COLUMN_USERNAME + " = ?";
        String[] selectionArgs = {username};

        try (Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null)) {
            if (cursor.moveToFirst()) {
                return cursor.getInt(getColumnIndex(cursor, COLUMN_USER_ID));
            }
            return -1;
        }
    }

    public String getUsername(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_USERNAME};
        String selection = COLUMN_USER_ID + " = ?";
        String[] selectionArgs = {String.valueOf(userId)};

        try (Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null)) {
            if (cursor.moveToFirst()) {
                return cursor.getString(getColumnIndex(cursor, COLUMN_USERNAME));
            }
            return null;
        }
    }

    public boolean updateUsername(int userId, String newUsername) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, newUsername);

        try {
            int rowsAffected = db.update(TABLE_USERS, values, COLUMN_USER_ID + " = ?",
                    new String[]{String.valueOf(userId)});
            return rowsAffected > 0;
        } finally {
            db.close();
        }
    }

    public long addRecipe(String title, String description, String ingredients,
                          String instructions, String category, int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, title);
        values.put(COLUMN_DESCRIPTION, description);
        values.put(COLUMN_INGREDIENTS, ingredients);
        values.put(COLUMN_INSTRUCTIONS, instructions);
        values.put(COLUMN_CATEGORY, category);
        values.put(COLUMN_USER_ID_FK, userId);

        try {
            return db.insert(TABLE_RECIPES, null, values);
        } finally {
            db.close();
        }
    }

    public List<Recipe> getAllRecipes() {
        List<Recipe> recipeList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT r.*, u." + COLUMN_USERNAME + " FROM " + TABLE_RECIPES + " r " +
                "JOIN " + TABLE_USERS + " u ON r." + COLUMN_USER_ID_FK + " = u." + COLUMN_USER_ID;

        try (Cursor cursor = db.rawQuery(query, null)) {
            if (cursor.moveToFirst()) {
                int idIndex = getColumnIndex(cursor, COLUMN_RECIPE_ID);
                int titleIndex = getColumnIndex(cursor, COLUMN_TITLE);
                int descIndex = getColumnIndex(cursor, COLUMN_DESCRIPTION);
                int ingIndex = getColumnIndex(cursor, COLUMN_INGREDIENTS);
                int instrIndex = getColumnIndex(cursor, COLUMN_INSTRUCTIONS);
                int catIndex = getColumnIndex(cursor, COLUMN_CATEGORY);
                int userIdIndex = getColumnIndex(cursor, COLUMN_USER_ID_FK);
                int usernameIndex = getColumnIndex(cursor, COLUMN_USERNAME);

                do {
                    Recipe recipe = new Recipe();
                    recipe.setId(cursor.getInt(idIndex));
                    recipe.setTitle(cursor.getString(titleIndex));
                    recipe.setDescription(cursor.getString(descIndex));
                    recipe.setIngredients(cursor.getString(ingIndex));
                    recipe.setInstructions(cursor.getString(instrIndex));
                    recipe.setCategory(cursor.getString(catIndex));
                    recipe.setUserId(cursor.getInt(userIdIndex));
                    recipe.setUsername(cursor.getString(usernameIndex));

                    recipeList.add(recipe);
                } while (cursor.moveToNext());
            }
            return recipeList;
        }
    }

    public List<Recipe> getRecipesByCategory(String category) {
        List<Recipe> recipeList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT r.*, u." + COLUMN_USERNAME + " FROM " + TABLE_RECIPES + " r " +
                "JOIN " + TABLE_USERS + " u ON r." + COLUMN_USER_ID_FK + " = u." + COLUMN_USER_ID +
                " WHERE r." + COLUMN_CATEGORY + " = ?";

        try (Cursor cursor = db.rawQuery(query, new String[]{category})) {
            if (cursor.moveToFirst()) {
                int idIndex = getColumnIndex(cursor, COLUMN_RECIPE_ID);
                int titleIndex = getColumnIndex(cursor, COLUMN_TITLE);
                int descIndex = getColumnIndex(cursor, COLUMN_DESCRIPTION);
                int ingIndex = getColumnIndex(cursor, COLUMN_INGREDIENTS);
                int instrIndex = getColumnIndex(cursor, COLUMN_INSTRUCTIONS);
                int catIndex = getColumnIndex(cursor, COLUMN_CATEGORY);
                int userIdIndex = getColumnIndex(cursor, COLUMN_USER_ID_FK);
                int usernameIndex = getColumnIndex(cursor, COLUMN_USERNAME);

                do {
                    Recipe recipe = new Recipe();
                    recipe.setId(cursor.getInt(idIndex));
                    recipe.setTitle(cursor.getString(titleIndex));
                    recipe.setDescription(cursor.getString(descIndex));
                    recipe.setIngredients(cursor.getString(ingIndex));
                    recipe.setInstructions(cursor.getString(instrIndex));
                    recipe.setCategory(cursor.getString(catIndex));
                    recipe.setUserId(cursor.getInt(userIdIndex));
                    recipe.setUsername(cursor.getString(usernameIndex));

                    recipeList.add(recipe);
                } while (cursor.moveToNext());
            }
            return recipeList;
        }
    }

    public List<Recipe> getUserRecipes(int userId) {
        List<Recipe> recipeList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT r.*, u." + COLUMN_USERNAME + " FROM " + TABLE_RECIPES + " r " +
                "JOIN " + TABLE_USERS + " u ON r." + COLUMN_USER_ID_FK + " = u." + COLUMN_USER_ID +
                " WHERE r." + COLUMN_USER_ID_FK + " = ?";

        try (Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)})) {
            if (cursor.moveToFirst()) {
                int idIndex = getColumnIndex(cursor, COLUMN_RECIPE_ID);
                int titleIndex = getColumnIndex(cursor, COLUMN_TITLE);
                int descIndex = getColumnIndex(cursor, COLUMN_DESCRIPTION);
                int ingIndex = getColumnIndex(cursor, COLUMN_INGREDIENTS);
                int instrIndex = getColumnIndex(cursor, COLUMN_INSTRUCTIONS);
                int catIndex = getColumnIndex(cursor, COLUMN_CATEGORY);
                int userIdIndex = getColumnIndex(cursor, COLUMN_USER_ID_FK);
                int usernameIndex = getColumnIndex(cursor, COLUMN_USERNAME);

                do {
                    Recipe recipe = new Recipe();
                    recipe.setId(cursor.getInt(idIndex));
                    recipe.setTitle(cursor.getString(titleIndex));
                    recipe.setDescription(cursor.getString(descIndex));
                    recipe.setIngredients(cursor.getString(ingIndex));
                    recipe.setInstructions(cursor.getString(instrIndex));
                    recipe.setCategory(cursor.getString(catIndex));
                    recipe.setUserId(cursor.getInt(userIdIndex));
                    recipe.setUsername(cursor.getString(usernameIndex));

                    recipeList.add(recipe);
                } while (cursor.moveToNext());
            }
            return recipeList;
        }
    }

    public Recipe getRecipeById(int recipeId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT r.*, u." + COLUMN_USERNAME + " FROM " + TABLE_RECIPES + " r " +
                "JOIN " + TABLE_USERS + " u ON r." + COLUMN_USER_ID_FK + " = u." + COLUMN_USER_ID +
                " WHERE r." + COLUMN_RECIPE_ID + " = ?";

        try (Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(recipeId)})) {
            if (cursor.moveToFirst()) {
                Recipe recipe = new Recipe();
                recipe.setId(cursor.getInt(getColumnIndex(cursor, COLUMN_RECIPE_ID)));
                recipe.setTitle(cursor.getString(getColumnIndex(cursor, COLUMN_TITLE)));
                recipe.setDescription(cursor.getString(getColumnIndex(cursor, COLUMN_DESCRIPTION)));
                recipe.setIngredients(cursor.getString(getColumnIndex(cursor, COLUMN_INGREDIENTS)));
                recipe.setInstructions(cursor.getString(getColumnIndex(cursor, COLUMN_INSTRUCTIONS)));
                recipe.setCategory(cursor.getString(getColumnIndex(cursor, COLUMN_CATEGORY)));
                recipe.setUserId(cursor.getInt(getColumnIndex(cursor, COLUMN_USER_ID_FK)));
                recipe.setUsername(cursor.getString(getColumnIndex(cursor, COLUMN_USERNAME)));

                return recipe;
            }
            return null;
        }
    }

    public boolean addLike(int userId, int recipeId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID_FK, userId);
        values.put(COLUMN_RECIPE_ID_FK, recipeId);

        try {
            long result = db.insert(TABLE_LIKES, null, values);
            return result != -1;
        } finally {
            db.close();
        }
    }

    public boolean removeLike(int userId, int recipeId) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            int rowsAffected = db.delete(TABLE_LIKES,
                    COLUMN_USER_ID_FK + " = ? AND " + COLUMN_RECIPE_ID_FK + " = ?",
                    new String[]{String.valueOf(userId), String.valueOf(recipeId)});
            return rowsAffected > 0;
        } finally {
            db.close();
        }
    }

    public boolean isRecipeLiked(int userId, int recipeId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_LIKE_ID};
        String selection = COLUMN_USER_ID_FK + " = ? AND " + COLUMN_RECIPE_ID_FK + " = ?";
        String[] selectionArgs = {String.valueOf(userId), String.valueOf(recipeId)};

        try (Cursor cursor = db.query(TABLE_LIKES, columns, selection, selectionArgs, null, null, null)) {
            return cursor.getCount() > 0;
        }
    }

    public List<Recipe> getLikedRecipes(int userId) {
        List<Recipe> recipeList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT r.*, u." + COLUMN_USERNAME + " FROM " + TABLE_RECIPES + " r " +
                "JOIN " + TABLE_USERS + " u ON r." + COLUMN_USER_ID_FK + " = u." + COLUMN_USER_ID + " " +
                "JOIN " + TABLE_LIKES + " l ON r." + COLUMN_RECIPE_ID + " = l." + COLUMN_RECIPE_ID_FK + " " +
                "WHERE l." + COLUMN_USER_ID_FK + " = ?";

        try (Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)})) {
            if (cursor.moveToFirst()) {
                int idIndex = getColumnIndex(cursor, COLUMN_RECIPE_ID);
                int titleIndex = getColumnIndex(cursor, COLUMN_TITLE);
                int descIndex = getColumnIndex(cursor, COLUMN_DESCRIPTION);
                int ingIndex = getColumnIndex(cursor, COLUMN_INGREDIENTS);
                int instrIndex = getColumnIndex(cursor, COLUMN_INSTRUCTIONS);
                int catIndex = getColumnIndex(cursor, COLUMN_CATEGORY);
                int userIdIndex = getColumnIndex(cursor, COLUMN_USER_ID_FK);
                int usernameIndex = getColumnIndex(cursor, COLUMN_USERNAME);

                do {
                    Recipe recipe = new Recipe();
                    recipe.setId(cursor.getInt(idIndex));
                    recipe.setTitle(cursor.getString(titleIndex));
                    recipe.setDescription(cursor.getString(descIndex));
                    recipe.setIngredients(cursor.getString(ingIndex));
                    recipe.setInstructions(cursor.getString(instrIndex));
                    recipe.setCategory(cursor.getString(catIndex));
                    recipe.setUserId(cursor.getInt(userIdIndex));
                    recipe.setUsername(cursor.getString(usernameIndex));

                    recipeList.add(recipe);
                } while (cursor.moveToNext());
            }
            return recipeList;
        }
    }

    public int getLikeCount(int recipeId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + TABLE_LIKES +
                " WHERE " + COLUMN_RECIPE_ID_FK + " = ?";

        try (Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(recipeId)})) {
            if (cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
            return 0;
        }
    }
}