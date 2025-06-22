plugins {
    id("java")
}

group = "com.nullXer0"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.typesafe:config:1.4.3")
    implementation("com.zaxxer:HikariCP:6.3.0")
    implementation("org.quartz-scheduler:quartz:2.5.0")
    implementation("net.dv8tion:JDA:5.6.1")
    {
        exclude(module = "opus-java")
        exclude(module = "tink")
    }

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}