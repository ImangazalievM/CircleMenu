package com.imangazaliev.circlemenu.sample;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.view.ViewGroup;

import com.imangazaliev.circlemenu.CircleMenu;
import com.imangazaliev.circlemenu.CircleMenuButton;

public class Custom1Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom1);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        final ViewGroup snackbarContainer = findViewById(R.id.snackbar_contaner);
        final CircleMenu circleMenu = findViewById(R.id.circle_menu);
        circleMenu.setOnItemClickListener(new CircleMenu.OnItemClickListener() {
            @Override
            public void onItemClick(CircleMenuButton menuButton) {
                Snackbar.make(snackbarContainer, menuButton.getHintText(), Snackbar.LENGTH_LONG).show();
                if (!menuButton.isShowClickAnim()) {
                    circleMenu.close(true);
                }
            }
        });

    }
}
