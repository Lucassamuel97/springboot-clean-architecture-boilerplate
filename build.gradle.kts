// C:\javaspring\spring-crud-starter\build.gradle.kts (Arquivo Raiz)

import org.springframework.boot.gradle.plugin.SpringBootPlugin

// Define os plugins e suas versões para todo o projeto.
// 'apply false' significa que eles não são aplicados no projeto raiz,
// mas ficam disponíveis para os submódulos.
plugins {
    java
    id("org.springframework.boot") version Versions.springBoot apply false
    id("io.spring.dependency-management") version "1.1.5" apply false
}

// Configurações que serão aplicadas a TODOS os submódulos (domain, application, etc.)
subprojects {
    // Aplica os plugins em cada submódulo
    apply(plugin = "java")
    apply(plugin = "io.spring.dependency-management")

    group = "com.starter.crudexample"
    version = "0.0.1-SNAPSHOT"

    java {
        sourceCompatibility = JavaVersion.toVersion(Versions.java)
    }

    repositories {
        mavenCentral()
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
    }
}