package com.example.studentinformationshasun;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;

import webservice.SqlliteController;

public class HomePageGridViewLayout extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private final String strEmployee = "";
    SQLiteDatabase db;
    SqlliteController controllerdb = new SqlliteController(this);
    private long lngEmployeeId = 0;
    ImageView imageView;
    GridView grid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gridlayout_cardview_home_page);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final SharedPreferences loginsession = getApplicationContext().getSharedPreferences("SessionLogin", 0);
        lngEmployeeId = loginsession.getLong("userid", 1);
        //Status Bar Color
        StatusColor.SetStatusColor(getWindow(), ContextCompat.getColor(this, R.color.colorblue));

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);


        View headerView = navigationView.getHeaderView(0);
        imageView= headerView.findViewById(R.id.imgStudentPhoto);

        TextView tvEmployee = headerView.findViewById(R.id.tvName);
        tvEmployee.setText(loginsession.getString("studentname",""));
        TextView tvDepartment = headerView.findViewById(R.id.tvDepartment);
        tvDepartment.setText(loginsession.getString("program",""));
        TextView tvRegNo = headerView.findViewById(R.id.tvRegNo);
        tvRegNo.setText("Reg.no "+loginsession.getString("registerno",""));
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(HomePageGridViewLayout.this);
        navigationView.setItemIconTintList(null);
        String base64 = loginsession.getString("profileImage","");
        //Log.i("TEST IMAGE",base64);
        if(!base64.equalsIgnoreCase("")){
            byte[] byteArrPhoto= Base64.decode(base64, Base64.DEFAULT);
            setCirularImage(byteArrPhoto);
        }


        getMenuValues();
       // showSnackber();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void getMenuValues(){
        GridView gridview = findViewById(R.id.gridview);
        gridview.setAdapter(new ImageAdapter(this));
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent,View v, int position, long id) {
                if (position == 12) {//Logout option
                    SharedPreferences myPrefs = getSharedPreferences("SessionLogin", MODE_PRIVATE);
                    SharedPreferences.Editor editor = myPrefs.edit();
                    editor.clear();
                    editor.commit();
                    SqlliteController sc = new SqlliteController(HomePageGridViewLayout.this);
                    sc.deleteLoginStudentDetails();
                    Intent intent = new Intent(HomePageGridViewLayout.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
                if (CheckNetwork.isInternetAvailable(HomePageGridViewLayout.this)){
                    // Send intent to SingleViewActivity
                    if (position == 0){
                        Intent intent = new Intent(HomePageGridViewLayout.this, PersonalDetails.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);

                    }
                    if (position == 1){
                        Intent intent = new Intent(HomePageGridViewLayout.this, FeeDetails.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);

                    }
                    if (position == 2){
                        Intent intent = new Intent(HomePageGridViewLayout.this, FinanceDetails.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                    if (position == 3){
                        Intent intent = new Intent(HomePageGridViewLayout.this, StudentWiseSubjects.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);

                    }
             /*       if (position == 4){
                        Intent intent = new Intent(HomePageGridViewLayout.this, HostelDetails.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);

                    }

              */
                    if (position == 4){
                        Intent intent = new Intent(HomePageGridViewLayout.this, SubjectWiseAttendance.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);

                    }
                    if (position == 5){
                        Intent intent = new Intent(HomePageGridViewLayout.this, CummulativAttendance.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);

                    }
                    if (position == 6){
                        Intent intent = new Intent(HomePageGridViewLayout.this, HourWiseAttendance.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);

                    }
                    if (position == 7){
                        Intent intent = new Intent(HomePageGridViewLayout.this, InternalMarkDetails.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);

                    }
                    if (position == 8){
                        Intent intent = new Intent(HomePageGridViewLayout.this, ExamDetails.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);

                    }
                    if (position == 9){
                        Intent intent = new Intent(HomePageGridViewLayout.this, LibraryTransaction.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);

                    }
                    if (position == 10) {
                        Intent intent = new Intent(HomePageGridViewLayout.this, NotificationView.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                    if (position == 11){
                        Intent intent = new Intent(HomePageGridViewLayout.this, ChangePassword.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);

                    }
                } else{
                    Toast.makeText(HomePageGridViewLayout.this,"You dont have Internet connection", Toast.LENGTH_LONG).show();
                }
            }
        });
        final GestureDetector mGestureDetector = new GestureDetector(HomePageGridViewLayout.this, new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });
    }
    public void setCirularImage(byte[] byteArray){
        Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        RoundedBitmapDrawable roundedBitmapDrawable= RoundedBitmapDrawableFactory.create(getResources(),bitmap);
        roundedBitmapDrawable.setCornerRadius(10);
        imageView.setImageDrawable(roundedBitmapDrawable);
    }

    // we are setting onClickListener for each element
    private void setSingleEvent(GridLayout gridLayout) {
        for(int i = 0; i<gridLayout.getChildCount();i++){
            CardView cardView=(CardView)gridLayout.getChildAt(i);
            final int finalI= i;
//            if (!CheckNetwork.isInternetAvailable(getApplicationContext())) {
//                Toast.makeText(getApplicationContext(),"You dont have Internet connection", Toast.LENGTH_LONG).show();
//                return;
//            }
            cardView.setOnClickListener(new View.OnClickListener() {
                final SharedPreferences loginsession = getApplicationContext().getSharedPreferences("SessionLogin", 0);
                final SharedPreferences.Editor ed = loginsession.edit();
                @Override
                public void onClick(View view){
                    if (finalI == 0){ //Profile
                        Intent intent = new Intent(getApplicationContext(), PersonalDetails.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);

                    } else if (finalI == 8){ //Logout
                        SharedPreferences myPrefs = getApplicationContext().getSharedPreferences("SessionLogin", MODE_PRIVATE);
                        SharedPreferences.Editor editor = myPrefs.edit();
                        editor.clear();
                        editor.commit();
                        SqlliteController sc = new SqlliteController(getApplicationContext());
                       // sc.deleteLoginStaffDetails();
                        Intent intent = new Intent(HomePageGridViewLayout.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);

                    }else{
                        /*
                        Intent intent = new Intent(HomePageGridViewLayout.this, HomeScreenCategory.class);
                        intent.putExtra("Flag",1);
                        ed.putInt("categoryid", finalI+1);
                        ed.commit();
//                        intent.putExtra("CategoryId",finalI+1);
                        startActivity(intent);
                        finish();

                         */
                    }
                }
            });
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item){
        // Handle navigation view item clicks here.
        Fragment fragment = null;
        Class fragmentClass = null;

        int id = item.getItemId();
        Toolbar toolbar = findViewById(R.id.toolbar);
//        if (CheckNetwork.isInternetAvailable(HomePageGridViewLayout.this)) {
//            Toast.makeText(HomePageGridViewLayout.this,"You dont have Internet connection", Toast.LENGTH_LONG).show();
//        }
        if (id == R.id.nav_personaldetails){
            toolbar.setTitle(getResources().getString(R.string.hProfile));
            Intent intent = new Intent(HomePageGridViewLayout.this,PersonalDetails.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            
            startActivity(intent);
            //fragmentClass = ProfileFragment.class;
//            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
//                    new ProfileFragment()).commit();
        }else if (id == R.id.nav_feesdues){
            toolbar.setTitle(getResources().getString(R.string.menu_feesdues));
            Intent intent = new Intent(HomePageGridViewLayout.this, FeeDetails.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            
            startActivity(intent);
        }else if (id == R.id.nav_feespayments){
            toolbar.setTitle(getResources().getString(R.string.menu_feespayments));
            Intent intent = new Intent(HomePageGridViewLayout.this, FinanceDetails.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            
            startActivity(intent);
        }else if (id == R.id.nav_studentwisesubject){
            toolbar.setTitle(getResources().getString(R.string.menu_studentwisesubjects));
            Intent intent = new Intent(HomePageGridViewLayout.this, StudentWiseSubjects.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            
            startActivity(intent);
        }else if (id == R.id.nav_subjectwiseattendance){
            toolbar.setTitle(getResources().getString(R.string.menu_subjectwiseattendance));
            Intent intent = new Intent(HomePageGridViewLayout.this, SubjectWiseAttendance.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            
            startActivity(intent);
        }else if (id == R.id.nav_cumulativeattendance){
            toolbar.setTitle(getResources().getString(R.string.menu_cumulativeattendance));
            Intent intent = new Intent(HomePageGridViewLayout.this, CummulativAttendance.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            
            startActivity(intent);
        }else if (id == R.id.nav_hourattendance){
            toolbar.setTitle(getResources().getString(R.string.menu_hourattendance));
            Intent intent = new Intent(HomePageGridViewLayout.this, HourWiseAttendance.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            
            startActivity(intent);
        }else if (id == R.id.nav_intermarks){
            toolbar.setTitle(getResources().getString(R.string.menu_internalmarkdetails));
            Intent intent = new Intent(HomePageGridViewLayout.this, InternalMarkDetails.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            
            startActivity(intent);
        }else if (id == R.id.nav_examinationdetails){
            toolbar.setTitle(getResources().getString(R.string.menu_examinationdetails));
            Intent intent = new Intent(HomePageGridViewLayout.this, ExamDetails.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            
            startActivity(intent);
        }else if (id == R.id.nav_librarytransactions){
            toolbar.setTitle(getResources().getString(R.string.menu_librarytransactions));
            Intent intent = new Intent(HomePageGridViewLayout.this, LibraryTransaction.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            
            startActivity(intent);
        }else if (id == R.id.nav_notifications){
            toolbar.setTitle(getResources().getString(R.string.menu_Notification));
            Intent intent = new Intent(HomePageGridViewLayout.this, NotificationView.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            
            startActivity(intent);
        }else if (id == R.id.nav_changepwd){
            toolbar.setTitle(getResources().getString(R.string.menu_changepwd));
            Intent intent = new Intent(HomePageGridViewLayout.this, ChangePassword.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            
            startActivity(intent);
        }else  if (id == R.id.nav_logout){
            SharedPreferences myPrefs = getApplicationContext().getSharedPreferences("SessionLogin", MODE_PRIVATE);
            SharedPreferences.Editor editor = myPrefs.edit();
            editor.clear();
            editor.commit();
            SqlliteController sc = new SqlliteController(getApplicationContext());
            //sc.deleteLoginStaffDetails();
            Intent intent = new Intent(HomePageGridViewLayout.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        }catch (Exception e) {
            e.printStackTrace();
        }
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_container, fragment);
            ft.commit();
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            if (item.getItemId() == R.id.nav_personaldetails) {
                item.setIcon(getDrawable(R.drawable.icon_profile));
            }
        }
        //getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //this.finish();
        finishAffinity();
    }


}
