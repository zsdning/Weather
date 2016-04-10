package com.example.administrator.weather;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.administrator.weather.Util.HttpCallbackListener;
import com.example.administrator.weather.Util.HttpUtil;
import com.example.administrator.weather.adapter.CityListAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CityActivity extends Activity {
    private ListView lv_city;
    List<String> list = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(getWindow().FEATURE_NO_TITLE);
        setContentView(R.layout.activity_city);
        initViews();
        getCities();
    }

    private void initViews() {
        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        lv_city = (ListView) findViewById(R.id.lv_city);
    }

    private void getCities() {
        String url4 = "http://v.juhe.cn/weather/citys?key=e72df95d38ce64d6395055944b9495b2";
        HttpUtil.sendHttpRequest(url4, new HttpCallbackListener() {
            @Override
            public void onFinish(final String response) {
                Log.d("response", response);
                //涉及到UI，需要重新回到主线程
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        parseCities(response);
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                Log.d("onError", e.toString());
            }
        });
    }

    //对返回的城市数据进行解析
    private void parseCities(String result) {
        if (result != null) {
            try {
                JSONObject obj = new JSONObject(result);
                /*获取返回状态码*/
                result = obj.getString("resultcode");
                /*如果状态码是200说明返回数据成功*/
                if (result != null && result.equals("200")) {
                    Set<String> citySet = new HashSet<String>();
                    //解析JSON数组
                    JSONArray resultArray = obj.getJSONArray("result");
                    for (int i = 0; i < resultArray.length(); i++) {
                        String city = resultArray.getJSONObject(i).getString("city");
                        //过滤重复的数据
                        citySet.add(city);
                    }
                    list.addAll(citySet);
                    //Log.d("list", list.toString());
                    CityListAdapter adapter = new CityListAdapter(CityActivity.this, list);
                    //下面是一个测试
                    // ArrayAdapter<String> adapter = new ArrayAdapter<String>(CityActivity.this,android.R.layout.simple_list_item_1,list);
                    lv_city.setAdapter(adapter);
                    lv_city.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent intent = new Intent();
                            intent.putExtra("city", list.get(position));
                            setResult(1, intent);
                            finish();
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
