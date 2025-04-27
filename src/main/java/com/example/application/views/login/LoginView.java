package com.example.application.views.login;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.example.application.views.signup.SignUpView;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;

@PageTitle("Login")
@Route("login")
@AnonymousAllowed
public class LoginView extends Composite<Main> implements BeforeEnterObserver {

    private final LoginForm login = new LoginForm();

    public LoginView() {
        // Sana: Spring Security hoitaa POSTin polkuun "/login"
        login.setAction("login");

        // Pakataan LoginForm ja Sign Up -nappi pystysuuntaiseksi layoutiksi
        VerticalLayout wrapper = new VerticalLayout();
        wrapper.setSizeFull();
        wrapper.setAlignItems(Alignment.CENTER);
        wrapper.setJustifyContentMode(JustifyContentMode.CENTER);

        // Linkki rekisteröitymissivulle
        Button signUp = new Button("Sign up", e ->
                UI.getCurrent().navigate(SignUpView.class)
        );
        signUp.getElement().getStyle().set("marginTop", "1em");

        wrapper.add(login, signUp);
        getContent().add(wrapper);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        // jos URL:ssa on ?error, näytä virhe
        if (event.getLocation()
                .getQueryParameters()
                .getParameters()
                .containsKey("error")) {
            login.setError(true);
        }
    }
}
