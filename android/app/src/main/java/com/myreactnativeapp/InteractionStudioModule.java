package com.myreactnativeapp;

import android.util.Log;
import androidx.annotation.NonNull;
import com.evergage.android.Campaign;
import com.evergage.android.CampaignHandler;
import com.evergage.android.Evergage;
import com.evergage.android.Context;
import com.evergage.android.Screen;
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
 * See https://reactnative.dev/docs/native-modules-android
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

        /*
         * Mobile data campaign handler for Home Banner 1. Make sure the campaign targets pages with the 
         * View Home action. A JavaScript event is dispatched to the React app when a campaign response is returned.
         */
        CampaignHandler homeBanner1CampaignHandler = new CampaignHandler() {

            @Override
            public void handleCampaign(@NonNull Campaign campaign) {

                Log.d(LOG_TAG, "Handling campaign: " + campaign.getCampaignName());
                context.trackImpression(campaign);
                try {
                    JSONObject campaignData = campaign.getData();

                    WritableMap eventData = new WritableNativeMap(); 
                    // make sure the campaign JSON data contains headerText and imageURL
                    eventData.putString("headerText", campaignData.getString("headerText"));
                    eventData.putString("imageURL", campaignData.getString("imageURL"));

                    // the React app listens for event with name homeBanner1_ready
                    reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                        .emit("homeBanner1_ready", eventData);

                } catch (Exception e) {
                    Log.d(LOG_TAG, e.getMessage());

                }
            }
        };
        // make sure the mobile data campaign has target homeBanner1
        context.setCampaignHandler(homeBanner1CampaignHandler, "homeBanner1");
    }

    /**
     * Called when the navigation screen changes. Sends view item, view category and custom actions 
     * to Interaction Studio
     */
    @ReactMethod
    void viewScreen(String screenName, String itemId) {

        Evergage evergage = Evergage.getInstance();
        Context context = Evergage.getInstance().getGlobalContext();

        // Reset any timers
        context.viewItem(null);
        context.viewCategory(null);

        // Make sure the process item data from native mobile apps general setting is checked
        if (screenName.equals("Product")) {
            context.viewItem(new Product(itemId));
        } else if (screenName.equals("Category")) {
            context.viewCategory(new Category(itemId));
        } else {
            context.trackAction("View " + screenName);
        }

        Log.d(LOG_TAG, "View " + screenName);
    }

    /**
     * Called when the add to cart button is pressed. A callback function is invoked once complete.
     */
    @ReactMethod
    void addToCart(String productId, Integer quantity, Callback callback) {
        Log.d(LOG_TAG, "Product Add to Cart");
        Evergage evergage = Evergage.getInstance();
        Context context = Evergage.getInstance().getGlobalContext();
        // create line item and add to cart
        context.addToCart(new LineItem(new Product(productId), quantity));
        callback.invoke();
    }

}