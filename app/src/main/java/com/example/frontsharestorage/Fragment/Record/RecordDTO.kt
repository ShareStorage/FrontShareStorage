package com.example.frontsharestorage.Fragment.Record

data class RecordDTO (
    val accountID : Int?,
    var recordTitle : String?,
    var recordLocation : String?,
    var recordDetail : String?,
    var recordDay : String?,
    var recordStartTime : String?,
    var recordEndTime : String?,
    var recordApprove : Boolean?
)
