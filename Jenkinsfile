// Scripted Pipeline
// Requires libraries from https://github.com/Prouser123/jenkins-tools
// Made by @Prouser123 for https://ci.jcx.ovh.

node('docker-cli') {
  docker.image('jcxldn/jenkins-containers:jdk11-gradle-ubuntu').inside {

    stage('Setup') {

      checkout scm

      sh 'cd bungee && chmod +x ./gradlew'
      sh 'cd client && chmod +x ./gradlew'
    }

    stage('Build Bungee') {
      // Setup the build environment and build the code
      sh 'cd bungee && gradle wrapper && ./gradlew build -s'
        
      archiveArtifacts artifacts: 'bungee/build/libs/*.jar', fingerprint: true
				
      ghSetStatus("The build passed.", "success", "ci/bungee")
    }
      
    stage('Build Bukkit') {	
      // Setup the build environment and build the code
      sh 'cd client && gradle wrapper && ./gradlew build -s'
				
      archiveArtifacts artifacts: 'client/build/libs/*.jar', fingerprint: true
				
      ghSetStatus("The build passed.", "success", "ci/bukkit")
    }
  }
}
