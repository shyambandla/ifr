package com.shyam.qupid;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Movie;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.ObservableSnapshotArray;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.WriterException;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    FirebaseUser firebaseuser;
    Intent intent;
    private String[] mPlanetTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mtoggle;
    NavigationView navigationView;
    private FirebaseListAdapter<user> friendlist;
    FirebaseDatabase dbase;
    DatabaseReference dref;
    StorageReference sref;
    String Qrcode;
    ListView listView;
    String myname;
    DatabaseHelper db;
    AlertDialog.Builder alertDialog;
    CircleImageView  display,dpimage;
    @Override
    protected void onStart() {
        super.onStart();
        firebaseuser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseuser == null) {
           // Intent i = new Intent(MainActivity.this, LoginActivity.class);
          //  startActivityForResult(i, 2);
        }
        //  getFriends();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPlanetTitles = getResources().getStringArray(R.array.drawer_items);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getBackground().setAlpha(50);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.navdrawer);
        createFolders();
        File dp;
           try {
               dp = new File("sdcard/hello/wallpaper/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + ".jpeg");

               if (!dp.exists()) {
                   mDrawerLayout.setBackgroundResource(R.drawable.smartphones);
               } else {
                   Resources res = getResources();
                   Bitmap image = BitmapFactory.decodeFile(dp.getPath());
                   BitmapDrawable bitmapDrawable = new BitmapDrawable(res, image);

                   View hView = navigationView.getHeaderView(0);
                   display = (CircleImageView) hView.findViewById(R.id.displaypicture);
                   display.setImageDrawable(bitmapDrawable);
                   mDrawerLayout.setBackground(bitmapDrawable);
               }
           }catch (NullPointerException except){
               Toast.makeText(getApplicationContext(),except.getMessage(),Toast.LENGTH_LONG).show();
               View hView = navigationView.getHeaderView(0);
               display = (CircleImageView) hView.findViewById(R.id.displaypicture);
               display.setImageResource(R.drawable.smartphones);
           }
           display.setOnLongClickListener(new View.OnLongClickListener() {
               @Override
               public boolean onLongClick(View v) {
                   CropImage.activity()
                           .setGuidelines(CropImageView.Guidelines.ON)
                           .start(MainActivity.this);

                   //intent = new   Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                   //startActivityForResult(intent, 1);
                   return false;
               }
           });
       //}catch (NullPointerException except){
       //    Toast.makeText(this,except.getLocalizedMessage(),Toast.LENGTH_LONG).show();
      // }
     // display.setImageResource(R.drawable.ic_launcher_foreground);
       sref= FirebaseStorage.getInstance().getReference();
      /* display.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
              Intent intent= new Intent();
              // intent.setAction(Intent.ACTION_GET_CONTENT);
              // startActivityForResult(Intent.createChooser(intent, "Select Picture"),1);
           }
       });*/

        mtoggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        if (firebaseuser != null) {
            dref = dbase.getInstance().getReference().child(firebaseuser.getUid()).child("friends");

        }
        mDrawerLayout.setDrawerListener(mtoggle);
        alertDialog = new AlertDialog.Builder(this);


        mtoggle.syncState();

        listView = (ListView) findViewById(R.id.mylist);
        db = new DatabaseHelper(this);

                try{
                friendlist = new FirebaseListAdapter<user>(getApplicationContext(), user.class, R.layout.custom_list, FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("friends")) {
                    @Override
                    protected void populateView(View v, user model, int position) {
                        final user fr=model;
                        StorageReference fsr=FirebaseStorage.getInstance().getReference().child("users").child(model.getUserid()).child(model.getUserid()+".jpeg");
                        TextView username = (TextView) v.findViewById(R.id.username);
                        final TextView lastmessage = (TextView) v.findViewById(R.id.lastmessage);
                        final CircleImageView dp=(CircleImageView)v.findViewById(R.id.dp);
                       final File frpic=new File("/sdcard/hello/.frdp/"+model.getUserid()+".jpeg");
                        if(frpic.exists()){
                            Bitmap Drawable=BitmapFactory.decodeFile(frpic.getPath());
                            Resources res=getResources();
                            BitmapDrawable bitmapDrawable=new BitmapDrawable(res,Drawable);
                            dp.setImageDrawable(bitmapDrawable);
                        }else{
                            fsr.getFile(frpic).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                    Bitmap Drawable=BitmapFactory.decodeFile(frpic.getPath());
                                    Resources res=getResources();
                                    BitmapDrawable bitmapDrawable=new BitmapDrawable(res,Drawable);
                                    dp.setImageDrawable(bitmapDrawable);
                                    Toast.makeText(getApplicationContext(),taskSnapshot.toString(),Toast.LENGTH_LONG).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(model.getUserid());
                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                lastmessage.setText(dataSnapshot.getValue().toString());

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        username.setText(model.getName()); 
                        lastmessage.setText("hidden");

                    }
                };

                friendlist.notifyDataSetChanged();
                listView.setAdapter(friendlist);

            }catch (NullPointerException except){

            }

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                  Intent intent=new Intent(MainActivity.this,ChatActivity.class);
                  intent.putExtra("frienduid",friendlist.getItem(position).getUserid());
                  intent.putExtra("friendname",friendlist.getItem(position).getName());
                  intent.putExtra("myname",myname);
                  startActivity(intent);
                }
            });
            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long id) {

                     alertDialog.setNegativeButton("Remove friend", new DialogInterface.OnClickListener() {
                         @Override
                         public void onClick(DialogInterface dialogInterface, int i) {
                             FirebaseDatabase.getInstance().getReference("users").child(firebaseuser.getUid()).child("friends").child(friendlist.getItem(position).getUserid()).removeValue();
                             FirebaseDatabase.getInstance().getReference("users").child(friendlist.getItem(position).getUserid()).child("friends").child(firebaseuser.getUid()).removeValue();
                             FirebaseDatabase.getInstance().getReference("users").child(firebaseuser.getUid()).child("chatmessages").child(friendlist.getItem(position).getUserid()).removeValue();
                             FirebaseDatabase.getInstance().getReference("users").child(friendlist.getItem(position).getUserid()).child("chatmessages").child(firebaseuser.getUid()).removeValue();
                             Toast.makeText(MainActivity.this, "Friend removed", Toast.LENGTH_SHORT).show();
                         }
                     });
                     alertDialog.setNeutralButton("delete chat", new DialogInterface.OnClickListener() {
                         @Override
                         public void onClick(DialogInterface dialogInterface, int i) {
                             FirebaseDatabase.getInstance().getReference("users").child(firebaseuser.getUid()).child("chatmessages").child(friendlist.getItem(position).getUserid()).removeValue();
                             FirebaseDatabase.getInstance().getReference("users").child(friendlist.getItem(position).getUserid()).child("chatmessages").child(firebaseuser.getUid()).removeValue();
                         }
                     });
                     alertDialog.create().show();
                    return true;
                }
            });
            try {
               // changelastmessage();
            }catch (NullPointerException except){
                Toast.makeText(this, "Unable to update last message", Toast.LENGTH_SHORT).show();
            }

    }
     public void changelastmessage(){
        for (int i=0;i<=friendlist.getCount();i++){
            final int j=i;
            FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("chatmessages").child(friendlist.getItem(i).getUserid()).limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    ChatMessage cur=dataSnapshot.getValue(ChatMessage.class);
                   TextView textView= (TextView)listView.getChildAt(j).findViewById(R.id.lastmessage);
                   textView.setText(cur.getMessage());

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
     }
     public void createFolders(){
         File maindir=new File("/sdcard/hello/wallpaper");
         File tempdir=new File("/sdcard/hello/.temp");
         maindir.mkdirs();
         tempdir.mkdirs();
     }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                File sourceLocation= new File (resultUri.getPath());
                Toast.makeText(getApplicationContext(),resultUri.getPath(),Toast.LENGTH_LONG).show();
                File targetLocation= new File ("/sdcard/hello/.temp/"+FirebaseAuth.getInstance().getCurrentUser().getUid()+".jpeg");
                InputStream in;
                OutputStream out;
                try {
                    in = new FileInputStream(sourceLocation);
                    out = new FileOutputStream(targetLocation);
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                    in.close();
                    out.close();
                }catch (IOException except){
                    Toast.makeText(this,except.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                }

                try {
                    File compress;
                    compress = new Compressor(this).setDestinationDirectoryPath("/sdcard/hello/wallpaper").compressToFile(targetLocation);
                }catch (IOException except){
                    Toast.makeText(getApplicationContext(),except.getMessage(),Toast.LENGTH_LONG).show();
                }
                // Copy the bits from instream to outstream
                File dpurif=new File("/sdcard/hello/wallpaper/"+FirebaseAuth.getInstance().getCurrentUser().getUid()+".jpeg");
                Uri dpuri=Uri.fromFile(dpurif);
                StorageReference dpref=FirebaseStorage.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()+".jpeg");
                dpref.putFile(dpuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot){
                        Toast.makeText(getApplicationContext(),"updated dp",Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                })
                ;


                Bitmap bitmap = BitmapFactory.decodeFile(resultUri.getPath());
                Drawable drawable = new BitmapDrawable(bitmap);
                display.setImageDrawable(drawable);
                mDrawerLayout.setBackground(drawable);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }else if (requestCode == 2) {
            if (resultCode == Activity.RESULT_OK) {
               /* Uri uri=data.getData();
                display.setImageURI(uri);
                Toast.makeText(this,"completed",Toast.LENGTH_SHORT).show();*/

            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }else if(requestCode==1){
            if(resultCode==Activity.RESULT_OK){

                Uri uri = data.getData();
                String[] projection = {MediaStore.Images.Media.DATA};

                Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(projection[0]);
                String filepath = cursor.getString(columnIndex);

                File sourceLocation= new File (filepath);
                File targetLocation= new File ("/sdcard/hello/.temp/dp.jpeg");
                InputStream in;
                OutputStream out;
               try {
                  in = new FileInputStream(sourceLocation);
                  out = new FileOutputStream(targetLocation);
                   byte[] buf = new byte[1024];
                   int len;
                   while ((len = in.read(buf)) > 0) {
                       out.write(buf, 0, len);
                   }
                   in.close();
                   out.close();
               }catch (IOException except){
                   Toast.makeText(this,except.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
               }

               try {
                   File compress;
                           compress = new Compressor(this).setDestinationDirectoryPath("/sdcard/hello/wallpaper").compressToFile(targetLocation);
               }catch (IOException except){
                     Toast.makeText(getApplicationContext(),except.getMessage(),Toast.LENGTH_LONG).show();
               }
                // Copy the bits from instream to outstream
                File dpurif=new File("/sdcard/hello/wallpaper/dp.jpeg");
                Uri dpuri=Uri.fromFile(dpurif);
               StorageReference dpref=FirebaseStorage.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("dp");
               dpref.putFile(dpuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                   @Override
                   public void onSuccess(UploadTask.TaskSnapshot taskSnapshot){
                       Toast.makeText(getApplicationContext(),"updated dp",Toast.LENGTH_SHORT).show();
                   }
               }).addOnFailureListener(new OnFailureListener() {
                   @Override
                   public void onFailure(@NonNull Exception e) {
                       Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                   }
               })
               ;
                cursor.close();

                Bitmap bitmap = BitmapFactory.decodeFile(filepath);
                Drawable drawable = new BitmapDrawable(bitmap);
                display.setImageDrawable(drawable);
                mDrawerLayout.setBackground(drawable);
            }
        }
    }//onActivityResult


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (item.isChecked()) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
            return false;
        }
        switch (id) {
            case R.id.chat:
                intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                break;
            case R.id.makefriend:
                intent = new Intent(getApplicationContext(), makefriend.class);
                intent.putExtra("Qrcode", Qrcode);
                startActivity(intent);
                break;
            case R.id.settings:
                intent= new Intent(getApplicationContext(),SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.status:
                intent=new Intent(getApplicationContext(),Status.class);
                startActivity(intent);
                break;
        }
        return true;
    }
}
