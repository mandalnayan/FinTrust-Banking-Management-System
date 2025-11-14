package com.fintrust.service;

import com.fintrust.dao.ContactDAO;
import com.fintrust.model.ContactModel;

public class ContactService {

    ContactDAO dao = new ContactDAO();

    public void saveTicket(ContactModel model) {
        dao.insert(model);
    }
    
    public ContactModel getTicketById(long id) {
        return dao.findById(id);
    }

}
