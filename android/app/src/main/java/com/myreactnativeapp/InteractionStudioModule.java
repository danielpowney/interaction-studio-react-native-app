package com.myreactnativeapp;

import android.util.Log;
import androidx.annotation.NonNull;
import com.evergage.android.Campaign;
import com.evergage.android.CampaignHandler;
import com.evergage.android.Evergage;
import com.evergage.android.Context;
import com.evergage.android.promote.Product;
import com.evergage.android.promote.Category;
import com.evergage.android.promote.LineItem;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
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
        setCampaignHandlers();
    }

    @Override
    public String getName() {
        return "InteractionStudioModule";
    }

    /**
     * Adds campaign handlers
     */
    public void setCampaignHandlers() {

        Evergage evergage = Evergage.getInstance();
        Context context = Evergage.getInstance().getGlobalContext();

        CampaignHandler homeBanner1CampaignHandler = new CampaignHandler() {

            @Override
            public void handleCampaign(@NonNull Campaign campaign) {

                Log.d(LOG_TAG, "Handling campaign: " + campaign.getCampaignName());
                context.trackImpression(campaign);
                try {
                    JSONObject campaignData = campaign.getData();

                    WritableMap eventData = new WritableNativeMap(); 
                    eventData.putString("headerText", campaignData.getString("headerText"));
                    eventData.putString("imageURL", campaignData.getString("imageURL"));

                    reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit("homeBanner1_ready", eventData);

                } catch (Exception e) {
                    Log.d(LOG_TAG, e.getMessage());

                }
            }
        };
        context.setCampaignHandler(homeBanner1CampaignHandler, "homeBanner1"); // make sure campaign has this target



    }

    /**
     * Called when screen changes. Sends an event to Interaction Studio
     */
    @ReactMethod
    void viewScreen(String screenName, String itemId) {

        Evergage evergage = Evergage.getInstance();
        Context context = Evergage.getInstance().getGlobalContext();

        if (screenName == "Product") {
            context.viewItem(new Product(itemId), "View " + screenName);
        } else if (screenName == "Category") {
            context.viewCategory(new Category(itemId), "View " + screenName);
        } else {
            context.trackAction("View " + screenName);
        }

        Log.d(LOG_TAG, "View " + screenName);
    }

    /**
     * Called when add to cart button is pressed
     */
    @ReactMethod
    void addToCart(String productId, Integer quantity, Callback callback) {
        Log.d(LOG_TAG, "Product Add to Cart");
        Evergage evergage = Evergage.getInstance();
        Context context = Evergage.getInstance().getGlobalContext();
        context.addToCart(new LineItem(new Product(productId), quantity));
        callback.invoke();
    }

}