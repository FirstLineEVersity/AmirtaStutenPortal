package com.example.studentinformationshasun;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONObject;
import webservice.SqlliteController;
import webservice.WebService;
import static android.content.ContentValues.TAG;
import static webservice.WebService.strParameters;

import com.google.android.material.textfield.TextInputEditText;

import java.nio.charset.StandardCharsets;

/**
 * Created by Firstline Infotech on 30-04-2019.
 */

public class PersonalDetails extends AppCompatActivity {
    private static String ResultString = "";
    private long lngStudentId=0;
    TextView tvLastUpdated,tvPageTitle;
    TextInputEditText tvStudname, tvDob, tvGender, tvAddress, tvAdmitdate;
    TextInputEditText tvRegisterNo, tvBatch, tvParent, tvProgram, tvSemester, tvSchool;
    private String strSchool="";
    SQLiteDatabase db;
    SqlliteController controllerdb = new SqlliteController(this);

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personaldetails);
        StatusColor.SetStatusColor(getWindow(), ContextCompat.getColor(this, R.color.colorblue));
        tvPageTitle = findViewById(R.id.pageTitle);
        tvPageTitle.setText("Profile");
        tvLastUpdated = findViewById(R.id.txtLastUpdated);
        Button btnBack= findViewById(R.id.button_back);
        Button btnRefresh= findViewById(R.id.button_refresh);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
          onBackPressed();  }
        });
        btnRefresh.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
            strParameters = new String[]{"Long", "studentid", String.valueOf(lngStudentId)};
            WebService.strParameters = strParameters;
            WebService.METHOD_NAME = "getPersonalDetails";
            AsyncCallWS task = new AsyncCallWS();
            task.execute();
            }
        });
        final SharedPreferences loginsession = getApplicationContext().getSharedPreferences("SessionLogin", 0);
        lngStudentId = loginsession.getLong("userid", 1);
        strSchool = loginsession.getString("school", "");
        displayProfile();
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();

    }

    private void displayProfile(){
        db = controllerdb.getReadableDatabase();
        try {
            Cursor cursor = db.rawQuery("SELECT strftime('%d-%m-%Y %H:%M:%S', lastupdatedate)  as lastupdated,* FROM profiledetails pf WHERE studentid=" + lngStudentId, null);
            if (cursor.moveToFirst()){
                do {
                    tvLastUpdated.setText("Last Updated: "+cursor.getString(cursor.getColumnIndex("lastupdated")));
                    tvSchool = findViewById(R.id.txtSchool);
                    tvSchool.setText(cursor.getString(cursor.getColumnIndex("school")));

                    tvStudname = findViewById(R.id.txtStudentName);
                    tvStudname.setText(cursor.getString(cursor.getColumnIndex("studentname")));

                    tvRegisterNo = findViewById(R.id.txtRegisterNo);
                    tvRegisterNo.setText(cursor.getString(cursor.getColumnIndex("registerno")));

                    tvBatch = findViewById(R.id.txtBatch);
                    tvBatch.setText(cursor.getString(cursor.getColumnIndex("batch")));

                    tvDob = findViewById(R.id.txtDob);
                    tvDob.setText(cursor.getString(cursor.getColumnIndex("dob")));

                    tvParent = findViewById(R.id.txtParent);
                    tvParent.setText(cursor.getString(cursor.getColumnIndex("fathername"))+" , " + cursor.getString(cursor.getColumnIndex("mothername")));

                    tvGender = findViewById(R.id.txtGender);
                    tvGender.setText(cursor.getString(cursor.getColumnIndex("gender")));

                    tvAddress = findViewById(R.id.txtAddress);
                    tvAddress.setText(cursor.getString(cursor.getColumnIndex("address")));

                    tvSemester = findViewById(R.id.txtSemesterSection);
                    tvSemester.setText(cursor.getString(cursor.getColumnIndex("semester")) + "-" + cursor.getString(cursor.getColumnIndex("section")));

                    tvProgram = findViewById(R.id.txtProgram);
                    tvProgram.setText(cursor.getString(cursor.getColumnIndex("program"))); //+ "-" + cursor.getString(cursor.getColumnIndex("universityname")));

                    tvAdmitdate = findViewById(R.id.txtAdmittedDate);
                    tvAdmitdate.setText(cursor.getString(cursor.getColumnIndex("admitteddate")));
                } while (cursor.moveToNext());
                try {
                    cursor = db.rawQuery("SELECT * FROM studentphoto pf WHERE studentid=" + lngStudentId, null);
                    if (cursor.moveToFirst()) {
                        do {
                            byte[] byteArrPhoto = cursor.getBlob(1); //Base64.decode(base64,Base64.DEFAULT);
//                            Bitmap bmp = BitmapFactory.decodeByteArray(byteArrPhoto, 0, byteArrPhoto.length);
//                            Bitmap croppedBmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getWidth());
//                            ImageView image = (ImageView) findViewById(R.id.imgStudentPhoto);
//                            image.setImageBitmap(croppedBmp);
                            setCirularImage(byteArrPhoto);

                        } while (cursor.moveToNext());
                    }
                }catch(Exception e){}
            } else {
                strParameters = new String[]{"Long", "studentid", String.valueOf(lngStudentId)};
                WebService.strParameters = strParameters;
                WebService.METHOD_NAME = "getPersonalDetails";
                AsyncCallWS task = new AsyncCallWS();
                task.execute();
            }
            cursor.close();
        }catch (Exception e){
            System.out.println(e.getMessage());
            strParameters = new String[]{"Long", "studentid", String.valueOf(lngStudentId)};
            WebService.strParameters = strParameters;
            WebService.METHOD_NAME = "getPersonalDetails";
            AsyncCallWS task = new AsyncCallWS();
            task.execute();
        }
    }

    public void setCirularImage(byte[] byteArray){

        Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        RoundedBitmapDrawable roundedBitmapDrawable= RoundedBitmapDrawableFactory.create(getResources(),bitmap);
        roundedBitmapDrawable.setCornerRadius(10);
        ImageView imageView= findViewById(R.id.imgStudentPhoto);
        imageView.setImageDrawable(roundedBitmapDrawable);
    }

