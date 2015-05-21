package zykj.com.barguotakeout.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

import zykj.com.barguotakeout.Mapplication;
import zykj.com.barguotakeout.R;
import zykj.com.barguotakeout.Utils.ToastUTil;
import zykj.com.barguotakeout.http.HttpErrorHandler;
import zykj.com.barguotakeout.http.HttpUtil;

/**
 * Created by ss on 15-4-17.
 */
public class RecommendActivity extends CommonActivity implements View.OnClickListener{

    private TextView luckText;
    private Button luckSubmit;
    private String userid;

    private AsyncHttpResponseHandler myHttpResponseHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTag(RecommendActivity.class.getSimpleName());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_luck);
        initView();
    }

    private void initView() {
        luckText = (TextView) findViewById(R.id.luck_tv_content);
        luckSubmit = (Button) findViewById(R.id.btn_luck_submit);

        userid = Mapplication.getModel().getUserid();
        if(TextUtils.isEmpty(userid))
            luckSubmit.setBackgroundResource(R.drawable.gray_angle_shape);
        luckSubmit.setOnClickListener(this);
    }

    @Override
    public void onClick(View view){
        if(TextUtils.isEmpty(userid)){
            //还没有登录 弹出对话框立即登录
            ToastUTil.showToastText(RecommendActivity.this, "提醒", "请先登录!", "确定");
            return;
        }else{
            final RequestParams params=new RequestParams();
            params.add("userid", userid);
            HttpUtil.drawlottery(new HttpErrorHandler() {
                @Override
                public void onRecevieSuccess(JSONObject json) {
                    ToastUTil.showToastText(RecommendActivity.this, "抽奖详情", "你的抽奖号码为123456，抽奖结果将于5月31日公布，请注意查收。", "确定");
                }

                @Override
                public void onRecevieFailed(String status, JSONObject json) {
                    ToastUTil.showToastText(RecommendActivity.this, "提醒", json.getString("msg"), "确定");
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                }
            }, params);
        }
    }

    public void back(View v){ finish(); }

}
