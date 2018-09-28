package com.shyam.qupid;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import me.dm7.barcodescanner.zxing.ZXingScannerView;


public class makefriend extends AppCompatActivity  {

    Bitmap bitmap;
    ImageView imageView;
    Thread thread;
    Button button,addf;
    Button editText;
    String Qrcode,decodedcode,frienduid,Friendname,myname,myuid;
    public final static int QRcodeWidth = 500 ;
    String EditTextValue;
    private ZXingScannerView mScannerView;
    IntentIntegrator integrator;
    FirebaseUser firebaseUser;
    TextView text;
    int nooffriends,myfriends;
    DatabaseReference dref=FirebaseDatabase.getInstance().getReference("users");
    private AdView adView;
    @Override
    protected void onStart() {
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        try {
            dref.child("qrcodes").child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Qrcode = dataSnapshot.getValue(String.class);
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } catch (NullPointerException exception) {

        }
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_makefriend);
        Intent intent = getIntent();
        Qrcode = intent.getStringExtra("Qrcode");
        integrator=new IntentIntegrator(this);
        button=(Button)findViewById(R.id.button);
        MobileAds.initialize(getApplicationContext(),"ca-app-pub-8765228041891722~3007755260");

        adView=(AdView)findViewById(R.id.ads);
        AdRequest adreq=new AdRequest.Builder().build();
        adView.loadAd(adreq);

// TODO: Add adView to your view hierarchy.
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              integrator.initiateScan();


            }
        });

        try {
            myuid = firebaseUser.getUid();
        }catch (NullPointerException exception){

        }
        imageView = (ImageView) findViewById(R.id.imageView);
        editText = (Button) findViewById(R.id.editText);
         addf=(Button)findViewById(R.id.addfriend);
         addf.setVisibility(View.GONE);
        addf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(Friendname!=null&&myname!=null){
                    addftome();
                    addmetof();
                    newQrwork();
                    newQr();
                    finish();
                }

                File file = new File(Environment.getExternalStorageDirectory() + File.separator + "Qupid/users"+frienduid);
                if(!file.exists()){
                    file.mkdirs();
                }
            }
        });
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    bitmap = TextToImageEncode(Qrcode);
                    imageView.setImageBitmap(bitmap);


                } catch (WriterException e) {
                    e.printStackTrace();
                }
            }
        });
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }




    public Bitmap TextToImageEncode(String Value) throws WriterException {
        BitMatrix bitMatrix;

        try {
            bitMatrix = new MultiFormatWriter().encode(
                    Value,
                    BarcodeFormat.DATA_MATRIX.QR_CODE,
                    QRcodeWidth, QRcodeWidth, null
            );

        } catch (IllegalArgumentException Illegalargumentexception) {

            return null;
        }catch (WriterException writerexception){
            return null;
        }catch (NullPointerException exception){

            Toast.makeText(getApplicationContext(), "try again", Toast.LENGTH_SHORT).show();
            return null;
        }
        int bitMatrixWidth = bitMatrix.getWidth();

        int bitMatrixHeight = bitMatrix.getHeight();

        int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];

        for (int y = 0; y < bitMatrixHeight; y++) {
            int offset = y * bitMatrixWidth;
            for (int x = 0; x < bitMatrixWidth; x++) {

                pixels[offset + x] = bitMatrix.get(x, y) ?
                        getResources().getColor(R.color.QrcodeBlack):getResources().getColor(R.color.Qrcodewhite);
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);

        bitmap.setPixels(pixels, 0, 500, 0, 0, bitMatrixWidth, bitMatrixHeight);
        return bitmap;
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            //if qrcode has nothing in it
            if (result.getContents() == null) {
                Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show();
            } else {
                //if qr contains data
                decodedcode=result.getContents().toString();
                editText.setVisibility(View.GONE);
                button.setVisibility(View.GONE);
                addf.setVisibility(View.VISIBLE);
              addfriend();


            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
        public void addfriend(){
                  try{
            dref.child("Qrdecodes").child(decodedcode).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    frienduid=dataSnapshot.getValue(String.class);
                    if(frienduid!=null) {
                        getMyname();
                        getFriendname();
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(getApplicationContext(),"ERror while reading friend uid",Toast.LENGTH_SHORT).show();
                }
            });}catch ( NullPointerException exception){
                       Toast.makeText(getApplicationContext(),"Exception while getting friend uid",Toast.LENGTH_SHORT).show();
                  }




        }
    @Override
    protected void onPause() {
        super.onPause();

    }
    public void getFriendname(){
        try{
            dref.child(frienduid).child("username").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Friendname = dataSnapshot.getValue(String.class);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });}catch ( NullPointerException exception){
            Toast.makeText(getApplicationContext(), "Exception in getting friend name", Toast.LENGTH_SHORT).show();
        }
    }
    public void getMyname(){
        try{
            dref.child(firebaseUser.getUid() ).child("username").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    myname=dataSnapshot.getValue(String.class);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(getApplicationContext(),"Error Occured",Toast.LENGTH_SHORT).show();
                }
            });}catch ( NullPointerException exception){
            Toast.makeText(getApplicationContext(), "Exception while getting my name", Toast.LENGTH_SHORT).show();
        }

    }
    public void getnooffriendsofme(){
        try{
            dref.child("friends").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {

                        myfriends =(int) dataSnapshot.getChildrenCount();
                    }catch (NumberFormatException except){
                        Toast.makeText(getApplicationContext(),"NUMBER FORMAT EXCEPTION",Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }catch (NullPointerException exception){

        }


    }
    public void getnooffriendsoffriends(){
        try{
            dref.child(frienduid).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    nooffriends = Integer.valueOf(((int) dataSnapshot.getChildrenCount()));
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }catch (NullPointerException except){

        }
    }
     public void addftome(){


             try {
                 user myuser=new user(frienduid,Friendname);
                 getnooffriendsofme();
                 dref.child(firebaseUser.getUid()).child("friends").child(frienduid).setValue(myuser);
             }catch ( NullPointerException exception){
             Toast.makeText(getApplicationContext(),"Exception while adding friend",Toast.LENGTH_SHORT);

             }
     }
     public void addmetof(){
         try{
             user addfriend=new user(firebaseUser.getUid(),myname);
             getnooffriendsoffriends();
         dref.child(frienduid).child("friends").child(firebaseUser.getUid()).setValue(addfriend);
         }catch (NullPointerException exception){

         }
     }
      public void newQrwork(){
          try {
              dref.child("qrcodes").child(frienduid).removeValue();
              dref.child("Qrdecodes").child(decodedcode).removeValue();
          }catch ( NullPointerException exception){
             Toast.makeText(getApplicationContext(),"exception while removing friend data",Toast.LENGTH_SHORT).show();
          }

      }
      public void newQr(){
           try {
               String newpush = dref.push().getKey();
               dref.child("qrcodes").child(frienduid).setValue(newpush);
               dref.child("Qrdecodes").child(newpush).setValue(frienduid);

           }catch ( NullPointerException exception){
               Toast.makeText(getApplicationContext(),"exception while adding friend data",Toast.LENGTH_SHORT).show();
           }

      }

    @Override
    protected void onRestart() {
        super.onRestart();
    }


}
