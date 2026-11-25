plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

android {

    // Identificador de EcoRecicla Mobile.
    namespace = "com.example.ecorecicla.mobile"

    compileSdk {
        version = release(37)
    }

    defaultConfig {

        applicationId = "com.example.ecorecicla.mobile"

        // Compatibilidad con Android 8.0 en adelante.
        minSdk = 26
        targetSdk = 37

        // Versión inicial del aplicativo.
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner =
            "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = true
                packageScope = setOf(
                    "androidx.**",
                    "kotlin.**",
                    "kotlinx.**"
                )
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    // Habilitar Jetpack Compose.
    buildFeatures {
        compose = true
    }
}

dependencies {

    // Componentes principales de Android.
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // Jetpack Compose y Material Design 3.
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)

    // Iconos para las interfaces.
    implementation(libs.androidx.material.icons.extended)

    // Navegación entre pantallas.
    implementation(libs.androidx.navigation.compose)

    // Gestión de estados y ciclo de vida.
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)

    // Comunicación con el backend Flask.
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.okhttp.logging)

    // Persistencia local.
    implementation(libs.androidx.datastore.preferences)

    // Carga de imágenes de recompensas.
    implementation(libs.coil.compose)

    // Operaciones asíncronas.
    implementation(libs.kotlinx.coroutines.android)

    // Pruebas unitarias.
    testImplementation(libs.junit)

    // Pruebas instrumentadas.
    androidTestImplementation(
        platform(libs.androidx.compose.bom)
    )

    androidTestImplementation(
        libs.androidx.compose.ui.test.junit4
    )

    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)

    // Herramientas de desarrollo.
    debugImplementation(
        libs.androidx.compose.ui.test.manifest
    )

    debugImplementation(
        libs.androidx.compose.ui.tooling
    )
}