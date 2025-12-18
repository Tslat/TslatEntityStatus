import net.darkhax.curseforgegradle.Constants
import net.darkhax.curseforgegradle.TaskPublishCurseForge

plugins {
    id("tes-convention")

    alias(libs.plugins.minotaur)
    alias(libs.plugins.curseforgegradle)
    alias(libs.plugins.forgegradle)
    alias(libs.plugins.forge.at)
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
val forgeVersion= libs.versions.forge.asProvider().get()

version = modVersion

base {
    archivesName = "${modDisplayName}-forge-${mcVersion}"
}

minecraft {
    mappings("parchment", "${parchmentMcVersion}-${parchmentVersion}")

    runs {
        configureEach {
            workingDir.convention(layout.projectDirectory.dir("runs/${name}"))
            systemProperty("forge.logging.console.level", "debug")
        }

        register("client") {
            args("--username", "Dev")
            args("-mixin.config=${modId}.mixins.json")
        }

        register("client2") {
            args("--username", "Dev2")
            args("-mixin.config=${modId}.mixins.json")
        }

        register("server") {
            args("-mixin.config=${modId}.mixins.json")
        }
    }
}

repositories {
    maven(minecraft.mavenizer)
    maven(fg.forgeMaven)
    maven(fg.minecraftLibsMaven)
    exclusiveContent {
        forRepository {
            maven {
                name = "Sponge"
                url = uri("https://repo.spongepowered.org/repository/maven-public")
            }
        }
        filter {
            includeGroupAndSubgroups("org.spongepowered")
        }
    }
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation(minecraft.dependency("net.minecraftforge:forge:1.21.11-61.0.2"))
    compileOnly(project(":common")) {
        accessTransformers.configure(this) {
            config.set(rootProject.file("common/src/main/resources/META-INF/accesstransformer.cfg"))
        }
    }
    annotationProcessor(libs.forge.eventbusvalidator)
    implementation(libs.forgeconfigapiport.forge)
}

tasks.withType<Test>().configureEach {
    enabled = false;
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
}

sourceSets.forEach {
    val dir = layout.buildDirectory.dir("sourcesSets/${it}.name")

    it.output.setResourcesDir(dir)
    it.java.destinationDirectory = dir
}

modrinth {
    token = System.getenv("modrinthKey") ?: "Invalid/No API Token Found"
    projectId = modModrinthId
    versionNumber.set(project.version.toString())
    versionName = "Forge ${mcVersion}"
    uploadFile.set(tasks.named<Jar>("jar"))
    changelog.set(modChangelogUrl)
    gameVersions.set(listOf(mcVersion))
    versionType = "release"
    loaders.set(listOf("forge"))

    //https://github.com/modrinth/minotaur#available-properties
}

tasks.register<TaskPublishCurseForge>("publishToCurseForge") {
    group = "publishing"
    apiToken = System.getenv("curseforge.apitoken") ?: "Invalid/No API Token Found"

    val mainFile = upload(modCurseforgeId, tasks.jar)
    mainFile.displayName = "${modDisplayName} Forge ${mcVersion} ${version}"
    mainFile.releaseType = "release"
    mainFile.addModLoader("Forge")
    mainFile.addGameVersion(mcVersion)
    mainFile.addJavaVersion("Java ${javaVersion}")
    mainFile.changelog = modChangelogUrl
    mainFile.addRelation("forge-config-api-port-fabric", Constants.RELATION_REQUIRED)

    //https://github.com/Darkhax/CurseForgeGradle#available-properties
}

publishing {
    publishing {
        publications {
            create<MavenPublication>("tes") {
                from(components["java"])
                artifactId = base.archivesName.get()
            }
        }
    }
}

tasks.named<DefaultTask>("publish").configure {
    finalizedBy("modrinth")
    finalizedBy("publishToCurseForge")
}

sourceSets.forEach {
    val dir = layout.buildDirectory.dir("sourcesSets/${it}.name")

    it.output.setResourcesDir(dir)
    it.java.destinationDirectory = dir
}
