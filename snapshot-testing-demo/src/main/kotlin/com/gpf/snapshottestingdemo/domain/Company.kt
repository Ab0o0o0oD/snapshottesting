package com.gpf.snapshottestingdemo.domain

data class Company(
    val organisasjonsnummer: String,
    val navn: String,
    val konkurs: Boolean
)
