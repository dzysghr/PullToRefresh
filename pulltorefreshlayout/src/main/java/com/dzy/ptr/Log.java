package com.dzy.ptr;

/**
 *
 * Created by dzysg on 2016/4/22 0022.
 */

public class Log
{
    static public boolean enable = true;
    static public String Tag = "ptr";

    public static void d(String tag,String msg)
    {
        if (enable)
            android.util.Log.d(tag,msg);
    }
    public static void e(String tag,String msg)
    {
        if (enable)
            android.util.Log.e(tag,msg);
    }

    public static void i(String tag,String msg)
    {
        if (enable)
            android.util.Log.i(tag,msg);
    }



}
