plugins {
    id("tes-convention")

    alias(libs.plugins.moddevgradle)
}

val modId              = project.property("modId") as String
val modDisplayName     = project.property("modDisplayName") as String
val modModrinthId      = project.property("modModrinthId") as String
val modCurseforgeId    = project.property("modCurseforgeId") as String
val modChangelogUrl    = project.property("modChangelogUrl") as String
val modVersion         = libs.versions.tes.get()
val javaVersion        = libs.versions.java.get()
val mcVersion          = libs.versions.minecraft.asProvider().get()
val parchmentMcVersion = libs.versions.parchment.minecraft.get()
val parchmentVersion   = libs.versions.parchment.asProvider().get()

version = modVersion

base {
    archivesName = "${modDisplayName}-common-${mcVersion}"
}

neoForge {
    neoFormVersion = libs.versions.neoform.get()
    validateAccessTransformers = true
    accessTransformers.files.setFrom("src/main/resources/META-INF/accesstransformer.cfg")

    parchment.minecraftVersion.set(parchmentMcVersion)
    parchment.mappingsVersion.set(parchmentVersion)
}

dependencies {
    compileOnly(libs.mixin)
    compileOnly(libs.mixinextras.common)
    compileOnly(libs.forgeconfigapiport.common)
}

publishing {
    publishing {
        publications {
            create<MavenPublication>(modId) {
                from(components["java"])
                artifactId = base.archivesName.get()
            }
        }
    }
}