pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
        maven {
            url = uri("https://android-sdk.is.com/")
        }
        maven {
            url =
                uri("https://dl-maven-android.mintegral.com/repository/mbridge_android_sdk_oversea")
        }
        maven {
            url = uri("https://artifact.bytedance.com/repository/pangle/")
        }
        maven {
            url = uri("https://maven.pkg.github.com/ngoxuanhungbk/ls-leansoft-publishing-sdk")
            credentials {
                username = "ngoxuanhungbk"
                password = "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"
            }
        }
    }
}

rootProject.name = "Offline Music Player"
include(":app")