// infrastructure/build.gradle.kts
plugins {
    `java-library`
    id("org.springframework.boot") // Plugin do Spring Boot
}


springBoot {
    mainClass.set("com.starter.crudexample.CrudexampleApplication")
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

    runtimeOnly(Libs.h2Database)
    developmentOnly(Libs.springBootDevTools)

    compileOnly(Libs.lombok)
    annotationProcessor(Libs.lombok)

    // Dependência de teste
    testImplementation(Libs.springBootStarterTest)
    testImplementation(Libs.springSecurityTest)
}