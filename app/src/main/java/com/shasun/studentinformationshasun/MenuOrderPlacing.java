package com.shasun.studentinformationshasun;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;

import webservice.WebService;

public class MenuOrderPlacing extends AppCompatActivity {
    private static String ResultString = "";
    private static String[] strParameters;
    ListView lstMenu;
    TextView tvTotal;
    int[] lMenuId;
    String[] lMenu;

    float[] lPrice;
    int[] lQuantity;
    int[] lMenuImagesIds;
    String[] lMenuImages;
    int[] lPosition;
    float lTotal = 0;
    ArrayList<String> alMenuItems = new ArrayList<String>();
    int intCanteenId = 0;
    AlertDialog.Builder alertDialogBuilder;
    private long lngEmployeeId = 0;
    TextView tvPageTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_order_placing);
        tvPageTitle = findViewById(R.id.pageTitle);
        tvPageTitle.setText("Place Order");
        Button btnBack= findViewById(R.id.button_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
        onBackPressed();    }
        });
        Button btnRef= findViewById(R.id.button_refresh);
        btnRef.setVisibility(View.GONE);

        StatusColor.SetStatusColor(getWindow(), ContextCompat.getColor(this, R.color.colorblue));
        lMenuId = getIntent().getIntArrayExtra("MenuId");
        lMenu = getIntent().getStringArrayExtra("Menu");
        lPrice = getIntent().getFloatArrayExtra("Price");
        lQuantity = getIntent().getIntArrayExtra("Quantity");
        //lMenuImagesIds = getIntent().getIntArrayExtra("MenuImages");
        lMenuImages=getIntent().getStringArrayExtra("MenuImage");
        lPosition = getIntent().getIntArrayExtra("ItemPosition");
        lTotal = getIntent().getFloatExtra("Total", 0);
        alMenuItems = getIntent().getExtras().getStringArrayList("CanteenItems");
        intCanteenId = getIntent().getIntExtra("CanteenId", 0);

        //intent.putExtra("MenuImageUrl", MenuImagesTemp);

        lstMenu = findViewById(R.id.lstmenu1);
        CustomAdapter1 customAdapter = new CustomAdapter1();
        lstMenu.setAdapter(customAdapter);

        tvTotal = findViewById(R.id.secondTotal);
        tvTotal.setText("\u20B9" + " " + String.format("%.02f", lTotal));
        //tvTotal.setText("\u20B9" + " " + Float.toString(lTotal));

        Button btnBack1 = findViewById(R.id.btnBack);
        btnBack1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCustomBackPressed();
            }
        });


        Button btnPlaceOrder = findViewById(R.id.btnPlaceOrder);
        btnPlaceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final SharedPreferences loginsession = getApplicationContext().getSharedPreferences("SessionLogin", 0);
                double lTotal = 0;
                lngEmployeeId = loginsession.getLong("userid", 1);
                String strOrderDetails = "";
                try {
                    for (int i = 0; i < lMenu.length; i++) {
                        if (lQuantity[i] > 0) {
                            float lItemtotal = lQuantity[i] * lPrice[i];
                            lTotal = lTotal + lItemtotal;
                            strOrderDetails = strOrderDetails + lMenuId[i] + "!~!" + lMenu[i] + "!~!" + lQuantity[i] + "!~!" + lPrice[i] + "!~!" + lItemtotal + "$$";
                        }
                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                if (lTotal > 0) {
                   // Log.i("Menu Order Details : ", intCanteenId +"--"+strOrderDetails);
                    strParameters = new String[]{
                            "String", "returndata", strOrderDetails,
                            "int", "canteenid", String.valueOf(intCanteenId),
                            "Long", "employeeid", String.valueOf(lngEmployeeId),
                            "double", "totalbillamount", String.valueOf(lTotal)
                    };
                    WebService.strParameters = strParameters;
                    WebService.METHOD_NAME = "PlaceOrder";
                    AsyncCallWS task = new AsyncCallWS();
                    task.execute();
                } else
                    Toast.makeText(getApplicationContext(), "Menu not selected", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        onCustomBackPressed();
    }

    public void onCustomBackPressed() {
        Intent intent = new Intent(MenuOrderPlacing.this, TestingActivity.class);
        intent.putExtra("ItemPosition", lPosition);
        intent.putExtra("Quantity", lQuantity);
        intent.putExtra("CanteenId",intCanteenId);
        intent.putExtra("Total", lTotal);
        intent.putExtra("ItemListCount", lQuantity.length);
        intent.putStringArrayListExtra("CanteenItems", alMenuItems);
        startActivity(intent);
        this.finish();
    }

    class CustomAdapter1 extends BaseAdapter {
        @Override
        public int getCount() {
            return lMenu.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            convertView = getLayoutInflater().inflate(R.layout.menu_layout, null);
            final TextView txtQuantity = convertView.findViewById(R.id.txtquantity);
            final TextView txtMenu = convertView.findViewById(R.id.txtmenu);
            final TextView txtPrice = convertView.findViewById(R.id.txtprice);
            final Button btnPlus = convertView.findViewById(R.id.btnplus);
            final Button btnMinus = convertView.findViewById(R.id.btnminus);
            final ImageView imgMenuImage = convertView.findViewById(R.id.imgMenuImage);

            btnPlus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String strQuantity = txtQuantity.getText().toString();
                    int tempQuantity = Integer.parseInt(strQuantity);
                    //float tempPrice=lPrice[position];
                    ++tempQuantity;
                    //Toast.makeText(getApplicationContext(), strQuantity, Toast.LENGTH_SHORT).show();
                    txtQuantity.setText(String.valueOf(tempQuantity));
                    lQuantity[position] = tempQuantity;
                    //lItemTotal[position]=tempQuantity*tempPrice;
                    getTotal();
                    btnMinus.setEnabled(tempQuantity != 0);
                }
            });

            btnMinus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String strQuantity1 = txtQuantity.getText().toString();
                    int tempQuantity1 = Integer.parseInt(strQuantity1);
                    float tempPrice1 = lPrice[position];
                    if (tempQuantity1 > 0) {
                        tempQuantity1--;
                        //Toast.makeText(getApplicationContext(), strQuantity1, Toast.LENGTH_SHORT).show();
                        txtQuantity.setText(String.valueOf(tempQuantity1));
                        lQuantity[position] = tempQuantity1;
                        //lItemTotal[position]=tempQuantity1*tempPrice1;
                        getTotal();
                        btnMinus.setEnabled(tempQuantity1 != 0);
                    }
                }
            });

            txtMenu.setText(lMenu[position]);
            txtPrice.setText(String.format("%.02f", lPrice[position]));
            txtQuantity.setText(Integer.toString(lQuantity[position]));
