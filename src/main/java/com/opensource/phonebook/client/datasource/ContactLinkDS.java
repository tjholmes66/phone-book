package com.opensource.phonebook.client.datasource;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.opensource.phonebook.client.services.ContactLinkService;
import com.opensource.phonebook.client.services.ContactLinkServiceAsync;
import com.opensource.phonebook.client.utils.datasource.GwtRpcDataSource;
import com.opensource.phonebook.shared.Constants;
import com.opensource.phonebook.shared.dto.ContactDTO;
import com.opensource.phonebook.shared.dto.ContactLinkDTO;
import com.opensource.phonebook.shared.dto.LinkTypeDTO;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.fields.DataSourceDateTimeField;
import com.smartgwt.client.data.fields.DataSourceIntegerField;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.rpc.RPCResponse;
import com.smartgwt.client.util.JSOHelper;
import com.smartgwt.client.widgets.grid.ListGridRecord;

public class ContactLinkDS extends GwtRpcDataSource
{
    private static ContactLinkDS instance = null;

    private final ContactLinkServiceAsync contactLinkService = GWT.create(ContactLinkService.class);

    public static ContactLinkDS getInstance()
    {
        if (instance == null)
        {
            instance = new ContactLinkDS();
        }
        return instance;
    }

    DataSourceIntegerField contactIdField;
    DataSourceIntegerField contactLinkIdField;
    DataSourceTextField linkTypeIdField;
    DataSourceTextField linkField;
    DataSourceDateTimeField enteredDateField;

    public ContactLinkDS()
    {
        super();
        setID("ContactLinksGwtRpcDataSource");

        contactIdField = new DataSourceIntegerField(Constants.LINK_CONTACT_ID, null);
        contactIdField.setCanEdit(false);
        contactIdField.setHidden(true);

        contactLinkIdField = new DataSourceIntegerField(Constants.LINK_ID, null);
        contactLinkIdField.setPrimaryKey(true);
        contactLinkIdField.setCanEdit(false);
        contactLinkIdField.setHidden(true);

        linkField = new DataSourceTextField(Constants.LINK_URL, Constants.TITLE_LINK_URL);

        linkTypeIdField = new DataSourceTextField(Constants.LINK_TYPE_ID, Constants.TITLE_LINK_TYPE_ID);

        enteredDateField = new DataSourceDateTimeField(Constants.LINK_ENTERED_DATE, Constants.TITLE_LINK_ENTERED_DATE);

        setFields(contactIdField, contactLinkIdField, linkField, linkTypeIdField, enteredDateField);
    }

    // *************************************************************************************
    // *************************************************************************************

    @Override
    protected void executeAdd(final String requestId, final DSRequest request, final DSResponse response)
    {
        // Retrieve record which should be added.
        JavaScriptObject data = request.getData();
        ListGridRecord rec = new ListGridRecord(data);
        ContactLinkDTO testRec = new ContactLinkDTO();
        copyValues(rec, testRec);
        contactLinkService.add(testRec, new AsyncCallback<ContactLinkDTO>()
        {
            public void onFailure(Throwable caught)
            {
                response.setStatus(RPCResponse.STATUS_FAILURE);
                processResponse(requestId, response);
            }

            public void onSuccess(ContactLinkDTO result)
            {
                ListGridRecord[] list = new ListGridRecord[1];
                ListGridRecord newRec = new ListGridRecord();
                copyValues(result, newRec);
                list[0] = newRec;
                response.setData(list);
                processResponse(requestId, response);
            }
        });
    }

    @Override
    protected void executeRemove(final String requestId, final DSRequest request, final DSResponse response)
    {
        // Retrieve record which should be removed.
        JavaScriptObject data = request.getData();
        final ListGridRecord rec = new ListGridRecord(data);
        ContactLinkDTO testRec = new ContactLinkDTO();
        copyValues(rec, testRec);
        contactLinkService.remove(testRec, new AsyncCallback<Void>()
        {
            public void onFailure(Throwable caught)
            {
                response.setStatus(RPCResponse.STATUS_FAILURE);
                processResponse(requestId, response);
            }

            public void onSuccess(Void result)
            {
                ListGridRecord[] list = new ListGridRecord[1];
                // We do not receive removed record from server.
                // Return record from request.
                list[0] = rec;
                response.setData(list);
                processResponse(requestId, response);
            }
        });
    }

