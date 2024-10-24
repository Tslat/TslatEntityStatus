plugins {
    id("tes-convention")

    alias(libs.plugins.moddevgradle)
}

version = libs.versions.tes.get()

base {
    archivesName = "tes-common-${libs.versions.minecraft.asProvider().get()}"
}

neoForge {
    neoFormVersion = libs.versions.neoform.get()
    validateAccessTransformers = true
    accessTransformers.files.setFrom("src/main/resources/META-INF/accesstransformer-nf.cfg")

    parchment.minecraftVersion.set(libs.versions.parchment.minecraft.get())
    parchment.mappingsVersion.set(libs.versions.parchment.asProvider().get())
}

dependencies {
    compileOnly(libs.mixin)
    compileOnly(libs.mixinextras.common)
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