//            imgMenuImage.setImageResource(lMenuImagesIds[position]);
            Picasso.with(MenuOrderPlacing.this).load(lMenuImages[position]).into(imgMenuImage);
            //Picasso.get().load(lMenuImages[position]).into(imgMenuImage);
            return convertView;
        }
        public void getTotal() {
            lTotal = 0;
            for (int i = 0; i < lMenu.length; i++) {
                float tempTotal = lQuantity[i] * lPrice[i];
                lTotal = lTotal + tempTotal;
            }
            tvTotal.setText("\u20B9" + " " + String.format("%.02f", lTotal));
        }
    }

    private class AsyncCallWS extends AsyncTask<Void, Void, Void> {
        ProgressDialog dialog = new ProgressDialog(MenuOrderPlacing.this);

        @Override
        protected void onPreExecute() {
            dialog.setMessage(getResources().getString(R.string.loading));
            //show dialog
            dialog.show();
            //Log.i(TAG, "onPreExecute");
        }

        @Override
        protected Void doInBackground(Void... params) {
            //Log.i(TAG, "doInBackground");
            if (android.os.Debug.isDebuggerConnected())
                android.os.Debug.waitForDebugger();
            ResultString = WebService.invokeWS();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            //Log.i(TAG, "onPostExecute");
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
            try {
                JSONObject jsonObject = new JSONObject(ResultString);
                alertDialogBuilder = new AlertDialog.Builder(MenuOrderPlacing.this);
                alertDialogBuilder.setTitle("SAVE Message");
                // set dialog message
                alertDialogBuilder
                        .setMessage(jsonObject.getString("Message"))
                        .setCancelable(false)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent(MenuOrderPlacing.this, HomePageGridViewLayout.class);
                                startActivity(intent);
                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();// show it
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
