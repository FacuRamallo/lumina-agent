// Archivo: build.gradle.kts
plugins {
    java
    id("org.springframework.boot") version "3.4.1"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.facundo.lumina"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    mavenCentral()
    // REPOSITORIOS OBLIGATORIOS PARA SPRING AI (Milestones)
    maven { url = uri("https://repo.spring.io/milestone") }
    maven { url = uri("https://repo.spring.io/snapshot") }
}

val springAiVersion = "1.0.0-M5"

dependencies {
    // --- Spring AI Core & Ollama ---
    implementation("org.springframework.ai:spring-ai-ollama-spring-boot-starter")

    // --- RAG & Vector Store ---
    implementation("org.springframework.ai:spring-ai-pgvector-store-spring-boot-starter")

    // Drivers de Base de Datos (Necesarios para que JPA/JDBC hable con Postgres)
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("org.postgresql:postgresql")

    // --- Utils & Web ---
    implementation("org.springframework.boot:spring-boot-starter-web")
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    // Módulo para leer PDFs
    implementation("org.springframework.ai:spring-ai-pdf-document-reader")

    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("io.micrometer:micrometer-registry-prometheus")

    // Gestión de trazas
    implementation("io.micrometer:micrometer-tracing-bridge-brave")
    // Exportador a Zipkin
    implementation("io.zipkin.reporter2:zipkin-reporter-brave")

    // --- CRÍTICO: Fix para Apple Silicon (M1/M2/M3) ---
    // Soluciona el error DNS de Netty
    implementation("io.netty:netty-resolver-dns-native-macos::osx-aarch_64")

    // --- Testing ---
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.ai:spring-ai-bom:$springAiVersion")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}