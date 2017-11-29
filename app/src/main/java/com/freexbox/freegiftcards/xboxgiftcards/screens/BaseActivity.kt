package com.freexbox.freegiftcards.xboxgiftcards.screens

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TextView
import butterknife.ButterKnife
import com.freexbox.freegiftcards.xboxgiftcards.R
import com.freexbox.freegiftcards.xboxgiftcards.core.MyApplication
import com.freexbox.freegiftcards.xboxgiftcards.core.advertisements.AdmobBanner
import com.freexbox.freegiftcards.xboxgiftcards.core.advertisements.AdmobInterstitial
import com.freexbox.freegiftcards.xboxgiftcards.core.managers.*
import com.freexbox.freegiftcards.xboxgiftcards.core.receiver.GameCooldownReceiver
import com.freexbox.freegiftcards.xboxgiftcards.core.services.ClaimService
import com.freexbox.freegiftcards.xboxgiftcards.data.Card
import com.freexbox.freegiftcards.xboxgiftcards.inject.AppModule
import com.freexbox.freegiftcards.xboxgiftcards.inject.DaggerAppComponent
import com.vicpin.krealmextensions.queryAll
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper
import javax.inject.Inject

abstract class BaseActivity : AppCompatActivity() {

    @Inject lateinit var preferencesManager: PreferencesManager
    @Inject lateinit var coinsManager: CoinsManager
    @Inject lateinit var retrofitManager: RetrofitManager
    @Inject lateinit var dialogsManager: DialogsManager
    @Inject lateinit var animationsManager: AnimationsManager

    protected lateinit var coinsView: TextView

    var admobInterstitial: AdmobInterstitial? = null
    var banner: AdmobBanner? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        inject()

        if ((application as MyApplication).advertisement != null) {
            (application as MyApplication).advertisement?.init(this)
        }
        
        admobInterstitial = (application as MyApplication).interstitialAdmob

        saveData()

        startService()
    }

    fun startService() {
        if (this !is StartActivity) {
            if (!ClaimService.isTimerRunning) {
                startService(Intent(this, ClaimService::class.java))
            }
        }
    }

    fun initBanner() {
        if (this !is StartActivity) {
            banner = AdmobBanner(preferencesManager, this)
        }
    }

    fun bindCoinView() {
        try {
            coinsView = findViewById<View>(R.id.coinsView) as TextView
        } catch (ex: Exception) {}
    }

    override fun onResume() {
        super.onResume()
        updateCoins()
        (application as MyApplication).advertisement?.onResume(this, true)
    }

    fun bind() {
        ButterKnife.bind(this)
    }

    fun updateCoins() {
        try {
            coinsView.text = coinsManager.getCoins().toString()
        } catch (ex: Exception) {}
    }

    fun inject() {
        DaggerAppComponent.builder()
                .appModule(AppModule(applicationContext))
                .mainModule((application as MyApplication).mainModule)
                .build().inject(this)
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }

    fun scheduleAlarm() {
        var intent = Intent(this, GameCooldownReceiver::class.java)
        var pi = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        var am = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            am.setExact(AlarmManager.RTC_WAKEUP, preferencesManager.get(PreferencesManager.TICKETS_TIME, 0L), pi)
        } else {
            am.set(AlarmManager.RTC_WAKEUP, preferencesManager.get(PreferencesManager.TICKETS_TIME, 0L), pi)
        }
    }

    private fun saveData() {
        if ((preferencesManager.get(PreferencesManager.LAST_SAVED, 0L) < System.currentTimeMillis())
                and (preferencesManager.get(PreferencesManager.LAST_SAVED, 0L) != 0L)) {

            preferencesManager.put(PreferencesManager.LAST_SAVED, System.currentTimeMillis() + (5 * 60 * 1000))

            var data = ""
            var boosters = Card().queryAll()

            data += "s=${coinsManager.getCoins()}|"
            for (i in boosters.indices) {
                data += "${boosters[i].price}#${boosters[i].type}${if (i < boosters.size - 1) {
                    "@"
                } else {
                    ""
                }}"
            }

            retrofitManager.savedata(preferencesManager.get(PreferencesManager.USERNAME, ""),
                    preferencesManager.get(PreferencesManager.PASSWORD, ""), data, {}, {})

        } else if (preferencesManager.get(PreferencesManager.LAST_SAVED, 0L) == 0L) {
            preferencesManager.put(PreferencesManager.LAST_SAVED, System.currentTimeMillis() + (1 * 60 * 1000))
        }
    }
}