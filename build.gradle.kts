plugins {
    java
    id("com.gradleup.shadow") version "8.3.6"
    id("org.jooq.jooq-codegen-gradle") version "3.20.6"
}

group = "com.nullXer0"
version = "1.4.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation("ch.qos.logback:logback-classic:1.5.18")
    implementation("com.typesafe:config:1.4.3")
    implementation("org.postgresql:postgresql:42.7.7")
    implementation("com.zaxxer:HikariCP:6.3.0")
    implementation("org.jooq:jooq:3.20.6")
    implementation("org.quartz-scheduler:quartz:2.5.0")
    implementation("net.dv8tion:JDA:5.6.1")
    {
        exclude(module = "opus-java")
        exclude(module = "tink")
    }
    jooqCodegen("org.postgresql:postgresql:42.7.7")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

jooq.configuration {
    jdbc {
        url = project.property("dbee.url").toString()
        user = project.property("dbee.user").toString()
        password = project.property("dbee.pass").toString()
    }

    generator {
        database {
            inputSchema = "public"
            target {
                packageName = "com.nullXer0.dbee"
                directory = "src/main/java"
            }
        }
    }
}


tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "com.nullXer0.beebot.BeeBot"
    }
}

tasks.shadowJar {
    archiveFileName.set("BeeBot.jar")
}

tasks.named("build") {
    dependsOn(tasks.named("shadowJar"))
}
tasks.test {
    useJUnitPlatform()
}