//    public static byte[] getBytes(Bitmap bitmap) {
//        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
//        return stream.toByteArray();
//    }

    private class AsyncCallWS extends AsyncTask<Void, Void, Void> {
        ProgressDialog dialog = new ProgressDialog(PersonalDetails.this);
        @Override
        protected void onPreExecute() {
            dialog.setMessage("Loading......");
            //show dialog
            dialog.show();
            Log.i(TAG, "onPreExecute");
        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.i(TAG, "doInBackground");
            if (android.os.Debug.isDebuggerConnected())
                android.os.Debug.waitForDebugger();
            ResultString = WebService.invokeWS();
            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            Log.i(TAG, "onPostExecute");
            if(dialog != null && dialog.isShowing()){
                dialog.dismiss();
            }
            try {
                JSONObject object = new JSONObject(ResultString);
                if (!object.isNull("studentid")){
                    SqlliteController sc = new SqlliteController(PersonalDetails.this);
                    sc.insertProfileDetails(object.getLong("studentid"),object.getString("studentname"),
                            object.getString("registerno"),object.getString("academicyear"),
                            object.getString("dob"),object.getString("sex"),
                            object.getString("semester") , object.getString("sectiondesc"),
                            object.getString("father"),object.getString("mother"),object.getString("address"),
                            object.getString("program"), object.getString("universityname"),
                            object.getString("admitteddate"), strSchool);

                    String base64 = object.getString("studentphoto");
                    byte[] byteArrPhoto=Base64.decode(base64,Base64.DEFAULT);
                    sc.insertStudentPhoto(object.getLong("studentid"),byteArrPhoto);
                    setCirularImage(byteArrPhoto);
                    SharedPreferences loginsession = getApplicationContext().getSharedPreferences("SessionLogin", 0);
                    SharedPreferences.Editor editP = loginsession.edit();
                    editP.putString("profileImage", base64);
                    editP.commit();

                    displayProfile();
                } else {
                    Toast.makeText(PersonalDetails.this, "No Personal Details Found", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e){
                System.out.println(e.getMessage());
            }
        }
    }
}