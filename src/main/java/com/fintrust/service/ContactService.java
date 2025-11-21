package com.fintrust.service;

import com.fintrust.model_copy.ContactModel;

import zcom.finrust.dao_copy.ContactDAO;

public class ContactService {

    ContactDAO dao = new ContactDAO();

    public void saveTicket(ContactModel model) {
        dao.insert(model);
    }
    
    public ContactModel getTicketById(long id) {
        return dao.findById(id);
    }

}
