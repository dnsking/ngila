package com.app.ngila.network;

import android.content.Context;

import com.app.ngila.App;
import com.app.ngila.network.actions.NetworkAction;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NetworkContentHelper {

    public static  Response ApiGatewayCaller(NetworkAction flexcineNetworkAction) throws IOException {
        String json = new Gson().toJson(flexcineNetworkAction);

        //  App.Log("test RequestS3Url "+json);
        OkHttpClient client =  new OkHttpClient.Builder().connectTimeout(30, TimeUnit.SECONDS).writeTimeout(30, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS).build();
        Request request = new Request.Builder()
                // .addHeader("Authorization",token)
                .url(App.Urls.Content).post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json))
                .build();
        Response response = client.newCall(request).execute();
        return response;

    }

    public static String AddContent(Context context, NetworkAction userNetworkAction) throws Exception {


       // Authentication authentication = GivderAccountHelper.FetchAccount(context);

        Gson gson = new Gson();
        String json = gson.toJson( userNetworkAction);

        //  App.Log("test RequestS3Url "+json);
        OkHttpClient client =  new OkHttpClient.Builder().connectTimeout(30, TimeUnit.SECONDS).writeTimeout(30, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS).build();
        Request request = new Request.Builder()
               // .addHeader("Authorization",authentication.getIdToken())
                .url(App.Urls.Content).post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json))
                .build();
        Response response = client.newCall(request).execute();
        String responseString =response.body().string().replace("\"", "");;
        return responseString;
    }
}
