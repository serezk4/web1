plugins {
    id("java")
    id("checkstyle")
    id("war")
}

group = "com.serezk4"
version = "2281337itmoflex"


repositories {
    mavenCentral()
}


dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    implementation("org.projectlombok:lombok:1.18.34")
    compileOnly("org.projectlombok:lombok")
}

tasks.test {
    useJUnitPlatform()
}

tasks.jar {
    manifest {
        attributes(
            "Main-Class" to "com.serezk4.server.worker.TestWorker"
        )
    }
}

tasks.register<Jar>("deploy.sh") {
    archiveClassifier.set("all")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    manifest {
        attributes(
            "Main-Class" to "com.serezk4.server.worker.TestWorker"
        )
    }
    from(sourceSets.main.get().output)

    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })

    doLast {
        val targetDir = file("helios-root/webapp/fcgi-bin")
        if (!targetDir.exists()) {
            targetDir.mkdirs()
        }
        val targetFile = targetDir.resolve("server.jar")
        archiveFile.get().asFile.copyTo(targetFile, overwrite = true)
    }
}

tasks.check {
    dependsOn("checkstyleMain")
    dependsOn("checkstyleTest")
}