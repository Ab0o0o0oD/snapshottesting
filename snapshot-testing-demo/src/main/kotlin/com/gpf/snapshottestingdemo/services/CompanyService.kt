package com.gpf.snapshottestingdemo.services

import com.gpf.snapshottestingdemo.adapter.AnotherAdapter
import com.gpf.snapshottestingdemo.adapter.BrregAdapter
import com.gpf.snapshottestingdemo.domain.Company
import com.gpf.snapshottestingdemo.domain.CompanyWithDetails
import org.springframework.stereotype.Service

@Service
class CompanyService(
    private val brregAdapter: BrregAdapter,
    private val anotherAdapter: AnotherAdapter
) {

    fun getCompany(orgNr: Int): Result<Company> = brregAdapter.getCompany(orgNr)

    fun getCompanyWithDetailes(orgNr: Int): CompanyWithDetails {
        val company = getCompany(orgNr).getOrThrow()
        val ownerName = anotherAdapter.getSupervisorName(fnr = 2).getOrThrow()
        return CompanyWithDetails(
            orgNr = company.organisasjonsnummer,
            name = company.navn,
            supervisorName = "${ownerName.data.firstName} ${ownerName.data.lastName}",
            //      konkurs = company.konkurs
        )
    }
}
