package com.example.vaadinapp;

import com.vaadin.flow.component.crud.BinderCrudEditor;
import com.vaadin.flow.component.crud.Crud;
import com.vaadin.flow.component.crud.CrudEditor;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.Route;

import java.util.*;

import static com.example.vaadinapp.DataSource.DATABASE;
import static com.example.vaadinapp.DataSource.IN_MEMORY;

@Route("")
public class ContactCrudView extends Div {

    private Crud<Contact> crud;

    private String FIRST_NAME = "firstName";
    private String LAST_NAME = "lastName";
    private String EMAIL = "email";
    private String STREET = "street";
    private String COUNTRY = "country";
    private String CITY = "city";
    private String PHONE = "phone";
    private String EDIT_COLUMN = "vaadin-crud-edit-column";
    private Crud.EditMode currentEditMode;
    private String originalPhone = null;
    private String editingId = null;
    private static final Map<DataSource, List<String>> editLockMap = new HashMap<>();
    private Binder<Contact> binder;
    private static DataSource selectedSource = DATABASE; // Default

    public ContactCrudView() {
        ContactDataProvider dataProvider;
        RadioButtonGroup<String> sourceSelector = new RadioButtonGroup<>();
        sourceSelector.setLabel("Select Data Source");
        sourceSelector.setItems(DATABASE.name(), IN_MEMORY.name());
        sourceSelector.setValue(selectedSource.name());

        sourceSelector.addValueChangeListener(e -> {
            selectedSource = DataSource.valueOf(e.getValue());
            getUI().ifPresent(ui -> ui.getPage().reload());
        });
        add(sourceSelector);
        if (DATABASE.equals(selectedSource)) {
            dataProvider = new DbContactDataProvider(new ContactDao());
        } else {
            dataProvider = new MapContactDataProvider();
        }
        editLockMap.putIfAbsent(selectedSource, new ArrayList<>());
        crud = new Crud<>(Contact.class, createEditor(dataProvider));
        setupGrid();
        setupDataProvider(dataProvider);
        crud.getGrid().getColumns().forEach(column -> column.setSortable(false));
        add(crud);
    }


    private void setupGrid() {
        Grid<Contact> grid = crud.getGrid();

        // Only show these columns (all columns shown by default):
        List<String> visibleColumns = Arrays.asList(FIRST_NAME, LAST_NAME,
                EMAIL, EDIT_COLUMN);
        grid.getColumns().forEach(column -> {
            String key = column.getKey();
            if (!visibleColumns.contains(key)) {
                grid.removeColumn(column);
            }
        });

//        // Reorder the columns (alphabetical by default)
//        grid.setColumnOrder(grid.getColumnByKey(FIRST_NAME),
//                grid.getColumnByKey(LAST_NAME), grid.getColumnByKey(EMAIL),
//                grid.getColumnByKey(EDIT_COLUMN));
    }

    private void setupDataProvider(ContactDataProvider dataProvider) {
        crud.setDataProvider(dataProvider);
        List<String> editLockList = editLockMap.get(selectedSource);

        crud.addEditListener(event -> {
            crud.getGrid().getDataProvider().refreshAll();
            if (editLockList.contains(event.getItem().getId())) {
                Notification.show("This contact is already being edited..", 5000, Notification.Position.MIDDLE);
                crud.setOpened(false);
                return;
            }
            currentEditMode = Crud.EditMode.EXISTING_ITEM;
            originalPhone = event.getItem().getPhone();
            editingId = event.getItem().getId();
            editLockList.add(editingId);  // locking contact
            Optional<Contact> freshObject = dataProvider.findById(event.getItem().getId());

            if (freshObject.isPresent()) {
                Contact clone = new Contact(freshObject.get());
                binder.readBean(clone);
                crud.getEditor().setItem(clone);
            } else {
                Notification.show("This contact no longer exists.", 3000, Notification.Position.MIDDLE);
                crud.setOpened(false);
            }
        });

        crud.addNewListener(event -> {
            currentEditMode = Crud.EditMode.NEW_ITEM;
            crud.getGrid().getDataProvider().refreshAll(); // Refresh grid to show latest state
        });
        crud.addSaveListener(saveEvent -> {
            Contact item = saveEvent.getItem();
            if ((Crud.EditMode.NEW_ITEM.equals(currentEditMode) && dataProvider.findByPhone(item.getPhone()).isPresent())
            || (Crud.EditMode.EXISTING_ITEM.equals(currentEditMode) && !originalPhone.equals(item.getPhone()) && !editingId.equals(item.getId())
                    && dataProvider.findByPhone(item.getPhone()).isPresent())) {
                Notification.show("Phone number already exists!", 3000, Notification.Position.MIDDLE);
                return;
            }
            dataProvider.save(item);
            dataProvider.refreshAll();
            editLockList.remove(editingId); // unlocking contact
        });
        crud.addDeleteListener(deleteEvent -> {
            dataProvider.delete(deleteEvent.getItem());
            editLockList.remove(editingId); // unlocking contact
            dataProvider.refreshAll();
        });
        crud.addCancelListener(cancelEvent -> {
            dataProvider.refreshAll();
            editLockList.remove(editingId); // unlocking contact
        });
    }

    private CrudEditor<Contact> createEditor(ContactDataProvider dataProvider) {
        TextField id = new TextField("Id");
        TextField firstName = new TextField("First Name");
        TextField lastName = new TextField("Last Name");
        TextField phone = new TextField("Phone");
        EmailField email = new EmailField("Email");
        TextField city = new TextField("City");
        TextField street = new TextField("Street");
        TextField country = new TextField("Country");

        binder = new Binder<>(Contact.class);
        binder.forField(id).asRequired().bind(Contact::getId, Contact::setId);
        binder.forField(firstName).asRequired().bind(Contact::getFirstName, Contact::setFirstName);
        binder.forField(lastName).asRequired().bind(Contact::getLastName, Contact::setLastName);
        binder.forField(email).asRequired().bind(Contact::getEmail, Contact::setEmail);
        binder.forField(city).asRequired().bind(Contact::getCity, Contact::setCity);
        binder.forField(country).asRequired().bind(Contact::getCountry, Contact::setCountry);
        binder.forField(street).asRequired().bind(Contact::getStreet, Contact::setStreet);
        binder.forField(phone)
                .asRequired("Phone number is required")
                .withValidator(phoneNum -> {
                    Optional<Contact> existing = dataProvider.findByPhone(phoneNum);
                    return existing.map(contact -> contact.getId().equals(editingId)).orElse(true);
                }, "Phone number already exists")
                .bind(Contact::getPhone, Contact::setPhone);

        FormLayout form = new FormLayout(
                firstName, lastName, phone, country, city, street, email
        );

        return new BinderCrudEditor<>(binder, form);
    }
}
