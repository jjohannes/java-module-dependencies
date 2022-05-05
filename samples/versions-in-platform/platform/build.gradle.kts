plugins {
    id("org.my.gradle.platform")
}


dependencies {
    api(platform("com.fasterxml.jackson:jackson-bom:2.13.2"))
    api(platform("org.junit:junit-bom:5.8.2"))
}

dependencies.constraints {
    javaModuleDependencies {
        api(gav("org.apache.xmlbeans", "5.0.1"))
        api(gav("org.slf4j", "1.7.28"))
        api(gav("org.slf4j.simple", "1.7.28"))
    }
}