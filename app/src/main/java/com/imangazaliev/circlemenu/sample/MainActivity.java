package com.imangazaliev.circlemenu.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.imangazaliev.circlemenu.CircleMenu;
import com.imangazaliev.circlemenu.CircleMenuButton;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CircleMenu circleMenu = (CircleMenu) findViewById(R.id.circleMenu);
        circleMenu.setOnItemClickListener(new CircleMenu.OnItemClickListener() {
            @Override
            public void onItemClick(CircleMenuButton menuButton) {
                switch (menuButton.getId()) {
                    case R.id.favorite:
                        showMessage("Favorite");
                        break;
                    case R.id.search:
                        showMessage("Search");
                        break;
                    case R.id.alert:
                        showMessage("Alert");
                        break;
                    case R.id.place:
                        showMessage("Place");
                        break;
                    case R.id.edit:
                        showMessage("Edit");
                        break;
                }
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

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}
