package com.example.efos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment

class DefectsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_defects, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Connect the cards
        val cardHealthy = view.findViewById<CardView>(R.id.card_healthy)
        val cardVerticillium = view.findViewById<CardView>(R.id.card_verticillium)
        val cardCaterpillar = view.findViewById<CardView>(R.id.card_caterpillar)
        val cardBeetle = view.findViewById<CardView>(R.id.card_beetle)
        val cardPhomopsis = view.findViewById<CardView>(R.id.card_phomopsis)

        // Set Click Listeners
        cardHealthy.setOnClickListener {
            showDefectDialog(
                "Healthy Eggplant Leaf",
                "Explanation: A healthy leaf is broad, vibrant green, deeply lobed, and entirely free of discoloration, spots, or physical holes.\n\nRecommendation: Maintain consistent moisture (watering at the base), ensure 6-8 hours of sunlight, and apply a balanced fertilizer to keep the plant's immune system strong."
            )
        }

        cardVerticillium.setOnClickListener {
            showDefectDialog(
                "Verticillium Wilt",
                "Explanation: A soil-borne fungus that blocks the plant's vascular system. Leaves turn yellow, begin to curl, and wilt, often starting at the bottom of the plant or affecting only one side of a leaf.\n\nRecommendation: There is no chemical cure once a plant is infected. Immediately pull up and destroy the infected plants (do not compost them). To prevent future outbreaks, practice a 3-4 year crop rotation away from other nightshades (tomatoes, peppers, potatoes) and plant certified resistant varieties."
            )
        }

        cardCaterpillar.setOnClickListener {
            showDefectDialog(
                "Tobacco Caterpillar (Spodoptera litura)",
                "Explanation: These larvae are aggressive foliage feeders. They chew large, irregular holes in the leaves and, in severe cases, can entirely skeletonize the foliage, leaving only the thickest veins behind.\n\nRecommendation: For minor infestations, manually pick off the caterpillars and crush any fuzzy egg masses found on the undersides of leaves. For larger outbreaks, apply neem oil or spray Bacillus thuringiensis (Bt), a natural bacteria that specifically targets caterpillars without harming beneficial insects."
            )
        }

        cardBeetle.setOnClickListener {
            showDefectDialog(
                "Hadda Beetle (Epilachna beetle)",
                "Explanation: Often mistaken for ladybugs, these pests (and their spiky grubs) are destructive herbivores. They feed by scraping the green epidermal tissue off the leaves, leaving behind a characteristic transparent, lace-like skeleton.\n\nRecommendation: Inspect the undersides of leaves frequently and physically remove the beetles, grubs, and clusters of yellow eggs. Spraying neem seed extract or insecticidal soap disrupts their feeding and breeding cycle."
            )
        }

        cardPhomopsis.setOnClickListener {
            showDefectDialog(
                "Phomopsis Blight",
                "Explanation: A fungal disease favored by hot, wet weather. It starts as pale, sunken, circular or oval spots on the leaves. As the spots age, tiny black dots (fruiting bodies holding spores) develop in the center.\n\nRecommendation: Immediately prune and discard affected leaves to stop the spread. Improve air circulation by spacing plants appropriately and strictly avoid overhead watering, which splashes spores onto healthy tissue. If the blight continues to spread, apply a copper-based fungicide."
            )
        }
    }

    private fun showDefectDialog(title: String, message: String) {
        AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Close") { dialog, _ -> dialog.dismiss() }
            .show()
    }
}