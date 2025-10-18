package com.example.fitlife.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.fitlife.databinding.FragmentNutritionBinding
import android.widget.RadioGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible

class NutritionFragment : Fragment() {
    
    private var _binding: FragmentNutritionBinding? = null
    private val binding get() = _binding!!
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNutritionBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupGoalSelection()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private enum class Goal {
        LOSE, MAINTAIN, GAIN, CONDITION_DIABETES, CONDITION_HEART
    }

    private fun setupGoalSelection() {
        binding.rgGoal.setOnCheckedChangeListener { _: RadioGroup, checkedId: Int ->
            when (checkedId) {
                binding.rbGoalLose.id -> applyPlan(Goal.LOSE)
                binding.rbGoalMaintain.id -> applyPlan(Goal.MAINTAIN)
                binding.rbGoalGain.id -> applyPlan(Goal.GAIN)
                binding.rbGoalCondition.id -> {
                    // Show conditions selector row
                    binding.layoutConditions.isVisible = true
                    // Wait for user to choose a condition
                    binding.layoutPlan.isGone = true
                    binding.textPlanHeader.isGone = true
                    binding.textTipsHeader.isGone = true
                    binding.textTips.isGone = true
                }
            }
        }

        binding.btnConditionDiabetes.setOnClickListener {
            applyPlan(Goal.CONDITION_DIABETES)
        }
        binding.btnConditionHeart.setOnClickListener {
            applyPlan(Goal.CONDITION_HEART)
        }
    }

    private fun applyPlan(goal: Goal) {
        // Hide condition row unless goal is generic condition selector
        binding.layoutConditions.isGone = goal !in listOf(Goal.CONDITION_DIABETES, Goal.CONDITION_HEART)

        // Show plan containers
        binding.textPlanHeader.isVisible = true
        binding.layoutPlan.isVisible = true
        binding.textTipsHeader.isVisible = true
        binding.textTips.isVisible = true

        when (goal) {
            Goal.LOSE -> {
                setMeals(
                    breakfast = listOf("Oats with fruit", "Green tea"),
                    lunch = listOf("Brown rice + grilled chicken", "Mixed salad"),
                    dinner = listOf("Vegetable soup", "Whole grain bread"),
                    avoid = emptyList()
                )
                binding.textTips.text = "Drink more water, avoid fried foods."
            }
            Goal.MAINTAIN -> {
                setMeals(
                    breakfast = listOf("Greek yogurt + berries", "Tea/Coffee (low sugar)"),
                    lunch = listOf("Rice + fish + veggies", "Side salad"),
                    dinner = listOf("Grilled vegetables", "Quinoa"),
                    avoid = listOf("Sugary drinks")
                )
                binding.textTips.text = "Balanced portions. Stay hydrated and consistent."
            }
            Goal.GAIN -> {
                setMeals(
                    breakfast = listOf("Peanut butter toast", "Banana smoothie"),
                    lunch = listOf("Whole grain pasta + chicken", "Avocado"),
                    dinner = listOf("Rice + beans", "Omelette"),
                    avoid = listOf("Excess junk food; prefer nutrient-dense calories")
                )
                binding.textTips.text = "Increase protein & healthy carbs."
            }
            Goal.CONDITION_DIABETES -> {
                setMeals(
                    breakfast = listOf("Oats + nuts", "Unsweetened tea"),
                    lunch = listOf("Brown rice + grilled fish", "Leafy salad"),
                    dinner = listOf("Lentil soup", "Whole wheat roti"),
                    avoid = listOf("White bread", "Sugary drinks", "Desserts")
                )
                binding.textTips.text = "Low sugar fruits, avoid white bread."
            }
            Goal.CONDITION_HEART -> {
                setMeals(
                    breakfast = listOf("Oatmeal + berries", "Green tea"),
                    lunch = listOf("Grilled chicken/fish", "Steamed veggies"),
                    dinner = listOf("Veggie soup", "Brown bread"),
                    avoid = listOf("High sodium foods", "Processed meats")
                )
                binding.textTips.text = "Low sodium meals, avoid processed meats."
            }
        }
    }

    private fun setMeals(
        breakfast: List<String>,
        lunch: List<String>,
        dinner: List<String>,
        avoid: List<String>
    ) {
        binding.textBreakfastItems.text = breakfast.joinToString(prefix = "- ", separator = "\n- ")
        binding.textLunchItems.text = lunch.joinToString(prefix = "- ", separator = "\n- ")
        binding.textDinnerItems.text = dinner.joinToString(prefix = "- ", separator = "\n- ")

        val avoidText = if (avoid.isEmpty()) "" else "❌ Avoid: ${avoid.joinToString()}"
        binding.textBreakfastAvoid.apply { text = avoidText; isVisible = avoid.isNotEmpty() }
        binding.textLunchAvoid.apply { text = avoidText; isVisible = avoid.isNotEmpty() }
        binding.textDinnerAvoid.apply { text = avoidText; isVisible = avoid.isNotEmpty() }
    }
}
