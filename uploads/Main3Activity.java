package com.startup.naveen.foodtrack;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class Main3Activity extends AppCompatActivity {

    public FirebaseAuth abf;

    public String name;

    public FirebaseAuth mAuth;

    public FirebaseAuth.AuthStateListener mAuthListener;

    public GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        abf=FirebaseAuth.getInstance();
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.food);
        final FirebaseUser fbu=abf.getCurrentUser();
        //Toast.makeText(getApplicationContext(),fbu.getDisplayName(),Toast.LENGTH_LONG).show();
        //final LinearLayout rl = (LinearLayout) findViewById(R.id.relative);
        final LinearLayout rl=(LinearLayout) findViewById(R.id.linear);
        final String hoteln = getIntent().getExtras().getString("hotelname");
        Firebase.setAndroidContext(Main3Activity.this);
        Firebase fm=new Firebase("https://foodtrack-1afcd.firebaseio.com/hotels/"+hoteln+"/Menus");
        fm.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists()) {
                    CheckBox cb=null;
                    EditText ed;
                    String val = dataSnapshot.getValue(String.class);
                    //Toast.makeText(getApplicationContext(), "values1" + val, Toast.LENGTH_LONG).show();
                    //obj.values = val;
                    final String food[]=val.split(",");
                    final ArrayList<CheckBox> foodname=new ArrayList<CheckBox>();
                    final ArrayList<EditText> Edit=new ArrayList<EditText>();
                    for(int i=0;i<food.length;i++)
                    {
                        cb=new CheckBox(Main3Activity.this);
                        cb.setLayoutParams(new LinearLayout.LayoutParams(500,100));
                        cb.setText(food[i]);
                        cb.setId(i);

                        ed= new EditText(Main3Activity.this);
                        ed.setLayoutParams(new LinearLayout.LayoutParams(1000,200));
                        ed.setHint("enter no of quantity or addson");
                        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                            }
                        });
                        foodname.add(cb);
                        Edit.add(ed);
                        rl.addView(cb);
                        rl.addView(ed);
                    }
                    Button b1=(Button)findViewById(R.id.b1);
                    final Main3Activity obj=new Main3Activity();
                    b1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String order="";
                            for(int i=0;i<foodname.size();i++)
                            {
                                if(foodname.get(i).isChecked())
                                {
                                    order=order+foodname.get(i).getText()+";";
                                    order=order+Edit.get(i).getText()+",";
                                }
                            }
                            Toast.makeText(Main3Activity.this,"Order successfully placed",Toast.LENGTH_LONG).show();
                            Firebase fm=new Firebase("https://foodtrack-1afcd.firebaseio.com/hotels/"+hoteln+"/customers/"+fbu.getDisplayName());
                            Firebase fg=fm.child("orders");
                           fg.setValue(order);
                            fg=fm.child("nofs");
                            EditText eee=(EditText)findViewById(R.id.ed1);
                            String hh= String.valueOf(eee.getText());
                            fg.setValue(hh);
                            Firebase fm1=new Firebase("https://foodtrack-1afcd.firebaseio.com/hotels/"+hoteln+"/cusname");
                            fm1.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot)
                                {
                                    if(dataSnapshot.exists()) {
                                        String val = dataSnapshot.getValue(String.class);
                                        obj.name = val;
                                        if (obj.name.equals("na")) {
                                            Firebase fm = new Firebase("https://foodtrack-1afcd.firebaseio.com/hotels/" + hoteln);
                                            Firebase fg = fm.child("cusname");
                                            fg.setValue(fbu.getDisplayName());
                                            //Intent i=new Intent(Main3Activity.this,Main2Activity.class);
                                            //startActivity(i);
                                        } else
                                        {
                                            Firebase fm1=new Firebase("https://foodtrack-1afcd.firebaseio.com/hotels/"+hoteln);
                                            Firebase fg1=fm1.child("cusname");
                                            fg1.setValue(obj.name+","+fbu.getDisplayName());
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(FirebaseError firebaseError) {

                                }
                            });
                        }
                       // Toast.makeText(Main3Activity.this," "+firebaseAuth.getCurrentUser(),Toast.LENGTH_LONG).show();

                    });

                    Button b3=(Button)findViewById(R.id.b2);
                    b3.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                           /* if(obj.name.equals("na"))
                            {
                                Firebase fm=new Firebase("https://foodtrack-1afcd.firebaseio.com/hotels/"+hoteln);
                                Firebase fg=fm.child("cusname");
                                fg.setValue(fbu.getDisplayName());
                                //Intent i=new Intent(Main3Activity.this,Main2Activity.class);
                                //startActivity(i);
                            }
                            else
                            {
                                Firebase fm=new Firebase("https://foodtrack-1afcd.firebaseio.com/hotels/"+hoteln);
                                Firebase fg=fm.child("cusname");
                                fg.setValue(obj.name+","+fbu.getDisplayName());*/
                            Intent i = new Intent(Main3Activity.this, MapsActivity.class);
                            i.putExtra("customer", fbu.getDisplayName());
                            i.putExtra("hotel", hoteln);
                            startActivity(i);
                        }

                    });
                    //Intent i=new Intent(Main2Activity.this,Main2Activity.class);
                    //i.putExtra("hotel",obj.values);
                    //Toast.makeText(getApplicationContext(),"values"+obj.values,Toast.LENGTH_LONG).show();
                    //startActivity(i);
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "no data", Toast.LENGTH_LONG).show();

                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        mAuth=FirebaseAuth.getInstance();
        mAuthListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()==null)
                {
                    Toast.makeText(getApplicationContext()," "+firebaseAuth.getCurrentUser(),Toast.LENGTH_LONG).show();
                    Intent i=new Intent(Main3Activity.this,MainActivity.class);
                    startActivity(i);
                }
            }
        };
    }
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menus, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.logout:
            {
                Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                        new ResultCallback<Status>() {
                            @Override
                            public void onResult(Status status) {
                                mAuth.signOut();
                                Toast.makeText(getApplicationContext(),"Logged Out",Toast.LENGTH_SHORT).show();
                                Intent i=new Intent(Main3Activity.this,MainActivity.class);
                                startActivity(i);
                            }
                        });
                //startActivity(new Intent(this, About.class));
                return true;
            }
            case R.id.help:
                //startActivity(new Intent(this, Help.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
    @Override
    protected void onStart() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mGoogleApiClient.connect();
        super.onStart();
    }
}
