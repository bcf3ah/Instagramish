package tech.bfitzsimmons.instagramv3;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class UserListActivity extends AppCompatActivity {
    //create list to hold users, which we will pass to RecyclerView.Adapter
    List<UserListItem> users = new ArrayList<>();
    UserListItemAdapter adapter;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //share photo button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //check for user permission for accessing camera's photos. Ask if we don't have permission
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                } else {
                    goToPhoto();
                }
            }
        });

        //set up recycler view for users list
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);//better performance, since we know the size of each list item won't change


        //get all the users from a Parse Query to populate userList
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());
        query.addAscendingOrder("username"); //order list by alphabetical username
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                //check if objects is not null
                if (e == null) {
                    if (objects.size() > 0) {
                        //fill users list with all the users from the response
                        for (ParseUser user : objects) {
                            String createdAt = new SimpleDateFormat("MMMM yyyy").format(user.getCreatedAt());
                            users.add(new UserListItem(user.getUsername(), "Member since " + createdAt));
                        }
                        //apply the newly populated adapter to the recycler view
                        adapter = new UserListItemAdapter(users);
                        RecyclerView.LayoutManager manager = new LinearLayoutManager(getApplicationContext());
                        recyclerView.setLayoutManager(manager);
                        recyclerView.setAdapter(adapter);
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });

        //create click listener for each user list item, which will take them to the user's photo feed via intent

    }

    //Create convenience method to change activity to photo activity
    public void goToPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 1);
    }

    //See if we have permission to use camera's photos. Tied to the requestPermissions method in onOptionsItemSelected above
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            goToPhoto();
        }
    }


    //this method saves accessed camera image to ParseServer after we select the image.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            //grab the image data
            final Uri selectedImage = data.getData();

            //Before we send off the image to Parse Server let's add an Alert Dialog with an EditText so users can add a caption
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            final EditText captionInput = new EditText(UserListActivity.this);
            FrameLayout container = new FrameLayout(UserListActivity.this);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(25, 0, 25, 0);
            captionInput.setLayoutParams(params);
            container.addView(captionInput);
            builder.setView(container);
            builder.setTitle("Enter a caption");

            //dialog box is set up, let's capture the user input, then send both it and the selectedImage to Parse Server
            builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(UserListActivity.this.getContentResolver(), selectedImage);

                        //okay, we have the photo the user selected. Now we need to upload it to our Parse Server db
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();//converts our image to a ParseFile we can pass to Parse Server as a ParseObject
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);//converting our bitmap to a compressed JPEG. Quality scale is 1-100, 100 being highest quality but slow as a mofo!
                        byte[] byteArray = stream.toByteArray(); //converting stream to byte array. TO GET A PARSE FILE, YOU NEED TO USE A BYTE ARRAY!
                        ParseFile file = new ParseFile("image.jpg", byteArray);

                        //create a class to store all user images
                        ParseObject parseObject = new ParseObject("Image");
                        parseObject.put("image", file); //save the ParseFile image to the Image class
                        parseObject.put("username", ParseUser.getCurrentUser().getUsername()); //associate it with the current user's username
                        parseObject.put("caption", captionInput.getText().toString());
                        parseObject.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    Toast.makeText(UserListActivity.this, "Image shared!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(UserListActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            //build the dialog box
            builder.show();
        }
    }

    //inflate sign out menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.user_list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.signout) {
            ParseUser.logOut();
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}
