package Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import Model.MyOrder;
import Model.MyOrderDetails;
import Model.MyOrderDetailsItem;

import Util.RetrofitApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import somame.amebleysystems.com.somame.PaymentActivity;
import somame.amebleysystems.com.somame.R;
import somame.amebleysystems.com.somame.URLs;

/**
 * Created by Akwasi on ${9/27/2018}.
 */

public class MyOrderAdapter extends RecyclerView.Adapter<MyOrderAdapter.ViewHolder> {
    public static RetrofitApiInterfaceOrderDetails apiInterfaceOrderDetails;
    private Context context;
    private List<MyOrder> myOrderList;
    private List<MyOrderDetailsItem> myOrderDetailsItemList;

    public MyOrderAdapter(Context context, List<MyOrder> myOrderList) {
        this.context = context;
        this.myOrderList = myOrderList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_orders_item,parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MyOrder myOrderItem = myOrderList.get(position);
        holder.textViewOrderNo.setText(new StringBuilder().append(": ").append(myOrderItem.getOrderNo()));

        String payStat = myOrderItem.getPaymentStatus();
        holder.textViewPaymentStatus.setTextColor((payStat.equalsIgnoreCase("paid"))?Color.GREEN:Color.RED);
        holder.textViewPaymentStatus.setText(payStat);

        holder.textViewOrderStatus.setText(new StringBuilder().append(": ").append(myOrderItem.getOrderStatus()));

        holder.textViewOrderDate.setText((myOrderItem.getOrderDate()));

//        Setting Currency for Price Text
        Locale locale = new Locale("en", "GH");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
        holder.textViewOrderTotal.setText(fmt.format((myOrderItem.getOrderTotal())));

        //Tweaking Width and Heights
        context.getTheme().resolveAttribute(android.R.attr.actionBarSize,holder.typedValue,true);
        int actionBarSize = context.getResources().getDimensionPixelSize(holder.typedValue.resourceId);
//        holder.imageViewDelete.getLayoutParams().width = (actionBarSize/2);
//        holder.imageViewDelete.getLayoutParams().height = (actionBarSize/2);
        holder.imageViewReOrder.getLayoutParams().width = (actionBarSize/2);
        holder.imageViewReOrder.getLayoutParams().height = (actionBarSize/2);
    }

