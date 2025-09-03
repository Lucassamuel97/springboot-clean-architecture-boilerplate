// infrastructure/build.gradle.kts
plugins {
    `java-library`
    id("org.springframework.boot") // Plugin do Spring Boot
}

// Mova a classe CrudexampleApplication.java para infrastructure/src/main/java/...
// e informe ao plugin do Spring Boot onde encontrá-la.
springBoot {
    mainClass.set("com.starter.crudexample.CrudexampleApplication")
}

dependencies {
    // Depende do módulo de aplicação
    implementation(project(":application"))

    // Implementações concretas do Spring
    implementation(Libs.springBootStarterWeb)
    implementation(Libs.springBootStarterDataJpa)
    implementation(Libs.springBootStarterSecurity)
    implementation(Libs.springBootStarterValidation)

    runtimeOnly(Libs.h2Database)
    developmentOnly(Libs.springBootDevTools)

    compileOnly(Libs.lombok)
    annotationProcessor(Libs.lombok)

    // Dependência de teste
    testImplementation(Libs.springBootStarterTest)
}