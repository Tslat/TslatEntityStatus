import net.darkhax.curseforgegradle.Constants
import net.fabricmc.loom.task.RemapJarTask
import net.darkhax.curseforgegradle.TaskPublishCurseForge
import org.gradle.internal.extensions.stdlib.capitalized

plugins {
    alias(libs.plugins.loom)
    id("project-setup")
    alias(libs.plugins.minotaur)
    alias(libs.plugins.curseforgegradle)
}

val modId = project.property("modId") as String

dependencies {
    minecraft(libs.minecraft)
    mappings(loom.layered() {
        officialMojangMappings()
        parchment("org.parchmentmc.data:parchment-${libs.versions.parchment.minecraft.get()}:${libs.versions.parchment.asProvider().get()}@zip")
    })
    modImplementation(libs.fabric)
    modImplementation(libs.fabric.api)
    implementation(libs.jspecify)
    compileOnly(project(":common"))

    // Mod Dependencies below
    modImplementation(libs.midnightlib)
    modImplementation(libs.modmenu)

}

loom {
    file("src/main/resources/$modId.classtweaker").takeIf { it.exists() }?.let(accessWidenerPath::set)

    runs {
        configureEach {
            displayName.set("Fabric ${name.capitalized()}")
            runDirectory.set(project.file("runs/$name"))
            generateRunConfig.set(true)
        }

        named("client") {
            client()
            programArguments.addAll("--username", "Dev")
        }

        named("server") {
            server()
        }
    }
}

tasks.withType<ProcessResources>().configureEach {
    exclude("**/accesstransformer.cfg")
}

//<editor-fold defaultstate="collapsed" desc="<Publishing>">
// Must have your Modrinth API Key as an environment variable under 'MODRINTH_TOKEN'
modrinth {
    token = System.getenv("MODRINTH_TOKEN") ?: "Invalid/No API Token Found"
    uploadFile.set(tasks.named<RemapJarTask>("remapJar"))
    projectId.set(project.property("modrinthProjectId") as String)
    versionName = "Fabric ${libs.versions.minecraft.asProvider().get()}"
    versionType = "release"
    loaders.set(listOf("fabric"))
    versionNumber.set(project.version.toString())
    gameVersions.set(listOf(libs.versions.minecraft.asProvider().get()))
    dependencies {
        required.project("fabric-api")
    }

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

    val mainFile = upload(project.property("curseforgeProjectId"), tasks.remapJar)
    mainFile.displayName = "${project.property("modDisplayName")} Fabric ${libs.versions.minecraft.asProvider().get()} ${project.version}"
    mainFile.releaseType = "release"
    mainFile.addModLoader("Fabric")
    mainFile.addGameVersion(libs.versions.minecraft.asProvider().get())
    mainFile.addJavaVersion("Java ${libs.versions.java.get()}")
    mainFile.addRelation("fabric-api", Constants.RELATION_REQUIRED)
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
    publications {
        create<MavenPublication>(modId) {
            from(components["java"])
            artifactId = base.archivesName.get()
        }
    }
}

tasks.named<DefaultTask>("publish") {
    finalizedBy("modrinth")
    finalizedBy("publishToCurseForge")
}
//</editor-fold>