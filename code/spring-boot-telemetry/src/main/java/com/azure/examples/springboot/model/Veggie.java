package com.azure.examples.springboot.model;

import jakarta.persistence.*;

@Entity
public class Veggie {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column(nullable = false)
  private String name;

  @Column(length = 2000)
  private String description;

  public Veggie(Long id, String name, String description) {
    this.id = id;
    this.name = name;
    this.description = description;
  }

  public Veggie(String name, String description) {
    this.name = name;
    this.description = description;
  }

  public Veggie() {
  }

  public static VeggieBuilder builder() {
    return new VeggieBuilder();
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
    return "Veggie(id=" + this.getId() + ", name=" + this.getName() + ", description=" + this.getDescription() + ")";
  }

  public boolean equals(final Object o) {
    if (o == this) return true;
    if (!(o instanceof Veggie)) return false;
    final Veggie other = (Veggie) o;
    if (!other.canEqual((Object) this)) return false;
    final Object this$id = this.getId();
    final Object other$id = other.getId();
    if (this$id == null ? other$id != null : !this$id.equals(other$id)) return false;
    return true;
  }

  protected boolean canEqual(final Object other) {
    return other instanceof Veggie;
  }

  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    final Object $id = this.getId();
    result = result * PRIME + ($id == null ? 43 : $id.hashCode());
    return result;
  }

  public static class VeggieBuilder {
    private Long id;
    private String name;
    private String description;

    VeggieBuilder() {
    }

    public VeggieBuilder id(Long id) {
      this.id = id;
      return this;
    }

    public VeggieBuilder name(String name) {
      this.name = name;
      return this;
    }

    public VeggieBuilder description(String description) {
      this.description = description;
      return this;
    }

    public Veggie build() {
      return new Veggie(this.id, this.name, this.description);
    }

    public String toString() {
      return "Veggie.VeggieBuilder(id=" + this.id + ", name=" + this.name + ", description=" + this.description + ")";
    }
  }
}
