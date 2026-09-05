plugins { id("com.android.application") }

android {
    namespace = "com.jackson.queenslandsnake"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.jackson.queenslandsnake"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
}
