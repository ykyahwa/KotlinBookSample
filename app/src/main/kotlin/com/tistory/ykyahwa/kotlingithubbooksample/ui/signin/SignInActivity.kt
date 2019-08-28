package com.tistory.ykyahwa.kotlingithubbooksample.ui.signin

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.customtabs.CustomTabsIntent
import android.view.View
import com.tistory.ykyahwa.kotlingithubbooksample.BuildConfig
import com.tistory.ykyahwa.kotlingithubbooksample.R
import com.tistory.ykyahwa.kotlingithubbooksample.extensions.AutoClearedDisposable
import com.tistory.ykyahwa.kotlingithubbooksample.extensions.plusAssign
import com.tistory.ykyahwa.kotlingithubbooksample.ui.main.MainActivity
import dagger.android.support.DaggerAppCompatActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_sign_in.*
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.longToast
import org.jetbrains.anko.newTask
import javax.inject.Inject

class SignInActivity : DaggerAppCompatActivity() {

    internal val disposable = AutoClearedDisposable(this)

    internal val viewDisposable = AutoClearedDisposable(this, alwaysClearOnStop = false)

    @Inject lateinit var viewModelFactory: SignViewModelFactory

//    @Inject lateinit var authTokenProvider: AuthTokenProvider

    lateinit var viewModel: SignViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        viewModel = ViewModelProviders.of(this, viewModelFactory)[SignViewModel::class.java]

        lifecycle += disposable

        lifecycle += viewDisposable

        viewDisposable += viewModel.accessToken
            .filter{!it.isEmpty}
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { launchMainActivity() }

        viewDisposable += viewModel.message
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { message -> showError(message) }

        viewDisposable += viewModel.isLoading
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { isLoading ->
                if (isLoading) {
                    showProgress()
                } else {
                    hideProgress()
                }
            }

        disposable += viewModel.loadAccessToken()


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
//
//        if (null != authTokenProvider.token) {
//            launchMainActivity()
//        }
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
        disposable += viewModel.requestAccessToken(BuildConfig.GITHUB_CLIENT_ID, BuildConfig.GITHUB_CLIENT_SECRET, code)
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

    private fun showError(message: String) {
        longToast(message)
    }

    private fun launchMainActivity() {
        startActivity(intentFor<MainActivity>().clearTask().newTask())
    }

}
