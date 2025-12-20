// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    // Khai báo plugin cho ứng dụng Android, 'apply false' để các module con tự quyết định.
    // Sử dụng phiên bản ổn định và phổ biến.
    id("com.android.application") version "8.2.2" apply false

    // Khai báo plugin KSP. Sử dụng phiên bản đi kèm với phiên bản Kotlin ổn định.
    id("com.google.devtools.ksp") version "1.9.22-1.0.17" apply false

    // Khai báo plugin Google Services cho Firebase
    id("com.google.gms.google-services") version "4.4.2" apply false

    // Thêm plugin Kotlin cho Android
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
}
