import jenkins.model.*
import hudson.model.*
import hudson.model.StringParameterDefinition
import hudson.tasks.*
import hudson.tasks.Shell
import hudson.scm.NullSCM
import hudson.util.*

def environment = env

def jobName = environment+'_mycs1stjob'
   
job(jobName) {
    def jenkins = Jenkins.getInstance()

    // Check if the view already exists, and create it if not
    def viewName = "MyView_${environment}"
    def listView = jenkins.getView(viewName)
    if (listView == null) {
        listView = new ListView(viewName, jenkins)
        listView.owner = jenkins
        listView.save()
    }



    // Set log rotation
    job.buildDiscarder = new LogRotator(4, -1, -1, -1)

    // Add a string parameter
    job.addProperty(new ParametersDefinitionProperty(
        new StringParameterDefinition("custName", "NONE")
    ))

    // Enable concurrent builds
    job.concurrentBuild = true

    // Configure source code management (none)


    // Add an Execute Shell build step
    job.getBuildersList().add(new Shell("echo 'Hello, Jenkins! This is ${environment}'"))

    job.save()
}

// Call the function with the desired 'env' value
// Replace with your desired environment






