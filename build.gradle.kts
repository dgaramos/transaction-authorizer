plugins {
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	id("org.springframework.boot") version "3.4.0"
	id("io.spring.dependency-management") version "1.1.6"
	id("org.flywaydb.flyway") version "9.1.1" // Added Flyway plugin
}

group = "br.com"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

repositories {
	mavenCentral()
}

val exposedVersion = "0.52.0"

dependencies {
	// Core Spring Boot dependencies
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("org.springframework.boot:spring-boot-starter-hateoas")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")

	// Kotlin support
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib")

	// Database-related dependencies
	implementation("org.flywaydb:flyway-core:9.6.0")
	runtimeOnly("org.postgresql:postgresql")
	implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
	implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
	implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
	implementation("org.jetbrains.exposed:exposed-java-time:$exposedVersion")

	// OpenAPI documentation
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.1.0")

	// Utilities
	implementation("com.google.code.gson:gson")

	// Development tools
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	developmentOnly("org.springframework.boot:spring-boot-docker-compose")

	// Testing dependencies
	testImplementation("org.springframework.boot:spring-boot-starter-test") {
		exclude(group = "org.junit.vintage")
		exclude(group = "org.mockito")
		exclude(group = "org.mockito.kotlin")
	}
	testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
	testImplementation("org.jetbrains.kotlin:kotlin-test")
	testImplementation("io.mockk:mockk:1.13.12")
	testImplementation("org.testcontainers:testcontainers:1.19.3")
	testImplementation("org.testcontainers:postgresql:1.19.3")
	testImplementation("org.testcontainers:junit-jupiter:1.19.3")

	// Optional runtime dependencies for tests
	testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.10.0") // Ensure platform compatibility
}

val springDataSourceUrl: String = System.getenv("SPRING_DATASOURCE_URL") ?: "jdbc:postgresql://localhost:5432/demo_db"
val springDataSourceUsername: String = System.getenv("SPRING_DATASOURCE_USERNAME") ?: "demo_dev_rw"
val springDataSourcePassword: String = System.getenv("SPRING_DATASOURCE_PASSWORD") ?: "dev_database_passwd"

flyway {
	cleanDisabled = false
	url = springDataSourceUrl
	user = springDataSourceUsername
	password = springDataSourcePassword
}
tasks.withType<Test> {
	useJUnitPlatform()
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
