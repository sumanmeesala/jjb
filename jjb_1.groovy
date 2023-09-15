import hudson.model.*
import hudson.tasks.*
import hudson.matrix.*
import hudson.util.Secret

def createJenkinsJob(env) {
    def jenkins = Jenkins.getInstance()
    
    // Check if the view already exists, and create it if not
    def viewName = "View_${env}"
    def view = jenkins.getView(viewName)
    if (view == null) {
        view = new ListView(viewName, jenkins)
        view.owner = jenkins
        view.save()
    }
    
    // Create a Matrix Project inside the view
    def jobName = "Job_${env}"
    def job = new MatrixProject(jenkins, jobName)
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
    job.getAxes().add(new Axis("DynamicAxisName", "DynamicAxisValues"))
    
    // Add user-defined axis
    job.getAxes().add(new UserDefinedAxis("AxisName", "AxisValues"))
    
    // Add an Execute Shell build step with a sample Java command
    def shellScript = """
        #!/bin/bash
        echo 'Hello, Jenkins! This is $env'
        java -jar your-java-application.jar --param1 \$custName
    """
    job.getBuildersList().add(new Shell(shellScript))
    
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
    println("Jenkins Job '$jobName' created inside the view '$viewName'.")
}

// Call the function with the desired 'env' value
// Replace with your desired environment
def environment = env
createJenkinsJob(environment)

