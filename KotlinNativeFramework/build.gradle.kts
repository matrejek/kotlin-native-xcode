import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    kotlin("multiplatform") version "1.3.61"
}

repositories {
    mavenCentral()
}

kotlin {
  ios {
    binaries {
      framework {
        baseName = "KotlinNativeFramework"
      }
    }
  }

  val configuration = (System.getenv("CONFIGURATION") ?: "Debug").capitalize()
  val platform = System.getenv("PLATFORM_NAME") ?: "iphoneos"
  val target = (if (platform == "iphoneos") targets["iosArm64"] else targets["iosX64"]) as KotlinNativeTarget
  val frameworkLinkTask = target.binaries.getFramework(configuration).linkTask
  val frameworkFile = frameworkLinkTask.outputFile.get()
  val frameworkOutputDir = File(System.getenv("CONFIGURATION_BUILD_DIR") ?: projectDir.path)

  val dsymTask by project.tasks.creating(Sync::class) {
      group = "build"
      dependsOn(frameworkLinkTask)
      val outputDSYM = File(frameworkFile.parent, frameworkFile.name + ".dSYM")
      from(outputDSYM)
      into(File(frameworkOutputDir, outputDSYM.name))
  }
  val buildForXcode by project.tasks.creating(Sync::class) {
      group = "build"
      dependsOn(dsymTask)
      from(frameworkLinkTask.outputFile)
      into(File(frameworkOutputDir, frameworkFile.name))
  }
}

tasks.withType<Wrapper> {
  gradleVersion = "5.3.1"
  distributionType = Wrapper.DistributionType.ALL
}
