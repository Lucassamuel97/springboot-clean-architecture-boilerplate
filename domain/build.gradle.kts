// domain/build.gradle.kts
plugins {
    `java-library` // Usamos java-library em vez de java para expor a API corretamente
}

dependencies {
    // O domínio deve ter o mínimo de dependências possível.
    // Lombok é aceitável pois é uma ferramenta de compilação.
    compileOnly(Libs.lombok)
    annotationProcessor(Libs.lombok)

    // Dependências de teste
    testImplementation(Libs.springBootStarterTest) {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}