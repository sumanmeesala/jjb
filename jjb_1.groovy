package hudson.cli;

import hudson.model.*
import jenkins.model.Jenkins
import hudson.model.ListView
import jenkins.model.GlobalConfiguration
import hudson.tasks.*
import hudson.util.Secret

def jenkins = Jenkins.instance
jenkins.reload() 
def viewName = env+'_CS_view'

// Check if the view already exists, and create it if not
def existingView = jenkins.getView(viewName)

if (existingView == null) {
    def newView = new ListView(viewName)
    jenkins.addView(newView) // Corrected method name here
    println("View '$viewName' created.")
} else {
    println("View '$viewName' already exists.")
}

matrixJob(env+'_CS_job') {
    description('This is an 1st cc Job DSL job')

    logRotator {
        numToKeep(4)
    }

    concurrentBuild(true)

    scm {
        none()
    }

    parameters {
        stringParam('custName', 'NONE', 'description')
    }

    axes {
        axis {
            name('userDefined')
            valueString('USER')
        }
    }
    
    axes {
        axis {
            name('dynamic')
            valueString(CC_CLIENTS)
        }
    }

    def globalConfiguration = GlobalConfiguration.all().find { it.displayName == 'Mask Passwords and Regexes' }

    if (globalConfiguration) {
        globalConfiguration.setMaskPasswords(true)
        globalConfiguration.setMaskPasswordsConsoleLog(true)
        globalConfiguration.save()

        System.setProperty("org.jenkinsci.plugins.workflow.steps.SecretEnvVarHelper.CONFIGURED", "true")

        println("Enabled 'Mask passwords and regexes' feature and global passwords.")
    } else {
        println("'Mask passwords and regexes' feature and global passwords not found.")
    }

    steps {
        shell('echo "' + env + '"')
    }


    jenkins.save()
  }
  




