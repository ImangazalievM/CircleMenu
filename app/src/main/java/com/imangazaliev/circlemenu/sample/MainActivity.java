package com.imangazaliev.circlemenu.sample;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.imangazaliev.circlemenu.CircleMenu;
import com.imangazaliev.circlemenu.CircleMenuButton;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ViewGroup snackbarContainer = (ViewGroup) findViewById(R.id.snackbar_contaner);
        CircleMenu circleMenu = (CircleMenu) findViewById(R.id.circle_menu);
        circleMenu.setOnItemClickListener(new CircleMenu.OnItemClickListener() {
            @Override
            public void onItemClick(CircleMenuButton menuButton) {
                Snackbar.make(snackbarContainer, menuButton.getHintText(), Snackbar.LENGTH_LONG).show();
            }
        });

        circleMenu.setStateUpdateListener(new CircleMenu.OnStateUpdateListener() {
            @Override
            public void onMenuExpanded() {
                Log.d("CircleMenuStatus", "Expanded");
            }

            @Override
            public void onMenuCollapsed() {
                Log.d("CircleMenuStatus", "Collapsed");
            }
        });
    }

}