    // *************************************************************************************
    // *************************************************************************************

    @Override
    protected void executeFetch(final String requestId, DSRequest request, final DSResponse response)
    {

        // for this example I will do "paging" on client side
        final int startIndex = (request.getStartRow() < 0) ? 0 : request.getStartRow();
        final int endIndex = (request.getEndRow() == null) ? -1 : request.getEndRow();

        String contactId = request.getCriteria().getAttribute("contactId");
        ContactDTO contactDto = new ContactDTO();
        contactDto.setId(Long.parseLong(contactId));

        contactLinkService.fetch(contactDto, new AsyncCallback<List<ContactLinkDTO>>()
        {
            public void onFailure(Throwable caught)
            {
                caught.printStackTrace();
                response.setStatus(RPCResponse.STATUS_FAILURE);
                processResponse(requestId, response);
            }

            public void onSuccess(List<ContactLinkDTO> result)
            {
                // Calculating size of return list
                int size = result.size();
                if (endIndex >= 0)
                {
                    if (endIndex < startIndex)
                    {
                        size = 0;
                    }
                    else
                    {
                        size = endIndex - startIndex + 1;
                    }
                }
                // Create list for return - it is just requested records
                ListGridRecord[] list = new ListGridRecord[size];
                if (size > 0)
                {
                    for (int i = 0; i < result.size(); i++)
                    {
                        if (i >= startIndex && i <= endIndex)
                        {
                            ListGridRecord record = new ListGridRecord();
                            copyValues(result.get(i), record);
                            list[i - startIndex] = record;
                        }
                    }
                }
                response.setStatus(RPCResponse.STATUS_SUCCESS);
                response.setData(list);
                // IMPORTANT: for paging to work we have to specify size of full
                // result set
                response.setTotalRows(result.size());
                processResponse(requestId, response);
            }
        });
    }

    @Override
    protected void executeUpdate(String requestId, DSRequest request, DSResponse response)
    {
        // TODO Auto-generated method stub
    }

    // *************************************************************************************
    // *************************************************************************************

    private void copyValues(ListGridRecord from, ContactLinkDTO to)
    {
        to.setContactId(from.getAttributeAsInt(contactIdField.getName()));
        to.setLinkId(from.getAttributeAsInt(contactLinkIdField.getName()));
        to.setLink(from.getAttributeAsString(linkField.getName()));
        to.setEnteredDate(from.getAttributeAsDate(enteredDateField.getName()));
        // ================================================================================
        LinkTypeDTO linkType = new LinkTypeDTO();
        linkType.setId(from.getAttributeAsInt(linkTypeIdField.getName()));
        to.setLinkType(linkType);
    }

    private static void copyValues(ContactLinkDTO from, ListGridRecord to)
    {
        to.setAttribute(Constants.LINK_CONTACT_ID, from.getContactId());
        to.setAttribute(Constants.LINK_ID, from.getLinkId());
        to.setAttribute(Constants.LINK_URL, from.getLink());
        to.setAttribute(Constants.LINK_ENTERED_DATE, from.getEnteredDate());
        to.setAttribute(Constants.LINK_TYPE_ID, from.getLinkType().getId());
    }

    private ListGridRecord getEditedRecord(DSRequest request)
    {
        // Retrieving values before edit
        JavaScriptObject oldValues = request.getAttributeAsJavaScriptObject("oldValues");
        // Creating new record for combining old values with changes
        ListGridRecord newRecord = new ListGridRecord();
        // Copying properties from old record
        JSOHelper.apply(oldValues, newRecord.getJsObj());
        // Retrieving changed values
        JavaScriptObject data = request.getData();
        // Apply changes
        JSOHelper.apply(data, newRecord.getJsObj());
        return newRecord;
    }

}
