package com.shyam.qupid;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.ObservableSnapshotArray;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class  ChatActivity extends AppCompatActivity {
    private static  String uid;
           String friendname,myname;
    EditText text;
    FloatingActionButton fab;
    public ListView listView;
    FirebaseListAdapter<ChatMessage> messagelist;
    DatabaseReference databaseReference,friendreference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Intent intent = getIntent();
        uid = intent.getStringExtra("frienduid");


        listView = (ListView) findViewById(R.id.chatmessageslist);
        FirebaseDatabase.getInstance().getReference("users").child(uid).child("username").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                friendname=dataSnapshot.getValue(String.class);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("username").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
             myname=dataSnapshot.getValue(String.class);            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        friendreference=FirebaseDatabase.getInstance().getReference("users").child(uid).child("chatmessages").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("chatmessages").child(uid);

         text=(EditText)findViewById(R.id.input);
       // listView.setAdapter(messagelist);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        messagelist=new FirebaseListAdapter<ChatMessage>(getApplicationContext(),ChatMessage.class,R.layout.chatmessage,databaseReference) {
            @Override
            protected void populateView(View v, ChatMessage model, int position) {
                TextView username=(TextView)v.findViewById(R.id.listuser);
                TextView message=(TextView)v.findViewById(R.id.listmessage);
                TextView time=(TextView)v.findViewById(R.id.listtime);
                username.setText(model.getUser());
                message.setText(model.getMessage());
                time.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)",
                        model.getMessagetime()));
            }
        };
        listView.setAdapter(messagelist);
        listView.setSelection(listView.getCount()-1);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!text.getText().toString().contentEquals("")){
                    ChatMessage message=new ChatMessage(text.getText().toString(),myname,new Date().getTime());
                    databaseReference.push().setValue(message);
                    friendreference.push().setValue(message);
                    text.setText("");
                }

            }
        });

    }

  }

