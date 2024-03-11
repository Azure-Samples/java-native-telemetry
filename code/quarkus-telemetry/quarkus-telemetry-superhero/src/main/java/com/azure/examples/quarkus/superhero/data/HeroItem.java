
package com.azure.examples.quarkus.superhero.data;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import com.azure.examples.quarkus.superhero.model.CapeType;

@RegisterForReflection
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"name"})
@Builder
public class HeroItem {

    private String id;

    private String name;

    private String originalName;

    private CapeType capeType;
}

