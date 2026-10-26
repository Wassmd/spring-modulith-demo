@org.springframework.modulith.ApplicationModule(
    displayName = "awesome-order",
    allowedDependencies = {"payment :: service", "inventory"}
)
package com.paxier.spring_modulith_demo.order;