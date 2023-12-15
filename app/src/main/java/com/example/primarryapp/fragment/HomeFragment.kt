package com.example.primarryapp.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.primarryapp.ActivityChat2
import com.example.primarryapp.R
import com.example.primarryapp.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.communitycard.setOnClickListener {
            navigateToFragment(CommunityFragment())
        }

        binding.profilecard.setOnClickListener {
            navigateToFragment(ProfileFragment())
        }

        binding.consultationcard.setOnClickListener {
            startChatActivity()
        }
    }

    private fun navigateToFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun startChatActivity() {
        val chatIntent = Intent(requireActivity(), ActivityChat2::class.java)
        startActivity(chatIntent)
    }
}
