package tech.bfitzsimmons.instagramv3;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseACL;

/**
 * Created by Brian on 7/4/2017.
 */

public class ParseApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        String name = "brian";

        //Enable us to store data locally
        Parse.enableLocalDatastore(this);

        //Initialize Parse Server
        Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())
                .applicationId("7568ce85c06814f98dd8756a26e13400da8853c8")
                .clientKey("fb36a2b3a4712b338f79c78c90ccdf9453ef4c01")
                .server("http://ec2-34-228-160-88.compute-1.amazonaws.com:80/parse/")
                .build()
        );


        //Grant read/write permissions
        ParseACL defaultACL = new ParseACL();
        defaultACL.setPublicReadAccess(true);
        defaultACL.setPublicWriteAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);

    }
}
