package my.tarc.mycontact

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class ContactViewModel (application: Application): AndroidViewModel(application) {
    //LiveData gives us updated contacts when they change
    var contactList : LiveData<List<Contact>>
    private val repository: ContactRepository

    init {
        //Get an instance of the DB and return the DAO
        val contactDao = ContactDatabase.getDatabase(application).contactDao()
        //Connect DAO with the repository
        repository = ContactRepository(contactDao)
        //Pass a copy of data to the View Model
        contactList = repository.allContacts
    }

    fun addContact(contact: Contact) = viewModelScope.launch{
         repository.add(contact)
    }

    fun updateContact(contact: Contact) = viewModelScope.launch {
        repository.update(contact)
    }
}
