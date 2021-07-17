package com.bignerdranch.android.criminalintent

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import java.util.*

private const val ARG_DATE = "date"

class DatePickerFragment : DialogFragment() {

    interface Callbacks {
        fun onDateSelected(date: Date)
    }

    companion object {
        fun newInstance(date: Date): DatePickerFragment {
            val args = Bundle().apply {
                putSerializable(ARG_DATE, date)
            }

            val fragment = DatePickerFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private val date by lazy {  arguments?.getSerializable(ARG_DATE) as Date }
    private val calendar by lazy{ Calendar.getInstance().apply { time = date }}

    private val dateListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
        val resultDate = GregorianCalendar(
            year,
            month,
            dayOfMonth,
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE)
        ).time
        targetFragment?.let { fragment ->
            (fragment as Callbacks).onDateSelected(resultDate)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val initialYear = calendar.get(Calendar.YEAR)
        val initialMonth = calendar.get(Calendar.MONTH)
        val initialDay = calendar.get(Calendar.DAY_OF_MONTH)
        return DatePickerDialog(
            requireContext(),
            dateListener,
            initialYear,
            initialMonth,
            initialDay
        )
    }
}

