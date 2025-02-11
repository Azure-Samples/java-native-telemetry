package com.azure.examples.quarkus.superhero.data;

import com.azure.examples.quarkus.superhero.model.CapeType;
import io.quarkus.runtime.annotations.RegisterForReflection;

public class HeroItem {

  private String id;
  private String name;
  private String originalName;
  private CapeType capeType;

  public HeroItem(String id, String name, String originalName, CapeType capeType) {
    this.id = id;
    this.name = name;
    this.originalName = originalName;
    this.capeType = capeType;
  }

  public HeroItem() {
  }

  public static HeroItemBuilder builder() {
    return new HeroItemBuilder();
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
    return "HeroItem(id=" + this.getId() + ", name=" + this.getName() + ", originalName=" + this.getOriginalName() + ", capeType=" + this.getCapeType() + ")";
  }

  public boolean equals(final Object o) {
    if (o == this) return true;
    if (!(o instanceof HeroItem)) return false;
    final HeroItem other = (HeroItem) o;
    if (!other.canEqual((Object) this)) return false;
    final Object this$name = this.getName();
    final Object other$name = other.getName();
    if (this$name == null ? other$name != null : !this$name.equals(other$name)) return false;
    return true;
  }

  protected boolean canEqual(final Object other) {
    return other instanceof HeroItem;
  }

  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    final Object $name = this.getName();
    result = result * PRIME + ($name == null ? 43 : $name.hashCode());
    return result;
  }

  public static class HeroItemBuilder {
    private String id;
    private String name;
    private String originalName;
    private CapeType capeType;

    HeroItemBuilder() {
    }

    public HeroItemBuilder id(String id) {
      this.id = id;
      return this;
    }

    public HeroItemBuilder name(String name) {
      this.name = name;
      return this;
    }

    public HeroItemBuilder originalName(String originalName) {
      this.originalName = originalName;
      return this;
    }

    public HeroItemBuilder capeType(CapeType capeType) {
      this.capeType = capeType;
      return this;
    }

    public HeroItem build() {
      return new HeroItem(this.id, this.name, this.originalName, this.capeType);
    }

    public String toString() {
      return "HeroItem.HeroItemBuilder(id=" + this.id + ", name=" + this.name + ", originalName=" + this.originalName + ", capeType=" + this.capeType + ")";
    }
  }
}

