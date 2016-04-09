package com.example.administrator.weather;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.administrator.weather.Bean.FutureWeatherBean;
import com.example.administrator.weather.Bean.HoursWeatherBean;
import com.example.administrator.weather.Bean.PMBean;
import com.example.administrator.weather.Bean.WeatherBean;
import com.example.administrator.weather.Util.HttpCallbackListener;
import com.example.administrator.weather.Util.HttpUtil;
import com.example.administrator.weather.service.WeatherService;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class WeatherActivity extends Activity {
    private Context mContext;
    private WeatherService mService;

    private PullToRefreshScrollView mPullRefreshScrollView;
    private ScrollView mScrollView;
    private TextView tv_city,     //城市
            tv_release,           //发布时间
            tv_now_weather,       //天气
            tv_today_temp,        //湿度
            tv_now_temp,          //当前温度
            tv_aqi,               //空气质量指数
            tv_quality,           //空气质量
            tv_next_three,        //3小时
            tv_next_six,          //6小时
            tv_next_nine,         //9小时
            tv_next_twelve,       //12小时
            tv_next_fifteen,      //15小时
            tv_next_three_temp,   //3小时温度
            tv_next_six_temp,     //6小时温度
            tv_next_nine_temp,    //9小时温度
            tv_next_twelve_temp,  //12小时温度
            tv_next_fifteen_temp, //15小时温度
            tv_today_temp_a,      //今天温度a
            tv_today_temp_b,      //今天温度b
            tv_tomorrow,          //明天
            tv_tomorrow_temp_a,   //明天温度a
            tv_tomorrow_temp_b,   //明天温度b
            tv_thirdday,          //第三天
            tv_thirdday_temp_a,   //第三天温度a
            tv_thirdday_temp_b,   //第三天温度b
            tv_fourthday,         //第四天
            tv_fourthday_temp_a,  //第四天温度a
            tv_fourthday_temp_b,  //第四天温度b
            tv_humidity,          //湿度
            tv_wind, tv_uv_index,  //紫外线指数
            tv_dressing_index;    //穿衣指数

    private ImageView iv_now_weather,//现在
            iv_next_three,        //3小时
            iv_next_six,          //6小时
            iv_next_nine,         //9小时
            iv_next_twelve,       //12小时
            iv_next_fifteen,      //15小时
            iv_today_weather,     //今天
            iv_tomorrow_weather,  //明天
            iv_thirdday_weather,  //第三天
            iv_fourthday_weather; //第四天

    private boolean isRunning = false;
    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(getWindow().FEATURE_NO_TITLE);
        setContentView(R.layout.activity_weather);
        mContext = this;
        init();
        //initService();
        getCityWeather();
    }

    private void getCityWeather() {
        if (isRunning) {
            return;
        }
        isRunning = true;
        count = 0;

        //此处以返回json格式数据示例,所以format=2,以根据城市名称为例,cityName传入中文
        String cityName = "上海";
        try {
            //手机自带浏览器不能自动将中文参数编码，需要进行转码，
            // 否则URL中城市名字为汉字在浏览器中访问数据返回正常，手机上返回查询不到该城市
            cityName = URLEncoder.encode(cityName + "", "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //实时和未来3天天气数据
        String url =
                "http://v.juhe.cn/weather/index?cityname=" + cityName + "&key=e72df95d38ce64d6395055944b9495b2";
        HttpUtil.sendHttpRequest(url, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                //Log.d("response", response);
                count++;
                WeatherBean weatherBean = parseWeather(response);
                if (weatherBean != null) {
                    setWeatherViews(weatherBean);
                }
                if (count == 3) {
                    mPullRefreshScrollView.onRefreshComplete();
                    isRunning = true;
                }
            }

            @Override
            public void onError(Exception e) {
                Log.d("url", e.toString());
            }
        });

        //未来间隔3小时数据
        String url2 = "http://v.juhe.cn/weather/forecast3h.php?cityname=" + cityName + "&key=e72df95d38ce64d6395055944b9495b2";
        HttpUtil.sendHttpRequest(url2, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                // Log.d("response", response);
                count++;
                List<HoursWeatherBean> list = parseForcast3h(response);
                if (list != null && list.size() >= 5) {
                    setHourViews(list);
                }
                if (count == 3) {
                    mPullRefreshScrollView.onRefreshComplete();
                    isRunning = true;
                }
            }

            @Override
            public void onError(Exception e) {
                Log.d("url2", e.toString());
            }
        });

        //空气质量
        String url3 = "http://web.juhe.cn:8080/environment/air/cityair?city=" + cityName + "&key=613c563685da986f1eb4a14c2a27d764";
        HttpUtil.sendHttpRequest(url3, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                //Log.d("response",response);
                count++;
                PMBean bean = parsePM(response);
                if (bean != null) {
                    setPMView(bean);
                }
                if (count == 3) {
                    mPullRefreshScrollView.onRefreshComplete();
                    isRunning = true;
                }
            }

            @Override
            public void onError(Exception e) {
                Log.d("url3", e.toString());
            }
        });
    }

    //解析城市天气数据
    private WeatherBean parseWeather(String result) {
        WeatherBean bean = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        if (result != null) {
            try {
                JSONObject obj = new JSONObject(result);
                /*获取返回状态码*/
                result = obj.getString("resultcode");
                /*如果状态码是200说明返回数据成功*/
                if (result != null && result.equals("200")) {
                    bean = new WeatherBean();
                    //result为object类型
                    JSONObject resultJson = obj.getJSONObject("result");
                    //today为object类型
                    JSONObject todayJson = resultJson.getJSONObject("today");
                    //以下是string类型
                    bean.setCity(todayJson.getString("city"));
                    bean.setUv_index(todayJson.getString("uv_index"));
                    bean.setTemp(todayJson.getString("temperature"));
                    bean.setWether_str(todayJson.getString("weather"));
                    bean.setWeather_id(todayJson.getJSONObject("weather_id").getString("fa"));
                    bean.setDressing_index(todayJson.getString("dressing_index"));

                    //sk
                    JSONObject skJson = resultJson.getJSONObject("sk");
                    bean.setWind(skJson.getString("wind_direction"));
                    bean.setNow_temp(skJson.getString("temp"));
                    bean.setRelease(skJson.getString("time"));
                    bean.setHumidity(skJson.getString("humidity"));

                    //future
                    Date date = new Date(System.currentTimeMillis());
                    //future为object类型
                    JSONObject future = resultJson.getJSONObject("future");
                    //使用迭代器遍历future节点
                    Iterator iterator = future.keys();
                    String key = null;
                    String value = null;
                    List<FutureWeatherBean> futureList = new ArrayList<FutureWeatherBean>();
                    while (iterator.hasNext()) {
                        key = (String) iterator.next();
                        value = future.getString(key);
                        JSONObject futureJson = new JSONObject(value);
                        //   Log.d("futureJson",futureJson.getString("temperature"));
                        FutureWeatherBean futureBean = new FutureWeatherBean();
                        try {
                            Date datef = sdf.parse(futureJson.getString("date"));
                            if (!datef.after(date)) {
                                continue;
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        futureBean.setTemp(futureJson.getString("temperature"));
                        futureBean.setWeek(futureJson.getString("week"));
                        futureBean.setWeather_id(futureJson.getJSONObject("weather_id").getString("fa"));

                        futureList.add(futureBean);
                        if (futureList.size() == 3) {
                            break;
                        }
                    }
                    bean.setFutureWeatherBeanList(futureList);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return bean;
    }

    //解析未来间隔3小时数据
    private List<HoursWeatherBean> parseForcast3h(String result) {
        List<HoursWeatherBean> list = null;
        if (result != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
            Date date = new Date(System.currentTimeMillis());
            try {
                JSONObject obj = new JSONObject(result);
                int code = obj.getInt("resultcode");
                int error_code = obj.getInt("error_code");
                if (error_code == 0 && code == 200) {
                    list = new ArrayList<HoursWeatherBean>();
                    JSONArray resultArray = obj.getJSONArray("result");
                    for (int i = 0; i < resultArray.length(); i++) {
                        JSONObject hourJson = resultArray.getJSONObject(i);
                        Date hDate = sdf.parse(hourJson.getString("sfdate"));
                        if (!hDate.after(date)) {
                            continue;
                        }
                        HoursWeatherBean bean = new HoursWeatherBean();
                        bean.setWeather_id(hourJson.getString("weatherid"));
                        bean.setTemp(hourJson.getString("temp1"));
                        //Log.d("temp1", bean.getTemp());
                        Calendar c = Calendar.getInstance();
                        c.setTime(hDate);
                        bean.setTime(c.get(Calendar.HOUR_OF_DAY) + "");
                        list.add(bean);
                        if (list.size() == 5) {
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    //解析空气质量
    private PMBean parsePM(String result) {
        PMBean bean = null;
        try {
            JSONObject obj = new JSONObject(result);
            int code = obj.getInt("resultcode");
            int error_code = obj.getInt("error_code");
            if (error_code == 0 && code == 200) {
                bean = new PMBean();
                JSONObject pmJson = obj.getJSONArray("result").getJSONObject(0).getJSONObject("citynow");
                bean.setAqi(pmJson.getString("AQI"));
                bean.setQuality(pmJson.getString("quality"));
                Log.d("quality", bean.getQuality());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bean;
    }

    //填充实时天气
    private void setWeatherViews(WeatherBean bean) {
        tv_city.setText(bean.getCity());
        tv_release.setText(bean.getRelease() + "发布");
        tv_now_weather.setText(bean.getWether_str());
        String[] tempArr = bean.getTemp().split("~");
        String temp_str_a = tempArr[1].substring(0, tempArr[1].indexOf("℃"));
        String temp_str_b = tempArr[0].substring(0, tempArr[0].indexOf("℃"));
        //温度：14℃~23℃   ↑↓ °
        tv_today_temp.setText("↑" + temp_str_a + "° ↓" + temp_str_b + "°");
        tv_now_temp.setText(bean.getNow_temp() + "°");

        tv_today_temp_a.setText(temp_str_a + "");
        tv_today_temp_b.setText(temp_str_b + "");
        List<FutureWeatherBean> futureList = bean.getFutureWeatherBeanList();
        if (futureList.size() == 3) {
            setFutureViews(tv_tomorrow, iv_tomorrow_weather, tv_tomorrow_temp_a, tv_tomorrow_temp_b, futureList.get(0));
            setFutureViews(tv_thirdday, iv_thirdday_weather, tv_thirdday_temp_a, tv_thirdday_temp_b, futureList.get(1));
            setFutureViews(tv_fourthday, iv_fourthday_weather, tv_fourthday_temp_a, tv_fourthday_temp_b, futureList.get(2));
        }

        //区分白天和夜晚图标
        String prefixStr = null;
        Calendar c = Calendar.getInstance();
        int time = c.get(Calendar.HOUR_OF_DAY);
        if (time >= 6 && time < 18) {
            prefixStr = "d";
        } else {
            prefixStr = "n";
        }
        iv_now_weather.setImageResource(getResources().getIdentifier(prefixStr + bean.getWeather_id(), "drawable", "com.example.administrator.weather"));

        tv_humidity.setText(bean.getHumidity());
        tv_dressing_index.setText(bean.getDressing_index());
        tv_uv_index.setText(bean.getUv_index());
        tv_wind.setText(bean.getWind());
    }

    //填充未来天气数据
    private void setFutureViews(TextView tv_week, ImageView iv_weather, TextView tv_temp_a, TextView tv_temp_b, FutureWeatherBean futureWeatherBean) {
        tv_week.setText(futureWeatherBean.getWeek());
        iv_weather.setImageResource(getResources().getIdentifier("d" + futureWeatherBean.getWeather_id(), "drawable", "com.example.administrator.weather"));
        String[] tempArr = futureWeatherBean.getTemp().split("~");
        String temp_str_a = tempArr[1].substring(0, tempArr[1].indexOf("℃"));
        String temp_str_b = tempArr[0].substring(0, tempArr[0].indexOf("℃"));
        tv_temp_a.setText(temp_str_a);
        tv_temp_b.setText(temp_str_b);
    }

    //填充未来5小时数据
    private void setHourViews(List<HoursWeatherBean> list) {
        setFutureHourViews(tv_next_three, iv_next_three, tv_next_three_temp, list.get(0));
        setFutureHourViews(tv_next_six, iv_next_six, tv_next_six_temp, list.get(1));
        setFutureHourViews(tv_next_nine, iv_next_nine, tv_next_nine_temp, list.get(2));
        setFutureHourViews(tv_next_twelve, iv_next_twelve, tv_next_twelve_temp, list.get(3));
        setFutureHourViews(tv_next_fifteen, iv_next_fifteen, tv_next_fifteen_temp, list.get(4));
    }

    //填充未来间隔3小时数据
    private void setFutureHourViews(TextView tv_hour, ImageView iv_weather, TextView tv_temp, HoursWeatherBean bean) {
        //区分白天和夜晚图标
        String prefixStr = null;
        int time = Integer.valueOf(bean.getTime());
        if (time >= 6 && time < 18) {
            prefixStr = "d";
        } else {
            prefixStr = "n";
        }
        tv_hour.setText(bean.getTime() + "时");
        iv_weather.setImageResource(getResources().getIdentifier(
                prefixStr + bean.getWeather_id(), "drawable", "com.example.administrator.weather"));
        tv_temp.setText(bean.getTemp() + "°");
    }

    //填充PM数据
    private void setPMView(PMBean bean) {
        tv_aqi.setText(bean.getAqi());
        tv_quality.setText(bean.getQuality());
    }

    //启动服务
    private void initService() {
        Intent intent = new Intent(mContext, WeatherService.class);
        startService(intent);
        bindService(intent, conn, Context.BIND_AUTO_CREATE);
    }

    ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = ((WeatherService.WeatherServiceBinder) service).getService();
            mService.test();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    //初始化控件
    private void init() {
        //TextView
        tv_city = (TextView) findViewById(R.id.tv_city);
        tv_release = (TextView) findViewById(R.id.tv_release);
        tv_now_weather = (TextView) findViewById(R.id.tv_now_weather);
        tv_today_temp = (TextView) findViewById(R.id.tv_today_temp);
        tv_now_temp = (TextView) findViewById(R.id.tv_now_temp);
        tv_aqi = (TextView) findViewById(R.id.tv_aqi);
        tv_quality = (TextView) findViewById(R.id.tv_quality);
        tv_next_three = (TextView) findViewById(R.id.tv_next_three);
        tv_next_six = (TextView) findViewById(R.id.tv_next_six);
        tv_next_nine = (TextView) findViewById(R.id.tv_next_nine);
        tv_next_twelve = (TextView) findViewById(R.id.tv_next_twelve);
        tv_next_fifteen = (TextView) findViewById(R.id.tv_next_fifteen);
        tv_next_three_temp = (TextView) findViewById(R.id.temp_next_three);
        tv_next_six_temp = (TextView) findViewById(R.id.temp_next_six);
        tv_next_nine_temp = (TextView) findViewById(R.id.temp_next_nine);
        tv_next_twelve_temp = (TextView) findViewById(R.id.temp_next_twelve);
        tv_next_fifteen_temp = (TextView) findViewById(R.id.temp_next_fifteen);
        tv_today_temp_a = (TextView) findViewById(R.id.today_temp_a);
        tv_today_temp_b = (TextView) findViewById(R.id.today_temp_b);
        tv_tomorrow = (TextView) findViewById(R.id.tomorrow);
        tv_tomorrow_temp_a = (TextView) findViewById(R.id.tomorrow_temp_a);
        tv_tomorrow_temp_b = (TextView) findViewById(R.id.tomorrow_temp_b);
        tv_thirdday = (TextView) findViewById(R.id.third);
        tv_thirdday_temp_a = (TextView) findViewById(R.id.third_temp_a);
        tv_thirdday_temp_b = (TextView) findViewById(R.id.third_temp_b);
        tv_fourthday = (TextView) findViewById(R.id.forth);
        tv_fourthday_temp_a = (TextView) findViewById(R.id.forth_temp_a);
        tv_fourthday_temp_b = (TextView) findViewById(R.id.forth_temp_b);
        tv_humidity = (TextView) findViewById(R.id.humidity);
        tv_wind = (TextView) findViewById(R.id.wind);
        tv_uv_index = (TextView) findViewById(R.id.uv_index);
        tv_dressing_index = (TextView) findViewById(R.id.dressing_index);

        //ImageView
        iv_now_weather = (ImageView) findViewById(R.id.iv_now_weather);
        iv_next_three = (ImageView) findViewById(R.id.iv_next_three);
        iv_next_six = (ImageView) findViewById(R.id.iv_next_six);
        iv_next_nine = (ImageView) findViewById(R.id.iv_next_nine);
        iv_next_twelve = (ImageView) findViewById(R.id.iv_next_twelve);
        iv_next_fifteen = (ImageView) findViewById(R.id.iv_next_fifteen);
        iv_today_weather = (ImageView) findViewById(R.id.today_weather);
        iv_tomorrow_weather = (ImageView) findViewById(R.id.today_weather);
        iv_thirdday_weather = (ImageView) findViewById(R.id.third_weather);
        iv_fourthday_weather = (ImageView) findViewById(R.id.forth_weather);

        //下拉刷新
        mPullRefreshScrollView = (PullToRefreshScrollView) findViewById(R.id.pull_refresh_scrollview);
        mPullRefreshScrollView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ScrollView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
                new GetDataTask().execute();
            }
        });
        mScrollView = mPullRefreshScrollView.getRefreshableView();
    }

    private class GetDataTask extends AsyncTask<Void, Void, String[]> {
        @Override
        protected String[] doInBackground(Void... params) {
            try {
                Thread.sleep(4000);
                getCityWeather();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String[] strings) {
            mPullRefreshScrollView.onRefreshComplete();
            super.onPostExecute(strings);
        }
    }

    @Override
    protected void onDestroy() {
        unbindService(conn);
        super.onDestroy();
    }
}
