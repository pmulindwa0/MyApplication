package com.example.pe_code.myapplication.models;

import android.content.Context;
import android.telephony.TelephonyManager;

/**
 * Created by PE-CODE on 2/26/2016.
 */
public class QuestionModel {
    private String question;
    private String person;
    private String image;

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getPerson() {
        return person;
    }

    public void setPerson(String person) {
        this.person = person;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getIMEI(Context context){

        TelephonyManager mngr = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
        String imei = mngr.getDeviceId();
        return imei;

    }
}
