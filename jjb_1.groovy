import hudson.model.*
import hudson.model.StringParameterDefinition
import hudson.tasks.*
import hudson.plugins.parameterizedtrigger.*
import hudson.plugins.parameterizedtrigger.jobs.*
import hudson.tasks.Shell
package hudson.cli;
import hudson.matrix.*
import hudson.util.*
import javaposse.jobdsl.dsl.DslFactory



def createJenkinsJob(env) {
    def jenkins = Jenkins.instance
    
        // Check if the view already exists, and create it if not
    def viewName = "MyView_${env}"
    def listView = jenkins.getView(viewName)
    if (listView == null) {
        listView = new ListView(viewName, jenkins)
        listView.owner = jenkins
        listView.save()
    }

    // Create a job
    def jobName = "MyJob_${env}"
    def job = new FreeStyleProject(jenkins, jobName)
    job.save()

    // Set log rotation
    job.buildDiscarder = new LogRotator(4, -1, -1, -1)

    // Add a string parameter
    job.addProperty(new ParametersDefinitionProperty(
        new StringParameterDefinition("custName", "NONE")
    ))

    // Enable concurrent builds
    job.concurrentBuild = true

    // Configure source code management (none)
    job.scm = new NullSCM()

    // Add dynamic axis
    job.getAxes().add(new DynamicAxis("DynamicAxisName", "DynamicAxisValues"))

    // Add user-defined axis
    job.getAxes().add(new UserDefinedAxis("AxisName", "AxisValues"))

    // Add an Execute Shell build step
    job.getBuildersList().add(new Shell("echo 'Hello, Jenkins! This is ${env}'"))

    job.save()
}

// Call the function with the desired 'env' value
// Replace with your desired environment
createJenkinsJob(env)


