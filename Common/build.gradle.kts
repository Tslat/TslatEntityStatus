plugins {
    id("project-setup")
    
    alias(libs.plugins.moddevgradle)
}

val modId = project.property("modId") as String

neoForge {
    neoFormVersion = libs.versions.neoform.get()
    
    file("src/main/resources/META-INF/accesstransformer.cfg").takeIf { it.exists() }?.let {
        accessTransformers.files.setFrom(it.path)
        validateAccessTransformers = true
    }
    
    interfaceInjectionData {
        from("src/main/resources/META-INF/interface_injections.json")
        publish(file("src/main/resources/META-INF/interface_injections.json"))
    }
}

dependencies {
    compileOnly(libs.mixin)
    compileOnly(libs.mixinextras.common)
    
    // Mod Dependencies below
    implementation(libs.forgeconfigapiport.common)
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