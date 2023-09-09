package api3.plugin

import api3.plugin.enums.Operation
import api3.plugin.enums.Situation
import grails.gorm.transactions.Transactional
import org.grails.web.json.JSONObject

import javax.servlet.http.HttpServletRequest
import java.time.LocalDate

@Transactional
class LogService {

    void salvarLog(HttpServletRequest request, JSONObject response, LocalDate data) {
        if (request.method == 'GET') return

        Log log = new Log(data: data, descricao: getLogDescription(request, response))
        log.save(flush: true)
    }

    private static String getLogDescription(HttpServletRequest request, JSONObject response) {
        Closure<String> getLogDescriptionByOperation = { Operation operation ->
            Situation situation = !(response?.message || response?.errors) ? Situation.SUCCESS : Situation.FAILURE
            String log = "${getSituation(situation)} na ${getOperation(operation)} do recurso ${getResource(request)}"
            String resourceId = request.getParameter("id") ?: response?.data?.id

            if (resourceId) log += " de código identificador ${resourceId}"
            if (situation == Situation.FAILURE) log += ": ${getErrors(response)}"

            return log
        }

        if (request.method == 'POST') return getLogDescriptionByOperation(Operation.CREATE)
        if (request.method == 'PUT') return getLogDescriptionByOperation(Operation.UPDATE)

        return getLogDescriptionByOperation(Operation.DELETE)
    }

    private static String getResource(HttpServletRequest request) {
        String resource = request.servletPath
        resource = resource.substring(resource.indexOf("/") + 1)
        resource = resource.substring(0, resource.indexOf("/"))

        return resource.toUpperCase()
    }

    private static String getErrors(JSONObject response) {
        if (!response.errors) return response.message

        ArrayList<String> errors = []
        for (LinkedHashMap error : response.errors) errors.add("${error.field}: ${error.message}")

        return errors.toString()
    }

    private static String getSituation(Situation situation) {
        if (situation == Situation.SUCCESS) return "Sucesso"

        return "Falha"
    }

    private static String getOperation(Operation operation) {
        if (operation == Operation.CREATE) return "criação"
        else if (operation == Operation.UPDATE) return "atualização"

        return "remoção"
    }
}
