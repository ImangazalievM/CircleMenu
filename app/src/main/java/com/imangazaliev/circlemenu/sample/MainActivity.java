package com.imangazaliev.circlemenu.sample;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Toast;

import com.imangazaliev.circlemenu.CircleMenu;
import com.imangazaliev.circlemenu.CircleMenuButton;
import com.imangazaliev.circlemenu.CircleMenuText;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ViewGroup snackbarContainer = (ViewGroup) findViewById(R.id.snackbar_contaner);

        //final CircleMenu circleMenuDefault = (CircleMenu) findViewById(R.id.circle_menu_default);
        final CircleMenu circleMenuMultiple = (CircleMenu) findViewById(R.id.circle_menu_multiple_border);

        //prepareViewCircleMenuDefault(circleMenuDefault, snackbarContainer);
        prepareViewCircleMenuMultiple(circleMenuMultiple);
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

            CircleMenuText circleMenuText = new CircleMenuText(this);
            circleMenuText.setMetaData(new ExampleData(i));
            circleMenuText.setIconResId(R.drawable.ic_favorite);
            circleMenuText.setFullDrawable(false);
            circleMenuText.setEnableBorder(true);
            circleMenuText.setClickable(false);
            circleMenuText.setColorBorder(Color.RED);

            circleMenuText.setTitle("NetoDevel");
            circleMenuText.setTitleColor(Color.BLACK);
            circleMenuMultiple.addButton(circleMenuText);
        }

        /**
         * get meta data of circles selected
         */
        circleMenuMultiple.setOnConfirmationListener(new CircleMenu.OnConfirmationListener() {
            @Override
            public void onConfirmation(List<Object> listData) {
                Toast.makeText(getBaseContext(), "Size checked:" + listData.size(), Toast.LENGTH_SHORT).show();
                for (int i =0; i< listData.size(); i++ ) {
                    ExampleData exampleData = (ExampleData) listData.get(i);
                    Log.d("MainActivity", "Id: " + exampleData.getId());
                }
            }
        });
    }

}
