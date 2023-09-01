package api3.plugin

import api3.plugin.commands.LogCommand
import grails.gorm.transactions.Transactional

@Transactional
class LogService {

    void salvarLog(LogCommand command) {
        Log log = new Log(data: command.data, descricao: command.descricao)

        log.save(flush: true)
    }
}
