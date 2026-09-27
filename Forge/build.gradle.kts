import net.minecraftforge.jarjar.gradle.JarJar
import net.darkhax.curseforgegradle.TaskPublishCurseForge

plugins {
    id("project-setup")

    alias(libs.plugins.minotaur)
    alias(libs.plugins.curseforgegradle)
    alias(libs.plugins.forgegradle)
    alias(libs.plugins.forge.jarjar)
}

val modId = project.property("modId") as String

jarJar.register {
    archiveClassifier = null
}

minecraft {
    rootProject.file("common/src/main/resources/META-INF/accesstransformer.cfg").takeIf { it.exists() }?.let {
        accessTransformers.setFrom(it)
    }

    runs {
        configureEach {
            workingDir.convention(layout.projectDirectory.dir("runs/${name}"))
            systemProperty("forge.logging.console.level", "debug")
        }

        register("client") {
            args("--username", "Dev")
        }

        register("client2") {
            args("--username", "Dev2")
        }

        register("server")
    }
}

repositories {
    @Suppress("DEPRECATION")
    minecraft.mavenizer(this@repositories)
    maven(fg.forgeMaven)
    maven(fg.minecraftLibsMaven)
    mavenCentral()
}

dependencies {
    implementation(minecraft.dependency(libs.forge))
    compileOnly(project(":common"))

    compileOnly(libs.mixinextras.common)
    testCompileOnly(libs.mixinextras.common)
    runtimeOnly(libs.mixinextras.forge)
    implementation(libs.jopt.simple)
    implementation(libs.jspecify)

    annotationProcessor(libs.mixinextras.common)
    //annotationProcessor(libs.forge.eventbusvalidator)

    "jarJar"(libs.mixinextras.forge)

    // Mod Dependencies below
    //implementation(libs.geckolib.forge)

}

tasks.named<Jar>("jar") {
    archiveClassifier.set("slim")
}

// Must have your Modrinth API Key as an environment variable under 'MODRINTH_TOKEN'
modrinth {
    token = System.getenv("MODRINTH_TOKEN") ?: "Invalid/No API Token Found"
    uploadFile.set(tasks.named<JarJar>("jarJar"))
    projectId.set(project.property("modrinthProjectId") as String)
    versionName = "Forge ${libs.versions.minecraft.asProvider().get()}"
    loaders.set(listOf("forge"))
    versionNumber.set(project.version.toString())
    gameVersions.set(listOf(libs.versions.minecraft.asProvider().get()))

    if (rootProject.file("CHANGELOG.md").exists())
        changelog.set(rootProject.file("CHANGELOG.md").readText(Charsets.UTF_8))

    // Comment out below to enable publishing properly
    //debugMode = true
    // See below for other properties and info
    // https://github.com/modrinth/minotaur#available-properties
}

// Must have your CurseForge API Key as an environment variable under 'CURSEFORGE_TOKEN'
tasks.register<TaskPublishCurseForge>("publishToCurseForge") {
    group = "publishing"
    apiToken = System.getenv("CURSEFORGE_TOKEN") ?: "Invalid/No API Token Found"

    val mainFile = upload(project.property("curseforgeProjectId"), tasks.named<JarJar>("jarJar"))
    mainFile.displayName = "${project.property("modDisplayName")} Forge ${libs.versions.minecraft.asProvider().get()} ${project.version}"
    mainFile.releaseType = "release"
    mainFile.addModLoader("Forge")
    mainFile.addGameVersion(libs.versions.minecraft.asProvider().get())
    mainFile.addJavaVersion("Java ${libs.versions.java.get()}")
    mainFile.addEnvironment("Client", "Server")

    if (rootProject.file("CHANGELOG.md").exists()) {
        mainFile.changelog = rootProject.file("CHANGELOG.md").readText(Charsets.UTF_8)
        mainFile.changelogType = "markdown"
    }

    // Comment out below to enable publishing properly
    //debugMode = true
    // See below for other properties and info
    // https://github.com/Darkhax/CurseForgeGradle#available-properties
}

publishing {
    publishing {
        publications {
            create<MavenPublication>(modId) {
                from(components["jarJar"])
                artifactId = base.archivesName.get()
            }
        }
    }
}

tasks.named<DefaultTask>("publish") {
    finalizedBy("modrinth")
    finalizedBy("publishToCurseForge")
}

sourceSets.forEach {
    val dir = layout.buildDirectory.dir("sourcesSets/${it}.name")

    it.output.setResourcesDir(dir)
    it.java.destinationDirectory = dir
}