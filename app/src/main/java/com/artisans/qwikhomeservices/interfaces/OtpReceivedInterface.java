package com.artisans.qwikhomeservices.interfaces;

public interface OtpReceivedInterface {

    void onOtpReceived(String otp);

    void onOtpTimeout();

}
