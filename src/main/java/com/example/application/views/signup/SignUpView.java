package com.example.application.views.signup;

import com.example.application.data.User;
import com.example.application.data.UserType;
import com.example.application.services.UserService;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.springframework.security.crypto.password.PasswordEncoder;

@PageTitle("Sign up")
@Route("signup")
@AnonymousAllowed
public class SignUpView extends Composite<Main> {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    // Kentät lomakkeessa
    private final TextField firstNameField = new TextField("First name");
    private final TextField lastNameField  = new TextField("Last name");
    private final EmailField emailField     = new EmailField("Email");
    private final PasswordField passwordField = new PasswordField("Password");

    public SignUpView(UserService userService,
                      PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;

        buildView();
    }

    private void buildView() {
        // Otsikko
        H3 title = new H3("Create your account");

        // Form-layout
        FormLayout form = new FormLayout();
        form.add(firstNameField, lastNameField, emailField, passwordField);

        // Rekisteröinti-nappi
        Button register = new Button("Register", e -> {
            User newUser = new User();
            newUser.setFirstName(firstNameField.getValue());
            newUser.setLastName(lastNameField.getValue());
            newUser.setEmail(emailField.getValue());
            newUser.setPassword(passwordEncoder.encode(passwordField.getValue()));
            // Aina USER-oikeudella
            newUser.setUserType(UserType.USER);

            userService.save(newUser);

            Notification.show("Registration successful! You can now log in.");
            UI.getCurrent().navigate("login");
        });
        register.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        // Layout
        VerticalLayout layout = new VerticalLayout(title, form, register);
        layout.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        layout.setSizeFull();
        layout.getStyle().set("padding", "var(--lumo-space-l)");
        getContent().add(layout);
    }
}
