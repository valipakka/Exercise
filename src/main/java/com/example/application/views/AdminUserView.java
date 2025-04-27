package com.example.application.views;

import com.example.application.data.User;
import com.example.application.data.UserType;
import com.example.application.services.UserService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.security.crypto.password.PasswordEncoder;

@PageTitle("User Administration")
@Route(value = "admin/users", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class AdminUserView extends VerticalLayout {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    private final Grid<User> grid = new Grid<>(User.class);
    private final TextField filter = new TextField("Search by email");

    private final FormLayout form = new FormLayout();
    private User currentUser;

    // form‐kentät
    private final TextField firstName    = new TextField("First name");
    private final TextField lastName     = new TextField("Last name");
    private final EmailField email       = new EmailField("Email");
    private final PasswordField password = new PasswordField("Password");
    private final ComboBox<UserType> role = new ComboBox<>("Role", UserType.values());

    // lomake‐napit
    private final Button save   = new Button("Save");
    private final Button delete = new Button("Delete");
    private final Button cancel = new Button("Cancel");
    private final Button addNew = new Button("Add new");

    public AdminUserView(UserService userService,
                         PasswordEncoder passwordEncoder) {
        this.userService      = userService;
        this.passwordEncoder  = passwordEncoder;

        setSizeFull();
        configureGrid();
        configureForm();

        H2 title = new H2("User management");
        HorizontalLayout toolbar = new HorizontalLayout(filter, addNew);
        toolbar.setAlignItems(Alignment.END);

        HorizontalLayout content = new HorizontalLayout(grid, form);
        content.setSizeFull();
        grid.setSizeFull();
        form.setVisible(false);

        add(title, toolbar, content);
        updateGrid();
    }

    private void configureGrid() {
        grid.setColumns("firstName", "lastName", "email", "userType");
        grid.getColumnByKey("userType").setHeader("Role");
        grid.asSingleSelect().addValueChangeListener(e -> editUser(e.getValue()));

        filter.setPlaceholder("Filter by email...");
        filter.addValueChangeListener(e -> updateGrid());

        addNew.addClickListener(e -> {
            grid.asSingleSelect().clear();
            editUser(new User());
        });
    }

    private void configureForm() {
        form.add(firstName, lastName, email, password, role,
                new HorizontalLayout(save, delete, cancel));

        save.addClickListener(e -> saveUser());
        delete.addClickListener(e -> deleteUser());
        cancel.addClickListener(e -> closeEditor());
    }

    private void editUser(User user) {
        if (user == null) {
            closeEditor();
            return;
        }
        this.currentUser = user;
        firstName.setValue(user.getFirstName() != null ? user.getFirstName() : "");
        lastName.setValue(user.getLastName()   != null ? user.getLastName()   : "");
        email.setValue(user.getEmail()        != null ? user.getEmail()      : "");
        role.setValue(user.getUserType()      != null ? user.getUserType()   : UserType.USER);
        password.clear();
        form.setVisible(true);
    }

    private void saveUser() {
        currentUser.setFirstName(firstName.getValue());
        currentUser.setLastName(lastName.getValue());
        currentUser.setEmail(email.getValue());
        currentUser.setUserType(role.getValue());
        if (!password.isEmpty()) {
            currentUser.setPassword(passwordEncoder.encode(password.getValue()));
        }
        userService.save(currentUser);
        Notification.show("User saved");
        updateGrid();
        closeEditor();
    }

    private void deleteUser() {
        userService.delete(currentUser);
        Notification.show("User deleted");
        updateGrid();
        closeEditor();
    }

    private void closeEditor() {
        form.setVisible(false);
    }

    private void updateGrid() {
        String term = filter.getValue().trim().toLowerCase();
        if (term.isEmpty()) {
            grid.setItems(userService.findAll());
        } else {
            grid.setItems(userService.findAll().stream()
                    .filter(u -> u.getEmail().toLowerCase().contains(term)).toList());
        }
    }
}
