import hudson.model.*
import hudson.model.StringParameterDefinition
import hudson.tasks.*
import hudson.plugins.parameterizedtrigger.*
import hudson.plugins.parameterizedtrigger.jobs.*
import hudson.tasks.Shell
import hudson.plugins.matrix.*
import hudson.slaves.*

def createMultiConfigJob(env) {
    def jenkins = Jenkins.instance

    // Create a view
    def viewName = "MyView_${env}"
    def listView = new ListView(viewName, jenkins)
    listView.owner = jenkins
    listView.save()

    // Create a multi-configuration job
    def jobName = "MultiConfigJob_${env}"
    def multiConfigJob = new MatrixProject(jenkins, jobName)
    multiConfigJob.save()

    // Set log rotation
    multiConfigJob.buildDiscarder = new LogRotator(4, -1, -1, -1)

    // Add a string parameter
    multiConfigJob.addProperty(new ParametersDefinitionProperty(
        new StringParameterDefinition("custName", "NONE")
    ))

   // Enable concurrent builds
    multiConfigJob.concurrentBuild = true

    // Configure source code management (none)
    multiConfigJob.scm = new NullSCM()

    // Define axes
    def axis1 = new Axis("Axis1", "Value1 Value2 Value3")
    def axis2 = new Axis("Axis2", "ValueA ValueB ValueC")

    // Add axes to the job
    multiConfigJob.setAxes(new Axes(axis1, axis2))

    // Add an Execute Shell build step
    multiConfigJob.getBuildersList().add(new Shell("echo 'Hello, Jenkins! This is ${env}'"))

    multiConfigJob.save()
}

// Call the function with the desired 'env' value
// Replace with your desired environment
createMultiConfigJob(env)








