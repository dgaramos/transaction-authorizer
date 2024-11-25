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
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("org.springframework.boot:spring-boot-starter-hateoas")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.1.0")
	implementation("org.flywaydb:flyway-core:9.6.0")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	developmentOnly("org.springframework.boot:spring-boot-docker-compose")
	runtimeOnly("org.postgresql:postgresql")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	implementation("com.google.code.gson:gson")
	implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
	implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
	implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
	implementation("org.jetbrains.exposed:exposed-java-time:$exposedVersion")
	testImplementation("com.h2database:h2:2.1.214") // H2 for testing only (in-memory)
	testRuntimeOnly("com.h2database:h2:2.1.214") // Runtime usage for tests
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("io.mockk:mockk:1.12.0")
	testImplementation("org.springframework:spring-test")
	testImplementation("org.junit.jupiter:junit-jupiter-params:5.8.2")
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
