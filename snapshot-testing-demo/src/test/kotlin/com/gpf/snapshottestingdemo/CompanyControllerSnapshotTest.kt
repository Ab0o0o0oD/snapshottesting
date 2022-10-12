package com.gpf.snapshottestingdemo

import com.gpf.snapshottestingdemo.api.CompanyController
import com.gpf.snapshottestingdemo.domain.Company
import com.gpf.snapshottestingdemo.domain.CompanyWithDetails
import com.karumi.kotlinsnapshot.matchWithSnapshot
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class CompanyControllerSnapshotTest : SnapshotBase(CompanyController::class) {

    @ParameterizedTest
    @CsvSource(
        "995568217"
    )
    fun getCompany(orgnr: Int) {
        val response = jsonGetRequest<Company>(
            "/api/company/$orgnr"
        )
        assertThatHttpStatusIsOk(response)
        response.body.matchWithSnapshot("$namespace.getCompany-$orgnr")
    }

    @ParameterizedTest
    @CsvSource(
        "995568217"
    )
    fun getCompanyWithDetails(orgnr: Int) {
        val response = jsonGetRequest<CompanyWithDetails>(
            "/api/company/$orgnr/details"
        )
        assertThatHttpStatusIsOk(response)
        response.body.matchWithSnapshot("$namespace.getCompanyWithDetails-$orgnr")
    }
}
