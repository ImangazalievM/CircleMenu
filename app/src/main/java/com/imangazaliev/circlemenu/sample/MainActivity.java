package com.imangazaliev.circlemenu.sample;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ViewGroup;

import com.imangazaliev.circlemenu.CircleMenu;
import com.imangazaliev.circlemenu.CircleMenuButton;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ViewGroup snackbarContainer = (ViewGroup) findViewById(R.id.snackbar_contaner);

        final CircleMenu circleMenuDefault = (CircleMenu) findViewById(R.id.circle_menu_default);
        //final CircleMenu circleMenuMultiple = (CircleMenu) findViewById(R.id.circle_menu_multiple);
        final CircleMenu circleMenuMultipleBorder = (CircleMenu) findViewById(R.id.circle_menu_multiple_border);

        prepareViewCircleMenuDefault(circleMenuDefault, snackbarContainer);
        //prepareViewCircleMenuMultiple(circleMenuMultiple);
        prepareViewCircleMenuMultipleBorder(circleMenuMultipleBorder);
    }

    private void prepareViewCircleMenuDefault(CircleMenu circleMenu, final ViewGroup snackbarContainer) {
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

    private void prepareViewCircleMenuMultiple(CircleMenu circleMenuMultiple) {
        /**
         * adding dynamically
         */
        for (int i = 0; i < 4; i ++) {
            CircleMenuButton circleMenuButton =  new CircleMenuButton(this);

            circleMenuButton.setColorNormal(R.color.color_normal);
            circleMenuButton.setColorPressed(R.color.color_pressed);
            circleMenuButton.setIconResId(R.drawable.ic_favorite);
            circleMenuButton.setFullDrawable(false);
            circleMenuButton.setMetaData(new ExampleData(i));

            circleMenuMultiple.addButton(circleMenuButton);
        }

        /**
         * get meta data of circles selected
         */
        circleMenuMultiple.setOnConfirmationListener(new CircleMenu.OnConfirmationListener() {
            @Override
            public void onConfirmation(List<Object> listData) {
                for (int i =0; i< listData.size(); i++ ) {
                    ExampleData exampleData = (ExampleData) listData.get(i);
                    Log.d("MainActivity", "Id: " + exampleData.getId());
                }
            }
        });
    }


    private void prepareViewCircleMenuMultipleBorder(CircleMenu circleMenu) {
        /**
         * adding dynamically
         */
        for (int i = 0; i < 4; i ++) {
            CircleMenuButton circleMenuButton =  new CircleMenuButton(this);

            circleMenuButton.setColorNormal(R.color.color_normal);
            circleMenuButton.setColorPressed(R.color.color_pressed);
            circleMenuButton.setIconResId(R.drawable.ic_favorite);
            circleMenuButton.setEnableBorder(true);
            circleMenuButton.setMetaData(new ExampleData(i));

            circleMenu.addButton(circleMenuButton);
        }

        /**
         * get meta data of circles selected
         */
        circleMenu.setOnConfirmationListener(new CircleMenu.OnConfirmationListener() {
            @Override
            public void onConfirmation(List<Object> listData) {
                for (int i =0; i< listData.size(); i++ ) {
                    ExampleData exampleData = (ExampleData) listData.get(i);
                    Log.d("MainActivity", "Id: " + exampleData.getId());
                }
            }
        });
    }

}
