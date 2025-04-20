package com.javanostra.meetyourmatch.kotlin

import android.content.Context
import android.content.Intent
import com.javanostra.meetyourmatch.activity.LoginActivity
import com.javanostra.meetyourmatch.activity.RegistrationActivity
import com.vk.id.VKID
import com.vk.id.onetap.xml.OneTap
import java.util.Locale

class VKAuth private constructor() {

    private var isInit : Boolean = false

    companion object {

        @Volatile
        private var instance: VKAuth? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: VKAuth().also { instance = it }
            }
    }

    fun vkInit(context : Context) {
        if (!isInit) {
            isInit = true
            VKID.init(context)
            VKID.instance.setLocale(Locale("ru"))
        }
    }

    fun vkAuth(context : LoginActivity, vkButton : OneTap) {
        println("Hello")
        vkButton.setCallbacks(
            onAuth = {
                oAuth, accessToken -> context.vkAuth(
                    accessToken.userData.firstName,
                    accessToken.userData.email,
                    accessToken.token
                )
            }
        )
    }
}