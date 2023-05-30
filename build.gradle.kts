import org.jetbrains.compose.jetbrainsCompose
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpack

object Versions {
    const val koin = "3.2.0"
    const val ktor = "2.2.4"
    const val logback = "1.2.11"
}


plugins {
    kotlin("multiplatform") version "1.8.10"
    kotlin("plugin.serialization") version "1.8.10"
    id("org.jetbrains.compose") version "1.3.1"
    id("io.ktor.plugin") version "2.2.4"
    application //to run JVM part
}

group = "io.github.jsixface"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    jetbrainsCompose()
}

kotlin {
    jvm {
        withJava()
    }
    js(IR) {
        browser {
            binaries.executable()
            useCommonJs()
            commonWebpackConfig {
                scssSupport {
                    enabled.set(true)
                }
            }
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("io.ktor:ktor-serialization-kotlinx-json:${Versions.ktor}")
                implementation("io.ktor:ktor-client-core:${Versions.ktor}")
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")
                api("io.ktor:ktor-resources:${Versions.ktor}")
                api("io.ktor:ktor-client-resources:${Versions.ktor}")
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation("ch.qos.logback:logback-classic:${Versions.logback}")

                implementation("io.insert-koin:koin-core:${Versions.koin}")
                implementation("io.insert-koin:koin-ktor:${Versions.koin}")
                implementation("io.insert-koin:koin-logger-slf4j:${Versions.koin}")

                implementation("io.ktor:ktor-serialization-kotlinx-json:${Versions.ktor}")
                implementation("io.ktor:ktor-server-cio:${Versions.ktor}")
                implementation("io.ktor:ktor-server-config-yaml:${Versions.ktor}")
                implementation("io.ktor:ktor-server-compression:${Versions.ktor}")
                implementation("io.ktor:ktor-server-content-negotiation:${Versions.ktor}")
                implementation("io.ktor:ktor-server-core-jvm:${Versions.ktor}")
                implementation("io.ktor:ktor-server-cors:${Versions.ktor}")
                implementation("io.ktor:ktor-server-netty:${Versions.ktor}")
                implementation("io.ktor:ktor-server-resources:${Versions.ktor}")
                implementation("io.ktor:ktor-server-websockets:${Versions.ktor}")
            }
        }

        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation("junit:junit:4.13")
            }
        }

        val jsMain by getting {
            dependencies {
                implementation(compose.web.core)
                implementation(compose.runtime)
                implementation("io.ktor:ktor-client-js:${Versions.ktor}")
                implementation("io.ktor:ktor-client-content-negotiation:${Versions.ktor}")
                implementation("io.ktor:ktor-serialization-kotlinx-json:${Versions.ktor}")

                implementation("app.softwork:bootstrap-compose:0.1.14")
                implementation("app.softwork:routing-compose:0.2.11")

            }
        }
    }
}

application {
    mainClass.set("io.ktor.server.cio.EngineMain")
//    mainClass.set("io.github.jsixface.ApplicationKt")
    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

// include JS artifacts in any JAR we generate
tasks.getByName<Jar>("jvmJar") {
    val taskName = if (project.hasProperty("isProduction")
            || project.gradle.startParameter.taskNames.contains("installDist")
    ) {
        "jsBrowserProductionWebpack"
    } else {
        "jsBrowserDevelopmentWebpack"
    }
    val webpackTask = tasks.getByName<KotlinWebpack>(taskName)
    dependsOn(webpackTask) // make sure JS gets compiled first
    from(
            File(
                    webpackTask.destinationDirectory,
                    webpackTask.outputFileName
            )
    ) // bring output file along into the JAR
}

tasks {
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            jvmTarget = "1.8"
        }
    }
}

distributions {
    main {
        contents {
            from("$buildDir/libs") {
                rename("${rootProject.name}-jvm", rootProject.name)
                into("lib")
            }
        }
    }
}

// Alias "installDist" as "stage" (for cloud providers)
tasks.create("stage") {
    dependsOn(tasks.getByName("installDist"))
}

tasks.getByName<JavaExec>("run") {
    classpath(tasks.getByName<Jar>("jvmJar")) // so that the JS artifacts generated by `jvmJar` can be found and served
}
