// infrastructure/build.gradle.kts
plugins {
    `java-library`
    id("org.springframework.boot") // Plugin do Spring Boot
}


springBoot {
    mainClass.set("com.starter.crudexample.infrastructure.CrudexampleApplication")
}

dependencies {
    // Depende do módulo de aplicação
    implementation(project(":application"))
    implementation(project(":domain"))

    // Implementações concretas do Spring
    implementation(Libs.springBootStarterWeb)
    implementation(Libs.springBootStarterDataJpa)
    implementation(Libs.springBootStarterSecurity)
    implementation(Libs.springBootStarterValidation)
    
    // Swagger/OpenAPI
    implementation(Libs.springdocOpenapi)
    
    // Jackson modules
    implementation(Libs.jacksonAfterburner)
    
    // JWT
    implementation(Libs.jjwtApi)
    runtimeOnly(Libs.jjwtImpl)
    runtimeOnly(Libs.jjwtJackson)

    // Database
    runtimeOnly(Libs.mysqlConnector)
    implementation(Libs.flywayCore)
    implementation(Libs.flywayMysql)

    runtimeOnly(Libs.h2Database)
    developmentOnly(Libs.springBootDevTools)

    compileOnly(Libs.lombok)
    annotationProcessor(Libs.lombok)

    // Dependência de teste
    testImplementation(Libs.springBootStarterTest)
    testImplementation(Libs.springSecurityTest)
    testImplementation(Libs.springSecurityOauth2Jose)
    testImplementation(Libs.springSecurityOauth2ResourceServer)
    
    // TestContainers para testes E2E
    testImplementation(Libs.testcontainersJupiter)
    testImplementation(Libs.testcontainersMysql)
    testImplementation(Libs.restAssured)
}
