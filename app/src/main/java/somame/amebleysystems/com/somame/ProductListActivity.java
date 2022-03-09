package somame.amebleysystems.com.somame;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Adapter.ProdSubCatAdapter;
import Adapter.ProductAdapter;
import Model.Customer;
import Model.Product;
import Model.ProductBasketItem;
import Model.ProductSubCat;
import SharedPref.SharedPrefManager;
import Util.RetrofitApiClient2;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public class ProductListActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{
    public static RetrofitApiInterfaceLoadProductList apiInterfaceLoadProductList;
    public static RetrofitApiInterfaceProductListLoadBasketSize apiInterfaceProductListLoadBasketSize;
    /** Stores the selected item's position */
    int position = 0;
    RecyclerView recyclerView;
    ProductAdapter recyclerAdapter;
    List<Product> productList;
    List<Product> searchViewList;
    List<String> searchViewStringList;
    ProgressBar progressBar;
    private Bundle extras;
    private int subCatId;
    private String subCatName;
    String sortOption="";
    private AppCompatTextView txtSubCatName;
    SearchView searchView;
    Intent ProductIntent;
    private CharSequence[] arraySortOptions;
    int checkedSortItem=0;
    int customerId, basketSize=0;
    Customer customer;
    private Menu menu;
    MenuItem basketMenuItem;
    List<ProductBasketItem> basketItemList;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
    @Override
    protected void onResume() {
        super.onResume();
        //Refresh Menu Items
        invalidateOptionsMenu();
    }


    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Retrofit api
        apiInterfaceLoadProductList = RetrofitApiClient2.getApiClient().create(RetrofitApiInterfaceLoadProductList.class);
        apiInterfaceProductListLoadBasketSize = RetrofitApiClient2.getApiClient().create(RetrofitApiInterfaceProductListLoadBasketSize.class);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        //This array for sort items
        arraySortOptions = new CharSequence[]{"All","A to Z","Z to A","Lowest Price","Highest Price"};
        sortOption = arraySortOptions[0].toString();

        txtSubCatName = findViewById(R.id.textview_product_top_name);
        txtSubCatName.setTextColor(getResources().getColor(R.color.colorMyVioletDark));

        //GET Intent Here
        extras = getIntent().getExtras();

        //        Toolbar Declare
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_product);
        if (extras != null) {
            subCatId = extras.getInt("subCatId");
            subCatName = extras.getString("subCatName");

//            toolbar.setSubtitle();
            txtSubCatName.setText(subCatName);
        }

        toolbar.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        toolbar.setSubtitleTextColor(Color.WHITE);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        customer = SharedPrefManager.getInstance(getApplicationContext()).getCustomer();
        customerId = customer.getCustomerId();

//      Progressbar
        progressBar = findViewById(R.id.progress_product);
        progressBar.setVisibility(View.VISIBLE);

        //        More On Toolbar
        toolbar.setNavigationIcon(R.drawable.ic_action_arrow_back);
        toolbar.setNavigationOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                        finish();
                    }
                }
        );

        recyclerView = findViewById(R.id.recycler_product);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Extras Go on...
        if (subCatId > 0) {
            //Load Volley Data
            loadProductList();
        }
    }


