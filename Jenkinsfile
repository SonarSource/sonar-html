@Library('SonarSource@2.1.1') _

pipeline {
  agent none
  parameters {
    string(name: 'GIT_SHA1', description: 'Git SHA1 (provided by travisci hook job)')
    string(name: 'CI_BUILD_NAME', defaultValue: 'cix-pipelines', description: 'Build Name (provided by travisci hook job)')
    string(name: 'CI_BUILD_NUMBER', description: 'Build Number (provided by travisci hook job)')
    string(name: 'GITHUB_BRANCH', defaultValue: 'branch-declarative-qa', description: 'Git branch (provided by travisci hook job)')
    string(name: 'GITHUB_REPOSITORY_OWNER', defaultValue: 'SonarSource', description: 'Github repository owner(provided by travisci hook job)')
  }
  environment {
    SONARSOURCE_QA = 'true'
    MAVEN_TOOL = 'Maven 3.3.x'
    // To simulate the build phase
    ARTIFACTORY_DEPLOY_REPO = "sonarsource-public-qa"
  }
  stages {
    stage('Notify BURGR QA start') {
      steps {
        sendAllNotificationQaStarted()
      }
    }
    stage('QA') {
      parallel {
        stage('LATEST_RELEASE[6.7]') {
          agent {
            label 'linux || shortbuilds'
          }
          steps {
            runPlugin "LATEST_RELEASE[6.7]"
          }
        }
        stage('LATEST_RELEASE') {
          agent {
            label 'linux || shortbuilds'
          }
          steps {
            runPlugin "LATEST_RELEASE"
            withMaven(maven: MAVEN_TOOL) {
              dir('its/ruling') {
                sh 'git submodule update --init --recursive'
                sh "mvn -Dsonar.runtimeVersion=\"LATEST_RELEASE\" -Dmaven.test.redirectTestOutputToFile=false test"
              }
            }
          }
        }
        stage('DOGFOOD') {
          agent {
            label 'linux || shortbuilds'
          }
          steps {
            runPlugin "DOGFOOD"
          }
        }
      }
      post {
        always {
          sendAllNotificationQaResult()
        }
      }
    }
    stage('Promote') {
      steps {
        repoxPromoteBuild()
      }
      post {
        always {
          burgrNotifyPromote()
        }
      }
    }
  }
}

def runPlugin(String sqRuntimeVersion) {
  withCredentials([string(credentialsId: 'ARTIFACTORY_PRIVATE_API_KEY', variable: 'ARTIFACTORY_API_KEY')]) {
    withMaven(maven: MAVEN_TOOL) {
      mavenSetBuildVersion()
      dir('its/plugin') {
        sh "mvn -B -e -V  -Dsonar.runtimeVersion=\"${sqRuntimeVersion}\" -Dmaven.test.redirectTestOutputToFile=false -Dorchestrator.artifactory.apiKey=${env.ARTIFACTORY_API_KEY} -Dorchestrator.configUrl=${env.ARTIFACTORY_URL}/orchestrator.properties/orch-h2.properties  test"
      }
    }
  }
}
