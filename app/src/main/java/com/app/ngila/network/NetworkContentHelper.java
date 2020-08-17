package com.app.ngila.network;

import android.content.Context;

import com.google.gson.Gson;

import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NetworkContentHelper {


    public static AlbatalAdvert[] GetAdverts(Context context) throws Exception {

        String json= AddContentNonLoginNormal( new GetAllPropertyNetworkAction("GetAdverts"));
        //json = json.replaceAll("\\\\","");
        //json = json.replaceAll("\"\\[","[").replaceAll("]\"","]");
        //json=""+json;

        AlbatalAdvert[] data =new Gson().fromJson(json, AlbatalAdvert[].class);

        return data;


    }

    public static String FetchUploadUrl(String key) throws Exception {



        Gson gson = new Gson();
        String json = gson.toJson(new PutUrlAction(key));


        OkHttpClient client =  new OkHttpClient.Builder().connectTimeout(30, TimeUnit.SECONDS).writeTimeout(30, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS).build();
        Request request = new Request.Builder()
                .url(App.Urls.Content).post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json))
                .build();
        Response response = client.newCall(request).execute();
        String url = response.body().string().replace("\"", "");
        App.Log("FetchUploadUrl url "+url);
        return url;
    }

    public static String FetchDownloadUrl(Context context, String key) throws Exception {



        Gson gson = new Gson();
        String json = gson.toJson(new GetUrlAction(key));

        //  App.Log("test RequestS3Url "+json);
        OkHttpClient client =  new OkHttpClient.Builder().connectTimeout(30, TimeUnit.SECONDS).writeTimeout(30, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS).build();
        Request request = new Request.Builder()
                .url(App.Urls.Content).post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json))
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string().replace("\"", "");
    }
    public static AlbatalUser[] GetUsers(Context context) throws Exception {

        String json= AddContentNonLoginNormal( new GetAllPropertyNetworkAction("GetUsers"));
        json = json.replaceAll("\\\\","");
        json = json.replaceAll("\"\\[","[").replaceAll("]\"","]");
        //json=""+json;

        AlbatalUser[] data =new Gson().fromJson(json, AlbatalUser[].class);


        return data;

    }

    public static AlbatalMessage[] GetMessagesTo(Context context, String to) throws Exception {

        SendMessageNetworkAction mSendMessageNetworkAction = new SendMessageNetworkAction("GetMsgTo");
        mSendMessageNetworkAction.setTo(to);
        String json= AddContentNonLoginNormal(mSendMessageNetworkAction);
        //json = json.replaceAll("\\\\","");
        /*json = json.replaceAll("\"\\[","[").replaceAll("]\"","]")

                .replaceAll("\"\\{","{").replaceAll("\\}\"","\\}");*/
        //.replaceAll(", \"lo\"","}");
        //json=""+json;

        App.Log("GetMessagesTo to "+json);
        AlbatalMessage[] data =new Gson().fromJson(json, AlbatalMessage[].class);

        return data;

    }

    public static AlbatalMessagePretty[] GetMessagesToDistinct(Context context, String to) throws Exception {

        SendMessageNetworkAction mSendMessageNetworkAction = new SendMessageNetworkAction("GetMsgToDistinct");
        mSendMessageNetworkAction.setTo(to);
        String json= AddContentNonLoginNormal(mSendMessageNetworkAction);
        json = json.replaceAll("\\\\","");
        json = json.substring(1,json.length()-1);
        //json = json.replaceAll("\"\\[","[").replaceAll("]\"","]") .replaceAll("\"\\{","{").replaceAll("\\}\"","\\}");
        //.replaceAll(", \"lo\"","}");
        //json=""+json;

        App.Log("GetMessagesTo to "+json);
        AlbatalMessagePretty[] data =new Gson().fromJson(json, AlbatalMessagePretty[].class);

        return data;

    }
    public static Boolean AccountExists(Context context, String to) throws Exception {

        SendMessageNetworkAction mSendMessageNetworkAction = new SendMessageNetworkAction("UserExits");
        mSendMessageNetworkAction.setTo(to);
        String json= AddContentNonLoginNormal(mSendMessageNetworkAction);
        json = json.substring(1,json.length()-1);

        App.Log("AccountExists to "+json);
        return json.equals("True");
    }
    public static AlbatalMessageComment[] GetMessagesToComments(Context context, String to) throws Exception {

        SendMessageNetworkAction mSendMessageNetworkAction = new SendMessageNetworkAction("GetMsgTo");
        mSendMessageNetworkAction.setTo(to);
        String json= AddContentNonLoginNormal(mSendMessageNetworkAction);
        //json = json.replaceAll("\\\\","");
        /*json = json.replaceAll("\"\\[","[").replaceAll("]\"","]")

                .replaceAll("\"\\{","{").replaceAll("\\}\"","\\}");*/
        //.replaceAll(", \"lo\"","}");
        //json=""+json;

        App.Log("GetMessagesTo to "+json);
        AlbatalMessageComment[] data =new Gson().fromJson(json, AlbatalMessageComment[].class);

        return data;

    }
    public static void DeleteMessagesTo(Context context, String to) throws Exception {

        SendMessageNetworkAction mSendMessageNetworkAction = new SendMessageNetworkAction("DeleteMsg");
        mSendMessageNetworkAction.setTo(to);
        String json= AddContentNonLoginNormal(mSendMessageNetworkAction);
        App.Log("DeleteMessagesTo "+json);


    }
    public static AlbatalAdvert[] GetAdverts(Context context) throws Exception {

            String json= AddContentNonLoginNormal( new GetAllPropertyNetworkAction("GetAdverts"));
            //json = json.replaceAll("\\\\","");
            //json = json.replaceAll("\"\\[","[").replaceAll("]\"","]");
            //json=""+json;

        AlbatalAdvert[] data =new Gson().fromJson(json, AlbatalAdvert[].class);

            return data;


    }

    public static Property[] GetPropertiesFrom(Context context, String seller) throws Exception {
        String city = Utils.GetString(App.City,context);
        String country = Utils.GetString(App.Country,context);

            String json= AddContentNonLoginNormal( new GetAllPropertyNetworkActionFrom(seller));
            json = json.replaceAll("\\\\","");
            json = json.replaceAll("\"\\[","[").replaceAll("]\"","]");

            App.Log("GetPropertiesFrom "+json);
            //json=""+json;

            Property[] properties =new Gson().fromJson(json, Property[].class);;

            /*
            for (Property property:properties
                 ) {
                for (String img:property.getImages()
                     ) {

                    AlbatalContentHelper.AddContentNonLogin(new MakePublicNetworkAction(img));
                }
            }*/
            return properties;

    }
    public static Property[] GetProperties(Context context) throws Exception {
        String city = Utils.GetString(App.City,context);
        String country = Utils.GetString(App.Country,context);
        if(city==null){

            String json= AddContentNonLoginNormal( new GetAllPropertyNetworkAction());
            //re json = json.replaceAll("\\\\","");
            //re json = json.replaceAll("\"\\[","[").replaceAll("]\"","]");

            App.Log("GetProperties "+json);
            //json=""+json;

            Property[] properties =new Gson().fromJson(json, Property[].class);;

            /*
            for (Property property:properties
                 ) {
                for (String img:property.getImages()
                     ) {

                    AlbatalContentHelper.AddContentNonLogin(new MakePublicNetworkAction(img));
                }
            }*/
            return properties;
        }
        else{

            String json= AddContentNonLoginNormal( new GetPropertyFromNetworkAction(city,country));
            App.Log("GetProperties "+json);
            json = json.replaceAll("\\\\","");
            json = json.replaceAll("\"\\[","[").replaceAll("]\"","]");

            App.Log("GetProperties "+json);
            return new Gson().fromJson(json, Property[].class);
        }
    }

    public static String AddContentNonLogin(NetworkAction userNetworkAction) throws Exception {



        Gson gson = new Gson();
        String json = gson.toJson( userNetworkAction);

          App.Log("test RequestS3Url "+json);
        OkHttpClient client =  new OkHttpClient.Builder().connectTimeout(30, TimeUnit.SECONDS).writeTimeout(30, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS).build();
        Request request = new Request.Builder()
                .url(App.Urls.Content).post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json))
                .build();
        Response response = client.newCall(request).execute();
        String responseString =response.body().string().replace("\"", "");;
        App.Log("AddContent "+responseString);
        return responseString;
    }
    public static String AddContentNonLoginNormal(NetworkAction userNetworkAction) throws Exception {



        Gson gson = new Gson();
        String json = gson.toJson( userNetworkAction);

        //  App.Log("test RequestS3Url "+json);
        OkHttpClient client =  new OkHttpClient.Builder().connectTimeout(30, TimeUnit.SECONDS).writeTimeout(30, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS).build();
        Request request = new Request.Builder()
                .addHeader("Content-Type", "application/json; charset=UTF-8")
                .addHeader("Accept-Encoding", "gzip")
                .url(App.Urls.Content).post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json))
                .build();
        Response response = client.newCall(request).execute();
        String responseString =response.body().string();;
        // App.Log("AddContent "+responseString);
        return responseString;
    }
    public static String AddContent(Context context, NetworkAction userNetworkAction) throws Exception {


        Authentication authentication = AlbatalAccountHelper.FetchAccount(context);

        Gson gson = new Gson();
        String json = gson.toJson( userNetworkAction);

        //  App.Log("test RequestS3Url "+json);
        OkHttpClient client =  new OkHttpClient.Builder().connectTimeout(30, TimeUnit.SECONDS).writeTimeout(30, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS).build();
        Request request = new Request.Builder()
                .addHeader("Authorization",authentication.getIdToken())
                .url(App.Urls.Content).post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json))
                .build();
        Response response = client.newCall(request).execute();
        String responseString =response.body().string().replace("\"", "");;
        App.Log("AddContent "+responseString);
        return responseString;
    }
    public static UserNetworkAction[] GetContent(Context context) throws Exception {


        Authentication authentication = AlbatalAccountHelper.FetchAccount(context);

        Gson gson = new Gson();
        String json = gson.toJson( new GetContentNetworkAction());

        //  App.Log("test RequestS3Url "+json);
        OkHttpClient client =  new OkHttpClient.Builder().connectTimeout(30, TimeUnit.SECONDS).writeTimeout(30, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS).build();
        Request request = new Request.Builder()
                .addHeader("Authorization",authentication.getIdToken())
                .url(App.Urls.Content).post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json))
                .build();
        Response response = client.newCall(request).execute();
        String responseString =response.body().string();
        App.Log("GetContent "+responseString);
        return new Gson().fromJson(responseString, UserNetworkAction[].class);
    }
    public static GiverMessageNetwork[] CheckMessages(Context context, CheckGiverMessageNetwork checkGiverMessageNetwork) throws Exception {


        Authentication authentication = AlbatalAccountHelper.FetchAccount(context);

        Gson gson = new Gson();
        String json = gson.toJson( checkGiverMessageNetwork);

        //  App.Log("test RequestS3Url "+json);
        OkHttpClient client =  new OkHttpClient.Builder().connectTimeout(30, TimeUnit.SECONDS).writeTimeout(30, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS).build();
        Request request = new Request.Builder()
                .addHeader("Authorization",authentication.getIdToken())
                .url(App.Urls.Content).post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json))
                .build();
        Response response = client.newCall(request).execute();
        String responseString =response.body().string();
        App.Log("CheckMessages "+responseString);
        return new Gson().fromJson(responseString, GiverMessageNetwork[].class);
    }
}
