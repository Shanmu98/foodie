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
import android.widget.ListView;
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

import java.util.List;

public class Main2Activity extends AppCompatActivity {
    public Button b1;

    public FirebaseAuth mAuth;

    public FirebaseAuth.AuthStateListener mAuthListener;

    public GoogleApiClient mGoogleApiClient;

    public String values;

    public int[] dra={R.drawable.nawab,R.drawable.anandha,R.drawable.lakshmi};

    public ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        final Main2Activity obj=new Main2Activity();
        //String val= getIntent().getExtras().get("hotel").toString();
        b1=(Button)findViewById(R.id.b1);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.food);
        Firebase.setAndroidContext(Main2Activity.this);
        Firebase fm=new Firebase("https://foodtrack-1afcd.firebaseio.com/hotels/Names");
        fm.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists()) {
                    String val = dataSnapshot.getValue(String.class);
                    //Toast.makeText(getApplicationContext(), "values1" + val, Toast.LENGTH_LONG).show();
                    obj.values = val;
                    final String hotel[]=val.split(",");
                    listView=(ListView)findViewById(R.id.l1);
                    listView.setAdapter(new CustomAdap(Main2Activity.this,hotel,dra));
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            Intent i1=new Intent(Main2Activity.this,Main3Activity.class);
                            i1.putExtra("hotelname",hotel[i]);
                            startActivity(i1);
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
                   // Toast.makeText(getApplicationContext()," "+firebaseAuth.getCurrentUser(),Toast.LENGTH_LONG).show();
                    Intent i=new Intent(Main2Activity.this,MainActivity.class);
                    startActivity(i);
                }
            }
        };
       /* b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
            }
        });*/
       /* b1.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View v) {
                                           Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                                                   new ResultCallback<Status>() {
                                                       @Override
                                                       public void onResult(Status status) {
                                                           mAuth.signOut();
                                                           Toast.makeText(getApplicationContext(),"Logged Out",Toast.LENGTH_SHORT).show();
                                                           Intent i=new Intent(Main2Activity.this,MainActivity.class);
                                                           startActivity(i);
                                                       }
                                                   });
                                       }
    });*/
    }
   /* public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        //FirebaseUser currentUser = mAuth.getCurrentUser();
        mAuth.addAuthStateListener(mAuthListener);
        //updateUI(currentUser);
    }*/
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
                                Intent i=new Intent(Main2Activity.this,MainActivity.class);
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
