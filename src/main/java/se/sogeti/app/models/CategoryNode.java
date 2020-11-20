
package se.sogeti.app.models;

import java.io.Serializable;
import java.util.Objects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CategoryNode implements Serializable {

    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("href")
    @Expose
    private String href;
    @SerializedName("isTopLevel")
    @Expose
    private Boolean isTopLevel;
    private static final long serialVersionUID = 8814279577886480608L;

    /**
     * No args constructor for use in serialization
     * 
     */
    public CategoryNode() {
    }

    /**
     * 
     * @param isTopLevel
     * @param href
     * @param title
     */
    public CategoryNode(String title, String href, Boolean isTopLevel) {
        super();
        this.title = title;
        this.href = href;
        this.isTopLevel = isTopLevel;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public Boolean isTopLevel() {
        return isTopLevel;
    }

    public void setTopLevel(Boolean isTopLevel) {
        this.isTopLevel = isTopLevel;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof CategoryNode)) {
            return false;
        }
        CategoryNode categoryNode = (CategoryNode) o;
        return Objects.equals(title, categoryNode.title) && Objects.equals(href, categoryNode.href)
                && Objects.equals(isTopLevel, categoryNode.isTopLevel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, href, isTopLevel);
    }

    @Override
    public String toString() {
        return "{" + " title='" + getTitle() + "'" + ", href='" + getHref() + "'" + ", isTopLevel='" + isTopLevel()
                + "'" + "}";
    }

}
