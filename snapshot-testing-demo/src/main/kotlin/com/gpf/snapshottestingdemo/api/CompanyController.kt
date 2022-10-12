package com.gpf.snapshottestingdemo.api

import com.gpf.snapshottestingdemo.services.CompanyService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class CompanyController(
    private val companyService: CompanyService
) {

    @GetMapping("company/{orgNr}/details")
    fun getCompanyWithDetailes(@PathVariable orgNr: Int) = companyService.getCompanyWithDetailes(orgNr)
}
