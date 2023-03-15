# Mobillium Android Case

## Table of Contents
* **[Basic Info](#basic-info)**
* **[3rd Party Libraries](#3rd-party-libraries)**
* **[Preview Videos](#preview)**
* **[Notes](#notes)**

## Basic Info

* Language: Kotlin
* Software Architecture: MVVM
* Dependency Injection: Dagger Hilt

## 3rd Party Libraries:

* Retrofit
* OkHttp
* Dagger Hilt
* Glide
* Coroutines
* Jetpack Navigation
* Leak Canary (Memory Leak Detection)
* Shimmer
* Lottie

## Preview

### UI Preview

https://user-images.githubusercontent.com/25686023/225448904-26f0e5f9-87a4-437e-afb2-37868746eac8.mp4

### Error Handling

https://user-images.githubusercontent.com/25686023/225452694-4e94df3a-719b-48d6-87e1-55452585c51d.mp4

## Notes

- `utils/Constants.kt` file is hidden. It'll be provided externally.
- To test process death you can use this function,

    ```shell
    adb shell am kill com.example.mobilliumcase
    ```