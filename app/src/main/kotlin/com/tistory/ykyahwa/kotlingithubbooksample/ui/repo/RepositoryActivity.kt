package com.tistory.ykyahwa.kotlingithubbooksample.ui.repo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.tistory.ykyahwa.kotlingithubbooksample.R
import com.tistory.ykyahwa.kotlingithubbooksample.api.provideGithubApi
import com.tistory.ykyahwa.kotlingithubbooksample.extensions.plusAssign
import com.tistory.ykyahwa.kotlingithubbooksample.ui.GlideApp
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_repository.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class RepositoryActivity : AppCompatActivity() {

    companion object {
        const val KEY_USER_LOGIN = "user_login"
        const val KEY_REPO_NAME = "repo_name"
    }

    internal val api by lazy { provideGithubApi(this) }

//    internal var repoCall: Call<GithubRepo>? = null

    internal val dateFormatInResponse = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX", Locale.getDefault())

    internal val dateFormatToShow = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    internal val disposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_repository)


        val login =
            intent.getStringExtra(KEY_USER_LOGIN) ?: throw IllegalArgumentException("No login info exists in extras")
        val repo =
            intent.getStringExtra(KEY_REPO_NAME) ?: throw IllegalArgumentException("No repo info exists in extras")

        showRepositoryInfo(login, repo)
    }

    override fun onStop() {
        super.onStop()

        disposable.clear()
    }

    private fun showRepositoryInfo(login: String, repoName: String) {

        disposable += api.getRepository(login, repoName)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { showProgress() }
            .doOnError { hideProgress(false)}
            .doOnComplete {hideProgress(true)}
            .subscribe({ repo ->
                GlideApp.with(this@RepositoryActivity)
                    .load(repo.owner.avatarUrl)
                    .into(ivActivityRepositoryProfile)

                tvActivityRepositoryName.text = repo.fullName
                tvActivityRepositoryStars.text = resources
                    .getQuantityString(R.plurals.star, repo.stars, repo.stars)
                if (null == repo.description) {
                    tvActivityRepositoryDescription.setText(R.string.no_description_provided)
                } else {
                    tvActivityRepositoryDescription.text = repo.description
                }
                if (null == repo.language) {
                    tvActivityRepositoryLanguage.setText(R.string.no_language_specified)
                } else {
                    tvActivityRepositoryLanguage.text = repo.language
                }

                try {
                    val lastUpdate = dateFormatInResponse.parse(repo.updatedAt)
                    tvActivityRepositoryLastUpdate.text = dateFormatToShow.format(lastUpdate)
                } catch (e: ParseException) {
                    tvActivityRepositoryLastUpdate.text = getString(R.string.unknown)
                }
            }) {
                showError(it.message)
            }
    }

    private fun showProgress() {
        llActivityRepositoryContent.visibility = View.GONE
        pbActivityRepository.visibility = View.VISIBLE
    }

    private fun hideProgress(isSucceed: Boolean) {
        llActivityRepositoryContent.visibility = if (isSucceed) View.VISIBLE else View.GONE
        pbActivityRepository.visibility = View.GONE
    }

    private fun showError(message: String?) {
        with(tvActivityRepositoryMessage) {
            text = message ?: "error"
            visibility = View.VISIBLE
        }
    }

}
