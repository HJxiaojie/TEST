package com.place.order;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.liveangel.test.R;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ConfirmOrderActivity extends ActionBarActivity {
    private Button confirmButton;
    private EditText goodsName;
    private EditText totalPrice;
    private EditText deliveryTime;
    private EditText remarks;
    private EditText destination;
    Map<String,String> orderSettings;
    private String requestIP = "http://10.0.3.2:8080/LazyGift/confirmOrderSubmit";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_order);
        confirmButton = (Button) findViewById(R.id.confirmButton);
        goodsName = (EditText) findViewById(R.id.goodsName);
        totalPrice = (EditText) findViewById(R.id.totalPrice);
        deliveryTime = (EditText) findViewById(R.id.deliveryTime);
        remarks = (EditText) findViewById(R.id.remarks);
        destination = (EditText) findViewById(R.id.destination);
        orderSettings = new HashMap<String,String>();
        confirmButton.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View view){
                orderSettings.put("goodsName",goodsName.getText().toString());
                orderSettings.put("totalPrice",totalPrice.getText().toString());
                orderSettings.put("deliveryTime",deliveryTime.getText().toString());
                orderSettings.put("remarks",remarks.getText().toString());
                orderSettings.put("destination",destination.getText().toString());
                new SubmitOrderTask().execute();


            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_confirm_order, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class SubmitOrderTask extends AsyncTask<String,Void,Object> {
        @Override
        protected Object doInBackground(String... params) {
            submitOrderInfo(orderSettings);

            return null;
        }

        private void submitOrderInfo(Map<String,String> inputs){
            System.out.println("提交订单信息" +
                    "");
            HttpClient client = new DefaultHttpClient();
            HttpPost httpRequest = new HttpPost(requestIP);
            ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
            for(String key :inputs.keySet()){
                params.add(new BasicNameValuePair(key,inputs.get(key)));
            }
            try {
                httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
                HttpResponse response = client.execute(httpRequest);
                if(response.getStatusLine().getStatusCode() == 200)
                {
            /*取出响应字符串*/
                    String strResult = EntityUtils.toString(response.getEntity());
                    //show strResult?
                    System.out.println(strResult);
                }
                else
                {
                    //处理错误。。。。
                    System.out.println("Error Response: " + response.getStatusLine().toString());
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
}
