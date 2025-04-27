package com.example.application.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;

/**
 * Yleisluokka CRUD-näkymille ilman maksullista Vaadinin Crud-komponenttia.
 */
public abstract class AbstractCrudView<T> extends VerticalLayout {

    protected final Grid<T> grid;
    protected final FormLayout form;
    protected final Binder<T> binder;
    protected final Button save   = new Button("Save");
    protected final Button delete = new Button("Delete");
    protected T current;

    protected AbstractCrudView(Class<T> beanType) {
        setSizeFull();

        // 1) Binder ja grid
        binder = new BeanValidationBinder<>(beanType);
        grid = new Grid<>(beanType, false);
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        grid.setSizeFull();

        // 2) Form-layout
        form = new FormLayout();

        save.addClickListener(e -> {
            try {
                T bean = (current == null) ? createNewBean() : current;
                binder.writeBean(bean);
                saveBean(bean);
                Notification.show("Saved")
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                clearForm();
                updateGrid();
            } catch (Exception ex) {
                Notification.show("Error: " + ex.getMessage())
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });

        delete.addClickListener(e -> {
            if (current != null) {
                deleteBean(current);
                clearForm();
                updateGrid();
            }
        });

        HorizontalLayout buttons = new HorizontalLayout(save, delete);
        form.add(buttons);

        // 3) Layout
        HorizontalLayout content = new HorizontalLayout(grid, form);
        content.setSizeFull();
        content.expand(grid);
        add(content);
    }

    /** Luo uuden entiteetin instanssin */
    protected abstract T createNewBean();

    /** Tallentaa entiteetin */
    protected abstract void saveBean(T bean);

    /** Poistaa entiteetin */
    protected abstract void deleteBean(T bean);

    /** Päivittää gridin sisällön */
    protected abstract void updateGrid();

    /** Tyhjentää lomakkeen tilan */
    protected void clearForm() {
        current = null;
        binder.readBean(null);
    }
}
