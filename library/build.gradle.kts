import java.util.*

plugins {
    id("com.android.library")
    // id("org.jetbrains.kotlin.android")
    id("maven-publish")
    id("signing")
}

android {
    compileSdk = 33
    namespace = "io.github.chr56.observablescrollview.retro"

    defaultConfig {
        minSdk = 21
        targetSdk = 33

        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    //kotlinOptions {
    //    jvmTarget = "1.8"
    //}

    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
}

tasks.create("sourceJar", Jar::class.java) {
    from(android.sourceSets.getByName("main").java.srcDirs)
    archiveClassifier.set("sources")
}

dependencies {
    implementation("androidx.annotation:annotation:1.3.0")
    compileOnly("androidx.appcompat:appcompat:1.5.1")
    compileOnly("androidx.recyclerview:recyclerview:1.2.1")
    compileOnly("androidx.fragment:fragment:1.4.1")
    compileOnly("androidx.core:core:1.7.0")
}

val libVersion = "0.0.1"

val secretPropsFile = rootProject.file("secrets.properties")
var secrets = Properties()
if (secretPropsFile.exists()) {
    secretPropsFile.inputStream().use {
        secrets.load(it)
    }
}

publishing {
    publications {
        create("release", MavenPublication::class.java) {

            groupId = "io.github.chr56"
            artifactId = "observable-scrollview-retro"
            version = libVersion
            afterEvaluate {
                from(components["release"])
            }

            pom {
                name.set("Android Observable Scrollview Retro")
                description.set("Android library to observe scroll events on scrollable views.")
                url.set("")

                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("chr_56")
                        name.set("chr_56")
                        timezone.set("UTC+8")
                    }
                }
                scm {
                    connection.set("")
                    developerConnection.set("")
                    url.set("")
                }
            }
        }
    }
    repositories {
        maven("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2") {
            name = "MavenCentral"
            if (secretPropsFile.exists()) {
                credentials {
                    username = secrets["sonatype_username"] as String
                    password = secrets["sonatype_password"] as String
                }
            }
        }
    }
}
if (secretPropsFile.exists()) {
    signing {
        sign(publishing.publications)
        val key = File(secrets["signing_file"] as String).readText()
        useInMemoryPgpKeys(
            secrets["signing_key"] as String,
            key,
            secrets["signing_password"] as String
        )
    }
}
