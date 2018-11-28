package com.tistory.ykyahwa.kotlingithubbooksample.ui.signin

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.customtabs.CustomTabsIntent
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.tistory.ykyahwa.kotlingithubbooksample.BuildConfig
import com.tistory.ykyahwa.kotlingithubbooksample.R
import com.tistory.ykyahwa.kotlingithubbooksample.api.provideAuthApi
import com.tistory.ykyahwa.kotlingithubbooksample.data.AuthTokenProvider
import com.tistory.ykyahwa.kotlingithubbooksample.extensions.AutoClearedDisposable
import com.tistory.ykyahwa.kotlingithubbooksample.extensions.plusAssign
import com.tistory.ykyahwa.kotlingithubbooksample.ui.main.MainActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_sign_in.*
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.longToast
import org.jetbrains.anko.newTask

class SignInActivity : AppCompatActivity() {

    internal val api by lazy {  provideAuthApi() }

    internal val authTokenProvider by lazy { AuthTokenProvider(this) }

    internal val disposable = AutoClearedDisposable(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        lifecycle += disposable

        btnActivitySignInStart.setOnClickListener {
            val authUri = Uri.Builder().scheme("https").authority("github.com")
                .appendPath("login")
                .appendPath("oauth")
                .appendPath("authorize")
                .appendQueryParameter("client_id", BuildConfig.GITHUB_CLIENT_ID)
                .build()

            val intent = CustomTabsIntent.Builder().build()
            intent.launchUrl(this@SignInActivity, authUri)
        }

        if (null != authTokenProvider.token) {
            launchMainActivity()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        showProgress()

        val uri = intent.data ?: throw IllegalArgumentException("No data exists")

        val code = uri.getQueryParameter("code") ?: throw IllegalStateException("No code exists")

        getAccessToken(code)
    }

    override fun onStop() {
        super.onStop()
    }
    private fun getAccessToken(code: String) {
        disposable += api.getAccessToken(BuildConfig.GITHUB_CLIENT_ID, BuildConfig.GITHUB_CLIENT_SECRET, code)
            .map { it.accessToken }
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { showProgress() }
            .doOnTerminate { hideProgress() }
            .subscribe({ token ->
                authTokenProvider.updateToken(token)
                launchMainActivity()
            }) {
                showError(it)
            }
    }

    private fun showProgress() {
        btnActivitySignInStart.visibility = View.GONE
        pbActivitySignIn.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        btnActivitySignInStart.visibility = View.VISIBLE
        pbActivitySignIn.visibility = View.GONE
    }

    private fun showError(throwable: Throwable) {
        longToast(throwable.message ?: "No Message")
    }

    private fun launchMainActivity() {
        startActivity(intentFor<MainActivity>().clearTask().newTask())
    }

}
