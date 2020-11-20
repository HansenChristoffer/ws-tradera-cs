package se.sogeti.app.models.dto;

import java.io.Serializable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class CategoryDTO implements Serializable {

    private static final long serialVersionUID = -379512724456875830L;

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("href")
    @Expose
    private String href;

    @SerializedName("isOpen")
    @Expose
    private boolean isOpen = true;

    public CategoryDTO() {
    }

    public CategoryDTO(String name, String href, boolean isOpen) {
        this.name = name;
        this.href = href;
        this.isOpen = isOpen;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean isOpen) {
        this.isOpen = isOpen;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("id", id).append("name", name).append("href", href)
                .append("isOpen", isOpen).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(name).append(id).append(href).append(isOpen).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof CategoryDTO)) {
            return false;
        }
        CategoryDTO rhs = ((CategoryDTO) other);
        return new EqualsBuilder().append(name, rhs.name).append(id, rhs.id).append(href, rhs.href)
                .append(isOpen, rhs.isOpen).isEquals();
    }

}
