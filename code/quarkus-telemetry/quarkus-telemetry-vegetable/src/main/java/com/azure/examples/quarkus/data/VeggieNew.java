package com.azure.examples.quarkus.data;

public class VeggieNew {

  private String name;

  private String description;

  public VeggieNew(String name, String description) {
    this.name = name;
    this.description = description;
  }

  public VeggieNew() {
  }

  public static VeggieNewBuilder builder() {
    return new VeggieNewBuilder();
  }

  public String getName() {
    return this.name;
  }

  public String getDescription() {
    return this.description;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String toString() {
    return "VeggieNew(name=" + this.getName() + ", description=" + this.getDescription() + ")";
  }

  public boolean equals(final Object o) {
    if (o == this) return true;
    if (!(o instanceof VeggieNew)) return false;
    final VeggieNew other = (VeggieNew) o;
    if (!other.canEqual((Object) this)) return false;
    final Object this$name = this.getName();
    final Object other$name = other.getName();
    if (this$name == null ? other$name != null : !this$name.equals(other$name)) return false;
    return true;
  }

  protected boolean canEqual(final Object other) {
    return other instanceof VeggieNew;
  }

  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    final Object $name = this.getName();
    result = result * PRIME + ($name == null ? 43 : $name.hashCode());
    return result;
  }

  public static class VeggieNewBuilder {
    private String name;
    private String description;

    VeggieNewBuilder() {
    }

    public VeggieNewBuilder name(String name) {
      this.name = name;
      return this;
    }

    public VeggieNewBuilder description(String description) {
      this.description = description;
      return this;
    }

    public VeggieNew build() {
      return new VeggieNew(this.name, this.description);
    }

    public String toString() {
      return "VeggieNew.VeggieNewBuilder(name=" + this.name + ", description=" + this.description + ")";
    }
  }
}
