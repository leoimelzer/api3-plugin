package api3.plugin

import java.time.LocalDate

class Log {
    LocalDate data
    String descricao

    static mapping = {
        id generator: 'increment'
        version: false
    }

    static constraints = {
        data nullable: false, blank: false
        descricao nullable: false, blank: false, maxSize: 1000
    }
}
