package com.example.application.views;

import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

@PageTitle("Access Denied")
@Route(value = "access-denied", layout = MainLayout.class)
@PermitAll
public class AccessDeniedView extends VerticalLayout {
    public AccessDeniedView() {
        addClassName("access-denied-view");
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        add(new H2("Access Denied"),
                new Paragraph("You do not have permission to view the requested page."));
    }
}
