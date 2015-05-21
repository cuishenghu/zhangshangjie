package zykj.com.barguotakeout.fragment;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.loopj.android.http.RequestParams;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import zykj.com.barguotakeout.Mapplication;
import zykj.com.barguotakeout.R;
import zykj.com.barguotakeout.adapter.CategoryAdapter;
import zykj.com.barguotakeout.adapter.RanksAdapter;
import zykj.com.barguotakeout.adapter.RestaurantAdapter;
import zykj.com.barguotakeout.http.EntityHandler;
import zykj.com.barguotakeout.http.HttpUtil;
import zykj.com.barguotakeout.http.SimpleHttpHandler;
import zykj.com.barguotakeout.model.BaGuoRank;
import zykj.com.barguotakeout.model.ResturantModel;

/**
 * Created by ss on 15-4-22.
 */
public class RankList extends CommonFragment implements View.OnClickListener{
    private static Integer category=1;//
    private static Integer order=0;//排序方式 默认为0 全部

    private RelativeLayout btn_paixu;
    private RelativeLayout btn_fenlei;
    private Animation myAnimation;

    private int page=1;
    private int num=7;
    private String oneid,twoid,threeid;
    private PullToRefreshListView ranklist;
    private RestaurantAdapter adapter;
    private RanksAdapter ranksAdapter;
    private Button btn_recommend;//我要推荐
    private RadioGroup.LayoutParams mRadioParams;
    private List<ResturantModel> list;

    private PopupWindow popupWindow;
    private ListView publicList;
    private RadioGroup oneCategory,twoCategory,threeCategory;
    private ScrollView left_show;
    private String[] paixulist;
    private String[] resid;
    private ArrayList<HashMap<String, String>> listItem;

    private int sWrapContent = LinearLayout.LayoutParams.WRAP_CONTENT;
    private int sFileParent = LinearLayout.LayoutParams.MATCH_PARENT;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_rank,container,false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initPop();
    }

    private void initPop() {
        paixulist = new String[]{"默认排序","点赞最多","评分最高","认证最高","距离最近"};
        resid = new String[]{R.mipmap.paixu+"",R.mipmap.dianzan+"",R.mipmap.pingfen+"",R.mipmap.renzheng+"",R.mipmap.juli+""};
        initData();
        View view=LayoutInflater.from(getActivity()).inflate(R.layout.popu_layout,null);
        publicList = (ListView) view.findViewById(R.id.lv_content);
        if(popupWindow == null){
            popupWindow=new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            ColorDrawable cd = new ColorDrawable(-0000);
            popupWindow.setBackgroundDrawable(cd);
            popupWindow.setOutsideTouchable(true);
            popupWindow.setFocusable(true);
        }
    }

    private void initData() {
        RequestParams params = new RequestParams();
        params.add("type", "0");//一级目录
        HttpUtil.getbaguoRankCate(new SimpleHttpHandler() {
            @Override
            public void onJsonSuccess(JSONObject json) {
                final JSONArray jsonArray = json.getJSONArray("data");
                addCategory(oneCategory,jsonArray);
                oneCategory.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup radioGroup, int position) {
                        twoid = ((JSONObject)jsonArray.get(position)).getString("oneid");
                        getTwoCateByOne((JSONObject) jsonArray.get(position));
                    }
                });
            }
        }, params);
    }

    private void getTwoCateByOne(JSONObject jsonObject){
        RequestParams params2=new RequestParams();
        params2.add("type", "1");//二级目录
        params2.add("upid",jsonObject.getString("oneid"));
        HttpUtil.getbaguoRankCate(new SimpleHttpHandler() {
            @Override
            public void onJsonSuccess(JSONObject json) {
                final JSONArray jsonArray = json.getJSONArray("data");
                addCategory(twoCategory,jsonArray);
                twoCategory.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup radioGroup, int position) {
                        twoid = ((JSONObject)jsonArray.get(position)).getString("twoid");
                        getThreeCateByTwo((JSONObject) jsonArray.get(position));
                    }
                });
            }
        }, params2);
    }

    private void getThreeCateByTwo(JSONObject jsonObject){
        RequestParams params3=new RequestParams();
        params3.add("type", "2");//二级目录
        params3.add("upid",jsonObject.getString("twoid"));
        HttpUtil.getbaguoRankCate(new SimpleHttpHandler() {
            @Override
            public void onJsonSuccess(JSONObject json) {
                final JSONArray jsonArray = json.getJSONArray("data");
                addCategory(threeCategory,jsonArray);
                threeCategory.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup radioGroup, int position) {
                        threeid = ((JSONObject)jsonArray.get(position)).getString("threeid");
                        twoCategory.removeAllViews();
                        threeCategory.removeAllViews();
                    }
                });
            }
        }, params3);
    }

    private void addCategory(RadioGroup category,JSONArray jsonArray){
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = (JSONObject) jsonArray.get(i);
            RadioButton mRadio = new RadioButton(getActivity());
            mRadio.setText(jsonObject.getString("name"));
            mRadio.setTextSize(16);
            mRadio.setPadding(22, 22, 22, 22);
            mRadio.setGravity(Gravity.CENTER);
            mRadio.setButtonDrawable(new ColorDrawable(Color.TRANSPARENT));
            mRadio.setBackground(getResources().getDrawable(R.drawable.checked_rank));
            mRadio.setTextColor(getResources().getColorStateList(R.color.button_text));
            mRadioParams = new RadioGroup.LayoutParams(sFileParent, sWrapContent);
            category.addView(mRadio, mRadioParams);
        }
    }

    private void initView() {
        btn_fenlei = (RelativeLayout) getView().findViewById(R.id.btn_fenlei);//默认分类
        btn_paixu = (RelativeLayout) getView().findViewById(R.id.btn_paixu);//排序方式
        btn_recommend = (Button) getView().findViewById(R.id.btn_recommended);//我要推荐
        oneCategory = (RadioGroup) getView().findViewById(R.id.category_list_one);//一级分类
        twoCategory = (RadioGroup) getView().findViewById(R.id.category_list_two);//二级分类
        threeCategory = (RadioGroup) getView().findViewById(R.id.category_list_three);//三级分类
        ranklist = (PullToRefreshListView) getView().findViewById(R.id.right_category_list);//巴国榜列表
        left_show = (ScrollView) getView().findViewById(R.id.left_category_one);//显示分类

        btn_fenlei.setOnClickListener(this);
        btn_paixu.setOnClickListener(this);
        btn_recommend.setOnClickListener(this);

        ranklist.setMode(PullToRefreshBase.Mode.BOTH);
        //myAnimation= AnimationUtils.loadAnimation(getActivity(), R.anim.left_category);


        ranklist.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                //重新加载数据
                requestData(Mapplication.getModel().getLatitude(),Mapplication.getModel().getLongitude());
            }
        });

        ranklist.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                //下拉加载更多
                requestData(Mapplication.getModel().getLatitude(),Mapplication.getModel().getLongitude());
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                loadmore(Mapplication.getModel().getLatitude(), Mapplication.getModel().getLongitude());
            }
        });

        ranklist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //判断餐厅是否营业
