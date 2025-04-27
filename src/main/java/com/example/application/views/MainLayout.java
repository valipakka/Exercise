package com.example.application.views;

import com.example.application.security.SecurityUtils;
import com.example.application.views.exercises.MyExercisesView;
import com.example.application.views.homepage.HomePageView;
import com.example.application.views.login.LoginView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RouterLink;

public class MainLayout extends AppLayout {

    // Tämä kontti sisältää aina näkymän + footerin
    private final VerticalLayout contentWrapper = new VerticalLayout();
    private final HorizontalLayout footer = new HorizontalLayout();

    public MainLayout() {
        setPrimarySection(Section.NAVBAR);

        createHeader();
        initFooter();

        // Valmistele wrapper ja rekisteröi se AppLayoutin content-alueeksi
        contentWrapper.setSizeFull();
        contentWrapper.setPadding(false);
        contentWrapper.setSpacing(false);
        super.setContent(contentWrapper);
    }

    private void createHeader() {
        H1 logo = new H1("Your App");
        logo.getStyle().set("margin", "0");

        HorizontalLayout header = new HorizontalLayout();
        header.setDefaultVerticalComponentAlignment(Alignment.CENTER);
        header.expand(logo);
        header.setWidthFull();
        header.add(logo,
                new RouterLink("Home", HomePageView.class));

        String user = SecurityUtils.getCurrentUserEmail();
        if (user != null) {
            header.add(new RouterLink("My Exercises", MyExercisesView.class));
            if (SecurityUtils.isCurrentUserAdmin()) {
                header.add(new RouterLink("Admin", AdminUserView.class));
            }
            // Log-out napilla tehdään aito selaimen GET /logout
            Button logout = new Button("Log out", e ->
                    UI.getCurrent().getPage().setLocation("/logout")
            );
            logout.getElement().setAttribute("router-ignore", "");
            header.add(logout);

        } else {
            header.add(new RouterLink("Login", LoginView.class));
        }

        addToNavbar(header);
    }

    private void initFooter() {
        footer.setWidthFull();
        footer.setJustifyContentMode(JustifyContentMode.CENTER);
        footer.getStyle().set("padding", "0.5em 0");
        footer.getStyle().set("background", "#f0f0f0");
        footer.add(new Span("© 2025 Your Company"));
    }


    @Override
    public void setContent(Component content) {
        contentWrapper.removeAll();
        // Lisää ensin näkymä ja anna sen venyä täyteen tilaan
        contentWrapper.addAndExpand(content);
        // Ja lopuksi footer
        contentWrapper.add(footer);
    }
}
