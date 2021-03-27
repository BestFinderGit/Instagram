package com.hashim.instagram.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.hashim.instagram.R
import com.hashim.instagram.databinding.FragmentProfileBinding
import com.hashim.instagram.di.component.FragmentComponent
import com.hashim.instagram.ui.base.BaseFragment
import com.hashim.instagram.ui.login.LoginActivity
import com.hashim.instagram.ui.profile.editprofile.EditProfileFragment
import com.hashim.instagram.ui.profile.settings.SettingsDialog
import com.hashim.instagram.ui.profile.userposts.UserPostAdapter
import com.hashim.instagram.utils.common.GlideHelper
import com.hashim.instagram.utils.common.GridSpacingItemDecoration
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

    @Inject
    lateinit var gridSpacingItemDecoration: GridSpacingItemDecoration

    private var _binding: FragmentProfileBinding? = null

    private val binding get() = _binding!!

    override fun provideLayoutId(): Int = R.layout.fragment_profile

    override fun injectDependencies(fragmentComponent: FragmentComponent) = fragmentComponent.inject(this)

    override fun setupView(view: View) {

        _binding = FragmentProfileBinding.bind(view)

        binding.tvLogout.setOnClickListener {
            viewModel.doLogout()
        }

        binding.btEditProfile.setOnClickListener{
            findNavController().navigate(R.id.action_itemProfile_to_editProfileFragment)
        }

        binding.tvNighMode.setOnClickListener {
            findNavController().navigate(R.id.action_itemProfile_to_settingsDialog)
        }

        binding.rvPosts.apply {
            layoutManager = gridLayoutManager
            adapter = userPostAdapter
        }.addItemDecoration(gridSpacingItemDecoration)
    }

    override fun setupObservers() {
        super.setupObservers()

        viewModel.name.observe(this, {
            binding.tvName.text = it
        })

        viewModel.loading.observe(this, {
            binding.pbLoading.visibility = if (it) View.VISIBLE else View.GONE
        })

        viewModel.tagLine.observe(this, {
            binding.tvDescription.text = it
        })

        viewModel.profile.observe(this, {
            it?.run {
                val glideRequest = Glide
                    .with(binding.ivProfile.context)
                    .load(GlideHelper.getProtectedUrl(url, headers))
                    .apply(RequestOptions.circleCropTransform())
                    .apply(RequestOptions.placeholderOf(R.drawable.ic_profile_selected))

                glideRequest.into(binding.ivProfile)
            }
        })

        viewModel.launchLogin.observe(this, {
            it.getIfNotHandled()?.run {
                val intent = Intent(context,LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK or
                        Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)


            }
        })


        viewModel.numberOfPosts.observe(this, {
            binding.tvPostNumber.text = getString(R.string.post_number_label,it)
        })

        viewModel.userPosts.observe(this, {
            userPostAdapter.appendData(it)
        })


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