//                ResturantModel model=adapter.getItem(position-1);
//                if(model.getIsopen()!=null){
//                    if(model.getIsopen().equals("0")){
//                        CommonDialogFragment.getInstance("提示","当前时间餐厅没有营业，请稍后再来").show(getFragmentManager(),"dialog");
//                    }else{
//                        //启动餐厅详情页面
//                        Intent intent=new Intent(getActivity(), RestaurantDetail.class);
//                        intent.putExtra(RestaurantDetail.KEY,model);
//                        startActivity(intent);
//                    }
//                }
            }
        });

        //requestData(Mapplication.getModel().getLatitude(), Mapplication.getModel().getLongitude());
        requestData(35.348000, 118.201900);

    }

    /**
     * @param lat 纬度
     * @param lo 经度
     */
    private void requestData(double lat,double lo){
        //page;num;latitude，纬度；longitude，经度；sort:排序，int,必填，0->默认(附近)、1->赞最多、2->评分最多、3->认证分数最多；
        //oneid,twoid,threeid,分类的id;userid,int,必填； 说明：添加istarted字段，1表示已经点赞，0表示没有点赞
        page=1;
        RequestParams params=new RequestParams();
        params.put("page",page);//1-->>
        params.put("num",num);//7
        params.put("latitude","35.056418");//纬度
        params.put("longitude","118.437424");//精度
        params.put("oneid","1");//1 一级分类
        params.put("twoid","2");//1 二级分类
        params.put("threeid","9");//1 三级分类
        params.put("userid",Mapplication.getModel().getUserid());//1
        params.put("sort",String.valueOf(order));//1
        HttpUtil.getbaguoRank(new EntityHandler<BaGuoRank>(BaGuoRank.class) {
            @Override
            public void onReadSuccess(List<BaGuoRank> list) {
                ranksAdapter = new RanksAdapter(getActivity(),list);
                ranklist.setAdapter(ranksAdapter);
            }
        }, params);
    }

    private void loadmore(double lat,double lo){
        ++page;
        RequestParams params=new RequestParams();
        params.put("page",page);
        params.put("num",num);
        params.put("lat",lat);//纬度
        params.put("long",lo);
        params.put("order",String.valueOf(order));
        //params.put("actid",String.valueOf(huodong));
        params.put("category",String.valueOf(category));
        HttpUtil.getRestaurantList(new EntityHandler<ResturantModel>(ResturantModel.class) {
            @Override
            public void onReadSuccess(List<ResturantModel> llist) {
                list.addAll(llist);
                adapter = new RestaurantAdapter(getActivity(),list);
                ranklist.setAdapter(adapter);
                if(ranklist.isRefreshing()){
                    ranklist.onRefreshComplete();
                }
            }

            @Override
            public void onRecevieFailed(String status, JSONObject json) {
                super.onRecevieFailed(status, json);
                if(status.equals("0")){
                    Toast.makeText(getActivity(),"没有数据了",Toast.LENGTH_SHORT).show();
                }
                ranklist.onRefreshComplete();
            }
        },params);
    }





    @Override
    public void onClick(View view) {
        //点击顶部的排序按钮 弹出popu window 点击popuwindow 按钮 重新获取数据
        switch (view.getId()){
            case R.id.btn_fenlei:
                if(left_show.getVisibility() == View.GONE){
                    left_show.setVisibility(View.VISIBLE);
                }else{
                    left_show.setVisibility(View.GONE);
                }
                break;
            case R.id.btn_paixu:
                //排序
                publicList.setAdapter(new CategoryAdapter(getActivity(),paixulist,resid));
                publicList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        RankList.order=position;
                        if(popupWindow.isShowing()){
                            popupWindow.dismiss();
                        }
                        ranklist.setRefreshing();
                    }
                });
                popupWindow.showAsDropDown(view);
                break;
            case R.id.btn_recommended:
//                if(activitiesModels != null){
//                    pList.setAdapter(new ActivityModelAdapter(getActivity(),activitiesModels));
//                    pList.setOnItemClickListener(new categoryItemClickListener() {
//                        @Override
//                        void setUpType(int position) {
//
//                        }
//                    });
//                    popupWindow.showAsDropDown(v);
//                }
                //活动
                break;
        }
    }

    public Integer getCategory() {
        return category;
    }

    public void setCategory(Integer category) {
        this.category = category;
    }
}