@org.springframework.modulith.ApplicationModule(
    displayName = "awesome-order",
    allowedDependencies = {"customer :: model", "payment :: service"}
)
package com.paxier.spring_modulith_demo.order;