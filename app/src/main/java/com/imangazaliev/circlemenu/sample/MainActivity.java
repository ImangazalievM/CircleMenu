package com.imangazaliev.circlemenu.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.imangazaliev.circlemenu.CircleMenu;
import com.imangazaliev.circlemenu.CircleMenuButton;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final CircleMenu circleMenu = (CircleMenu) findViewById(R.id.circle_menu);
        circleMenu.setConfimationButton(true);

        for (int i = 0; i < 4; i ++) {
            CircleMenuButton circleMenuButton =  new CircleMenuButton(this);

            circleMenuButton.setColorNormal(R.color.color_normal);
            circleMenuButton.setColorPressed(R.color.color_pressed);
            circleMenuButton.setIconResId(R.drawable.ic_favorite);
            circleMenuButton.setTypeCheck(true);
            circleMenuButton.setMetaData(new DataGroup(i));

            circleMenu.addButton(circleMenuButton);
        }

        circleMenu.setOnConfirmationListener(new CircleMenu.OnConfirmationListener() {
            @Override
            public void onConfirmation(List<Object> listData) {
                for (int i =0; i< listData.size(); i++ ) {
                    DataGroup dataGroup = (DataGroup) listData.get(i);
                    Log.d("MainActivity", "ID: " + dataGroup.getIdGroup());
                }
            }
        });
    }

}
