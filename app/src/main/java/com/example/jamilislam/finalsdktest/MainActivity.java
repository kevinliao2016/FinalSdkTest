package com.example.jamilislam.finalsdktest;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import com.driversiti.driversitisdk.ApioCloud.OnCompleteTaskHandler;
import com.driversiti.driversitisdk.driversiti.Driversiti;
import com.driversiti.driversitisdk.driversiti.DriversitiConfiguration;
import com.driversiti.driversitisdk.driversiti.DriversitiEventListener;
import com.driversiti.driversitisdk.driversiti.DriversitiException;
import com.driversiti.driversitisdk.driversiti.DriversitiSDK;
import com.driversiti.driversitisdk.driversiti.UserManager;
import com.driversiti.driversitisdk.driversiti.data.User;
import com.driversiti.driversitisdk.driversiti.data.UserBuilder;
import com.driversiti.driversitisdk.driversiti.event.CarModeEvent;
import com.driversiti.driversitisdk.driversiti.event.CrashDetectedEvent;
import com.driversiti.driversitisdk.driversiti.event.DriverDeviceHandlingEvent;
import com.driversiti.driversitisdk.driversiti.event.GenericDeviceHandlingEvent;
import com.driversiti.driversitisdk.driversiti.event.HardBrakeEvent;
import com.driversiti.driversitisdk.driversiti.event.LaneChangeEvent;
import com.driversiti.driversitisdk.driversiti.event.PassengerDeviceHandlingEvent;
import com.driversiti.driversitisdk.driversiti.event.RapidAccelerationEvent;
import com.driversiti.driversitisdk.driversiti.event.SpeedExceededEvent;
import com.driversiti.driversitisdk.driversiti.event.SpeedRestoredEvent;
import com.driversiti.driversitisdk.driversiti.event.TripEndEvent;
import com.driversiti.driversitisdk.driversiti.event.TripStartEvent;

import static java.util.UUID.randomUUID;


public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    UserManager mUserManager;
    DriversitiSDK mDriversitiSDK;
    DriversitiEventListener mListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DriversitiConfiguration driversitiConfiguration = new DriversitiConfiguration.ConfigurationBuilder()
                .setContext(this)
                .setApplicationId("")
                .setDetectionMode(DriversitiConfiguration.DetectionMode.AUTO_ON)
                .setEnabledEvents(DriversitiConfiguration.getEnabledEvents())
                .setEnableVehicleIdentification(true)
                .setSetupHandler(new DriversitiConfiguration.SetupHandler() {
                    @Override
                    public void onSetupSuccess() {
                        Log.i(LOG_TAG, "Driversiti setup successfull");
                    }

                    @Override
                    public void onSetupFailure(DriversitiException errorMessage) {
                        Log.i(LOG_TAG, "Driversiti setup error: " + errorMessage);
                    }
                })
                .build();
        Button registrationButton = (Button) findViewById(R.id.register_button);


        Driversiti.setConfiguration(driversitiConfiguration);
        mDriversitiSDK = Driversiti.getSDK();
        mListener = getDriversitiEventListener();
        mUserManager = mDriversitiSDK.getUserManager();
        registrationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final User userRegistrationRequest = getApioUserData();
                OnCompleteTaskHandler callback = new OnCompleteTaskHandler() {
                    @Override
                    public void onSuccess(Object result) {
                        User newUser = null;
                        if (result != null && result instanceof User) {
                            newUser = (User) result;
                        } else {
                            Log.e(LOG_TAG, "onSuccess() returned a null result, this shouldn't happen...");
                            this.onFailure(new DriversitiException("Error in create user response, unable to parse a null User object"));
                            return;
                        }
                        Log.d(LOG_TAG, "onSuccess() returned a newUser: " + newUser.toString());
                        //Set the unique Id

                        if(mUserManager == null){
                            mUserManager = Driversiti.getSDK().getUserManager();
                        }
                        // Log the user in.
                        mUserManager.loginUser(newUser);


                    }

                    @Override
                    public void onFailure(Exception errorMessage) {
                        Log.e(LOG_TAG, "Exception during User Registration: " + errorMessage.getMessage(), errorMessage);
                        Toast.makeText(getApplicationContext(), "Unable to register, please try again: " + errorMessage.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                };
                mUserManager.addUser(userRegistrationRequest, callback);

            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        mDriversitiSDK.addEventListener(mListener);
    }


    @Override
    protected void onPause() {
        super.onPause();
        mDriversitiSDK.removeEventListener(mListener);
    }

    private User getApioUserData(){
        User user = new UserBuilder()
                .setFirstName("android")
                .setLastName("dev")
                .setPassword("drive")
                .setEmail(randomUUID() + "@publicdriversiti.com")
                .setUsername(randomUUID() + "@publicdriversiti.com")
                .setCompanyId("Soteria")
                .setCountry("USA").create();
        return user;
    }

    private DriversitiEventListener getDriversitiEventListener() {
        return new DriversitiEventListener("DrawerActivity") {
            @Override
            public void onCarModeStatusChange(final CarModeEvent event) {
                Log.i(LOG_TAG, "event: " + event.getEventType().toString());
            }

            @Override
            public void onRapidAccelerationDetected(final RapidAccelerationEvent event) {
                Log.i(LOG_TAG, "event: " + event.getEventType().toString());
            }

            @Override
            public void onHardBrakingDetected(final HardBrakeEvent event) {
                Log.i(LOG_TAG, "event: " + event.getEventType().toString());
            }

            @Override
            public void onLaneChangingDetected(final LaneChangeEvent event) {
                Log.i(LOG_TAG, "event: " + event.getEventType().toString());
            }

            @Override
            public void onCrashDetected(final CrashDetectedEvent event) {

            }

            @Override
            public void onGenericDeviceHandlingEvent(final GenericDeviceHandlingEvent event) {
                Log.i(LOG_TAG, "event: " + event.getEventType().toString());

            }

            @Override
            public void onDriverDeviceHandlingEvent(final DriverDeviceHandlingEvent event) {
                Log.i(LOG_TAG, "event: " + event.getEventType().toString());

            }

            @Override
            public void onPassengerDeviceHandlingEvent(final PassengerDeviceHandlingEvent event) {
                Log.i(LOG_TAG, "event: " + event.getEventType().toString());

            }

            @Override
            public void onError(final DriversitiException error) {
                Log.e(LOG_TAG, "ApioException: " + error.getMessage());
            }

            @Override
            public void onSpeedExceeded(final SpeedExceededEvent speedExceededEvent) {
                Log.i(LOG_TAG, "event: " + speedExceededEvent.getEventType().toString());

            }

            @Override
            public void onSafeSpeedRestored(final SpeedRestoredEvent speedRestoredEvent) {
                Log.i(LOG_TAG, "event: " + speedRestoredEvent.getEventType().toString());

            }

            @Override
            public void onTripStart(final TripStartEvent event) {
                Log.i(LOG_TAG, "event: " + event.getEventType().toString());

            }

            @Override
            public void onTripEnd(final TripEndEvent event) {
                super.onTripEnd(event);
                Log.i(LOG_TAG, "event: " + event.getEventType().toString());

            }
        };
    }
}
