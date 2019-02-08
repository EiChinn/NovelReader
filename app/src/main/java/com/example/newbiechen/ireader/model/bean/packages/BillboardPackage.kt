package com.example.newbiechen.ireader.model.bean.packages

import com.example.newbiechen.ireader.model.bean.BaseBean
import com.example.newbiechen.ireader.model.bean.BillboardBean

class BillboardPackage(ok: Boolean) : BaseBean(ok) {
    var male: List<BillboardBean>? = null
    var female: List<BillboardBean>? = null
}