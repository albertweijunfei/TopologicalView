package com.example.administrator.autotopologicalview.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.administrator.autotopologicalview.R;
import com.example.administrator.autotopologicalview.entity.BitmapEntity;
import com.example.administrator.autotopologicalview.widget.AutoTopoView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private AutoTopoView mAutoTopoView;
    private ArrayList<BitmapEntity> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAutoTopoView = findViewById(R.id.AutoTopoView);

        initData();
    }

    private void initData() {
        BitmapEntity b1 = new BitmapEntity();
        b1.setName("锤子科技");
        b1.setId(R.mipmap.logo_smartisan);
        b1.setType(0);
        list.add(b1);
        BitmapEntity b2 = new BitmapEntity();
        b2.setName("iPhone");
        b2.setId(R.mipmap.logo_apple);
        b2.setType(0);
        list.add(b2);
        BitmapEntity b3 = new BitmapEntity();
        b3.setName("MEIZU");
        b3.setId(R.mipmap.logo_meizu);
        b3.setType(0);
        list.add(b3);
        BitmapEntity b4 = new BitmapEntity();
        b4.setName("NOKIA");
        b4.setId(R.mipmap.logo_nokia);
        b4.setType(0);
        list.add(b4);
        BitmapEntity b5 = new BitmapEntity();
        b5.setName("Pad");
        b5.setId(R.mipmap.logo_samsung);
        b5.setType(0);
        list.add(b5);
        BitmapEntity b6 = new BitmapEntity();
        b6.setName("索尼科技");
        b6.setId(R.mipmap.logo_sony);
        b6.setType(0);
        list.add(b6);
        BitmapEntity b7 = new BitmapEntity();
        b7.setName("Windows-PC");
        b7.setId(R.mipmap.logo_honghai);
        b7.setType(1);
        list.add(b7);
        BitmapEntity b8 = new BitmapEntity();
        b8.setName("UNKOWN");
        b8.setId(R.mipmap.logo_router_unkown);
        b8.setType(1);
        list.add(b8);
        BitmapEntity b9 = new BitmapEntity();
        b9.setName("锤子科技");
        b9.setId(R.mipmap.logo_smartisan);
        b9.setType(0);
        list.add(b9);
        BitmapEntity b10 = new BitmapEntity();
        b10.setName("iPhone");
        b10.setId(R.mipmap.logo_apple);
        b10.setType(0);
        list.add(b10);
        BitmapEntity b11 = new BitmapEntity();
        b11.setName("MEIZU");
        b11.setId(R.mipmap.logo_meizu);
        b11.setType(0);
        list.add(b11);
        BitmapEntity b12 = new BitmapEntity();
        b12.setName("NOKIA");
        b12.setId(R.mipmap.logo_nokia);
        b12.setType(0);
        list.add(b12);
        BitmapEntity b13 = new BitmapEntity();
        b13.setName("Pad");
        b13.setId(R.mipmap.logo_samsung);
        b13.setType(0);
        list.add(b13);
        BitmapEntity b14 = new BitmapEntity();
        b14.setName("索尼科技");
        b14.setId(R.mipmap.logo_sony);
        b14.setType(0);
        list.add(b14);
        BitmapEntity b15 = new BitmapEntity();
        b15.setName("Windows-PC");
        b15.setId(R.mipmap.logo_honghai);
        b15.setType(1);
        list.add(b15);
        BitmapEntity b16 = new BitmapEntity();
        b16.setName("UNKOWN");
        b16.setId(R.mipmap.logo_router_unkown);
        b16.setType(1);
        list.add(b16);

        mAutoTopoView.setData(list);
//        mAutoTopoView.canTranslateLayout(false);
    }
}
