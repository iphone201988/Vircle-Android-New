plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt)
    id("kotlin-parcelize")
    alias(libs.plugins.kotlin1kept)
    alias(libs.plugins.google.services)
}

android {
    namespace = "com.tech.vircle"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.tech.vircle"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        buildConfig = true
        viewBinding = true
        dataBinding = true
    }

    buildTypes {
        debug {
            buildConfigField("String", "API_KEY", "\"bcf11eff790149b4a011928607eeed26\"")
            buildConfigField(
                "String",
                "default_web_client_id",
                "\"AIzaSyC6Zx7EPbi9uexCfjt_5LdqI4PHgH4PrCA.apps.googleusercontent.com\""
            )
            buildConfigField("String", "BASE_URL", "\"https://52.200.106.168:8000/api/v1/\"")
            //buildConfigField("String", "BASE_URL", "\"http://192.168.1.5:8000/api/v1/\"")
            buildConfigField("String", "SOCKET_URL", "\"https://52.200.106.168:8000\"")
            buildConfigField("String", "MEDIA_BASE_URL", "\"https://vircle.s3.us-east-1.amazonaws.com/\"")
        }
        release {
            buildConfigField("String", "API_KEY", "\"bcf11eff790149b4a011928607eeed26\"")
            buildConfigField(
                "String",
                "default_web_client_id",
                "\"AIzaSyC6Zx7EPbi9uexCfjt_5LdqI4PHgH4PrCA.apps.googleusercontent.com\""
            )

            buildConfigField("String", "BASE_URL", "\"https://52.200.106.168:8000/api/v1/\"")
            buildConfigField("String", "SOCKET_URL", "\"https://52.200.106.168:8000\"")
            buildConfigField("String", "MEDIA_BASE_URL", "\"https://52.200.106.168:8000/\"")
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx.v277)
    implementation(libs.androidx.navigation.ui.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.androidx.databinding.runtime)
    implementation(libs.dagger)
    implementation(libs.gson)
    implementation(libs.retrofit)
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)
    implementation(libs.play.services.location)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.glide)
    implementation(libs.sdp.android)
    implementation(libs.ssp.android)
    implementation(libs.gson)
    kapt(libs.dagger.compiler)
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    implementation(libs.lottie)
    implementation(libs.converter.gson)
    //  image picker
    implementation(libs.imagepicker)
    // dot indicator
    implementation(libs.viewpagerindicator)

    // SwipeItem
    implementation ("com.github.zerobranch:SwipeLayout:1.3.1")

    //firebase
    implementation(platform("com.google.firebase:firebase-bom:34.0.0"))
    implementation("com.google.android.gms:play-services-auth:21.3.0")
    implementation("com.google.firebase:firebase-messaging")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-messaging-ktx:24.0.1")

    //socket dependency
    implementation("io.socket:socket.io-client:2.0.1")

    // refresh layout
    implementation ("com.airbnb.android:lottie:5.2.0")
    implementation ("com.github.nabil6391:LottieSwipeRefreshLayout:1.0.0")

}