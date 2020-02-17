import net.fabricmc.loom.task.RemapJarTask
import org.ajoberstar.grgit.Grgit

plugins {
    kotlin("jvm").version("1.3.60")
    id("fabric-loom").version("0.2.6-SNAPSHOT")
    id("org.ajoberstar.grgit").version("3.1.1")
}
apply(plugin = "org.ajoberstar.grgit")

repositories {
    maven("http://maven.fabricmc.net/")
    maven("https://minecraft.curseforge.com/api/maven")
    maven("http://maven.sargunv.s3-website-us-west-2.amazonaws.com/")
    maven("http://maven.modmuss50.me/")
    maven("https://grondag-repo.appspot.com").credentials { username = "guest"; password = "" }
    maven("https://jitpack.io")
}
val gitHash = (project.ext.get("grgit") as Grgit).head().abbreviatedId

val semVer = "${project.version}+$gitHash"

val jarName = "BetterFoliage-$semVer-Fabric-${properties["mcVersion"]}"
print("VERSION: $jarName")

dependencies {
    "minecraft"("com.mojang:minecraft:${properties["mcVersion"]}")
    "mappings"("net.fabricmc:yarn:${properties["yarnMappings"]}:v2")

    "modImplementation"("net.fabricmc:fabric-loader:${properties["loaderVersion"]}")
    "modImplementation"("net.fabricmc.fabric-api:fabric-api:${properties["fabricVersion"]}")
    "modImplementation"("net.fabricmc:fabric-language-kotlin:${properties["fabricKotlinVersion"]}")

    "modImplementation"("io.github.prospector:modmenu:${properties["modMenuVersion"]}")
    listOf("modImplementation", "include").forEach { configuration ->
        configuration("me.shedaniel.cloth:config-2:${properties["clothConfigVersion"]}")
        configuration("me.zeroeightsix:fiber:${properties["fiberVersion"]}")
    }

    // Canvas Renderer
//    "modImplementation"("grondag:canvas:0.7.+")

    // Optifabric
    "modImplementation"("com.github.modmuss50:OptiFabric:df03dc2c22")
    "implementation"("org.zeroturnaround:zt-zip:1.13")
}

sourceSets {
    get("main").ext["refMap"] = "betterfoliage.refmap.json"
    get("main").resources.srcDir("src/forge/resources")
    get("main").java.srcDir("src/forge/java")
}
kotlin.sourceSets {
    get("main").kotlin.srcDir("src/forge/kotlin")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

kotlin {
    target.platformType
    target.compilations.configureEach {
        kotlinOptions.jvmTarget = "1.8"
        kotlinOptions.freeCompilerArgs += listOf("-Xno-param-assertions", "-Xno-call-assertions")
    }
}

tasks.getByName<ProcessResources>("processResources") {
    filesMatching("fabric.mod.json") { expand(mutableMapOf("version" to semVer)) }
}

tasks.getByName<RemapJarTask>("remapJar") {
    archiveName = "$jarName.jar"
    exclude("net")
}