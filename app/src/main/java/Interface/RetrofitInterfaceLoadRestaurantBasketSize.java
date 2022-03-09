package Interface;

import java.util.List;

import Model.RestaurantBasketItem;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import somame.amebleysystems.com.somame.URLs;

/**
 * Created by Akwasi on ${9/27/2018}.
 */
public interface RetrofitInterfaceLoadRestaurantBasketSize {
    public String url = "";
    //Retrofit API Interface
    String setUrl(String url);
    @POST(url)
    @FormUrlEncoded
    public Call<List<RestaurantBasketItem>> getBasketSize(@Field("customerId") String customerId);
}
