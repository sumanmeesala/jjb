def env = envParam.get('env')

def viewExists = Jenkins.instance.views.find { it.name == SampleCS_+$env }

if (!viewExists) {
  def view = new View(name: env )
  view.description = "Jenkins view for ${env}"
  view.jobs = ['job1', 'job2']

  Jenkins.instance.addView(view)
}

def job = Jenkins.instance.getItemByFullName(env + '_job')

if (!job) {
  def jobConfig = """
    logRotation: 4
    stringParameterDefinitions:
      - name: custName
        value: NONE
    concurrentBuilds: true
    scm: none
    axes:
      - axisName: env
        values: [${env}]
      - axisName: userDefined
        values: ['value1', 'value2']
    shell: echo "This is a shell command"
  """

  job = Jenkins.instance.createItem(jobConfig, 'Freestyle project')
}
