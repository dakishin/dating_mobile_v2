package module.christian.ru.dating.model

/**
 *   Created by dakishin@gmail.com
 */
class TelegramUser(val uuid: String, val telegramId: String, val longitude: Double, val latitude: Double,
                   val city: String, val firstName: String, val lastName: String)

class NearUser(val telegramId: Int,  val distance: Int, val city: String)