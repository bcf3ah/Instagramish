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

        //Enable us to store data locally
        Parse.enableLocalDatastore(this);

        //Initialize Parse Server
        Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())
                .applicationId("APP_ID")
                .clientKey("CLIENT_KEY")
                .server("SERVER_URL")
                .build()
        );


        //Grant read/write permissions
        ParseACL defaultACL = new ParseACL();
        defaultACL.setPublicReadAccess(true);
        defaultACL.setPublicWriteAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);

    }
}
