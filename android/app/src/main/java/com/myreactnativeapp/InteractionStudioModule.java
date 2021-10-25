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

    /**
     * Called when screen changes. Sends a custom action event to Interaction Studio
     */
    @ReactMethod
    void viewScreen(String screenName) {
        Log.d(LOG_TAG, "View " + screenName);

        Evergage evergage = Evergage.getInstance();
        Context context = Evergage.getInstance().getGlobalContext();
        context.trackAction("View " + screenName);
    }

    /**
     * Called when button on home screen is clicked. Sends a custom action event to Interaction Studio
     * and also emits an event with any campaign response data to the React App to display
     */
    @ReactMethod
    void homeBtnClick() {

        Log.d(LOG_TAG, "Home Button Click");

        Evergage evergage = Evergage.getInstance();
        Context context = Evergage.getInstance().getGlobalContext();

        CampaignHandler handler = new CampaignHandler() {

            @Override
            public void handleCampaign(@NonNull Campaign campaign) {

                Log.d(LOG_TAG, "Handling campaign with name " + campaign.getCampaignName());
                context.trackImpression(campaign);
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

        context.setCampaignHandler(handler, "homeScreen"); // make sure campaign has this target
        context.trackAction("Home Button Click");

    }
}