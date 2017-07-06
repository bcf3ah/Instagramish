package tech.bfitzsimmons.instagramv3;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class PhotoListActivity extends AppCompatActivity {

    //Create list to hold PhotoListItems
    List<PhotoListItem> photoListItems = new ArrayList<>();
    //create adapter using PhotoListItemAdapter
    PhotoListItemAdapter adapter;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_list);

        //get the username from the intent. We'll use this to query Parse
        Intent intent = getIntent();
        String username = intent.getStringExtra("username");

        //set up recycler view for users list
        recyclerView = (RecyclerView) findViewById(R.id.photoRecyclerView);
        recyclerView.setHasFixedSize(true);//better performance, since we know the size of each list item won't change
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(manager);
        //apply the newly populated adapter to the recycler view
        adapter = new PhotoListItemAdapter(photoListItems);
        recyclerView.setAdapter(adapter);

        //start our Parse query to the Image class
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Image");
        //get only the passed user's photos
        query.whereEqualTo("username", username);
        //order by descending date (so newest up top)
        query.orderByDescending("createdAt");

        //let's get the photos
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() > 0) {
                        for (ParseObject photo : objects) {
                            //Get the creation date of the photo
                            final String createdAt = new SimpleDateFormat("MMMM yyyy").format(photo.getCreatedAt());
                            //get the ParseFile image
                            final ParseFile imageFile = photo.getParseFile("image");
                            //get the image caption
                            final String caption = photo.getString("caption");
                            imageFile.getDataInBackground(new GetDataCallback() {
                                @Override
                                public void done(byte[] data, ParseException e) {
                                    if (e == null) {
                                        //if the user DOES have photos, decode the byteArray data, convert it to bitmap, add the imageView to the layout
                                        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                                        //we've got the image bitmap and the creation date. Now let's add it to the photoListItems array
                                        photoListItems.add(new PhotoListItem(createdAt, bitmap, caption));
                                        //update the adapter
                                        adapter.notifyDataSetChanged();
                                    } else {
                                        Toast.makeText(PhotoListActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }
                }
            }
        });


    }
}
