package com.bignerdranch.android.criminalintent

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import java.util.*

private const val ARG_DATE = "date"

class TimePickerFragment : DialogFragment() {

    interface Callbacks {
        fun onTimeSelected(date: Date)
    }

    companion object {
        fun newInstance(date: Date): TimePickerFragment {
            val args = Bundle().apply {
             putSerializable(ARG_DATE, date) }

            val fragment = TimePickerFragment()
            fragment.apply { arguments = args }
            return fragment
        }
    }

    private val date by lazy {  arguments?.getSerializable(ARG_DATE) as Date }
    private val calendar by lazy{ Calendar.getInstance().apply { time = date }}

    private val timeListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
        val resultDate = GregorianCalendar(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH),
            hourOfDay,
            minute
        ).time
        targetFragment?.let { fragment ->
            (fragment as Callbacks).onTimeSelected(resultDate)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val initialHourOfDay = calendar.get(Calendar.HOUR_OF_DAY)
        val initialMinute = calendar.get(Calendar.MINUTE)
        return TimePickerDialog(requireContext(), timeListener, initialHourOfDay, initialMinute, true)
    }
}