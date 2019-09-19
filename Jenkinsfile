@Library('SonarSource@2.2') _

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
    MAVEN_TOOL = 'Maven 3.6.x'
    JDK_VERSION = 'Java 11'
  }
  stages {
    stage('Notify BURGR QA start') {
      steps {
        sendAllNotificationQaStarted()
      }
    }
    stage('QA') {
      parallel {
        stage('LATEST_RELEASE[7.9]') {
          agent {
            label 'linux || shortbuilds'
          }
          steps {
            runITs("plugin","LATEST_RELEASE[7.9]")
          }
        }
        stage('LATEST_RELEASE') {
          agent {
            label 'linux || shortbuilds'
          }
          steps {
            runITs("plugin","LATEST_RELEASE")
          }          
        }
        stage('RULING/LATEST_RELEASE') {
          agent {
            label 'linux || shortbuilds'
          }
          steps {
            runITs("ruling","LATEST_RELEASE")            
          }
        }
        stage('DOGFOOD') {
          agent {
            label 'linux || shortbuilds'
          }
          steps {
            runITs("plugin","DOGFOOD")
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

def runITs(TEST, SQ_VERSION) {
  withMaven(maven: MAVEN_TOOL) {
    mavenSetBuildVersion()
    gitFetchSubmodules()
    dir("its/$TEST") {
      runMavenOrch(JDK_VERSION, "test -Dsonar.runtimeVersion=$SQ_VERSION", "-Dmaven.test.redirectTestOutputToFile=false")
    }
  }
}
