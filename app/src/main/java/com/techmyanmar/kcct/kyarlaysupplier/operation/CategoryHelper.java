package com.techmyanmar.kcct.kyarlaysupplier.operation;

import androidx.appcompat.app.AppCompatActivity;

import com.techmyanmar.kcct.kyarlaysupplier.VO.Category;

import java.util.List;

public class CategoryHelper {
    public static String getCategoryName(AppCompatActivity activity, int id){
        DatabaseAdapter databaseAdapter = new DatabaseAdapter(activity);
        List<Category> categoryList = databaseAdapter.getCategories();
        String returnName = "";

        for(int i=0; i<categoryList.size(); i++){
            for(int j=0; j< categoryList.get(i).getCategoryArrayList().size(); j++){
                if (categoryList.get(i).getCategoryArrayList().get(j).getId() == id)
                    returnName = categoryList.get(i).getCategoryArrayList().get(j).getName();
            }
        }

        return returnName;
    }
}
