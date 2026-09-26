plugins {
    id("project-setup")

    alias(libs.plugins.vanillagradle)
}

val modId = project.property("modId") as String

minecraft {
    this.version(libs.versions.minecraft.asProvider().get())

    file("src/main/resources/$modId.accesswidener").takeIf { it.exists() }?.let {
        accessWideners(it)
    }
}

dependencies {
    compileOnly(libs.mixin)
    compileOnly(libs.mixinextras.common)
    compileOnly(libs.jspecify)

    // Mod Dependencies below
    //implementation(libs.geckolib.common)

}

//<editor-fold defaultstate="collapsed" desc="<Publishing>">
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
//</editor-fold>