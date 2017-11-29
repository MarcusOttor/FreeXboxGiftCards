package com.freexbox.freegiftcards.xboxgiftcards.screens

import android.content.Intent
import android.os.Bundle
import android.view.View
import butterknife.OnClick
import com.freexbox.freegiftcards.xboxgiftcards.R
import com.freexbox.freegiftcards.xboxgiftcards.data.Card
import com.vicpin.krealmextensions.query
import com.vicpin.krealmextensions.save
import kotlinx.android.synthetic.main.toolbar.*

class GiftCardsActivity : BaseActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.giftcards_activity)

        bindCoinView()
        bind()

        toolbarText.text = "Gift Cards"

        initBanner()
    }

    @OnClick(R.id.addCoinsText)
    fun back(view: View) {
        startActivity(Intent(this, MainActivity::class.java)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP))
        finish()
    }

    @OnClick(R.id.card25,
            R.id.card50,
            R.id.card75,
            R.id.card100,
            R.id.card150,
            R.id.card200,
            R.id.card_live_1,
            R.id.card_live_3,
            R.id.card_live_6,
            R.id.card_live_9,
            R.id.card_live_12,
            R.id.card_live_24)
    fun purchaseCard(v: View) {
        when (v.id) {
            R.id.card25 -> {processCard(6000, 1)}
            R.id.card50 -> {processCard(10000, 1)}
            R.id.card75 -> {processCard(14000, 1)}
            R.id.card100 -> {processCard(18000, 1)}
            R.id.card150 -> {processCard(24000, 1)}
            R.id.card200 -> {processCard(30000, 1)}

            R.id.card_live_1 -> {processCard(6000, 2)}
            R.id.card_live_3 -> {processCard(8000, 2)}
            R.id.card_live_6 -> {processCard(10000, 2)}
            R.id.card_live_9 -> {processCard(13000, 2)}
            R.id.card_live_12 -> {processCard(16000, 2)}
            R.id.card_live_24 -> {processCard(24000, 2)}
        }
    }

    override fun onBackPressed() {
        startActivity(Intent(this, MainActivity::class.java)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP))
        finish()
    }

    private fun processCard(price: Int, type: Int) {
        if (Card().query { q -> q.equalTo("price", price); q.equalTo("type", type) }.isNotEmpty()) {
            var intent = Intent(this, RedeemActivity::class.java)
            intent.putExtra("price", price)
            intent.putExtra("type",type)
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish()
        } else {
            if (coinsManager.getCoins() >= price) {
                dialogsManager.showAdvAlertDialog(supportFragmentManager, "Do you really want to buy this Gift Card?", {
                    coinsManager.subtractCoins(price)
                    Card(price, type).save()
                    updateCoins()
                    var intent = Intent(this, RedeemActivity::class.java)
                    intent.putExtra("price", price)
                    intent.putExtra("type",type)
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                    finish()
                }, {
                    admobInterstitial?.show {  }
                })
            } else {
                dialogsManager.showAlertDialog(supportFragmentManager, "Not enough coins!", {
                    admobInterstitial?.show {  }
                })
            }
        }
    }
}