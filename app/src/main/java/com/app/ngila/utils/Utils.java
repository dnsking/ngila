package com.app.ngila.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.location.Address;
import android.location.Geocoder;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.core.content.PermissionChecker;

import com.app.ngila.App;
import com.app.ngila.R;
import com.app.ngila.data.NgilaUser;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;


public class Utils {


    public static final int PricePerKm = 35;
    public static double[] LatLonFromString(String location){
      String[] locs=  location.split(":");
      return new double[]{Double.parseDouble(locs[0]), Double.parseDouble(locs[1])};
    }
    public static LatLng LatLonOBjFromString(String location){
        String[] locs=  location.split(":");
        return new LatLng(Double.parseDouble(locs[0]), Double.parseDouble(locs[1]));
    }

    public static String LatLonOBjToString(LatLng location){
        return location.latitude+":"+location.longitude;
    }
    public static   BitmapDescriptor CarIcon(Context context){
        int height = 100;
        int width = 100;
        BitmapDrawable bitmapdraw = (BitmapDrawable)context.getResources().getDrawable(R.drawable.pickupcar);
        Bitmap b = bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
        return BitmapDescriptorFactory.fromBitmap(smallMarker);
    }

    public static   BitmapDescriptor CarIconSmall(Context context){
        int height = 100;
        int width = 100;
        BitmapDrawable bitmapdraw = (BitmapDrawable)context.getResources().getDrawable(R.drawable.ic_car);
        Bitmap b = bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
        return BitmapDescriptorFactory.fromBitmap(smallMarker);
    }
    public static   BitmapDescriptor Wave(Context context){
        int height = 100;
        int width = 100;
        BitmapDrawable bitmapdraw = (BitmapDrawable)context.getResources().getDrawable(R.drawable.pngwave);
        Bitmap b = bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
        return BitmapDescriptorFactory.fromBitmap(smallMarker);
    }
    /*
    public static void UploadImage(Context context, String name, File file){

        try {

            App.Log("UploadImage started "+name+" file.getPath() "+file.getPath());

            Utils.SaveBitmapToCache(context,Utils.ScaleBitmapAndKeepRation(BitmapFactory.decodeFile(file.getPath()),120,120),name);

            String url=  AlbatalContentHelper.FetchUploadUrl(name);
            UploadImageToServer(new File(context.getExternalCacheDir(),name),url);

            AlbatalContentHelper.AddContentNonLogin(new MakePublicNetworkAction(name));
            App.Log("UploadImage uploaded "+name);
        }
        catch (Exception ex){
            App.Log("UploadImage failed "+ex.getMessage());
        }
    }
    public static void UploadImage500(Context context, String name, File file){

        try {

            App.Log("UploadImage started "+name+" file.getPath() "+file.getPath());

            Utils.SaveBitmapToCache(context,Utils.ScaleBitmapAndKeepRation(BitmapFactory.decodeFile(file.getPath()),500,500),name);

            String url=  AlbatalContentHelper.FetchUploadUrl(name);
            UploadImageToServer(new File(context.getExternalCacheDir(),name),url);

            AlbatalContentHelper.AddContentNonLogin(new MakePublicNetworkAction(name));
            App.Log("UploadImage uploaded "+name);
        }
        catch (Exception ex){
            App.Log("UploadImage failed "+ex.getMessage());
        }
    }*/

    public static String ConvertSecondsToHMmSs(long milisec) {
        long seconds=milisec/1000;
        long s = seconds % 60;
        long m = (seconds / 60) % 60;
        long h = (seconds / (60 * 60)) % 24;
        return String.format("%d:%02d:%02d", h,m,s);
    }
    public static String GetUserCountry(Context context) {
        try {
            final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            final String simCountry = tm.getSimCountryIso();
            if (simCountry != null && simCountry.length() == 2) { // SIM country code is available
                return simCountry.toLowerCase(Locale.US);
            }
            else if (tm.getPhoneType() != TelephonyManager.PHONE_TYPE_CDMA) { // device is not 3G (would be unreliable)
                String networkCountry = tm.getNetworkCountryIso();
                if (networkCountry != null && networkCountry.length() == 2) { // network country code is available
                    return networkCountry.toLowerCase(Locale.US);
                }
            }
        }
        catch (Exception e) { }
        return null;
    }
    public static Drawable CircleFromColor(int color){

        ShapeDrawable shape = new ShapeDrawable(new OvalShape());
        shape.getPaint().setColor(color);
        return shape;
    }


