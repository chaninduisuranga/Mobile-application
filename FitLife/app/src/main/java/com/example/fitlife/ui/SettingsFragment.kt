package com.example.fitlife.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.Fragment
import com.example.fitlife.AuthActivity
import com.example.fitlife.R
import com.example.fitlife.data.Prefs
import com.example.fitlife.databinding.FragmentSettingsBinding
import com.example.fitlife.worker.HydrationWorkManager

class SettingsFragment : Fragment() {
    
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var prefs: Prefs
    private lateinit var hydrationWorkManager: HydrationWorkManager
    
    private val intervalOptions = listOf(
        "01 minutes" to 1,
        "60 minutes" to 60,
        "120 minutes" to 120
    )
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        prefs = Prefs(requireContext())
        hydrationWorkManager = HydrationWorkManager(requireContext())
        
        setupSpinners()
        setupClickListeners()
        loadCurrentSettings()
    }
    
    private fun setupSpinners() {
        // Interval spinner
        val intervalAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            intervalOptions.map { it.first }
        )
        binding.spinnerInterval.setAdapter(intervalAdapter)
        
        // Set up listener for interval changes
        binding.spinnerInterval.setOnItemClickListener { _, _, position, _ ->
            val selectedInterval = intervalOptions[position].second
            prefs.setHydrationIntervalMinutes(selectedInterval)
            updateSaveButtonState()
        }
    }
    
    private fun setupClickListeners() {
        // Toggle switch listener
        binding.switchHydrationReminders.setOnCheckedChangeListener { _, isChecked ->
            prefs.setHydrationEnabled(isChecked)
            updateIntervalSpinnerState(isChecked)
            updateSaveButtonState()
        }
        
        // Save button listener
        binding.btnSaveHydrationSettings.setOnClickListener {
            saveHydrationSettings()
        }
        
        // Logout button listener
        binding.btnLogout.setOnClickListener {
            showLogoutConfirmation()
        }
    }
    
    private fun loadCurrentSettings() {
        val isEnabled = prefs.isHydrationEnabled()
        val intervalMinutes = prefs.getHydrationIntervalMinutes()
        
        // Set switch state
        binding.switchHydrationReminders.isChecked = isEnabled
        
        // Set interval spinner
        val intervalIndex = intervalOptions.indexOfFirst { it.second == intervalMinutes }
        if (intervalIndex >= 0) {
            binding.spinnerInterval.setText(intervalOptions[intervalIndex].first, false)
        }
        
        updateIntervalSpinnerState(isEnabled)
        updateSaveButtonState()
    }
    
    private fun updateIntervalSpinnerState(isEnabled: Boolean) {
        binding.spinnerInterval.isEnabled = isEnabled
        binding.layoutIntervalSelector.isEnabled = isEnabled
        
        // Update visual state
        if (isEnabled) {
            binding.layoutIntervalSelector.alpha = 1.0f
        } else {
            binding.layoutIntervalSelector.alpha = 0.5f
        }
    }
    
    private fun updateSaveButtonState() {
        val hasChanges = hasSettingsChanged()
        binding.btnSaveHydrationSettings.isEnabled = hasChanges
    }
    
    private fun hasSettingsChanged(): Boolean {
        val currentEnabled = prefs.isHydrationEnabled()
        val currentInterval = prefs.getHydrationIntervalMinutes()
        
        // Check if settings have changed from their saved state
        // This is a simple implementation - in a real app you might want to track original values
        return true // For now, always enable save button when settings change
    }
    
    private fun saveHydrationSettings() {
        val isEnabled = prefs.isHydrationEnabled()
        val intervalMinutes = prefs.getHydrationIntervalMinutes()
        
        if (isEnabled) {
            // Schedule hydration reminders
            hydrationWorkManager.scheduleHydrationReminders(intervalMinutes)
        } else {
            // Cancel hydration reminders
            hydrationWorkManager.cancelHydrationReminders()
        }
        
        // Disable save button after saving
        binding.btnSaveHydrationSettings.isEnabled = false
        
        // Show confirmation (optional)
        // You could add a snackbar or toast here
    }
    
    private fun showLogoutConfirmation() {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.settings_logout_confirm_title))
            .setMessage(getString(R.string.settings_logout_confirm_message))
            .setPositiveButton(getString(R.string.settings_logout_confirm_yes)) { _, _ ->
                performLogout()
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }
    
    private fun performLogout() {
        // Clear user session
        prefs.logout()
        
        // Navigate to AuthActivity
        val intent = Intent(requireContext(), AuthActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        
        // Finish the current activity
        requireActivity().finish()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
