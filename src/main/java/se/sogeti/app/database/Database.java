package se.sogeti.app.database;

import java.util.Set;

import se.sogeti.app.controllers.Controller;
import se.sogeti.app.models.dto.CategoryDTO;

public class Database {

    private Controller controller;

    public Database() {
        this.controller = new Controller();
    }

    public CategoryDTO fetchOpenCategory() {
        return controller.getOpenCategory();
    }

    public Set<CategoryDTO> postMultiple(Set<CategoryDTO> objects, String uri) {
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
