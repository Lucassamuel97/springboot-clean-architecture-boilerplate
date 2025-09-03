// application/build.gradle.kts
plugins {
    `java-library`
}

dependencies {
    // Depende do módulo de domínio
    implementation(project(":domain"))

    // Dependências de frameworks, se estritamente necessário (ex: anotações)
    // Para @Transactional
    implementation("org.springframework:spring-tx")
    implementation(Libs.springBootStarterDataJpa) // Traz o spring-tx e APIs do JPA

    compileOnly(Libs.lombok)
    annotationProcessor(Libs.lombok)
}