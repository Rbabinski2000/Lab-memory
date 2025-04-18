package pl.babinski.lab.Lab06.data

import java.time.LocalDate

interface CurrentDateProvider {
    val currentDate: LocalDate
}

class DefaultCurrentDateProvider : CurrentDateProvider {
    override val currentDate: LocalDate
        get() = LocalDate.now()
}