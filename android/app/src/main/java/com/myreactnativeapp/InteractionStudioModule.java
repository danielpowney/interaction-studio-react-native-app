package com.myreactnativeapp;

import android.util.Log;
import androidx.annotation.NonNull;
import com.evergage.android.Campaign;
import com.evergage.android.CampaignHandler;
import com.evergage.android.Evergage;
import com.evergage.android.Context;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeMap;
import org.json.JSONObject;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.Arguments;

/**
 * Module used to expose Interaction Studio Mobile SDK to React Native JavaScript
 * Based on https://stackoverflow.com/questions/42253397/call-android-activity-from-react-native-code
 * and https://github.com/petterh/react-native-android-activity/blob/master/android/app/src/main/java/com/demo/activity/ActivityStarterModule.java
 */
class InteractionStudioModule extends ReactContextBaseJavaModule {

    private static ReactApplicationContext reactContext;

    private static final String LOG_TAG = "InteractionStudio";

    InteractionStudioModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return "InteractionStudioModule";
    }

    @ReactMethod
    void renderApp() {
        Log.d(LOG_TAG, "renderApp()");
    }

    /**
     * Home screen button click
     */
    @ReactMethod
    void onClick() {

        Evergage evergage = Evergage.getInstance();
        Context screen = Evergage.getInstance().getGlobalContext();

        CampaignHandler handler = new CampaignHandler() {

            @Override
            public void handleCampaign(@NonNull Campaign campaign) {

                Log.d(LOG_TAG, "Handling campaign with name " + campaign.getCampaignName());
                screen.trackImpression(campaign);
                try {
                    JSONObject campaignData = campaign.getData();

                    WritableMap eventData = new WritableNativeMap(); 
                    eventData.putString("param1", campaignData.getString("param1"));  // make sure campaign response contains param1

                    Log.d(LOG_TAG, "Sending campaign response event to React app");
                    reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                        .emit("my_campaign_response", eventData);

                } catch (Exception e) {
                    Log.d(LOG_TAG, e.getMessage());

                }
            }
        };

        screen.setCampaignHandler(handler, "homeScreen"); // make sure campaign has this target
        screen.trackAction("Home sceen button click");


        Log.d(LOG_TAG, "onClick()");

    }
}