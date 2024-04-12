package com.azure.examples.springboot.model;

public class VeggieItem {
  private Long id;

  private String name;

  private String description;

  public VeggieItem(Long id, String name, String description) {
    this.id = id;
    this.name = name;
    this.description = description;
  }

  public VeggieItem() {
  }

  public static VeggieItemBuilder builder() {
    return new VeggieItemBuilder();
  }

  public Long getId() {
    return this.id;
  }

  public String getName() {
    return this.name;
  }

  public String getDescription() {
    return this.description;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String toString() {
    return "VeggieItem(id=" + this.getId() + ", name=" + this.getName() + ", description=" + this.getDescription() + ")";
  }

  public boolean equals(final Object o) {
    if (o == this) return true;
    if (!(o instanceof VeggieItem)) return false;
    final VeggieItem other = (VeggieItem) o;
    if (!other.canEqual((Object) this)) return false;
    final Object this$id = this.getId();
    final Object other$id = other.getId();
    if (this$id == null ? other$id != null : !this$id.equals(other$id)) return false;
    return true;
  }

  protected boolean canEqual(final Object other) {
    return other instanceof VeggieItem;
  }

  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    final Object $id = this.getId();
    result = result * PRIME + ($id == null ? 43 : $id.hashCode());
    return result;
  }

  public static class VeggieItemBuilder {
    private Long id;
    private String name;
    private String description;

    VeggieItemBuilder() {
    }

    public VeggieItemBuilder id(Long id) {
      this.id = id;
      return this;
    }

    public VeggieItemBuilder name(String name) {
      this.name = name;
      return this;
    }

    public VeggieItemBuilder description(String description) {
      this.description = description;
      return this;
    }

    public VeggieItem build() {
      return new VeggieItem(this.id, this.name, this.description);
    }

    public String toString() {
      return "VeggieItem.VeggieItemBuilder(id=" + this.id + ", name=" + this.name + ", description=" + this.description + ")";
    }
  }
}
