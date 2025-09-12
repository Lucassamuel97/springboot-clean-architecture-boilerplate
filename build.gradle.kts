// C:\javaspring\spring-crud-starter\build.gradle.kts (Arquivo Raiz)

import org.springframework.boot.gradle.plugin.SpringBootPlugin

// Define os plugins e suas versões para todo o projeto.
// 'apply false' significa que eles não são aplicados no projeto raiz,
// mas ficam disponíveis para os submódulos.
plugins {
    java
    id("org.springframework.boot") version Versions.springBoot apply false
    id("io.spring.dependency-management") version "1.1.5" apply false
    jacoco
}

// Adiciona repositórios para o projeto raiz (necessário para o JaCoCo)
repositories {
    mavenCentral()
}

// Configuração do JaCoCo para o projeto raiz
jacoco {
    toolVersion = "0.8.13"
}

// Configurações que serão aplicadas a TODOS os submódulos (domain, application, etc.)
subprojects {
    // Aplica os plugins em cada submódulo
    apply(plugin = "java")
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "jacoco")

    group = "com.starter.crudexample"
    version = "0.0.1-SNAPSHOT"

    java {
        sourceCompatibility = JavaVersion.toVersion(Versions.java)
    }

    repositories {
        mavenCentral()
    }

    // Configuração do JaCoCo
    jacoco {
        toolVersion = "0.8.13"
        reportsDirectory = layout.buildDirectory.dir("customJacocoReportDir")
    }

    // Gerencia as versões das dependências do Spring para todos os submódulos,
    // garantindo que todas usem a mesma versão compatível.
    configure<io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension> {
        imports {
            mavenBom(SpringBootPlugin.BOM_COORDINATES)
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
        finalizedBy(tasks.jacocoTestReport)
    }

    tasks.jacocoTestReport {
        dependsOn(tasks.test)
        reports {
            xml.required.set(true)
            html.required.set(true)
        }
    }
}

// Tarefa para gerar relatório de cobertura agregado para todo o projeto
tasks.register<JacocoReport>("codeCoverageReport") {
    group = "verification"
    description = "Generates an aggregate code coverage report for all subprojects"
    
    dependsOn(subprojects.map { it.tasks.named("test") })
    dependsOn(subprojects.map { it.tasks.named("jacocoTestReport") })
    
    // Coleta arquivos de execução do JaCoCo de todos os submódulos
    executionData.setFrom(fileTree(rootDir).include("**/build/jacoco/test.exec"))
    
    // Adiciona source sets de todos os submódulos
    subprojects.forEach { subproject ->
        sourceSets(subproject.the<SourceSetContainer>()["main"])
    }

    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
        html.outputLocation.set(layout.buildDirectory.dir("reports/jacoco/aggregate"))
    }
    
    // Garante que os diretórios existam
    doFirst {
        executionData.filter { it.exists() }
    }
}