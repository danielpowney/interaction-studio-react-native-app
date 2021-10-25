package com.myreactnativeapp;

import android.app.Application;
import android.content.Context;

import com.facebook.react.PackageList;
import com.facebook.react.ReactApplication;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactNativeHost;
import com.facebook.react.ReactPackage;
import com.facebook.react.shell.MainReactPackage;
import com.facebook.soloader.SoLoader;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import com.evergage.android.Evergage;
import com.evergage.android.ClientConfiguration;
import android.util.Log;
import com.evergage.android.LogLevel;
import com.myreactnativeapp.InteractionStudioReactPackage;
/**
 * To run:
 * react-native run-android
 *
 * To view logs:
 * adb logcat | findstr InteractionStudio
 *
 * If you need to uninstall the app, make sure you uninstall the package via adb:
 * adb uninstall com.myreactnativeapp
 */
public class MainApplication extends Application implements ReactApplication {

  private static final String LOG_TAG = "InteractionStudio";

  private final ReactNativeHost mReactNativeHost =
      new ReactNativeHost(this) {
        @Override
        public boolean getUseDeveloperSupport() {
          return BuildConfig.DEBUG;
        }

        @Override
        protected List<ReactPackage> getPackages() {
          @SuppressWarnings("UnnecessaryLocalVariable")
          List<ReactPackage> packages = new PackageList(this).getPackages();
          packages.add(new InteractionStudioReactPackage());
          return packages;
        }

        @Override
        protected String getJSMainModuleName() {
          return "index";
        }
      };

  @Override
  public ReactNativeHost getReactNativeHost() {
    return mReactNativeHost;
  }

  @Override
  public void onCreate() {

    Evergage.setLogLevel(LogLevel.ALL);
    Evergage.initialize(this);
    
    Evergage evergage = Evergage.getInstance();

    evergage.setUserId("testuser022");

    evergage.start(new ClientConfiguration.Builder()
      .account("dpowney1463884")
      .dataset("engage")
      .usePushNotifications(true)
      .build());

    super.onCreate();
    SoLoader.init(this, /* native exopackage */ false);
    initializeFlipper(this, getReactNativeHost().getReactInstanceManager());

    Log.d(LOG_TAG, "Initialising");

  }


  /**
   * Loads Flipper in React Native templates. Call this in the onCreate method with something like
   * initializeFlipper(this, getReactNativeHost().getReactInstanceManager());
   *
   * @param context
   * @param reactInstanceManager
   */
  private static void initializeFlipper(
      Context context, ReactInstanceManager reactInstanceManager) {
    if (BuildConfig.DEBUG) {
      try {
        /*
         We use reflection here to pick up the class that initializes Flipper,
        since Flipper library is not available in release mode
        */
        Class<?> aClass = Class.forName("com.myreactnativeapp.ReactNativeFlipper");
        aClass
            .getMethod("initializeFlipper", Context.class, ReactInstanceManager.class)
            .invoke(null, context, reactInstanceManager);
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
      } catch (NoSuchMethodException e) {
        e.printStackTrace();
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      } catch (InvocationTargetException e) {
        e.printStackTrace();
      }
    }
  }
}
