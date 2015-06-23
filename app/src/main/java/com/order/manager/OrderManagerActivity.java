package com.order.manager;

import android.app.ListActivity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.example.liveangel.test.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.HashMap;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.app.AlertDialog;
import android.content.DialogInterface;

import java.util.ArrayList;

public class OrderManagerActivity extends ListActivity {

    private ArrayList<HashMap<String, String>> list;
    private SimpleAdapter listAdapter;
    private String context = "http://218.94.159.104:5000/LazyGift/";

    private static Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_manager);

        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg){
                setListAdapter(listAdapter);
            }
        };

        list = new
                ArrayList<HashMap<String, String>>();

        //新建容器adapter
        listAdapter = new SimpleAdapter(this, list, R.layout.order,
                new String[]{"orderContext", "orderPrice","orderState","orderReceiveData"}, new
                int[]{R.id.orderContext, R.id.orderPrice, R.id.orderState,R.id.orderReceiveData});
        setListAdapter(listAdapter);


        // 使用Post方法向本地服务器发送数据
        OrdersThread ordersThread = new OrdersThread("test", "test");
        ordersThread.start();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_order_manager, menu);
        return true;
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        // TODO Auto-generated method stub

        super.onListItemClick(l, v, position, id);
        System.out.println("id--------------" + (id + 1));
        System.out.println("position--------------" + position);
        showInfo(position);
    }

    public void showInfo(final int position) {
        new AlertDialog.Builder(this).setTitle("我的提示").setMessage("确定已购买到该商品吗？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        list.remove(position);
                        // 通过程序我们知道删除了，但是怎么刷新ListView呢？
                        // 只需要重新设置一下adapter
                        setListAdapter(listAdapter);
                        Toast.makeText(getApplicationContext(),
                                "购买记录成功",
                                Toast.LENGTH_SHORT).show();


                    }
                }).show();
    }

    private boolean buyOrder(int id) {
        String requestIP = "http://218.94.159.104:5000/LazyGift/buyorder";
        HttpClient client = new DefaultHttpClient();
        HttpPost httpRequest = new HttpPost(requestIP);
        //String url = requestIP + "?ID="+ id + "&ADDRESS="+addr;
        //HttpGet getRequest = new HttpGet(url);
        ArrayList<NameValuePair> params;
        params = new ArrayList<>();
        params.add(new BasicNameValuePair("ID", id+""));
        try {
            httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));

            HttpResponse response = client.execute(httpRequest);

            if (response.getStatusLine().getStatusCode() == 200) {
            /*取出响应字符串*/

                String strResult = EntityUtils.toString(response.getEntity());
                //show strResult?
                Toast.makeText(getApplicationContext(),
                        strResult,
                        Toast.LENGTH_SHORT).show();
            } else {
                //处理错误。。。。
                System.out.println("Error Response: " + response.getStatusLine().toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }


    //子线程：获取订单列表
    class OrdersThread extends Thread {

        String name;
        String pwd;

        public OrdersThread(String name, String pwd) {
            this.name = name;
            this.pwd = pwd;
        }

        @Override
        public void run() {
            //用HttpClient发送请求，分为五步
            //第一步：创建HttpClient对象
            HttpClient httpClient = new DefaultHttpClient();
            //注意，下面这一行中，我之前把链接中的"test"误写成了"text"，导致调BUG调了半天没弄出来，真是浪费时间啊
            String url = context+"getorderlist";
            //第二步：创建代表请求的对象,参数是访问的服务器地址
            HttpPost httpPost = new HttpPost(url);
            try {
                //第三步：执行请求，获取服务器发还的相应对象
                HttpResponse response = httpClient.execute(httpPost);
                //第四步：检查相应的状态是否正常：检查状态码的值是200表示正常
                System.out.println("success");
                if (response.getStatusLine().getStatusCode() == 200) {
                    //第五步：从相应对象当中取出数据，放到entity当中
                    HttpEntity entity = response.getEntity();
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(entity.getContent()));
                    String result = reader.readLine();
                    System.out.println("result"+result);

                    JSONArray orders = new JSONArray(result);
                    for(int i = 0 ; i<orders.length() ; i++) {
                        JSONObject order = orders.getJSONObject(i);

                        HashMap<String, String> map1 = new HashMap<String, String>();
                        //初始化表数据

                        map1.put("orderContext", order.getString("context"));
                        map1.put("orderPrice", "￥" + order.getDouble("price"));
                        map1.put("orderState", order.getString("state"));
                        map1.put("orderReceiveData", order.getString("createTime"));

                        list.add(map1);
                    }
                    mHandler.sendEmptyMessageDelayed(0,0);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
