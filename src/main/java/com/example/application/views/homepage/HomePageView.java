package com.example.application.views.homepage;

import com.example.application.data.SamplePerson;
import com.example.application.services.SamplePersonService;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

@PageTitle("Home Page")
@Route("")
@Menu(order = 0, icon = LineAwesomeIconUrl.PENCIL_RULER_SOLID)
@Uses(Icon.class)
public class HomePageView extends Composite<VerticalLayout> {

    public HomePageView() {
        VerticalLayout layoutColumn2 = new VerticalLayout();
        H2 h2 = new H2();
        Grid basicGrid = new Grid(SamplePerson.class);
        HorizontalLayout layoutRow = new HorizontalLayout();
        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");
        layoutColumn2.setWidth("100%");
        layoutColumn2.getStyle().set("flex-grow", "1");
        h2.setText("Last Exercises");
        h2.setWidth("max-content");
        layoutColumn2.setAlignSelf(FlexComponent.Alignment.START, basicGrid);
        basicGrid.setWidth("100%");
        basicGrid.getStyle().set("flex-grow", "0");
        setGridSampleData(basicGrid);
        layoutRow.addClassName(Gap.MEDIUM);
        layoutRow.setWidth("100%");
        layoutRow.setHeight("min-content");
        getContent().add(layoutColumn2);
        layoutColumn2.add(h2);
        layoutColumn2.add(basicGrid);
        getContent().add(layoutRow);
    }

    private void setGridSampleData(Grid grid) {
        grid.setItems(query -> samplePersonService.list(VaadinSpringDataHelpers.toSpringPageRequest(query)).stream());
    }

    @Autowired()
    private SamplePersonService samplePersonService;
}
