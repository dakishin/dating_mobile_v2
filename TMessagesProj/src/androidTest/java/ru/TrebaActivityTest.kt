package ru

import android.support.test.espresso.Espresso.onData
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.action.ViewActions.typeText
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.dating.modules.AndroidModule
import com.dating.modules.AppComponentInstance
import com.dating.ui.treba.TrebaActivity
import org.hamcrest.Matchers.instanceOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.telegram.messenger.R

/**

Нужно протестировать

1. Нет локации, нет покупки. Вход в поиск. Включение локации. Покупка.
2. Покупка требы о здравии.

 **/

@RunWith(AndroidJUnit4::class)
@LargeTest
class TrebaActivityTest {

    @get:Rule
    val mActivityRule = ActivityTestRule(TrebaActivity::class.java)

    @Test
    fun testBuyOzdravii() {
        val appComponent = DaggerTestAppComponent.builder()
            .androidModule(AndroidModule(mActivityRule.activity))
            .build()

        AppComponentInstance.setAppComponent(appComponent)

//        Проверяем что отобразился заголовк первого шага
        onView(withText(getString(R.string.create_treba_step1))).check(matches(isDisplayed()))

//      Кликаем на первую требу в списке. Проверяем что галочка поставилась
        onData(instanceOf(String::class.java))
            .atPosition(0)
            .perform(click())
            .check(matches(isChecked()))

// Жмем на кнопку Далее
        onView(withId(R.id.create_treba_button))
            .perform(click())

//        Проверяем что открылась форма вротого шага
        onView(withText(getString(R.string.create_treba_step2))).check(matches(isDisplayed()))

// Жмем далее
        onView(withId(R.id.create_treba_button))
            .perform(click())

//   Проверяем что форма не пустила дальше без заполнянеия имени
        onView(withText(getString(R.string.create_treba_step2))).check(matches(isDisplayed()))


        val name = "myName " + System.currentTimeMillis()
//       Вводим имя в первый инпут
        onView(withId(R.id.nameEditText1))
            .perform(typeText(name))

        // Жмем далее
        onView(withId(R.id.create_treba_button))
            .perform(click())

        //        Проверяем что открылась форма третьего шага
        onView(withText(getString(R.string.create_treba_step3))).check(matches(isDisplayed()))

        //        Кликаем на храм отца Евгения
        onView(withId(R.id.yevgenTextView)).perform(click()).check(matches(isChecked()))

        // Жмем далее
        onView(withId(R.id.create_treba_button))
            .perform(click())

//       Кликаем на Купить
        onView(withText(mActivityRule.activity.resources.getStringArray(R.array.treba_prices_o_zdravii)[0]))
            .perform(click())

        Thread.sleep(3000)

        onView(withText(name)).check(matches(isDisplayed()))


    }

    private fun getString(stringId: Int) = mActivityRule.activity.getString(stringId)
}

