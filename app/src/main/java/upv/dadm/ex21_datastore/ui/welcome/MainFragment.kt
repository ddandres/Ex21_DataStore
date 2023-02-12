/*
 * Copyright (c) 2022
 * David de Andrés and Juan Carlos Ruiz
 * Development of apps for mobile devices
 * Universitat Politècnica de València
 */

package upv.dadm.ex21_datastore.ui.welcome

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import upv.dadm.ex21_datastore.R
import javax.inject.Inject

/**
 * Displays a help message that pints the user to the options menu in the ActionBar,
 * to show or hide the welcome dialog when the app starts.
 */
// The Hilt annotation @AndroidEntryPoint is required to receive dependencies from its parent class
@AndroidEntryPoint
class MainFragment @Inject constructor() : Fragment(R.layout.fragment_main), MenuProvider {

    // Reference to a ViewModel shared between Fragments
    private val viewModel: WelcomeViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Add this Fragment as MenuProvider to the MainActivity
        requireActivity().addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        // Display the welcome dialog after checking the preferences set by the user
        viewModel.showInitialDialog.observe(viewLifecycleOwner) { isVisible ->
            if (isVisible) findNavController().navigate(R.id.welcomeDialogFragment)
        }
        // Update the action elements according to the preferences set by the user
        viewModel.showDialogMenu.observe(viewLifecycleOwner) {
            requireActivity().invalidateMenu()
        }
    }

    // Populates the ActionBar with action elements
    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) =
        menuInflater.inflate(R.menu.menu_dialog, menu)

    // Allows the modification of elements of the already created menu before showing it
    override fun onPrepareMenu(menu: Menu) {
        menu.findItem(R.id.mShowDialog).isVisible =
            viewModel.showDialogMenu.value?.not() ?: false
        menu.findItem(R.id.mHideDialog).isVisible =
            viewModel.showDialogMenu.value ?: true
        super.onPrepareMenu(menu)
    }

    // Reacts to the selection of action elements
    override fun onMenuItemSelected(menuItem: MenuItem): Boolean =
        // Determine the action to take place according to its Id
        when (menuItem.itemId) {

            // Set the preference to display the dialog to true (show)
            R.id.mShowDialog -> {
                viewModel.setDialogVisibility(true)
                true
            }

            // Set the preference to display the dialog to false (hide)
            R.id.mHideDialog -> {
                viewModel.setDialogVisibility(false)
                true
            }

            // If none of the custom action elements was selected, let the system deal with it
            else -> false
        }
}