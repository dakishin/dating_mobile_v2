package com.dating.ui.treba

import com.dating.model.Treba
import com.dating.model.TrebaType

/**
 *   Created by dakishin@gmail.com
 */
data class TrebaViewModel(
    var isLoading: Boolean = false,
    var hasError: Boolean = false,
    var showBuyDialog: Boolean = false,
    var trebasHistory:List<Treba> = arrayListOf(),

    var selectedTrebaType: TrebaType? = null,
    var names: List<String> = arrayListOf(),
    var selectedPriestUuid: String? = null

)