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
    testImplementation(Libs.springBootStarterTest)
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.2")
}

tasks.withType<Test> {
    useJUnitPlatform()
}