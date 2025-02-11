package com.azure.examples.quarkus.superhero.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Hero {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  private String name;

  private String originalName;

  @Enumerated(EnumType.STRING)
  private CapeType capeType;

  public Hero(String id, String name, String originalName, CapeType capeType) {
    this.id = id;
    this.name = name;
    this.originalName = originalName;
    this.capeType = capeType;
  }

  public Hero() {
  }

  public static HeroBuilder builder() {
    return new HeroBuilder();
  }

  public String getId() {
    return this.id;
  }

  public String getName() {
    return this.name;
  }

  public String getOriginalName() {
    return this.originalName;
  }

  public CapeType getCapeType() {
    return this.capeType;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setOriginalName(String originalName) {
    this.originalName = originalName;
  }

  public void setCapeType(CapeType capeType) {
    this.capeType = capeType;
  }

  public String toString() {
    return "Hero(id=" + this.getId() + ", name=" + this.getName() + ", originalName=" + this.getOriginalName() + ", capeType=" + this.getCapeType() + ")";
  }

  public boolean equals(final Object o) {
    if (o == this) return true;
    if (!(o instanceof Hero)) return false;
    final Hero other = (Hero) o;
    if (!other.canEqual((Object) this)) return false;
    final Object this$name = this.getName();
    final Object other$name = other.getName();
    if (this$name == null ? other$name != null : !this$name.equals(other$name)) return false;
    return true;
  }

  protected boolean canEqual(final Object other) {
    return other instanceof Hero;
  }

  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    final Object $name = this.getName();
    result = result * PRIME + ($name == null ? 43 : $name.hashCode());
    return result;
  }

  public static class HeroBuilder {
    private String id;
    private String name;
    private String originalName;
    private CapeType capeType;

    HeroBuilder() {
    }

    public HeroBuilder id(String id) {
      this.id = id;
      return this;
    }

    public HeroBuilder name(String name) {
      this.name = name;
      return this;
    }

    public HeroBuilder originalName(String originalName) {
      this.originalName = originalName;
      return this;
    }

    public HeroBuilder capeType(CapeType capeType) {
      this.capeType = capeType;
      return this;
    }

    public Hero build() {
      return new Hero(this.id, this.name, this.originalName, this.capeType);
    }

    public String toString() {
      return "Hero.HeroBuilder(id=" + this.id + ", name=" + this.name + ", originalName=" + this.originalName + ", capeType=" + this.capeType + ")";
    }
  }
}