    public static void SaveUserName(Context context, String userName){

        SharedPreferences mySharedPreferences =context. getSharedPreferences(App.Prefs, Activity.MODE_PRIVATE);

        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putString(App.User ,userName);
        editor.apply();
    }

    public static String GetUserName(Context context){

        SharedPreferences mySharedPreferences =context. getSharedPreferences(App.Prefs, Activity.MODE_PRIVATE);
        return mySharedPreferences.getString(App.User,null);
    }
    public static double RoundOff(double a){
        return  Math.round(a * 100.0) / 100.0;
    }
    public static void SendDeviceToServer(final Context context){


        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {

                        final String token = task.getResult().getToken();

                        App.Log("Device Id "+token);

                        /*
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try{

                                    App.Log("SendDeviceToServer ");
                                    String result = AlbatalContentHelper.AddContentNonLogin(new AddUserLocationNetworkAction(
                                            Utils.GetUserName(context),
                                            token
                                    ));

                                    App.Log("SendDeviceToServer "+result);
                                    AlbatalContentHelper.AddContentNonLogin(new AddPhoneDeviceIdNetworkAction(Utils.GetUserName(context),token));
                                }
                                catch (Exception ex){}
                            }
                        }).start();*/


                    }
                });
    }
    public static void SaveString(String id, Context context, String value){

        SharedPreferences mySharedPreferences =context. getSharedPreferences(App.Prefs, Activity.MODE_PRIVATE);

        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putString(id ,value);
        editor.apply();
    }

    public static String GetString(String id, Context context){

        SharedPreferences mySharedPreferences =context. getSharedPreferences(App.Prefs, Activity.MODE_PRIVATE);
        return mySharedPreferences.getString(id,null);
    }
    public static NgilaUser GetNgilaUser(Context context){
        return new Gson().fromJson(GetString(App.UserData,  context),NgilaUser.class);
    }







    public static void SaveAccountType(Context context, String userName){

        SharedPreferences mySharedPreferences =context. getSharedPreferences(App.Prefs, Activity.MODE_PRIVATE);

        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putString(App.AccountType ,userName);
        editor.apply();
    }

    public static String GetAccountType(Context context){

        SharedPreferences mySharedPreferences =context. getSharedPreferences(App.Prefs, Activity.MODE_PRIVATE);
        return mySharedPreferences.getString(App.AccountType,null);
    }




    public static void SavePassword(Context context, String password){

        SharedPreferences mySharedPreferences =context. getSharedPreferences(App.Prefs, Activity.MODE_PRIVATE);

        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putString(App.Password ,password);
        editor.apply();
    }

    public static String GetPassword(Context context){

        SharedPreferences mySharedPreferences =context. getSharedPreferences(App.Prefs, Activity.MODE_PRIVATE);
        return mySharedPreferences.getString(App.Password,null);
    }
    public static String AdressName(Context context, double lat, double lon) throws IOException {
        Geocoder geo = new Geocoder(context.getApplicationContext(), Locale.getDefault());
        List<Address> addresses = geo.getFromLocation(lat, lon, 1);
        if (addresses.isEmpty()) {
        }
        else {
            if (addresses.size() > 0) {
                if(addresses.get(0).getThoroughfare()!=null)
                    return addresses.get(0).getThoroughfare();
                return  addresses.get(0).getAddressLine(0)  ;
            }
        }
        return "Unknown Location";
    }
    public static String LocationName(Context context, double lat, double lon) throws IOException {
        Geocoder geo = new Geocoder(context.getApplicationContext(), Locale.getDefault());
        List<Address> addresses = geo.getFromLocation(lat, lon, 1);
        if (addresses.isEmpty()) {
        }
        else {
            if (addresses.size() > 0) {
                return  addresses.get(0).getFeatureName() + ", " + addresses.get(0).getLocality() +", " + addresses.get(0).getAdminArea() + ", " + addresses.get(0).getCountryName();
            }
        }
        return null;
    }
    public static String LocationCity(Context context, double lat, double lon) throws IOException {
        Geocoder geo = new Geocoder(context.getApplicationContext(), Locale.getDefault());
        List<Address> addresses = geo.getFromLocation(lat, lon, 1);
        if (addresses.isEmpty()) {
        }
        else {
            if (addresses.size() > 0) {
                return   addresses.get(0).getLocality() ;
            }
        }
        return null;
    }
    public static String LocationCountry(Context context, double lat, double lon) throws IOException {
        Geocoder geo = new Geocoder(context.getApplicationContext(), Locale.getDefault());
        List<Address> addresses = geo.getFromLocation(lat, lon, 1);
        if (addresses.isEmpty()) {
        }
        else {
            if (addresses.size() > 0) {
                return   addresses.get(0).getCountryName() ;
            }
        }
        return null;
    }
    public static String LocationCityAndCountry(Context context, double lat, double lon) throws IOException {
        Geocoder geo = new Geocoder(context.getApplicationContext(), Locale.getDefault());
        List<Address> addresses = geo.getFromLocation(lat, lon, 1);
        if (addresses.isEmpty()) {
        }
        else {
            if (addresses.size() > 0) {
                return   addresses.get(0).getLocality() +", " + addresses.get(0).getAdminArea() + ", " + addresses.get(0).getCountryName();
            }
        }
        return null;
    }
    public static String Country(Context context, double lat, double lon) throws IOException {
        Geocoder geo = new Geocoder(context.getApplicationContext(), Locale.getDefault());
        List<Address> addresses = geo.getFromLocation(lat, lon, 1);
        if (addresses.isEmpty()) {
        }
        else {
            if (addresses.size() > 0) {
                return   addresses.get(0).getCountryName();
            }
        }
        return null;
    }
    public static File GetVideoFile(Context context, String videoId){
        return new File( context.getCacheDir(),videoId);
    }


    public static File FetchTempDir(Context context){
        File file = new File( context.getExternalCacheDir(),"temp");
        if(!file.exists())
            file.mkdirs();
        return file;
    }

    public static String FetchFileName(Context context, Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor =context. getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }
    public static void CopyFile(Context context, Uri uri, File dest) throws IOException {
        OutputStream os = null;
        InputStream is = null;
        try {
             is =context.getContentResolver().openInputStream(uri);
            os = new FileOutputStream(dest);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        } finally {
            is.close();
            os.close();
        }
    }
    public static void CopyFile(File source, File dest) throws IOException {
        InputStream is = null;
        OutputStream os = null;
        try {
            is = new FileInputStream(source);
            os = new FileOutputStream(dest);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        } finally {
            is.close();
            os.close();
        }
    }

    public static void SaveFile(String url, String filePath){
        try {
            URL website = new URL(url);

            FileUtils.copyURLToFile(website, new File(filePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static boolean SelfPermissionGranted(Context context, String permission) {
        // For Android < Android M, self permissions are always granted.
        boolean result = false;

        int targetSdkVersion =  Build.VERSION_CODES.M;
        try {
            final PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
            targetSdkVersion = info.applicationInfo.targetSdkVersion;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (targetSdkVersion >= Build.VERSION_CODES.M) {
                // targetSdkVersion >= Android M, we can
                // use Context#checkSelfPermission
                result = context.checkSelfPermission(permission)
                        == PackageManager.PERMISSION_GRANTED;
            } else {
                // targetSdkVersion < Android M, we have to use PermissionChecker
                result = PermissionChecker.checkSelfPermission(context, permission)
                        == PermissionChecker.PERMISSION_GRANTED;
            }
        }

        return result;
    }



/*





    public static void SavePassword(Context context, String password){

        SharedPreferences mySharedPreferences =context. getSharedPreferences(App.Prefs, Activity.MODE_PRIVATE);

        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putString(App.Password ,password);
        editor.apply();
    }

    public static String GetPassword(Context context){

        SharedPreferences mySharedPreferences =context. getSharedPreferences(App.Prefs, Activity.MODE_PRIVATE);
        return mySharedPreferences.getString(App.Password,null);
    }
    public static void SaveContent(Context context, UserNetworkAction userNetworkAction){


        SharedPreferences mySharedPreferences =context. getSharedPreferences(App.Prefs, Activity.MODE_PRIVATE);

        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putString(App.Password , new Gson().toJson(userNetworkAction));
        editor.apply();
    }

    public static Boolean HasContent(Context context){

        SharedPreferences mySharedPreferences =context. getSharedPreferences(App.Prefs, Activity.MODE_PRIVATE);
        return mySharedPreferences.getString(App.Content,null)!=null;
    }
    public static UserNetworkAction GetContent(Context context){

        SharedPreferences mySharedPreferences =context. getSharedPreferences(App.Prefs, Activity.MODE_PRIVATE);
        return new Gson().fromJson(mySharedPreferences.getString(App.Content,null),UserNetworkAction.class);
    }
    public static Boolean IsFirstRun(Context context){

        SharedPreferences mySharedPreferences =context. getSharedPreferences(App.Prefs, Activity.MODE_PRIVATE);
        return mySharedPreferences.getBoolean(App.FirstRun,true);
    }
    public static void IsFirstRun(Context context, boolean firstRun){

        SharedPreferences mySharedPreferences =context. getSharedPreferences(App.Prefs, Activity.MODE_PRIVATE);

        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putBoolean(App.FirstRun ,firstRun);
        editor.apply();
    }
    public static File FetchFile(Context context, String  videoId){
        return new File( context.getCacheDir(),videoId);
    }

*/
    public static Bitmap ScaleBitmapAndKeepRation(Bitmap source, int newHeight, int newWidth) {
        int sourceWidth = source.getWidth();
        int sourceHeight = source.getHeight();

        // Compute the scaling factors to fit the new height and width, respectively.
        // To cover the final image, the final scaling will be the bigger
        // of these two.
        float xScale = (float) newWidth / sourceWidth;
        float yScale = (float) newHeight / sourceHeight;
        float scale = Math.max(xScale, yScale);

        // Now get the size of the source bitmap when scaled
        float scaledWidth = scale * sourceWidth;
        float scaledHeight = scale * sourceHeight;

        // Let's find out the upper start coordinates if the scaled bitmap
        // should be centered in the new size give by the parameters
        float start = (newWidth - scaledWidth) / 2;
        float top = (newHeight - scaledHeight) / 2;

        // The target rectangle for the new, scaled version of the source bitmap will now
        // be
        RectF targetRect = new RectF(start, top, start + scaledWidth, top + scaledHeight);

        // Finally, we create a new bitmap of the specified size and draw our new,
        // scaled bitmap onto it.
        Bitmap dest = Bitmap.createBitmap(newWidth, newHeight, source.getConfig());
        Canvas canvas = new Canvas(dest);
        canvas.drawBitmap(source, null, targetRect, null);

        return dest;
    }
    public static Uri GetImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Images.Media._ID },
                MediaStore.Images.Media.DATA + "=? ",
                new String[] { filePath }, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            cursor.close();
            return Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }
    public static Bitmap UriToBitmap(Context context, Uri selectedFileUri) {
        try {
            ParcelFileDescriptor parcelFileDescriptor =
                    context. getContentResolver().openFileDescriptor(selectedFileUri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);


            parcelFileDescriptor.close();
            return image;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Bitmap LoadBitmapFromView(View v) {
        Bitmap b = Bitmap.createBitmap( v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.layout(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
        v.draw(c);
        return b;
    }
    public static Bitmap DrawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
    public static void SaveBitmapToCache(Context context, Bitmap bitmap, String name){
        try {
            bitmap.compress(Bitmap.CompressFormat.JPEG,100, new FileOutputStream(new File(context.getExternalCacheDir(),
                    name)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    public static String getRandomString() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 18) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;

    }
    public static void SaveImage(Context context, Bitmap bitmap, @NonNull String name) throws IOException {
        boolean saved;
        OutputStream fos;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentResolver resolver = context.getContentResolver();
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, name);
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/png");
            //   contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, "DCIM/" + IMAGES_FOLDER_NAME);
            Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,     contentValues);
            fos = resolver.openOutputStream(imageUri);
        } else {
            String imagesDir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DCIM).toString() ;

            File file = new File(imagesDir);

            if (!file.exists()) {
                file.mkdir();
            }

            File image = new File(imagesDir, name + ".png");
            fos = new FileOutputStream(image);


            MediaScannerConnection.scanFile(context,
                    new String[] { image.getAbsolutePath() }, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        @Override
                        public void onScanCompleted(String path, Uri uri) {
                            //....
                        }
                    });

        }

        saved = bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        fos.flush();
        fos.close();
    }


    public  static String CopyDirorfileFromAssetManager(Context context, String arg_assetDir, String arg_destinationDir) throws IOException
    {
        //    File sd_path = Environment.getExternalStorageDirectory();
        String dest_dir_path =  addLeadingSlash(arg_destinationDir);
        File dest_dir = new File(dest_dir_path);

        createDir(dest_dir);

        AssetManager asset_manager = context.getApplicationContext().getAssets();
        String[] files = asset_manager.list(arg_assetDir);

        for (int i = 0; i < files.length; i++)
        {

            String abs_asset_file_path = addTrailingSlash(arg_assetDir) + files[i];
            String sub_files[] = asset_manager.list(abs_asset_file_path);

            Log.i("Mitesh","Copying..."+abs_asset_file_path+" to "+arg_destinationDir);
            if (sub_files.length == 0)
            {
                // It is a file
                String dest_file_path = addTrailingSlash(dest_dir_path) + files[i];
                copyAssetFile(context,abs_asset_file_path, dest_file_path);
            } else
            {
                // It is a sub directory
                CopyDirorfileFromAssetManager(context,abs_asset_file_path, addTrailingSlash(arg_destinationDir) + files[i]);
            }
        }

        return dest_dir_path;
    }


    private static void copyAssetFile(Context context, String assetFilePath, String destinationFilePath) throws IOException
    {
        InputStream in = context.getApplicationContext().getAssets().open(assetFilePath);
        OutputStream out = new FileOutputStream(destinationFilePath);

        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0)
            out.write(buf, 0, len);
        in.close();
        out.close();
    }

    private static String addTrailingSlash(String path)
    {
        if (path.charAt(path.length() - 1) != '/')
        {
            path += "/";
        }
        return path;
    }

    private static String addLeadingSlash(String path)
    {
        if (path.charAt(0) != '/')
        {
            path = "/" + path;
        }
        return path;
    }

    private static void createDir(File dir) throws IOException
    {
        if (dir.exists())
        {
            if (!dir.isDirectory())
            {
                Log.i("Lottie","Unable to create directory");
                throw new IOException("Can't create directory, a file is in the way");
            }
        } else
        {
            dir.mkdirs();
            if (!dir.isDirectory())
            {
                Log.i("Lottie","Unable to create directory");
                throw new IOException("Unable to create directory");
            }
        }
    }
    public static void DeleteDirectory(File file) {
        if( file.exists() ) {
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                for(int i=0; i<files.length; i++) {
                    if(files[i].isDirectory()) {
                        DeleteDirectory(files[i]);
                    }
                    else {
                        files[i].delete();
                    }
                }
            }
            file.delete();
        }
    }
    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent px equivalent to dp depending on device density
     */
    public static float convertDpToPixel(float dp, Context context){
        return dp * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent dp equivalent to px value
     */
    public static float convertPixelsToDp(float px, Context context){
        return px / ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }


}
