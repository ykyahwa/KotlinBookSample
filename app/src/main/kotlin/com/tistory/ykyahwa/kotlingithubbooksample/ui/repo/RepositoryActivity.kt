package com.tistory.ykyahwa.kotlingithubbooksample.ui.repo

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.tistory.ykyahwa.kotlingithubbooksample.R
import com.tistory.ykyahwa.kotlingithubbooksample.extensions.AutoClearedDisposable
import com.tistory.ykyahwa.kotlingithubbooksample.extensions.plusAssign
import com.tistory.ykyahwa.kotlingithubbooksample.ui.GlideApp
import dagger.android.support.DaggerAppCompatActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_repository.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class RepositoryActivity : DaggerAppCompatActivity() {

    companion object {
        const val KEY_USER_LOGIN = "user_login"
        const val KEY_REPO_NAME = "repo_name"
    }

    internal val dateFormatInResponse = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX", Locale.getDefault())

    internal val dateFormatToShow = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    internal val disposable = AutoClearedDisposable(this)

    internal val viewDisposable = AutoClearedDisposable(this, alwaysClearOnStop = false)

    @Inject lateinit var viewModelFactory: RepositoryViewModelFactory

    lateinit var viewModel: RepositoryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_repository)


        viewModel = ViewModelProviders.of(this, viewModelFactory)[RepositoryViewModel::class.java]

        lifecycle += viewDisposable

        lifecycle += disposable

        viewDisposable += viewModel.repository
            .filter { !it.isEmpty }
            .map { it.value }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { repository ->
                GlideApp.with(this@RepositoryActivity)
                    .load(repository.owner.avatarUrl)
                    .into(ivActivityRepositoryProfile)
                tvActivityRepositoryName.text = repository.fullName
                tvActivityRepositoryStars.text = resources.getQuantityString(R.plurals.star, repository.stars, repository.stars)
                if (null == repository.description) {
                    tvActivityRepositoryDescription.setText(R.string.no_description_provided)
                } else {
                    tvActivityRepositoryDescription.text = repository.description
                }
                if (null == repository.language) {
                    tvActivityRepositoryLanguage.setText(R.string.no_language_specified)
                } else {
                    tvActivityRepositoryLanguage.text = repository.language
                }
                try {
                    val lastUpdate = dateFormatInResponse.parse(repository.updatedAt)
                    tvActivityRepositoryLastUpdate.text = dateFormatToShow.format(lastUpdate)
                } catch (e: ParseException) {
                    tvActivityRepositoryLastUpdate.text = getString(R.string.unknown)
                }
            }

        viewDisposable += viewModel.message
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { message -> showError(message) }

        viewDisposable += viewModel.isContentVisible
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { visible -> setContentVisibility(visible) }

        viewDisposable += viewModel.isLoading
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { isLoading ->
                if (isLoading) {
                    showProgress()
                } else {
                    hideProgress()
                }
            }


        val login =
            intent.getStringExtra(KEY_USER_LOGIN) ?: throw IllegalArgumentException("No login info exists in extras")
        val repo =
            intent.getStringExtra(KEY_REPO_NAME) ?: throw IllegalArgumentException("No repo info exists in extras")

        disposable += viewModel.requestRepositoryInfo(login, repo)
    }

    private fun showProgress() {
        pbActivityRepository.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        pbActivityRepository.visibility = View.GONE
    }

    private fun setContentVisibility(show: Boolean) {
        pbActivityRepository.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun showError(message: String?) {
        with(tvActivityRepositoryMessage) {
            text = message ?: "error"
            visibility = View.VISIBLE
        }
    }

}
