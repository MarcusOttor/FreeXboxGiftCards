package com.freexbox.freegiftcards.xboxgiftcards.inject

import com.freexbox.freegiftcards.xboxgiftcards.core.MyApplication
import com.freexbox.freegiftcards.xboxgiftcards.core.services.ClaimService
import com.freexbox.freegiftcards.xboxgiftcards.dialogs.LoginDialog
import com.freexbox.freegiftcards.xboxgiftcards.dialogs.PromocodeDialog
import com.freexbox.freegiftcards.xboxgiftcards.dialogs.SignupDialog
import com.freexbox.freegiftcards.xboxgiftcards.screens.BaseActivity
import dagger.Component

@Component(modules = arrayOf(AppModule::class, MainModule::class))
interface AppComponent {

    fun inject(screen: BaseActivity)
    fun inject(app: MyApplication)
    fun inject(dialog: LoginDialog)
    fun inject(dialog: SignupDialog)
    fun inject(dialog: PromocodeDialog)
    fun inject(service: ClaimService)
}