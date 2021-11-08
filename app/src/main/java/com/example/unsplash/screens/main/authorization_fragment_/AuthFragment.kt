package com.example.unsplash.screens.splash.fragmens.authorization_fragment_

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.unsplash.databinding.AuthorizationLayoutBinding
import com.example.unsplash.R
import com.skillbox.github.ui.auth.AuthViewModel
import com.skillbox.github.utils.toast
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationResponse

class AuthFragment : Fragment(R.layout.authorization_layout) {
    private val binding: AuthorizationLayoutBinding by viewBinding()
    private val viewModel: AuthViewModel by viewModels()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        bindViewModel()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == AUTH_REQUEST_CODE && data != null) {
            val tokenExchangeRequest = AuthorizationResponse.fromIntent(data)
                ?.createTokenExchangeRequest()
            val exception = AuthorizationException.fromIntent(data)
            Log.d(
                "UnsplashLogging",
                """in onActivityResult ${exception?.stackTraceToString() ?: " NULL "}
                | tokenExReq = ${tokenExchangeRequest}
                | exception is null = ${exception == null}
            """.trimMargin()
            )
            when {
                tokenExchangeRequest != null && exception == null ->
                    viewModel.onAuthCodeReceived(tokenExchangeRequest)
                exception != null -> viewModel.onAuthCodeFailed(exception)
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun bindViewModel() {
        binding.loginButton.setOnClickListener { viewModel.openLoginPage() }
        viewModel.loadingLiveData.observe(viewLifecycleOwner, ::updateIsLoading)
        viewModel.openAuthPageLiveData.observe(viewLifecycleOwner, ::openAuthPage)
        viewModel.toastLiveData.observe(viewLifecycleOwner, ::toast)
        viewModel.authSuccessLiveData.observe(viewLifecycleOwner) { goToNextFragment() }
    }

    private fun goToNextFragment() {
        val controller = findNavController()
        controller.navigate(R.id.action_signInFragment_to_tabsFragment)
        controller.popBackStack(R.id.signInFragment, true)
    }


    private fun updateIsLoading(isLoading: Boolean) {
        binding.loginButton.isVisible = !isLoading
        binding.loginProgress.isVisible = isLoading
    }

    private fun openAuthPage(intent: Intent) {
        startActivityForResult(intent, AUTH_REQUEST_CODE)
    }

    companion object {
        private const val AUTH_REQUEST_CODE = 342
    }
}