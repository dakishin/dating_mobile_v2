package com.dating.activity.near

import com.dating.model.CompoundUser

/**
 *   Created by dakishin@gmail.com
 */
data class NearMeViewModel(var isLoading: Boolean = false,
                           var hasError: Boolean = false,
                           var users: List<CompoundUser>? = null

)