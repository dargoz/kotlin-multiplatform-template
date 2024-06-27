plugins {
    id("com.jfrog.artifactory")
}

allprojects {
    group = "com.example"
    version = "0.0.1"
}

/*
 guide : https://plugins.gradle.org/plugin/com.jfrog.artifactory
 source : https://github.com/jfrog/artifactory-gradle-plugin
*/
artifactory {
    publish {
        // Define the Artifactory URL for publishing artifacts
        contextUrl = "http://127.0.0.1:8081/artifactory"
        // Define the project repository to which the artifacts will be published
        repository {
            // Option 1 - Define the Artifactory repository key
            repoKey = "libs-snapshot-local"
            // Option 2 - Specify release and snapshot repositories; let the plugin decide to which one to publish
            // releaseRepoKey = "libs-release-local"
            // snapshotRepoKey = "libs-snapshot-local"

            // Specify the publisher username
            username = "admin"
            // Provide the publisher password
            password = "P#@T6W4o3wW3pwo!"

        }

        // Optionally, you can specify global configurations. These configurations will be added for all projects instead of configuring them for each project.
        defaults {
            // artifactoryPublish task attributes...
            setPublishArtifacts(true)
            // Properties to be attached to the published artifacts.
            properties.put("gradle.test.multi.values.key", "val1, val2, val3")
            properties.put("gradle.test.single.value.key", "basic")
            setPublishPom(true) // Publish generated POM files to Artifactory (true by default)
        }

        // (default: 3) Number of threads that will work and deploy artifacts to Artifactory
        // forkCount = 5
    }
}