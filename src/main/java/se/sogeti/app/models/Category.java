
package se.sogeti.app.models;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Category implements Serializable {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("categoryNodes")
    @Expose
    private List<CategoryNode> categoryNodes = null;
    private static final long serialVersionUID = -7632951882654114387L;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Category() {
    }

    /**
     * 
     * @param categoryNodes
     * @param id
     * @param title
     */
    public Category(Integer id, String title, List<CategoryNode> categoryNodes) {
        super();
        this.id = id;
        this.title = title;
        this.categoryNodes = categoryNodes;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<CategoryNode> getCategoryNodes() {
        return categoryNodes;
    }

    public void setCategoryNodes(List<CategoryNode> categoryNodes) {
        this.categoryNodes = categoryNodes;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Category)) {
            return false;
        }
        Category category = (Category) o;
        return Objects.equals(id, category.id) && Objects.equals(title, category.title)
                && Objects.equals(categoryNodes, category.categoryNodes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, categoryNodes);
    }

    @Override
    public String toString() {
        return "{" + " id='" + getId() + "'" + ", title='" + getTitle() + "'" + ", categoryNodes='" + getCategoryNodes()
                + "'" + "}";
    }

}
