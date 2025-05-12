package com.javanostra.meetyourmatch.persistance;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.javanostra.meetyourmatch.MeetYourMatchApp;
import com.javanostra.meetyourmatch.persistance.api_service.AccountApiService;
import com.javanostra.meetyourmatch.persistance.entity.ResponseDTO;
import com.javanostra.meetyourmatch.persistance.entity.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServerPinger {
    private static final long PING_INTERVAL = 30000;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable pingRunnable;
    private boolean isActive = false;
    private static volatile ServerPinger instance = null;
    private final AccountApiService apiService;

    public static ServerPinger getInstance(Context context) {
        if (instance == null) {
            synchronized (ServerPinger.class) {
                if (instance == null) {
                    instance = new ServerPinger(context);
                }
            }
        }

        return instance;
    }

    private ServerPinger(Context context) {
        this.apiService = RetrofitClient.getRetrofit(context).create(AccountApiService.class);

        this.pingRunnable = new Runnable() {
            @Override
            public void run() {
                if (isActive
                        && UserSession.currentUser != null
                        && !((MeetYourMatchApp) ((AppCompatActivity) context).getApplication()).isAppInBackground()) {
                    pingServer();
                    handler.postDelayed(this, PING_INTERVAL);
                } else {
                    isActive = false;
                }
            }
        };
    }

    private void pingServer() {
        apiService.updateOnlineStatus().enqueue(new Callback<ResponseDTO>() {
            @Override
            public void onResponse(Call<ResponseDTO> call, Response<ResponseDTO> response) {
                if (response.isSuccessful()) {
                    Log.d("ServerPinger", "Ping successful");
                }
            }

            @Override
            public void onFailure(Call<ResponseDTO> call, Throwable t) {
                Log.e("ServerPinger", "Ping failed", t);
            }
        });
    }

    public void startPinging() {
        if (!isActive) {
            isActive = true;
            handler.post(pingRunnable);
        }
    }

    public void stopPinging() {
        isActive = false;
        handler.removeCallbacks(pingRunnable);
    }
}
