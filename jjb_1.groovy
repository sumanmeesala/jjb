import hudson.model.*
import hudson.tasks.*
import hudson.util.Secret

def createMatrixJob(env) {
    def jenkins = Jenkins.getInstance()

    // Check if the view already exists, and create it if not
    def viewName = "View_${env}"
    def listView = jenkins.getView(viewName)
    if (listView == null) {
        listView = new ListView(viewName, jenkins)
        listView.owner = jenkins
        listView.save()
    }

    // Create a Freestyle Project inside the view
    def jobName = "Job_${env}"
    def job = new FreeStyleProject(jenkins, jobName)
    job.save()

    // Set log rotation
    job.buildDiscarder = new LogRotator(4, -1, -1, -1)

    // Add a string parameter "custName" with default value "NONE"
    job.addProperty(new ParametersDefinitionProperty(
        new StringParameterDefinition("custName", "NONE")
    ))

    // Enable concurrent builds
    job.concurrentBuild = true

    // Configure source code management (none)
    job.scm = new NullSCM()

    // Add dynamic axis
    job.getBuildersList().add(new Shell("echo 'Hello, Jenkins! This is $env'"))

    // Add user-defined axis
    job.getBuildersList().add(new Shell("echo 'User-defined Axis: AxisValues'"))

    // Add masked and regex password parameters (replace with actual values)
    def maskedPassword = Secret.fromString("your_masked_password")
    def regexPassword = Secret.fromString("your_regex_password")
    job.addProperty(new ParametersDefinitionProperty(
        new PasswordParameterDefinition("maskedPassword", maskedPassword, "Masked Password"),
        new PasswordParameterDefinition("regexPassword", regexPassword, "Regex Password")
    ))

    // Save the job
    job.save()

    // Print a message
    println("Jenkins Freestyle Matrix Job '$jobName' created inside the view '$viewName'.")
}

// Call the function with the desired 'env' value
// Replace with your desired environment
def environment = env
createMatrixJob(environment)