    @Override
    public int getItemCount() {
        return myOrderList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private AppCompatTextView textViewOrderNo;
        private AppCompatTextView textViewOrderTotal;
        private AppCompatTextView textViewPaymentStatus;
        private AppCompatTextView textViewOrderStatus;
        private AppCompatTextView textViewOrderDate;
        private ImageView imageViewReOrder;
//        private ImageView imageViewDelete;
        TypedValue typedValue;
        public ViewHolder(View itemView) {
            super(itemView);
            textViewOrderNo = itemView.findViewById(R.id.textview_my_orders_item_order_no);
            textViewOrderTotal = itemView.findViewById(R.id.textview_my_orders_item_order_total);
            textViewPaymentStatus = itemView.findViewById(R.id.textview_my_orders_item_payment_status);
            textViewOrderStatus = itemView.findViewById(R.id.textview_my_orders_item_order_status);
            textViewOrderDate = itemView.findViewById(R.id.textview_my_orders_item_order_date);
            imageViewReOrder = itemView.findViewById(R.id.imageView_my_orders_item_details);
//            imageViewDelete = itemView.findViewById(R.id.imageView_my_orders_item_delete);
            typedValue = new TypedValue();
            //            Set OnClickListener

//            imageViewDelete.setOnClickListener(this);
            imageViewReOrder.setOnClickListener(this);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Go to next screen/Checkout
                    int position = getAdapterPosition();
                    MyOrder myOrder = myOrderList.get(position);
                    String orderNo = myOrder.getOrderNo();
                    String paymentStat = myOrder.getPaymentStatus();
                    //Check, if Unpaid, Go to Payment Screen, else, Checkout Screen
                    if (paymentStat.equalsIgnoreCase("unpaid")){
                        Intent myOrderIntent = new Intent(context, PaymentActivity.class);
                        myOrderIntent.putExtra("OrderNumber",myOrder.getOrderNo());
                        myOrderIntent.putExtra("CustomerName",myOrder.getCustomerName());
                        myOrderIntent.putExtra("momoNumber",myOrder.getMomoNumber());
                        myOrderIntent.putExtra("TotalCost",myOrder.getOrderTotal());
                        context.startActivity(myOrderIntent);
                    }else{
                        //Show order details if already paid
                        showOrderDetails(orderNo);
                    }
                }
            });
        }

        //Click Action
        @Override
        public void onClick(View v) {
            int id = v.getId();
            int position = getAdapterPosition();
            MyOrder myOrder = myOrderList.get(position);
            String orderNo = myOrder.getOrderNo();
            //                case R.id.imageView_my_orders_item_delete:
            //                    deleteOrder(orderNo, position);
            //                    break;
            if (id == R.id.imageView_my_orders_item_details) {
                showOrderDetails(orderNo);
            }
        }
    }

    //Function to Display details of selected order
    private void showOrderDetails(final String orderNo) {
        //Retrofit api
        apiInterfaceOrderDetails = RetrofitApiClient.getApiClient().create(RetrofitApiInterfaceOrderDetails.class);

        //            ------------------------RETROFIT----------ORDER DETAILS--------------------------------
        /*
         * Creating a String Request
         * */

        //Pass Order No.
        Call<String> call =apiInterfaceOrderDetails.getOrderDetails(orderNo);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, retrofit2.Response<String> response) {

                Log.e("OrderDetails_Response: ", response.body());
                //Toast.makeText()
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Log.e("onSuccess", response.body());

                        String jsonResponse = response.body();
                        loadOrderDetailResponse(jsonResponse);

                    } else {
                        Log.e("onEmptyResponse", "Returned empty response");
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                //display error message
                Log.d("retrofit_log", "Failed! Error = " + t.getMessage());
            }
        });


    }
    //This function handles the received Retrofit response
    private void loadOrderDetailResponse(String response) {
        try {
            //converting response to json object
            JSONObject obj = new JSONObject(response);
            myOrderDetailsItemList = new ArrayList<>();
            //getting the list from the response
            JSONArray array = obj.getJSONArray("product_list");
            //traversing through all the objects
            for (int i = 0; i < array.length(); i++) {
                JSONObject item = array.getJSONObject(i);
                myOrderDetailsItemList.add(new MyOrderDetailsItem(
                        item.getString("product_id"),
                        item.getString("order_no"),
                        item.getString("product_name"),
                        item.getDouble("product_price"),
                        item.getString("product_quantity")
                ));
            }


//            //If Success
            if(obj.optString("success").equals("true")){

                //getting the user from the response
                JSONObject userJson = obj.getJSONObject("user");
                //creating a new MyOrderDetails object
                MyOrderDetails myOrderDetails = new MyOrderDetails(
                        userJson.getString("order_no"),
                        userJson.getString("customer_name"),
                        userJson.getString("address"),
                        userJson.getString("gh_post_address"),
                        userJson.getString("delivery_location"),
                        userJson.getString("delivery_period"),
                        userJson.getString("payment_method"),
                        userJson.getString("momo_number"),
                        userJson.getString("payment_status"),
                        userJson.getString("order_status"),
                        userJson.getDouble("subtotal"),
                        userJson.getDouble("order_total"),
                        myOrderDetailsItemList
                );
                showOrderDetailsDialog(myOrderDetails.getOrderNo(),myOrderDetails.getCustomerName(),myOrderDetails.getDeliveryLocation(),myOrderDetails.getDeliveryPeriod(),myOrderDetails.getOrderTotal(),myOrderDetails.getPaymentStatus(),myOrderDetails.getOrderStatus(),myOrderDetailsItemList);

            }else {

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //Create Terms_Conditions AlertDialog TextView
    private void showOrderDetailsDialog(String orderNo, String customer, String deliveryLoc, String deliveryPeriod, Double orderTotal, String paymentStat, String orderStat, List<MyOrderDetailsItem> myOrderDetailsItemsList) {
//      Use layout created in xml and inflate as our AlertDialog EditText
        View view = LayoutInflater.from(context).inflate(R.layout.layout_my_orders_order_details_dialog, null);

        final TextView textViewName = view.findViewById(R.id.dialog_textView_my_order_details_customer_name);
        final TextView textViewOrderNo = view.findViewById(R.id.dialog_textView_my_order_details_order_no);
        final TextView textViewDeliveryLoc = view.findViewById(R.id.dialog_textView_my_order_details_delivery_location);
        final TextView textViewDeliveryPeriod = view.findViewById(R.id.dialog_textView_my_order_details_delivery_period);
        final TextView textViewOrderTotal = view.findViewById(R.id.dialog_textView_my_order_details_order_total);
        final TextView textViewPaymentStatus = view.findViewById(R.id.dialog_textView_my_order_details_payment_status);
        final TextView textViewOrderStatus = view.findViewById(R.id.dialog_textView_my_order_details_order_status);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Order Details");
        builder.setView(view);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
            }
        });

        //Get Text and Set to View
        //        Setting Currency for Price Text
        Locale locale = new Locale("en", "GH");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);

        textViewOrderNo.setText(orderNo);
        textViewName.setText(customer);
        textViewDeliveryLoc.setText(deliveryLoc);
        textViewDeliveryPeriod.setText(deliveryPeriod);
        textViewOrderTotal.setText(fmt.format(orderTotal));
        textViewPaymentStatus.setText(paymentStat);
        textViewOrderStatus.setText(orderStat);

        //Get Table Layout
        // Get TableLayout object in layout xml.
        final TableLayout tableLayout = (TableLayout)view.findViewById(R.id.table_layout2_order_detail_dialog);

        //Create table rows dynamically
        for (int i=0; i<myOrderDetailsItemsList.size(); i++){
            TableRow tableRow = new TableRow(context);
            // Set new table row layout parameters.
            TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
            tableRow.setLayoutParams(layoutParams);
            // TEXTVIEW
            TextView textView = new TextView(context);

            textView.setText(myOrderDetailsItemsList.get(i).getProductName());
            textView.setTextColor(Color.WHITE);
            tableRow.addView(textView,0);
            // TEXTVIEW
            textView = new TextView(context);
            textView.setText("\t\t\t x".concat(myOrderDetailsItemsList.get(i).getProductQuantity()));
            textView.setTextColor(Color.WHITE);
            tableRow.addView(textView,1);
            // TEXTVIEW
            textView = new TextView(context);
            textView.setText("\t\t\t".concat(fmt.format(myOrderDetailsItemsList.get(i).getPrice())));
            textView.setTextColor(Color.WHITE);
            tableRow.addView(textView,2);
            //Add rows to table
            tableLayout.addView(tableRow);
        }

        final AlertDialog dialog = builder.create();
        dialog.show();
        //Overriding the handler immediately after show is probably a better approach than OnShowListener as described below
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dialog.dismiss();
            }
        });

    }

}
//Retrofit API Interface
interface RetrofitApiInterfaceOrderDetails {
    @POST(URLs.URL_ORDER_DETAILS_FROM_MY_ORDERS)
    @FormUrlEncoded
    Call<String> getOrderDetails(@Field("orderNo") String orderNo);
}