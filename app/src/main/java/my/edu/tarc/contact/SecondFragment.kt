package my.edu.tarc.contact

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import my.edu.tarc.contact.databinding.FragmentSecondBinding
import my.tarc.mycontact.Contact
import my.tarc.mycontact.ContactViewModel

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment(), MenuProvider {

    private var _binding: FragmentSecondBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val myContactViewModel: ContactViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSecondBinding.inflate(inflater, container, false)

        //Let ProfileFragment to manage the Menu
        val menuHost: MenuHost = this.requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner,
            Lifecycle.State.RESUMED)

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Determine the mode of the fragment; Add or Edit
        if (myContactViewModel.selectedIndex != -1) {//Edit mode
            if (myContactViewModel.contactList.isInitialized){
                val contact: Contact = myContactViewModel.contactList.value!!.get(myContactViewModel.selectedIndex)
                with(binding) {
                    editTextName.setText(contact.name)
                    editTextPhone.setText(contact.phone)
                    editTextPhone.isEnabled = false
                    editTextName.requestFocus()//set focus
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        myContactViewModel.selectedIndex = -1
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menu.clear()
        menuInflater.inflate(R.menu.second_menu, menu)
        if(myContactViewModel.selectedIndex == -1) {
            menu.findItem(R.id.action_delete).isVisible = false
        }
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        if(menuItem.itemId == R.id.action_save){
            binding.apply {
                val name = editTextName.text.toString()
                val phone = editTextPhone.text.toString()
                val newContact = Contact(name, phone)
                if (myContactViewModel.selectedIndex == -1) {
                    myContactViewModel.addContact(newContact)
                } else {
                    myContactViewModel.updateContact(newContact)
                }
            }
            Toast.makeText(context, getString(R.string.contact_saved), Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
        } else if(menuItem.itemId == R.id.action_delete) {
            val deleteAlertDialog = AlertDialog.Builder(requireActivity())
            deleteAlertDialog.setMessage(R.string.delete_record)
            deleteAlertDialog.setPositiveButton(
                getString(R.string.delete)
            ) { _, _ ->
                val name = binding.editTextName.text.toString()
                val phone = binding.editTextPhone.text.toString()
                val newContact = Contact(name, phone)
                myContactViewModel.deleteContact(newContact)
                findNavController().navigateUp()
                Toast.makeText(context, getString(R.string.contact_deleted), Toast.LENGTH_SHORT)
                    .show()
            }
            deleteAlertDialog.setNegativeButton(
                getString(android.R.string.cancel)
            ) { _, _ ->
                //DO NOTHING
            }.create().show()
        } else if(menuItem.itemId == android.R.id.home){
            findNavController().navigateUp()
        }
        return true
    }

}