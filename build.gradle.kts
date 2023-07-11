import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.konan.target.KonanTarget

plugins {
    kotlin("multiplatform") version "1.9.0"
    id("maven-publish")
}

group = "org.libsdl"
version = "2.28.1"

repositories {
    mavenCentral()
}

val cmakeDirectory = project.buildDir.resolve("cmake")

kotlin {
    mingwX64()
    linuxX64()

    val requiredSubsystems = listOf(
        "SDL_ASSEMBLY",
        "SDL_ATOMIC",
        "SDL_AUDIO",
        "SDL_CCACHE",
        "SDL_EVENTS",
        "SDL_FILE",
        "SDL_FILESYSTEM",
        "SDL_HIDAPI",
        "SDL_HIDAPI_JOYSTICK",
        "SDL_JOYSTICK",
        "SDL_LIBUDEV",
        "SDL_LOADSO",
        "SDL_LOCALE",
        "SDL_MISC",
        "SDL_MMX",
        "SDL_OFFSCREEN",
        "SDL_POWER",
        "SDL_RENDER",
        "SDL_SENSOR",
        "SDL_SSE",
        "SDL_SSE2",
        "SDL_SSE3",
        "SDL_SSEMATH",
        "SDL_STATIC",
        "SDL_THREADS",
        "SDL_TIMERS",
        "SDL_VIDEO",
    )

    val disabledSdlSubsystems = listOf(
        "SDL_3DNOW",
        "SDL_ALTIVEC",
        "SDL_ARTS",
        "SDL_ASAN",
        "SDL_DIRECTFB",
        "SDL_DIRECTX",
        "SDL_DUMMYAUDIO",
        "SDL_DUMMYVIDEO",
        "SDL_ESD",
        "SDL_FUSIONSOUND",
        "SDL_HAPTIC",
        "SDL_SYSTEM_ICONV",
        "SDL_INSTALL_TESTS",
        "SDL_OPENGL",
        "SDL_OPENGLES",
        "SDL_RENDER_D3D",
        "SDL_SHARED",
        "SDL_SNDIO",
        "SDL_STATIC_PIC",
        "SDL_TEST",
        "SDL_TESTS",
        "SDL_VIRTUAL_JOYSTICK",
        "SDL_VIVANTE",
        "SDL_VULKAN",
        "SDL_X11"
    )

    targets.withType<KotlinNativeTarget> {
        val main by compilations.getting {
            tasks {
                val cmakeTask = register<Exec>("cmake${project.name.titlecaseFirstChar()}${konanTarget.name.titlecaseFirstChar()}") {
                    description = "Generate build file for ${konanTarget.name} target."

                    val setSubsystems = requiredSubsystems.map { listOf("-D", "${it}=1") }.flatten()
                    val disabledSubsystems = disabledSdlSubsystems.map { listOf("-D", "${it}=0") }.flatten()
                    val platformSpecificSystems = platformSpecificSubsystems(konanTarget).map { listOf("-D", "${it}=1") }.flatten()
                    commandLine = listOf("cmake", "-S", ".", "-B", cmakeDirectory.toString(), "-G", getCmakeGenerator(konanTarget)) + setSubsystems + disabledSubsystems + platformSpecificSystems
                }

                val makeTask = register<Exec>("make${project.name.titlecaseFirstChar()}${konanTarget.name.titlecaseFirstChar()}") {
                    dependsOn(cmakeTask)
                    workingDir = cmakeDirectory
                    description = "Generate build file for ${konanTarget.name} target."
                    commandLine = if (konanTarget == KonanTarget.MINGW_X64) {
                        listOf("mingw32-make")
                    } else {
                        listOf("make")
                    }
                }
                getByName(compileKotlinTaskName).dependsOn(makeTask)
            }

            cinterops {
                create("sdl") {
                    includeDirs("${rootDir}/include")
                }
            }

            kotlinOptions {
                freeCompilerArgs = listOf("-include-binary", cmakeDirectory.resolve("libSDL2.a").toString())
            }
        }
    }

    sourceSets {
        val commonMain by getting
        val nativeMain by creating {
            dependsOn(commonMain)
        }
        val mingwX64Main by getting {
            dependsOn(nativeMain)
        }
        val linuxX64Main by getting {
            dependsOn(nativeMain)
        }
    }
}

publishing {
    repositories {
        maven {
            name = "wyatt-repo"
            url = uri("http://192.168.1.16:8081/repository/maven-releases")
            isAllowInsecureProtocol = true

            credentials {
                val wyattRepoUsername : String by project
                val wyattRepoPassword : String by project
                username = wyattRepoUsername
                password = wyattRepoPassword
            }
        }
    }
}

fun platformSpecificSubsystems(konanTarget: KonanTarget) : List<String> = when (konanTarget) {
    KonanTarget.LINUX_X64 -> listOf("SDL_ALSA", "SDL_WAYLAND")
    KonanTarget.MINGW_X64 -> listOf("SDL_XINPUT", "SDL_WASAPI")
    else -> throw RuntimeException("Unsupported konan target")
}

fun String.titlecaseFirstChar() = replaceFirstChar(Char::titlecase)

fun getCmakeGenerator(target : KonanTarget) : String = when (target) {
    KonanTarget.MINGW_X64 -> "MinGW Makefiles"
    KonanTarget.LINUX_X64 -> "Unix Makefiles"
    else -> throw RuntimeException("Cannot determine generate for current target")
}