package edu.zsk.cooking_forum_mobile;

public class Category {
    private String strCategory;
    private String strCategoryThumb;
    private String strCategoryDescription;

    public Category(String strCategory, String strCategoryThumb, String strCategoryDescription) {
        this.strCategory = strCategory;
        this.strCategoryThumb = strCategoryThumb;
        this.strCategoryDescription = strCategoryDescription;
    }

    public String getStrCategory() {
        return strCategory;
    }

    public String getStrCategoryThumb() {
        return strCategoryThumb;
    }

    public String getStrCategoryDescription() {
        return strCategoryDescription;
    }
}