@Library('SonarSource@1.2') _

pipeline {
  agent {
    label 'linux'
  }
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
            label 'linux'
          }
          steps {
            runPlugin "LATEST_RELEASE[6.7]"
          }
        }
        stage('LATEST_RELEASE') {
          agent {
            label 'linux'
          }
          steps {
            withMaven(maven: MAVEN_TOOL) {
              runPlugin "LATEST_RELEASE"
              dir('its/ruling') {
                sh "mvn -Dsonar.runtimeVersion=\"LATEST_RELEASE\" -Dmaven.test.redirectTestOutputToFile=false test"
              }
            }
          }
        }
        stage('DEV') {
          agent {
            label 'linux'
          }
          steps {
            runPlugin "DEV"
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
  withQAEnv {
    withMaven(maven: MAVEN_TOOL) {
      mavenSetBuildVersion()
      dir('its/plugin') {
        sh "mvn -Dsonar.runtimeVersion=\"${sqRuntimeVersion}\" -Dmaven.test.redirectTestOutputToFile=false test"
      }
    }
  }
}