//    -----------------------RETROFIT---------------(Load Product List)------------------RETROFIT
    private void loadProductList() {
        Call<List<Product>> call = apiInterfaceLoadProductList.loadProductList(String.valueOf(subCatId), String.valueOf(sortOption));
        call.enqueue(new Callback<List<Product>>() {

            @Override
            public void onResponse(Call<List<Product>> call, retrofit2.Response<List<Product>> response) {
                Log.e("Product_TAG", "response: "+new Gson().toJson(response.body()) );
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Log.e("onSuccess", response.body().toString());
                        List<Product> productSubCatList = response.body();
                        //Creating a String array for the List
                        loadProductListResponse(productSubCatList);

                    } else {
                        Log.e("onEmptyResponse", "Returned empty response");
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                //display error message
                Log.d("retrofit_log", "Failed! Error = " + t.getMessage());
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ProductListActivity.this, "Error. Check connection and try again", Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void loadProductListResponse(List<Product> response) {
        //getting the whole json object from the response
        Product productItem;
        productList = new ArrayList<>();
        searchViewList = new ArrayList<>();
        searchViewStringList = new ArrayList<>();

        //traversing through all the objects
        for (int i = 0; i < response.size(); i++) {
            //getting product object from json array
            productItem = response.get(i);
            String productName = productItem.getProductName();
            //Adding items to our string list
            searchViewStringList.add(productName);
            //adding the product to Subcat List
            productList.add(new Product(
                    productItem.getProductId(),
                    productItem.getSubCatId(),
                    productItem.getProductName(),
                    productItem.getUnitPrice(),
                    productItem.getProductDesc(),
                    productItem.getProductManufacturer(),
                    productItem.getProductImage()
            ));
        }
        //creating adapter object and setting it to recyclerview
        recyclerAdapter = new ProductAdapter(productList,ProductListActivity.this);
        recyclerView.setAdapter(recyclerAdapter);
        progressBar.setVisibility(View.GONE);
    }

    private void loadBasketSize(){
        Call<List<ProductBasketItem>> call = apiInterfaceProductListLoadBasketSize.getBasketSize(String.valueOf(customerId));
        call.enqueue(new Callback<List<ProductBasketItem>>() {

            @Override
            public void onResponse(Call<List<ProductBasketItem>> call, retrofit2.Response<List<ProductBasketItem>> response) {
                Log.e("Basket_TAG", "response: "+new Gson().toJson(response.body()) );
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Log.e("onSuccess", response.body().toString());
                        List<ProductBasketItem> basketItemList = response.body();
                        //Creating a String array for the List
                        loadBasketResponse(basketItemList);

                    } else {
                        Log.e("onEmptyResponse", "Returned empty response");
                    }
                }
            }

            @Override
            public void onFailure(Call<List<ProductBasketItem>> call, Throwable t) {
                //display error message
                Log.d("retrofit_log", "Failed! Error = " + t.getMessage());
            }
        });
    }
    private void loadBasketResponse(List<ProductBasketItem> response) {
        //getting the whole json object from the response
        basketItemList = new ArrayList<>();
        int addCount = 0;
//                JSONObject productCat;
        ProductBasketItem basketItem;
        //traversing through all the object
        for (int i = 0; i < response.size(); i++) {
            //getting product object from json array
            addCount++;
        }
        basketSize = addCount;
        //Converting BasketIcon to BadgeIcon
        if (basketSize > 0) {
            basketMenuItem.setIcon(BadgeCounterIconConverter.convertLayoutToImage(ProductListActivity.this,basketSize,R.drawable.ic_product_basket));
        }else{
            basketMenuItem.setIcon(getResources().getDrawable(R.drawable.ic_product_basket));
        }
    }

    //Menu On Toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_product_list, menu);
        //Setting our search item to Toolbar menu
        MenuItem action_search = menu.findItem(R.id.action_product_list_search);
        searchView = (SearchView) action_search.getActionView();
        searchView.setQueryHint("Search for product");
        searchView.setOnQueryTextListener(this);
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                //Load default list when closed
                loadProductList();
                return true;
            }
        });
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id){
            case R.id.action_product_list_sort:
                //Show Sort Dialog
                showSortDialog();
                return true;
                case R.id.action_product_list_goto_basket:
                //Open Basket
                Utilities.gotoProductBasket(getApplicationContext());
                return true;
                case R.id.action_product_list_share:
                //Show Sort Dialog
                    Utilities.shareApp(getApplicationContext());
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //Needed for refresh toolbar items
        basketMenuItem = menu.findItem(R.id.action_product_list_goto_basket);
        loadBasketSize();
        return true;
    }

    //Function to help sort list
    private void showSortDialog() {
//
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle("Sort list by");
            alertDialog.setSingleChoiceItems(arraySortOptions, checkedSortItem, new DialogInterface
                    .OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    //Set variable to selected sort item
                    sortOption = arraySortOptions[item].toString();
                    //Update sort option index
                    checkedSortItem = item;
                }
            });

            alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();// dismiss the alertbox after chose option
                    //Load Product List Again
                    loadProductList();
                }
            });
            AlertDialog alert = alertDialog.create();
            alert.show();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    //Here for Search Functionality
    @Override
    public boolean onQueryTextChange(String newText) {
        if(newText !=null && !newText.isEmpty()){

            String userInput = newText.toLowerCase();
            String item;
//        List<String> newList = new ArrayList<>();
            List<Product> lstFound = new ArrayList<>();
            for (int i = 0; i < searchViewStringList.size(); i++) {
                item = searchViewStringList.get(i);
                if (item.toLowerCase().contains(userInput)) {
                    Product foundProductItem = new Product(productList.get(i).getProductId(),
                            productList.get(i).getSubCatId(), productList.get(i).getProductName(),
                            productList.get(i).getUnitPrice(), productList.get(i).getProductDesc(),
                            productList.get(i).getProductManufacturer(), productList.get(i).getProductImage());
                    lstFound.add(foundProductItem);
                }
            }
            recyclerAdapter.updateList(lstFound);
        } else {
            //If text is null, leave adapter to original
            loadProductList();
        }
        return true;
    }

}
//Retrofit API Interface
interface RetrofitApiInterfaceLoadProductList {
    @POST(URLs.URL_PRODUCT_LIST)
    @FormUrlEncoded
    Call<List<Product>> loadProductList(@Field("subCatId") String subCatId, @Field("sortOption") String sortOption);
}
//Retrofit API Interface
interface RetrofitApiInterfaceProductListLoadBasketSize {
    @POST(URLs.URL_PRODUCT_BASKET_ITEM_LIST)
    @FormUrlEncoded
    Call<List<ProductBasketItem>> getBasketSize(@Field("customerId") String customerId);
}