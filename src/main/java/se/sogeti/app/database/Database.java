package se.sogeti.app.database;

import java.util.Set;

import se.sogeti.app.controllers.Controller;
import se.sogeti.app.models.dto.CategoryDTO;

public class Database<T> {

    private Controller<T> controller;

    public Database() {
        this.controller = new Controller<>();
    }


    public CategoryDTO fetchOpenCategory() {
        return controller.getOpenCategory();
    }

    public Set<T> postMultiple(Set<T> objects, String uri) {
        return controller.postMultiple(objects, uri);
    }

    public String getPublished(String objectNumber) {
        return controller.getPublished(objectNumber);
    }

    public String callGet(String href) {
        return controller.callGet(href);
    }

    public void close() {
        this.controller = null;
    }

}
