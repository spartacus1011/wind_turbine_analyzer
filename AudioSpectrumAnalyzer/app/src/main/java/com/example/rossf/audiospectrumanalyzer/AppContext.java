package com.example.rossf.audiospectrumanalyzer;

import android.content.Context;

//This is hella cheaty, but works
public class AppContext extends android.app.Application
{
    private static AppContext mApp = null;
    /* (non-Javadoc)
     * @see android.app.Application#onCreate()
     */
    @Override
    public void onCreate()
    {
        super.onCreate();
        mApp = this;
    }
    public static Context context()
    {
        return mApp.getApplicationContext();
    }
}