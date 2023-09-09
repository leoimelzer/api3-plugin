package api3.plugin

import grails.gorm.transactions.Transactional
import org.grails.web.json.JSONObject

import javax.servlet.http.HttpServletRequest
import java.time.LocalDate

@Transactional
class LogService {

    void salvarLog(HttpServletRequest request, JSONObject response, LocalDate data) {
        if (request.method == 'GET') return

        Log log = new Log(data: data, descricao: getDescricaoLog(request, response))
        log.save(flush: true)
    }

    private static String getDescricaoLog(HttpServletRequest request, JSONObject response) {
        Closure<String> getDescricaoLogByOperacao = { String operacao ->
            String resourceId = request.getParameter("id") ?: response?.data?.id
            String situacao = response.status == 201 || response.status == 204 ? "Sucesso" : "Falha"
            String log = "${situacao} na ${operacao} do recurso ${getResource(request)}"

            if (resourceId) log += " de código identificador ${resourceId}"
            if (response.success == false) log += ": ${getErrors(response)}"

            return log
        }

        String descricao = new String()

        if (request.method == 'POST') descricao = getDescricaoLogByOperacao("Criação")
        else if (request.method == 'PUT') descricao = getDescricaoLogByOperacao("Atualização")
        else if (request.method == 'DELETE') descricao = getDescricaoLogByOperacao("Remoção")

        return descricao
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
}
