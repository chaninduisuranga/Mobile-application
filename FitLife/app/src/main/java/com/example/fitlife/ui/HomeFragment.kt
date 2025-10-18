package com.example.fitlife.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.fitlife.R
import com.example.fitlife.data.Prefs
import com.example.fitlife.databinding.FragmentHomeBinding
import com.example.fitlife.model.Habit
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {
    
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var prefs: Prefs
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val today = dateFormat.format(Date())
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        prefs = Prefs(requireContext())
        setupWelcomeMessage()
        updateProgress()
        setupClickListeners()
    }
    
    private fun setupWelcomeMessage() {
        // Get user name from preferences or use default
        val userName = prefs.getUserName() ?: "Heshani"
        binding.textWelcome.text = "Welcome, $userName 👋"
    }
    
    private fun updateProgress() {
        val habits = prefs.getHabits()
        // Only consider active habits for progress, to match Habits screen and widget
        val activeHabits = habits.filter { it.isActive }
        val completedCount = activeHabits.count { habit ->
            habit.completedDates.contains(today)
        }
        val totalCount = activeHabits.size

        binding.textProgressCount.text = "$completedCount / $totalCount completed"

        val progress = if (totalCount > 0) {
            (completedCount * 100) / totalCount
        } else {
            0
        }

        binding.progressBarHabits.progress = progress
    }
    
    private fun setupClickListeners() {
        // FAB removed - habits are now managed in calendar view
    }
    
    override fun onResume() {
        super.onResume()
        updateProgress() // Refresh progress when returning to home
    }
    
    override fun onStart() {
        super.onStart()
        updateProgress() // Also refresh when fragment becomes visible
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
