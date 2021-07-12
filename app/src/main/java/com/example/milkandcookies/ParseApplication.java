package com.example.milkandcookies;

import android.app.Application;

import com.parse.Parse;

public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("1Y3K7Ksj0bdYCrPpQyAzFxT0rFU4EtMq2eQBZprl")
                .clientKey("Z4wzvuLdyJLwli0PMMT9tcwpGoqqcShMsZgUnEaD")
                .server("https://parseapi.back4app.com")
                .build()
        );
    }
}

