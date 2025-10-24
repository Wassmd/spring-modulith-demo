package com.paxier.spring_modulith_demo;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

public class ModularityTests {
  ApplicationModules modules = ApplicationModules.of(SpringModulithDemoApplication.class);

  @Test
  void verifyModularity() {
    System.out.println(modules.toString());
    modules.verify();
  }

  @Test
  void writeDocumentationSnippets() {
    new Documenter(modules)
        .writeModulesAsPlantUml()
        .writeIndividualModulesAsPlantUml()
        .writeDocumentation()
        .writeModuleCanvases()
        .writeAggregatingDocument()
        .writeIndividualModulesAsPlantUml();
  }
}
