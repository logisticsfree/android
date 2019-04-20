package com.example.logisticsfree.Remote;

import com.example.logisticsfree.models.FCMResponse;
import com.example.logisticsfree.models.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IFCMService {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAolencC4:APA91bGlwVJD6jVQps7TbxMPfC8aO_6gTu1pH1NoarMZDGAUzyKyNGMqnfONBPz-qeU216dW167D3kuIv94EyAA6WH9uhPE3fX09k96nwXzgOOZyllwVP7JFhoQZ_UxB_AXfzzHnIHQp"
    })
    @POST("fcm/send")
    Call<FCMResponse> sendMessage(@Body Sender body);
}
