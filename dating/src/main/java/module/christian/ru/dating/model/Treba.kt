package module.christian.ru.dating.model

import android.support.annotation.ArrayRes
import android.support.annotation.StringRes
import module.christian.ru.dating.R

/**
 *   Created by dakishin@gmail.com
 */
val PRIEST_EVGEN_UUID = "3ce429f4-9a76-42f0-9337-4ed03235460d"
val PRIEST_NOKOLAY_UUID = "e54b93bb-1143-4681-8399-a79f0c3c3e1e"

class Treba(
    val uuid: String,
    val priestUuid: String?,
    val owner: String,
    val type: TrebaType,
    val createDate: Long,
    val names: List<String>?,
    val status: TrebaStatus
)
enum class TrebaStatus {
    WAIT, TAKEN
}

enum class TrebaType(@StringRes val nameRes: Int, @ArrayRes val prices: Int, @ArrayRes val skus: Int) {
    O_ZDRAVII(R.string.treba_ozdravii, R.array.treba_prices_o_zdravii, R.array.treba_skus_o_zdravii),
    OB_UPOKOY(R.string.treba_ob_upokoenii, R.array.treba_prices_o_zdravii, R.array.treba_skus_o_zdravii),

    MOLEBEN_PETRU_I_FEVRONII(R.string.treba_petru_i_fevronii, R.array.treba_prices_moleben, R.array.treba_skus_moleben),

    ALKO(R.string.treba_alko, R.array.treba_prices_moleben, R.array.treba_skus_moleben),
    K_RODAM(R.string.treba_k_rodam, R.array.treba_prices_moleben, R.array.treba_skus_moleben),
    PERED_DOBR_DELOM(R.string.treba_pered_dobrim_delom, R.array.treba_prices_moleben, R.array.treba_skus_moleben),

    BLAGODARSTVENNIY(R.string.treba_blagodarstvenniy, R.array.treba_prices_moleben, R.array.treba_skus_moleben),
    SOROCOUST_ZDR(R.string.treba_sorokoust_zdrav, R.array.treba_prices_moleben, R.array.treba_skus_moleben),
    SOROCOUST_UPOKOY(R.string.treba_sorokoust_upok, R.array.treba_prices_moleben, R.array.treba_skus_moleben);


}

