package com.example.frontsharestorage.Fragment.Record

data class UpdateRecordDTO (
    val recordID : Int?,
    var recordTitle : String?,
    var recordLocation : String?,
    var recordDetail : String?,
    var recordDay : String?,
    var recordStartTime : String?,
    var recordEndTime : String?,
)