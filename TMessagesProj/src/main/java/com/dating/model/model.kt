package com.dating.model

import com.fasterxml.jackson.annotation.JsonProperty
import org.telegram.tgnet.TLRPC

/**
 *   Created by dakishin@gmail.com
 */
data class NearUser(@JsonProperty("telegramId") val telegramId: Int, @JsonProperty("distance") val distance: Int, @JsonProperty("city") val city: String?)

data class CompoundUser(val user: NearUser?, val telegramUser: TLRPC.User)


class TelegramUser(@JsonProperty("uuid") val uuid: String, @JsonProperty("telegramId") val telegramId: String,
                   @JsonProperty("longitude") val longitude: Double?, @JsonProperty("latitude") val latitude: Double?,
                   @JsonProperty("city") val city: String?, @JsonProperty("firstName") val firstName: String, @JsonProperty("lastName") val lastName: String?)


class GoogleOrder(val orderId: String, val productId: String, val purchaseToken: String)
//{"orderId":"GPA.3353-7444-4120-25632","packageName":"com.pif.club","productId":"test_app","purchaseTime":1510929476956,"purchaseState":0,"developerPayload":"bGoa+V7g\/yqDXvKRqq+JTFn4uQZbPiQJo4pf9RzJ","purchaseToken":"klkfmplolfiakcohlhimmcbf.AO-J1OwHJ8aPGPphtyZI_3rLvbxfA007GHAJthy7q73KmmDo0HLQJ7kMIiZUhy4DITusoEnFL4sguXXOkS-sRVb0yjneRVNOp4gygrnLYIJIziWXZFOdYsw"}
//{"orderId":"GPA.3319-5727-4736-11618","packageName":"com.pif.club","productId":"test_inapp2","purchaseTime":1511284969810,"purchaseState":0,"developerPayload":"bGoa+V7g\/yqDXvKRqq+JTFn4uQZbPiQJo4pf9RzJ","purchaseToken":"ladepfbkdgbpkbhfgpdombmp.AO-J1OzrV59tIsSlW0MPv4p9SEV6e1R8lbkm61S0M85Ze9-3GwsZ2zpO-wZhu9viyZ1DgsovC9B6VU3RvcKI8Vdat-T5nxjFeUVFePeWo16e1OW1nO7OoUA"}
