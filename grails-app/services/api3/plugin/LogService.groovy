package api3.plugin

import grails.gorm.transactions.Transactional

import javax.servlet.http.HttpServletRequest
import java.time.LocalDate

@Transactional
class LogService {

    void salvarLog(HttpServletRequest request, def response, LocalDate data) {
        if (request.method == 'GET') return
        
        Log log = new Log(data: data, descricao: getDescricaoLog(request, response))
        log.save(flush: true)
    }

    private static String getDescricaoLog(HttpServletRequest request, def response) {
        Closure<String> getDescricaoLogByOperacao = { String operacao ->
            String resourceId = request.getParameter("id") ?: response.json.data.id
            String situacao = response.success == true ? "Sucesso" : "Falha"
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

    private static String getErrors(def response) {
        if (!response.json.errors) return response.json.message

        ArrayList<String> errors = []
        for (LinkedHashMap error : response.json.errors) errors.add("${error.field}: ${error.message}")

        return errors.toString()
    }
}
