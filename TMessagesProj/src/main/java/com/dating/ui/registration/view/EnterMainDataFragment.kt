package com.dating.ui.registration.view

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.DialogFragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import butterknife.ButterKnife
import butterknife.Unbinder
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.dating.model.TelegramUser
import com.dating.ui.base.BaseFragment
import com.dating.ui.registration.*
import org.telegram.messenger.R
import java.util.*

/**
 *   Created by dakishin@gmail.com
 */
class EnterMainDataFragment : BaseFragment(), RegistrationView, DatePickerDialog.OnDateSetListener {


    private lateinit var presenter: RegistrationPresenter

    @ProvidePresenter
    fun providePresenter() = RegistrationContainer()

    @InjectPresenter
    lateinit var container: RegistrationContainer

    private lateinit var binder: Unbinder

    lateinit var user: TelegramUser


    companion object {
        @JvmStatic
        fun create(): EnterMainDataFragment = EnterMainDataFragment()
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_register_main_data, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binder = ButterKnife.bind(this, view)
        initActionbarWithBackButton(R.string.register_fragment_title)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val component = getAppComponent().registrationComponent(RegistrationModule(activity as RegistrationActivity))
        presenter = component.presenter()
        presenter.container = container
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binder.unbind()
    }


    override fun renderViewModel(viewModel: RegistrationViewModel) {


    }

    var isEditMode = true


    class DatePickerFragment : DialogFragment() {
        internal var currentBirthDate: Long? = null

        override fun onCreateDialog(savedInstanceState: Bundle): Dialog {
            val currentBirthDate = Calendar.getInstance()

            val maxDate = Calendar.getInstance()
            maxDate.add(Calendar.YEAR, -18)

            val minDate = Calendar.getInstance()
            minDate.add(Calendar.YEAR, -80)

            if (this.currentBirthDate != null) {
                if (this.currentBirthDate!! > maxDate.timeInMillis) {
                    this.currentBirthDate = maxDate.timeInMillis
                }
                if (this.currentBirthDate!! < minDate.timeInMillis) {
                    this.currentBirthDate = minDate.timeInMillis
                }
                currentBirthDate.timeInMillis = this.currentBirthDate!!
            } else {
                currentBirthDate.timeInMillis = maxDate.timeInMillis
            }

            val year = currentBirthDate.get(Calendar.YEAR)
            val month = currentBirthDate.get(Calendar.MONTH)
            val day = currentBirthDate.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(activity, activity as DatePickerDialog.OnDateSetListener, year, month, day)
            datePickerDialog.datePicker.calendarViewShown = false


            datePickerDialog.datePicker.minDate = minDate.timeInMillis
            return datePickerDialog
        }

        fun setCurrentBirthDate(currentBirthDate: Long?) {
            this.currentBirthDate = currentBirthDate
        }

    }




    private fun initBirthDayDialog(dataView: View) {
        val holder = dataView.findViewById(R.id.birthDateHolder)
        holder.setOnClickListener { showDatePickerDialog() }
    }


    fun showDatePickerDialog() {
        try {
            val datePickerFragment = DatePickerFragment()
            if (isEditMode) {
                datePickerFragment.setCurrentBirthDate(user.birthDate)
            }
            datePickerFragment.show(fragmentManager, "datePicker")

        } catch (e: Exception) {

        }

    }

    override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, monthOfYear)
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
//        user.birthDate = calendar.timeInMillis
//        initBirthDate()
    }


    private fun getMarriageArray(): Int {
        return R.array.marriage
    }


}