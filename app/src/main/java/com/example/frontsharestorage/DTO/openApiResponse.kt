package com.example.frontsharestorage.DTO

import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root

@Root(name = "response", strict = false)
data class OpenApiResponse(
    @field:Element(name = "body")
    var body: Body? = null
)

data class Body(
    @field:Element(name = "items")
    var items: Items? = null,

    @field:Element(name = "numOfRows", required = false)
    var numOfRows: String? = "0",

    @field:Element(name = "pageNo", required = false)
    var pageNo: String? = "0",

    @field:Element(name = "totalCount", required = false)
    var totalCount: String? = "0"
)

data class Items(
    @field:ElementList(entry = "item", inline = true, required = false)
    var itemList: List<Item>? = null,

    @field:Element(name = "numOfRows", required = false)
    var numOfRows: String? = "0",

    @field:Element(name = "pageNo", required = false)
    var pageNo: String? = "0",

    @field:Element(name = "totalCount", required = false)
    var totalCount: String? = "0"


)

data class Item(
    @field:Element(name = "progrmSj")
    var progrmSj: String? = null,

    @field:Element(name = "actBeginTm")
    var actBeginTm: String? = null,

    @field:Element(name = "actEndTm")
    var actEndTm: String? = null,

    @field:Element(name = "actPlace")
    var actPlace: String? = null,

    @field:Element(name = "adultPosblAt")
    var adultPosblAt: String? = null,

    @field:Element(name = "gugunCd")
    var gugunCd: String? = null,

    @field:Element(name = "noticeBgnde")
    var noticeBgnde: String? = null,

    @field:Element(name = "nanmmbyNm")
    var nanmmbyNm: String? = null,

    @field:Element(name = "noticeEndde")
    var noticeEndde: String? = null,

    @field:Element(name = "progrmBgnde")
    var progrmBgnde: String? = null,

    @field:Element(name = "progrmEndde")
    var progrmEndde: String? = null,

    @field:Element(name = "progrmRegistNo")
    var progrmRegistNo: String? = null,

    @field:Element(name = "progrmSttusSe")
    var progrmSttusSe: String? = null,

    @field:Element(name = "sidoCd")
    var sidoCd: String? = null,

    @field:Element(name = "srvcClCode")
    var srvcClCode: String? = null,

    @field:Element(name = "url")
    var url: String? = null,

    @field:Element(name = "yngbgsPosblAt")
    var yngbgsPosblAt: String? = null
)