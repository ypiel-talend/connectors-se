#!/usr/bin/groovy
import groovy.json.JsonSlurper

import static java.util.Collections.emptyMap
import static java.util.Locale.ROOT

/**
 * UTILITIES TO PRE-FILL PARAMETERS
 */
class HttpConfig<T> {
    String method = 'GET'
    String url
    Map<String, String> headers = emptyMap()
    InputStream payload
    Closure<T> responseProcessor
}

trait Http<T> { // normally plain java is allowed on jenkins, HTTPBuilder is not always
    T http(HttpConfig<T> config) {
        HttpURLConnection connection = new URL(config.url).openConnection()
        connection.requestMethod = config.method
        config.headers.each({ e -> connection.setRequestProperty(e.key, e.value) })
        if (config.payload) {
            connection.doOutput = true
            connection.outputStream.write(config.payload.bytes)
        }
        if (connection.responseCode > 299) {
            throw new IllegalArgumentException("Invalid response: $connection.responseCode: $connection.inputStream.text")
        }
        config.responseProcessor(connection)
    }
}

class Artifactory implements Http<Map<String, Object>> {
    String token
    String base = 'https://talendregistry.jfrog.io/talendregistry'

    Collection<String> listTags(String image) {
        http(new HttpConfig<Map<String, Object>>(
                url: "$base/api/search/aql",
                method: 'POST',
                headers: [
                        'Content-Type': 'text/plain',
                        'Accept': 'application/json',
                        'X-JFrog-Art-Api': token
                ],
                payload: new ByteArrayInputStream("""items.find({
                "@docker.repoName":{ "\$match":"$image" }
            })""".stripMargin().bytes),
                responseProcessor: { HttpURLConnection connection -> new JsonSlurper().parse(connection.inputStream) }
        )).results.collect {
            def path = it.path
            def sep = path.lastIndexOf('/')
            "${path.substring(0, sep)}:${path.substring(sep + 1)}"
        }
    }
}

def addDockerTagProposal(image) {
    def parameter = image.replace('/', '_').replace('-', '').toUpperCase(ROOT) + '__DOCKER_IMAGE'
    def tags = new Artifactory(token: args[0]).listTags(image)
    def proposals = '[' + tags.collect { "\"$it\"" }.join(',') + ']'

    activeChoiceParam(parameter) {
        description("The $image docker image tag")
        filterable()
        choiceType('SINGLE_SELECT')
        groovyScript {
            script(proposals)
            fallbackScript('"No choice available')
        }
    }
}

/**
 * JOB DEFINITIONS
 */

def pipelineDefinitionContent = readFileFromWorkspace('.jenkins/jobs/templates/createDataCatalogStack.groovy')
pipelineJob('TDI/data-catalog-stack') {
    displayName("[TDI][generated] Create Data Catalog Stack")
    description("## Build Data Catalog Stack\n\nWARNING: generated job.")

    parameters {
        addDockerTagProposal('talend/data-catalog/dataset')
        // duplicate all parameters (docker images) used as variables in createDataCatalogStack.groovy
    }

    logRotator(30, -1, 1, -1)

    definition {
        cps {
            script(pipelineDefinitionContent)
            sandbox()
        }
    }
}
