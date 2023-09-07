package api3.plugin

import grails.gorm.transactions.Transactional

import javax.servlet.http.HttpServletRequest
import java.time.LocalDate

@Transactional
class LogService {

    void salvarLog(HttpServletRequest request, def response, LocalDate data) {
        if (request.method == 'GET') return

        Log log = new Log(data: data, descricao: getLogDescription(request, response))

        log.save(flush: true)
    }

    private static String getResource(HttpServletRequest request) {
        String resource = request.servletPath
        resource = resource.substring(resource.indexOf("/") + 1)
        resource = resource.substring(0, resource.indexOf("/"))

        return resource.toUpperCase()
    }

    private static String getResourceId(HttpServletRequest request) {
        return request.getParameter("id") ?: request.JSON.id?.toUpperCase()
    }

    private static String getErrors(def response) {
        if (!response.json.errors) return response.json.message

        ArrayList<String> errors = []
        for (LinkedHashMap error : response.json.errors) errors.add("${error.field}: ${error.message}")

        return errors.toString()
    }

    private static String getLogDescription(HttpServletRequest request, def response) {
        String description = ""

        Closure<String> getSuccessLogDetails = { String operacao ->
            "${operacao} do recurso ${getResource(request)} de código identificador ${getResourceId(request)} bem sucedida."
        }

        Closure<String> getErrorLogDetails = { String operacao ->
            "Falha na ${operacao.toLowerCase()} do recurso ${getResource(request)} de código identificador ${getResourceId(request)}: ${getErrors(response)}."
        }

        switch(request.method) {
            case 'POST':
                if (response.status == 201) description = getSuccessLogDetails("Criação");
                else description = getErrorLogDetails("Criação");

                break;
            case 'PUT':
                if (response.status == 204) description = getSuccessLogDetails("Atualização");
                else description = getErrorLogDetails("Atualização");

                break;
            case 'DELETE':
                if (response.status == 204) description = getSuccessLogDetails("Remoção");
                else description = getErrorLogDetails("Remoção");

                break;
        }

        return description
    }
}
