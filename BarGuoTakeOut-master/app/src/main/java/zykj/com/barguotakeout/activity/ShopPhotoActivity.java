package zykj.com.barguotakeout.activity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.loopj.android.http.RequestParams;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import zykj.com.barguotakeout.R;
import zykj.com.barguotakeout.http.HttpUtil;
import zykj.com.barguotakeout.http.SimpleHttpHandler;

/**
 * lss 2015/5/15 巴国币
 */
public class ShopPhotoActivity extends CommonActivity {

    private String resid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ba_guo_bi);
        resid = getIntent().getStringExtra("resid");
    }

    public void requestData(){
        final List<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>();
        RequestParams params = new RequestParams();
        params.add("resid", resid);
        HttpUtil.getShopPhoto(new SimpleHttpHandler() {
            @Override
            public void onJsonSuccess(JSONObject json) {
                final JSONArray jsonArray = (JSONArray)JSON.parse(json.toString());
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                    HashMap<String,String> map = new HashMap<String, String>();
                    map.put("picurl",jsonObject.getString("picurl"));
                    map.put("introduction",jsonObject.getString("introduction"));
                    list.add(map);
                }

            }
        }, params);
    }
}
