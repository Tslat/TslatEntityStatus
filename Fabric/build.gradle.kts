import net.darkhax.curseforgegradle.Constants
import net.fabricmc.loom.task.RemapJarTask
import net.darkhax.curseforgegradle.TaskPublishCurseForge

plugins {
    id("tes-convention")

    alias(libs.plugins.minotaur)
    alias(libs.plugins.loom)
}

val modId              : String by project
val modDisplayName     : String by project
val modModrinthId      : String by project
val modCurseforgeId    : String by project
val modChangelogUrl    : String by project
val modVersion         = libs.versions.tes.get()
val javaVersion        = libs.versions.java.get()
val mcVersion          = libs.versions.minecraft.asProvider().get()
val parchmentMcVersion = libs.versions.parchment.minecraft.get()
val parchmentVersion   = libs.versions.parchment.asProvider().get()

version = modVersion

base {
    archivesName = "${modDisplayName}-fabric-${mcVersion}"
}

repositories {
    exclusiveContent {
        forRepository {
            maven {
                name = "ParchmentMC"
                url = uri("https://maven.parchmentmc.org")
            }
        }
        filter {
            includeGroupAndSubgroups("org.parchmentmc")
        }
    }
}

dependencies {
    minecraft(libs.minecraft)
    mappings(loom.layered() {
        officialMojangMappings()
        parchment("org.parchmentmc.data:parchment-${parchmentMcVersion}:${parchmentVersion}@zip")
    })
    modImplementation(libs.fabric)
    modImplementation(libs.fabric.api)
    modImplementation(libs.forgeconfigapiport.fabric)
    compileOnly(project(":common"))
}

loom {
    accessWidenerPath = file("src/main/resources/${modId}.accesswidener")

    mixin.defaultRefmapName.set("${modId}.refmap.json")

    runs {
        named("client") {
            configName = "Fabric Client"

            client()
            ideConfigGenerated(true)
            runDir("runs/" + name)
            programArg("--username=Dev")
        }

        named("server") {
            configName = "Fabric Server"

            server()
            ideConfigGenerated(true)
            runDir("runs/" + name)
        }
    }
}

tasks.withType<JavaCompile>().configureEach {
    source(project(":common").sourceSets.getByName("main").allSource)
}

tasks.named<Jar>("sourcesJar").configure {
    from(project(":common").sourceSets.getByName("main").allSource)
}

tasks.withType<Javadoc>().configureEach {
    source(project(":common").sourceSets.getByName("main").allJava)
}

tasks.withType<ProcessResources>().configureEach {
    from(project(":common").sourceSets.getByName("main").resources)
    exclude("**/accesstransformer-nf.cfg")
}

modrinth {
    token = System.getenv("modrinthKey") ?: "Invalid/No API Token Found"
    projectId = modModrinthId
    versionNumber.set(modVersion)
    versionName = "Fabric ${mcVersion}"
    uploadFile.set(tasks.named<RemapJarTask>("remapJar"))
    changelog.set(modChangelogUrl)
    gameVersions.set(listOf(mcVersion))
    versionType = "release"
    loaders.set(listOf("fabric"))
    dependencies {
        required.project("fabric-api")
        required.project("forge-config-api-port")
    }

    //debugMode = true
    //https://github.com/modrinth/minotaur#available-properties
}

tasks.register<TaskPublishCurseForge>("publishToCurseForge") {
    group = "publishing"
    apiToken = System.getenv("curseforge.apitoken") ?: "Invalid/No API Token Found"

    val mainFile = upload(modCurseforgeId, tasks.remapJar)
    mainFile.displayName = "${modDisplayName} Fabric ${mcVersion} ${version}"
    mainFile.releaseType = "release"
    mainFile.addModLoader("Fabric")
    mainFile.addGameVersion(mcVersion)
    mainFile.addJavaVersion("Java ${javaVersion}")
    mainFile.changelog = modChangelogUrl
    mainFile.addRelation("forge-config-api-port-fabric", Constants.RELATION_REQUIRED)

    //debugMode = true
    //https://github.com/Darkhax/CurseForgeGradle#available-properties
}

publishing {
    publications {
        create<MavenPublication>(modId) {
            from(components["java"])
            artifactId = base.archivesName.get()
        }
    }
}

tasks.named<DefaultTask>("publish").configure {
    finalizedBy("modrinth")
    finalizedBy("publishToCurseForge")
}