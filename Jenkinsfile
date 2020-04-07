// Scripted Pipeline
// Requires libraries from https://github.com/Prouser123/jenkins-tools
// Made by @Prouser123 for https://ci.jcx.ovh.

node('docker-cli') {
  docker.image('adoptopenjdk:11-jdk-hotspot').inside {

    stage('Setup') {
      sh 'apt update && apt install git -y'

      checkout scm

      sh 'cd bungee && chmod +x ./gradlew'
      sh 'cd client && chmod +x ./gradlew'
    }

    stage('Build Bungee') {
      // Build the code
      sh 'cd bungee && ./gradlew build -s'
        
      archiveArtifacts artifacts: 'bungee/build/libs/*.jar', fingerprint: true
				
      ghSetStatus("The build passed.", "success", "ci/bungee")
    }
      
    stage('Build Bukkit') {	
      // Build the code
      sh 'cd client && ./gradlew build -s'
				
      archiveArtifacts artifacts: 'client/build/libs/*.jar', fingerprint: true
				
      ghSetStatus("The build passed.", "success", "ci/bukkit")
    }
  }
}
