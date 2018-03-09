
package com.alonsocage.componentes.services;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.alonsocage.componentes.managers.DatabaseHelper;
import com.alonsocage.componentes.utils.LogUtil;


/**
 * Created by Alonso on 20.02.18.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";


    @Override
    public void onTokenRefresh() {

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        LogUtil.logDebug(TAG, "Refreshed token: " + refreshedToken);


        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String token) {
        DatabaseHelper.getInstance(getApplicationContext()).updateRegistrationToken(token);
    }
}
