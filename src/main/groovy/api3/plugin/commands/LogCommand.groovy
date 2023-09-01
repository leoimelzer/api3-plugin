package api3.plugin.commands

import grails.validation.Validateable

import java.time.LocalDate

class LogCommand implements Validateable{
    LocalDate data
    String descricao
}
