package com.mindorks.bootcamp.instagram.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.mindorks.bootcamp.instagram.R
import com.mindorks.bootcamp.instagram.di.component.FragmentComponent
import com.mindorks.bootcamp.instagram.ui.base.BaseFragment
import com.mindorks.bootcamp.instagram.ui.login.LoginActivity
import com.mindorks.bootcamp.instagram.ui.profile.editprofile.EditProfileActivity
import com.mindorks.bootcamp.instagram.ui.profile.userposts.UserPostAdapter
import com.mindorks.bootcamp.instagram.utils.common.GlideHelper
import kotlinx.android.synthetic.main.fragment_profile.*
import javax.inject.Inject


class ProfileFragment : BaseFragment<ProfileViewModel>() {

    companion object {

        const val TAG = "ProfileFragment"

        fun newInstance(): ProfileFragment {
            val args = Bundle()
            val fragment = ProfileFragment()
            fragment.arguments = args
            return fragment
        }
    }

    @Inject
    lateinit var userPostAdapter: UserPostAdapter

    @Inject
    lateinit var gridLayoutManager : GridLayoutManager

    override fun provideLayoutId(): Int = R.layout.fragment_profile

    override fun injectDependencies(fragmentComponent: FragmentComponent) = fragmentComponent.inject(this)

    override fun setupView(view: View) {
        tvLogout.setOnClickListener {
            viewModel.doLogout()
        }

        bt_edit_profile.setOnClickListener{
            viewModel.doLaunchEditProfile()
        }

        rvPosts.apply {
            layoutManager = gridLayoutManager
            adapter = userPostAdapter
        }
    }

    override fun setupObservers() {
        super.setupObservers()

        viewModel.name.observe(this, Observer {
            tvName.text = it
        })

        viewModel.loading.observe(this, Observer {
            pb_loading.visibility = if (it) View.VISIBLE else View.GONE
        })

        viewModel.tagLine.observe(this, Observer {
            tvDescription.text = it
        })

        viewModel.profile.observe(this, Observer {
            it?.run {
                val glideRequest = Glide
                    .with(ivProfile.context)
                    .load(GlideHelper.getProtectedUrl(url, headers))
                    .apply(RequestOptions.circleCropTransform())
                    .apply(RequestOptions.placeholderOf(R.drawable.ic_profile_selected))

                glideRequest.into(ivProfile)
            }
        })

        viewModel.launchLogin.observe(this, Observer {
            it.getIfNotHandled()?.run {
                fragmentManager?.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                startActivity(Intent(context,LoginActivity::class.java))
            }
        })

        viewModel.launchEditProfile.observe(this, Observer {
            it.getIfNotHandled()?.run {
                startActivity(Intent(context,EditProfileActivity::class.java))
            }
        })

        viewModel.numberOfPosts.observe(this, Observer {
            tvPostNumber.text = getString(R.string.post_number_label,it)
        })

        viewModel.userPosts.observe(this, Observer {
            userPostAdapter.appendData(it)
        })
    }
}
