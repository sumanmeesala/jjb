pipeline {
    agent any

    parameters {
        string(name: 'env', description: 'Environment', defaultValue: 'dev')
    }

    stages {
        stage('Create Matrix-Like Job') {
            steps {
                script {
                    def viewName = "View_${params.env}"
                    def jobName = "Job_${params.env}"

                    // Check if the view already exists, and create it if not
                    def listView = jenkins.getView(viewName)
                    if (listView == null) {
                        listView = new ListView(viewName, jenkins)
                        listView.owner = jenkins
                        listView.save()
                    }

                    // Create a Freestyle Project inside the view
                    def job = Jenkins.instance.createProject(FreeStyleProject, jobName)
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

                    // Add dynamic and user-defined axes (simulated with build steps)
                    job.getBuildersList().add(new Shell("echo 'Hello, Jenkins! This is ${params.env}'"))
                    job.getBuildersList().add(new Shell("echo 'User-defined Axis: AxisValues'"))

                    // Save the job
                    job.save()

                    // Print a message
                    echo "Jenkins Freestyle Matrix-Like Job '$jobName' created inside the view '$viewName'."
                }
            }
        }
    }
}

