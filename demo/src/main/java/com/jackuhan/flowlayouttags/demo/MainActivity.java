package com.jackuhan.flowlayouttags.demo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.jackuhan.flowlayouttags.FlowlayoutTags;

import java.util.ArrayList;
import java.util.List;



public class MainActivity extends Activity{


    private FlowlayoutTags fldefault;
    private FlowlayoutTags flred,flred2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.flred = (FlowlayoutTags) findViewById(R.id.fl_red);
        this.flred2 = (FlowlayoutTags) findViewById(R.id.fl_red2);
        this.fldefault = (FlowlayoutTags) findViewById(R.id.fl_default);

        final List<String> list = new ArrayList<String>();
        list.add("绿色足球鞋");
        list.add("白色棒球帽");
        list.add("黑色毛衣外套");
        list.add("褐色牛仔连衣裙");
        list.add("白色圆领衬衫");
        list.add("红色长袖连衣裙");
        refreshCategorys(fldefault, list);
        refreshCategorys(flred, list);
        refreshCategorys(flred2, list);

        fldefault.setOnTagClickListener(new FlowlayoutTags.OnTagClickListener() {
            @Override
            public void onTagClick(String tag) {
                Log.v("Hanjh fldefault", fldefault.getCheckedTagsTextsArrayList().toString());

                ArrayList<Integer> tagList = fldefault.getCheckedTagsIndexArrayList();
                String mCategory = "";
                for (int i = 0; i < tagList.size(); i++) {
                    mCategory += list.get(tagList.get(i)) + ",";
                }
                Log.e("Hanjh","mCategory "+mCategory +" "+tagList);
            }
        });

        flred.setOnTagClickListener(new FlowlayoutTags.OnTagClickListener() {
            @Override
            public void onTagClick(String tag) {
                Log.v("Hanjh flred", flred.getCheckedTagsTextsArrayList().toString());

                ArrayList<Integer> tagList = flred.getCheckedTagsIndexArrayList();
                String mCategory = "";
                for (int i = 0; i < tagList.size(); i++) {
                    mCategory += list.get(tagList.get(i)) + ",";
                }
                Log.e("Hanjh","mCategory "+mCategory +" "+tagList);
            }
        });

        flred2.setOnTagClickListener(new FlowlayoutTags.OnTagClickListener() {
            @Override
            public void onTagClick(String tag) {
                Log.v("Hanjh flred2", flred2.getCheckedTagsTextsArrayList().toString());

                ArrayList<Integer> tagList = flred2.getCheckedTagsIndexArrayList();
                String mCategory = "";
                for (int i = 0; i < tagList.size(); i++) {
                    mCategory += list.get(tagList.get(i)) + ",";
                }
                Log.e("Hanjh","mCategory "+mCategory +" "+tagList);
            }
        });

        findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fldefault.setTagsUncheckedColorAnimal(true);
                flred.setTagsUncheckedColorAnimal(true);
                flred2.setTagsUncheckedColorAnimal(true);
            }
        });

    }

    public void refreshCategorys(FlowlayoutTags flowlayoutTags,List<String> list) {
        flowlayoutTags.removeAllViews();

        flowlayoutTags.setTags(list);
        flowlayoutTags.setTagsUncheckedColorAnimal(false);

    }

}