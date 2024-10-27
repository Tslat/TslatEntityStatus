import net.darkhax.curseforgegradle.Constants
import net.darkhax.curseforgegradle.TaskPublishCurseForge
import net.minecraftforge.gradle.userdev.tasks.JarJar

plugins {
    id("tes-convention")

    alias(libs.plugins.minotaur)
    alias(libs.plugins.curseforgegradle)
    alias(libs.plugins.forgegradle)
    alias(libs.plugins.mixin)
    alias(libs.plugins.parchmentforgegradle)
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

jarJar.enable()

minecraft {
    mappings("parchment", "${parchmentMcVersion}-${parchmentVersion}-${mcVersion}")
    accessTransformer(file("src/main/resources/META-INF/accesstransformer.cfg"))

    reobf = false
    copyIdeResources = true

    runs {
        create("client") {
            workingDirectory(project.file("runs/" + name))
            ideaModule("${rootProject.name}.${project.name}.main")
            isSingleInstance = true
            taskName("tesClient")
            args("--username", "Dev")

            property("forge.logging.console.level", "debug")
            property("mixin.env.remapRefMap", "true")

            property("mixin.env.refMapRemappingFile", "${project.projectDir}/build/createSrgToMcp/output.srg")
            args("-mixin.config=${modId}.mixins.json")

            mods {
                create(modId) {
                    source(sourceSets.getByName("main"))
                    source(project(":common").sourceSets.getByName("main"))
                }
            }
        }

        create("client2") {
            parent(minecraft.runs.named("client").get())
            workingDirectory(project.file("runs/"+ name))
            taskName("tesClient2")
            args("--username", "Dev2")
            args("-mixin.config=${modId}.mixins.json")
        }

        create("server") {
            workingDirectory(project.file("runs/"+ name))
            ideaModule("${rootProject.name}.${project.name}.main")
            isSingleInstance = true
            taskName("tesServer")

            property("forge.logging.console.level", "debug")
            property("mixin.env.remapRefMap", "true")
            property("mixin.env.refMapRemappingFile", "${project.projectDir}/build/createSrgToMcp/output.srg")
            args("-mixin.config=${modId}.mixins.json")

            mods {
                create(modId) {
                    source(project(":common").sourceSets.main.get())
                    source(sourceSets.main.get())
                }
            }
        }
    }
}

dependencies {
    minecraft(libs.forge)
    compileOnly(project(":common"))

    if (System.getProperty("idea.sync.active") != "true")
        annotationProcessor(variantOf(libs.mixin) { classifier("processor") })

    compileOnly(libs.mixinextras.common)
    annotationProcessor(libs.mixinextras.common)
    testCompileOnly(libs.mixinextras.common)
    implementation(libs.forgeconfigapiport.forge)

    runtimeOnly(libs.mixinextras.forge)
    jarJar(libs.mixinextras.forge) {
        jarJar.ranged(this, libs.versions.mixinextras.range.get())
    }

    implementation(libs.jopt.simple)
}

//Make the result of the jarJar task the one with no classifier instead of no classifier and "all"
tasks.named<Jar>("jar").configure {
    archiveClassifier.set("slim")
}

tasks.named<JarJar>("jarJar").configure {
    archiveClassifier.set("")
}

tasks.withType<JavaCompile>().configureEach {
    source(project(":common").sourceSets.getByName("main").allSource)
}

tasks.named<Jar>("sourcesJar").configure {
    from(project(":common").sourceSets.getByName("main").allSource)
}

tasks.named<DefaultTask>("assemble").configure {
    dependsOn("jarJar")
}

tasks.withType<Javadoc>().configureEach {
    source(project(":common").sourceSets.getByName("main").allJava)
}

tasks.withType<ProcessResources>().configureEach {
    from(project(":common").sourceSets.getByName("main").resources)
    exclude("**/accesstransformer-nf.cfg")
}

mixin {
    add(sourceSets.getByName("main"), "${modId}.refmap.json")
    config("${modId}.mixins.json")
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
    uploadFile.set(tasks.jarJar)
    changelog.set(modChangelogUrl)
    gameVersions.set(listOf(mcVersion))
    versionType = "release"
    loaders.set(listOf("forge"))

    //https://github.com/modrinth/minotaur#available-properties
}

tasks.register<TaskPublishCurseForge>("publishToCurseForge") {
    group = "publishing"
    apiToken = System.getenv("curseforge.apitoken") ?: "Invalid/No API Token Found"

    val mainFile = upload(modCurseforgeId, tasks.jarJar)
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
                jarJar.component(this)
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
