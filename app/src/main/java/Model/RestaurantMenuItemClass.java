package Model;

import org.json.JSONObject;

import java.sql.Struct;
import java.util.ArrayList;

/**
 * Created by Akwasi on ${9/27/2018}.
 */

public class RestaurantMenuItemClass {
    private static ArrayList<JSONObject> mainMenuItems = new ArrayList<>();
    private static ArrayList<JSONObject> subMenuItems = new ArrayList<>();


    /*public RestaurantMenuItemClass(){
        mainMenuItems = new ArrayList<>();
        subMenuItems = new ArrayList<>();
    }*/

    public static void setMainMenuItems(ArrayList<JSONObject> mainMenu_Items){
        RestaurantMenuItemClass.mainMenuItems.clear();
        RestaurantMenuItemClass.mainMenuItems.addAll(mainMenu_Items);

    }

    public static void setSubMenuItems(ArrayList<JSONObject> subMenu_Items){
        RestaurantMenuItemClass.subMenuItems.clear();
        RestaurantMenuItemClass.subMenuItems.addAll(subMenu_Items);
    }

    public static void resetAllItems(){
        RestaurantMenuItemClass.mainMenuItems.clear();
        RestaurantMenuItemClass.subMenuItems.clear();
    }

    public static ArrayList<JSONObject> getMainMenuItems(){
        return RestaurantMenuItemClass.mainMenuItems;
    }

    public static ArrayList<JSONObject> getSubMenuItems(){
        return RestaurantMenuItemClass.subMenuItems;
    }

    public static void createNew() {
        mainMenuItems = new ArrayList<>();
        subMenuItems = new ArrayList<>();
    }

//    private ArrayList<JSONObject> mainMenuItems= new ArrayList<>();
//    private ArrayList<JSONObject> subMenuItems= new ArrayList<>();

    /*public RestaurantMenuItemClass() {

    }

    public ArrayList<JSONObject> getMainMenuItems() {
        return mainMenuItems;
    }

    public void setMainMenuItems(ArrayList<JSONObject> mainMenuItems) {
        this.mainMenuItems = mainMenuItems;
    }

    public ArrayList<JSONObject> getSubMenuItems() {
        return subMenuItems;
    }

    public void setSubMenuItems(ArrayList<JSONObject> subMenuItems) {
        this.subMenuItems = subMenuItems;
    }*/
}
