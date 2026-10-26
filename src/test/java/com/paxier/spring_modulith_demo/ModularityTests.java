package com.paxier.spring_modulith_demo;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;
import org.springframework.modulith.docs.Documenter.DiagramOptions;
import org.springframework.modulith.docs.Documenter.DiagramOptions.ElementsWithoutRelationships;

public class ModularityTests {
  ApplicationModules modules = ApplicationModules.of(Application.class);

  @Test
  void verifyModularity() {
    System.out.println(modules.toString());
    modules.verify();
  }

  @Test
  void writeDocumentationSnippets() { // brew install graphviz - to render the PlantUML diagrams
    var options = DiagramOptions.defaults()
        .withElementsWithoutRelationships(ElementsWithoutRelationships.VISIBLE);

    new Documenter(modules)
        .writeModulesAsPlantUml(options)
        .writeIndividualModulesAsPlantUml(options)
        .writeModuleCanvases()
        .writeAggregatingDocument();
  }
}
