/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Java Endpoints Module" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/HelloEndpoints
*/

package com.example.sylta.myapplication.wmudatastore;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.repackaged.com.google.common.base.Pair;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;

/** An endpoint class we are exposing */
@Api(
  name = "myApi",
  version = "v1",
  namespace = @ApiNamespace(
    ownerDomain = "wmudatastore.myapplication.sylta.example.com",
    ownerName = "wmudatastore.myapplication.sylta.example.com",
    packagePath=""
  )
)
public class MyEndpoint {

    DatastoreService datastore;
    Key userKey;
    Entity user;

    @ApiMethod(name = "saveData")
    public void saveData(UserEntity userInfo){
        //set up datastore and userEntity
        if(datastore == null)
            datastore = DatastoreServiceFactory.getDatastoreService();

        if(userKey == null) {
            userKey = KeyFactory.createKey("User", userInfo.getUserID());
        }
        if(user == null)
            user = new Entity("User", userKey);

        user.setProperty("username", userInfo.getUsername());
        user.setProperty("userID", userInfo.getUserID());
        user.setProperty("tokenID", userInfo.getTokenID());
        user.setProperty("snoozeFreq", userInfo.getSnoozeFreq());

        datastore.put(user);
    }

    //restore user data when open app
    @ApiMethod(name = "loadData")
    public UserEntity loadData(){

        UserEntity userInfo = new UserEntity((String) user.getProperty("username"),
                (String) user.getProperty("userID"),
                (String) user.getProperty("tokenID"),
                ((int) user.getProperty("snoozeFreq")));

        return userInfo;
    }

    @ApiMethod(name = "loadAllUsers")
    public List<UserEntity> loadAllUsers(){
        if(datastore == null)
            datastore = DatastoreServiceFactory.getDatastoreService();

        if(userKey == null) {
            //userKey = KeyFactory.createKey("User", userInfo.getUserID());
        }
        if(user == null)
            user = new Entity("User", userKey);

        Query q = new Query("User");
        PreparedQuery pq = datastore.prepare(q);

        List<UserEntity> userList = new ArrayList<>();

        userList.add(new UserEntity("WRONG", "DELETE", "THIS", 269)); //for testing

        for (Entity result : pq.asIterable()) {
            String username = (String) result.getProperty("username");
            String userID = (String) result.getProperty("userID");
            String tokenID = (String) result.getProperty("tokenID");
            int snoozeFreq = (int) result.getProperty("snoozeFreq");

            userList.add(new UserEntity(username, userID, tokenID, snoozeFreq));
        }

        return userList;
    }


    /** A simple endpoint method that takes a name and says Hi back *//*
    @ApiMethod(name = "sayHi")
    public UserEntity sayHi(@Named("Kame") String name) {
        UserEntity response = new UserEntity();
        response.setUsername("Hii, " + name);

        saveData(response);

        return response;
    }
    */
}
