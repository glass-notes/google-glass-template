package com.thad.rfid_lib.Static;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import static com.thad.rfid_lib.Static.Prefs.DEMO_CART_COLS;
import static com.thad.rfid_lib.Static.Prefs.DEMO_CART_ROWS;
import static com.thad.rfid_lib.Static.Prefs.DEMO_RACK_COLS;
import static com.thad.rfid_lib.Static.Prefs.DEMO_RACK_ROWS;

/**
 * Created by theo on 2/27/18.
 */

public class Utils {

    public enum SOUNDS {ERROR, SUCCESS, CLICK}

    public static String[] getJSONStringKeys(JSONObject json){
        Iterator<String> iter = json.keys();
        List<String> keysList = new ArrayList<String>();
        while(iter.hasNext()) {
            String key = iter.next();
            keysList.add(key);
        }
        String[] keysArray = keysList.toArray(new String[keysList.size()]);
        return keysArray;
    }

    public static String tagToLetter(String tag){return String.valueOf(tag.charAt(0));}
    public static int[] tagToPos(String tag){
        return new int[]{Character.getNumericValue(tag.charAt(1))-1,
                Character.getNumericValue(tag.charAt(2))-1};
    }

    public static int getScreenWidth(Context context){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        Activity activity = (Activity)context;
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    public static int dp_to_pixels(Context context, float dp){
        return (int) ((dp)*context.getResources().getDisplayMetrics().density +0.5f);
    }

    public static String getDateInFormat(){
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
        return df.format(date);
    }

    /* Checks if external storage is available for read and write */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public static int countInHashmap(HashMap<String, Integer> map){
        int count = 0;
        for(String key : map.keySet()){
            count += map.get(key);
        }
        return count;
    }

    public static double distance(int[] p1, int[] p2){
        return Math.sqrt(Math.pow(p1[0]-p2[0],2)+Math.pow(p1[1]-p2[1],2));
    }

    public static Drawable loadDrawableFromAssets(Context context, String filename){
        try {
            InputStream ims = context.getAssets().open(filename);
             return Drawable.createFromStream(ims, null);
        }
        catch(IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static String formatTimestamp(Long time){return formatTimestamp(time, false);}
    public static String formatTimestamp(Long time, boolean showDetail){
        int subsecs = (int)(60*time/1000.)%60;
        int seconds = (int) (time / 1000)%60;
        int minutes = ((int)(time/1000)/60)%60;
        int hours = (int)(time/1000)/60/60;

        String hoursStr = Integer.toString(hours);
        if(hours < 10) hoursStr = "0"+hoursStr;

        String minutesStr = Integer.toString(minutes);
        if(minutes < 10) minutesStr = "0"+minutesStr;

        String secondsStr = Integer.toString(seconds);
        if(seconds < 10) secondsStr = "0"+secondsStr;

        String subsecsStr = Integer.toString(subsecs);
        if(subsecs < 10) subsecsStr = "0"+subsecsStr;

        String timestamp = hoursStr+":"+minutesStr+":"+secondsStr;
        if(showDetail)
            timestamp += ":"+subsecsStr;
        return timestamp;
    }
    public static long decodeTimestamp(String timestamp){
        if(timestamp.length() <= 4)
            return 0l;
        else if(timestamp.charAt(0) == '[')
            timestamp = timestamp.substring(1,timestamp.length()-2);

        String[] times_split = timestamp.split(":");
        if(times_split.length != 4)
            return 0l;

        long ms = Integer.parseInt(times_split[0])*60*60*1000;
        ms += Integer.parseInt(times_split[1])*60*1000;
        ms += Integer.parseInt(times_split[2])*1000;
        ms += (long)(Integer.parseInt(times_split[3])*1000f/60f);
        return ms;
    }

    public static boolean valueInArray (int val, int[] array){
        for(int i = 0 ; i < array.length ; i++)
            if(val == array[i])
                return true;
        return false;
    }

    public static boolean isTagInDemo(String tag){
        String unitTag = String.valueOf(tag.charAt(0));
        int row = Integer.valueOf(String.valueOf(tag.charAt(1)));
        int col = Integer.valueOf(String.valueOf(tag.charAt(2)));
        boolean is_row_in_demo, is_col_in_demo;

        if(unitTag.equals("C")) {
            is_row_in_demo = Utils.valueInArray(row, DEMO_CART_ROWS);
            is_col_in_demo = Utils.valueInArray(col, DEMO_CART_COLS);
        }else{
            is_row_in_demo = Utils.valueInArray(row, DEMO_RACK_ROWS);
            is_col_in_demo = Utils.valueInArray(col, DEMO_RACK_COLS);
        }

        if(unitTag.equals("B"))
            return false;

        Log.d("DEMO_TEST", "Tag, Row, Col -> "+tag+": ("+row+","+col+")");
        Log.d("DEMO_TEST", "Is row "+row+" in demo? "+is_row_in_demo);
        Log.d("DEMO_TEST", "Is col "+col+" in demo? "+is_col_in_demo);
        return is_row_in_demo && is_col_in_demo;
    }

    public static void changeDrawableColor(Drawable drawable, int color_resource){
        if (drawable instanceof ShapeDrawable) {
            // cast to 'ShapeDrawable'
            ShapeDrawable shapeDrawable = (ShapeDrawable) drawable;
            shapeDrawable.getPaint().setColor(color_resource);
        } else if (drawable instanceof GradientDrawable) {
            // cast to 'GradientDrawable'
            GradientDrawable gradientDrawable = (GradientDrawable) drawable;
            gradientDrawable.setColor(color_resource);
        } else if (drawable instanceof ColorDrawable) {
            // alpha value may need to be set again after this call
            ColorDrawable colorDrawable = (ColorDrawable) drawable;
            colorDrawable.setColor(color_resource);
        }
    }
}
