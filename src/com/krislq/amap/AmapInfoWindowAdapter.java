package com.krislq.amap;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.maps.AMap.InfoWindowAdapter;
import com.amap.api.maps.model.Marker;

/**
 * 
 * @{#} CarsInfoWindowAdapter.java Create on 2013-7-25 下午2:11:20    
 *    
 * class desc:   
 *
 * <p>Copyright: Copyright(c) 2013 </p>
 * @Version 1.0
 * @Author <a href="mailto:kris@krislq.com">Kris.lee</a>      
 *  
 *
 */
public class AmapInfoWindowAdapter implements InfoWindowAdapter {
    private View mContentView = null;
    private Context mContext = null;
    public AmapInfoWindowAdapter(Context context) {
        mContext = context;
        mContentView = LayoutInflater.from(context).inflate(R.layout.adapter_map_car, null);
        View content = mContentView.findViewById(R.id.car_content);
        //如果 这里不去设置params,布局就等同于wrap_content
        //在这里为什么是对car_content去设置的
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)content.getLayoutParams();
        params.width = Utils.getSceenWidth() -50;
        content.setLayoutParams(params);
    }

    @Override
    public View getInfoContents(Marker marker) {
        Log.e("AmapInfo", "getInfoContents");
        return null;
    }

    /**
     * http://api.amap.com/Public/reference/Android%20API%20v2/
     * 提供了一个个性化定制信息窗口的marker对象。 
     * 如果这个方法返回一个view，则它会被用来当对整个信息窗口。
     * 如果你在调用这个方法之后修改了信息窗口的view对象，那么这些改变不一定会起作用。
     * 如果这个方法返回null，则将会使用默认的信息窗口风格，内容将会从getInfoContents(Marker)方法获取。
     * 如果view.getBackground()为null，将会使用默认的信息窗口边框。
     */
    @Override
    public View getInfoWindow(Marker marker) {
        Log.e("AmapInfo", "getInfoWindow");
        TextView tv = (TextView)mContentView.findViewById(R.id.textView1);
        tv.setText(marker.getTitle());
        //可以从mark中获取 出想要的内容
        return mContentView;
    }

}
