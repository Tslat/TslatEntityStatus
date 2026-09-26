import net.darkhax.curseforgegradle.TaskPublishCurseForge
import net.neoforged.moddevgradle.dsl.RunModel
import org.slf4j.event.Level

plugins {
    id("project-setup")

    alias(libs.plugins.minotaur)
    alias(libs.plugins.curseforgegradle)
    alias(libs.plugins.moddevgradle)
}

val modId = project.property("modId") as String

legacyForge {
    setVersion(libs.versions.moddevgradle.forge.get())

    parchment.minecraftVersion.set(libs.versions.parchment.minecraft.get())
    parchment.mappingsVersion.set(libs.versions.parchment.asProvider().get())

    rootProject.file("common/src/main/resources/META-INF/accesstransformer.cfg").takeIf { it.exists() }?.let {
        accessTransformers.from(it)
    }

    runs {
        val modModel = mods.create(modId)

        modModel.sourceSet(project.sourceSets.getByName("main"))

        configureEach {
            logLevel = Level.DEBUG
        }

        runConfig(this, "client") {
            client()
            programArguments.addAll("--username", "Dev")
        }

        runConfig(this, "client2") {
            client()
            programArguments.addAll("--username", "Dev2")
        }

        runConfig(this, "server") {
            server()
            programArgument("--nogui")
        }
    }
}

dependencies {
    compileOnly(project(":common"))
    implementation(libs.jspecify)
    implementation(libs.jopt.simple)

    annotationProcessor("org.spongepowered:mixin:0.8.7:processor")
    annotationProcessor(libs.mixinextras.common)
    modCompileOnlyApi(libs.mixinextras.common)
    modApi(libs.mixinextras.forge)
    modApi("org.jetbrains:annotations:24.0.0")

    jarJar(libs.mixinextras.forge)

    // Mod Dependencies below
    //modImplementation(libs.geckolib.forge)

}

tasks.jar {
    finalizedBy("reobfJar")
}

tasks.publish {
    dependsOn("reobfJar")
}

tasks.processResources {
    exclude("**/*.accesswidener")
}

mixin {
    add(project.sourceSets.getByName("main"), "$modId.refmap.json")
    config("$modId.mixins.json")
}

// Must have your Modrinth API Key as an environment variable under 'MODRINTH_TOKEN'
modrinth {
    token = System.getenv("MODRINTH_TOKEN") ?: "Invalid/No API Token Found"
    uploadFile.set(tasks.named("reobfJar"))
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

    val mainFile = upload(project.property("curseforgeProjectId"), tasks.named("reobfJar"))
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
                artifactId = base.archivesName.get()
               artifact(tasks.named("reobfJar"))
               artifact(tasks.named("sourcesJar"))
            }
        }
    }
}

tasks.named<DefaultTask>("publish") {
    finalizedBy("modrinth")
    finalizedBy("publishToCurseForge")
}

sourceSets.forEach {
    val dir = layout.buildDirectory.dir("sourcesSets/${it.name}")

    it.output.setResourcesDir(dir)
    it.java.destinationDirectory = dir
}

/**
 * Not explicitly needed; but due to Gradle's failure to provide kotlin-dsl reified types for [NamedDomainObjectContainer], you'll get a bunch of IDE errors without it
 */
private fun runConfig(container: NamedDomainObjectContainer<RunModel>, name: String, configuration: Action<RunModel>) {
    configuration.execute(container.create(name));
}