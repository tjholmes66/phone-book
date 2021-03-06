package com.opensource.phonebook.server.rpc;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.opensource.phonebook.client.services.ContactService;
import com.opensource.phonebook.domain.ContactEntity;
import com.opensource.phonebook.domain.UserEntity;
import com.opensource.phonebook.server.dao.ContactDao;
import com.opensource.phonebook.shared.Mapping;
import com.opensource.phonebook.shared.dto.ContactDTO;
import com.opensource.phonebook.shared.dto.UserDTO;

@SuppressWarnings("serial")
@Transactional
@Service("contactService")
public class ContactServiceImpl extends BaseRPC implements ContactService
{

    @Autowired
    private ContactDao contactDao;

    public ContactDao getContactDao()
    {
        return contactDao;
    }

    public void setContactDao(ContactDao contactDao)
    {
        this.contactDao = contactDao;
    }

    @Transactional
    public ContactDTO add(ContactDTO contactDto)
    {
        UserEntity user = new UserEntity();
        user.setId(contactDto.getUserId());

        ContactEntity newContact = new ContactEntity();
        newContact.setUser(user);

        newContact.setId(contactDto.getId());
        newContact.setPrefix(contactDto.getPrefix());
        newContact.setFirstName(contactDto.getFirstName());
        newContact.setMiddleName(contactDto.getMiddleName());
        newContact.setLastName(contactDto.getLastName());
        newContact.setSuffix(contactDto.getSuffix());
        // ***************************************************
        newContact.setAddress1(contactDto.getAddress1());
        newContact.setAddress2(contactDto.getAddress2());
        newContact.setCity(contactDto.getCity());
        newContact.setState(contactDto.getState());
        newContact.setZip(contactDto.getZip());
        // ***************************************************
        newContact.setEnteredBy(contactDto.getEditedBy());
        newContact.setEnteredDate(contactDto.getEnteredDate());
        newContact.setEditedBy(contactDto.getEditedBy());
        newContact.setEditedDate(contactDto.getEditedDate());
        // ***************************************************
        newContact.setCompanyId(contactDto.getCompanyId());
        newContact.setBirthDate(contactDto.getBirthDate());
        // ***************************************************
        ContactEntity contactEntity = contactDao.createContactEntity(newContact);
        ContactDTO newContactDto = Mapping.createContact(contactEntity);
        return newContactDto;
    }

    @Transactional
    public void remove(ContactDTO contactDto)
    {
        ContactEntity contactEntity = new ContactEntity();
        contactEntity.setId(contactDto.getId());
        contactDao.deleteContactEntity(contactEntity);
    }

    @Transactional
    public void update(ContactDTO contactDto)
    {
        UserEntity user = new UserEntity();
        user.setId(contactDto.getUserId());

        ContactEntity newContact = contactDao.getContactEntity(contactDto.getId());
        newContact.setUser(user);
        newContact.setId(contactDto.getId());

        newContact.setPrefix(contactDto.getPrefix()); // not required
        if (contactDto.getFirstName() != null)
        {
            newContact.setFirstName(contactDto.getFirstName());
        }
        if (contactDto.getMiddleName() != null)
        {
            newContact.setMiddleName(contactDto.getMiddleName());
        }
        if (contactDto.getLastName() != null)
        {
            newContact.setLastName(contactDto.getLastName());
        }
        newContact.setSuffix(contactDto.getSuffix()); // not required
        // ***************************************************
        if (contactDto.getAddress1() != null)
        {
            newContact.setAddress1(contactDto.getAddress1());
        }
        newContact.setAddress2(contactDto.getAddress2()); // not required
        if (contactDto.getCity() != null)
        {
            newContact.setCity(contactDto.getCity());
        }
        if (contactDto.getState() != null)
        {
            newContact.setState(contactDto.getState());
        }
        if (contactDto.getZip() != null)
        {
            newContact.setZip(contactDto.getZip());
        }
        // ***************************************************
        // newContact.setEnteredBy(contactDto.getEditedBy());
        // newContact.setEnteredDate(contactDto.getEnteredDate());
        newContact.setEditedBy(contactDto.getUserId());
        newContact.setEditedDate(new Date());
        // ***************************************************
        newContact.setCompanyId(contactDto.getCompanyId());
        if (contactDto.getBirthDate() != null)
        {
            newContact.setBirthDate(contactDto.getBirthDate());
        }
        // ***************************************************
        contactDao.saveContactEntity(newContact);
    }

    @Transactional
    public List<ContactDTO> fetch(ContactDTO exampleEntity)
    {
        List<ContactDTO> contactList = null;
        if (exampleEntity != null)
        {
            ContactEntity exampleContactEntity = new ContactEntity();
            exampleContactEntity.setId(exampleEntity.getId());
            exampleContactEntity.setFirstName(exampleEntity.getFirstName());
            exampleContactEntity.setLastName(exampleEntity.getLastName());

            List<ContactEntity> contactEntityList = contactDao.getContactEntity(exampleContactEntity);
            if (contactEntityList != null)
            {
                contactList = new ArrayList<ContactDTO>();
                for (ContactEntity contactEntity : contactEntityList)
                {
                    ContactDTO contactDTO = Mapping.createContact(contactEntity);
                    if (contactDTO != null)
                    {
                        contactList.add(contactDTO);
                    }
                }
            }
        }
        return contactList;
    }

    @Transactional
    public List<ContactDTO> fetch(UserDTO exampleEntity)
    {
        List<ContactDTO> contactList = null;
        if (exampleEntity != null)
        {
            UserEntity exampleUserEntity = new UserEntity();
            exampleUserEntity.setId(exampleEntity.getId());

            List<ContactEntity> contactEntityList = contactDao.getContactEntityByUser(exampleUserEntity);
            if (contactEntityList != null)
            {
                contactList = new ArrayList<ContactDTO>();
                for (ContactEntity contactEntity : contactEntityList)
                {
                    ContactDTO contactDTO = Mapping.createContact(contactEntity);
                    if (contactDTO != null)
                    {
                        contactList.add(contactDTO);
                    }
                }
            }
        }
        return contactList;
    }

    @Transactional
    public List<ContactDTO> fetch()
    {
        List<ContactDTO> contactList = null;
        List<ContactEntity> contactEntityList = contactDao.getAllContactEntitys();
        if (contactEntityList != null)
        {
            contactList = new ArrayList<ContactDTO>();
            for (ContactEntity contactEntity : contactEntityList)
            {
                ContactDTO contactDTO = Mapping.createContact(contactEntity);
                if (contactDTO != null)
                {
                    contactList.add(contactDTO);
                }
            }
        }
        return contactList;
    }

    @Transactional
    public ContactDTO fetch(long id)
    {
        ContactEntity contactEntity = contactDao.getContactEntity(id);
        return Mapping.createContact(contactEntity);
    }

}
