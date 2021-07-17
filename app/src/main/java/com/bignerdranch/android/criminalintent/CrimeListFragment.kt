package com.bignerdranch.android.criminalintent

import android.content.Context
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "CrimeListFragment"
private const val TYPE_COMMON = 0
private const val TYPE_POLICE = 1
private const val DATE_FORMAT = "EEEE, MMM dd, yyyy"

class CrimeListFragment : Fragment() {

    companion object {
        fun newInstance(): CrimeListFragment {
            return CrimeListFragment()
        }
    }

    interface Callbacks {
        fun onCrimeSelected(crimeId: UUID)
    }

    private var callbacks: Callbacks? = null

    private val crimeListViewModel: CrimeListViewModel by lazy {
        ViewModelProvider(this).get(CrimeListViewModel::class.java)
    }

    private lateinit var ifEmptyLinearLayout: LinearLayout
    private lateinit var newCrimeButton: Button
    private lateinit var crimeRecyclerView: RecyclerView
    private var adapter: CrimeAdapter? = CrimeAdapter()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_crime_list, container, false)
        ifEmptyLinearLayout = view.findViewById(R.id.if_empty_list)
        newCrimeButton = view.findViewById(R.id.new_crime)
        crimeRecyclerView = view.findViewById(R.id.crime_recycler_view)
        crimeRecyclerView.layoutManager = LinearLayoutManager(context)
        crimeRecyclerView.adapter = adapter

        newCrimeButton.setOnClickListener { _ ->
            val crime = Crime()
            crimeListViewModel.addCrime(crime)
            callbacks?.onCrimeSelected(crime.id)
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        crimeListViewModel.crimeListLiveData.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer { crimes ->
                crimes?.let {
                    Log.i(TAG, "Got crimes ${crimes.size}")
                    updateUI(crimes)
                }
            })
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_crime_list,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.new_crime->{
                val crime = Crime()
                crimeListViewModel.addCrime(crime)
                callbacks?.onCrimeSelected(crime.id)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun updateUI(crimes: List<Crime>) {
        if(crimes.isNotEmpty()) {
            ifEmptyLinearLayout.visibility = View.GONE
            adapter?.submitList(crimes)
        }
        else ifEmptyLinearLayout.visibility = View.VISIBLE
    }

    private inner class CrimeHolder(view: View) : RecyclerView.ViewHolder(view) {

        private lateinit var crime: Crime

        private val titleTextView: TextView = itemView.findViewById(R.id.crime_title)
        private val dateTextView: TextView = itemView.findViewById(R.id.crime_date)
        private val solvedImageView: ImageView = itemView.findViewById(R.id.crime_solved)

        init {
            itemView.setOnClickListener { _: View ->
                callbacks?.onCrimeSelected(crime.id)
            }
        }

        fun bind(crime: Crime) {
            this.crime = crime
            titleTextView.text = this.crime.title
            val dateFormat = DateFormat.getLongDateFormat(requireContext())
            dateTextView.text = dateFormat.format(this.crime.date).toString()
            solvedImageView.visibility = if (this.crime.isSolved) View.VISIBLE
            else View.GONE
        }

    }

    private inner class CrimeRequiresPoliceHolder(view: View) : RecyclerView.ViewHolder(view) {
        private lateinit var crime: Crime

        private var titleTextView: TextView =
            itemView.findViewById(R.id.crime_requires_police_title)
        private var dateTextView: TextView =
            itemView.findViewById(R.id.crime_requires_police_date)
        private var policeButton: Button = itemView.findViewById(R.id.crime_contact_police_button)
        private val solvedImageView: ImageView = itemView.findViewById(R.id.crime_solved)

        init {
            itemView.setOnClickListener { _: View ->
                Toast.makeText(context, "${crime.title} pressed!", Toast.LENGTH_SHORT)
                    .show()
            }
            policeButton.setOnClickListener { _: View ->
                Toast.makeText(context, "${crime.title} call the police!", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        fun bind(crime: Crime) {
            this.crime = crime
            titleTextView.text = this.crime.title
            dateTextView.text = SimpleDateFormat(DATE_FORMAT, Locale.US).format(this.crime.date)
            solvedImageView.visibility = if (this.crime.isSolved) View.VISIBLE
            else View.GONE
        }

    }

    private inner class CrimeAdapter() : ListAdapter<Crime, RecyclerView.ViewHolder>(DiffCallBack()) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return if (viewType == TYPE_POLICE) {
                val view =
                    layoutInflater.inflate(R.layout.list_item_crime_requires_police, parent, false)
                CrimeRequiresPoliceHolder(view)
            } else {
                val view = layoutInflater.inflate(R.layout.list_item_crime, parent, false)
                CrimeHolder(view)
            }
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val crime = getItem(position)
            if (holder.itemViewType == TYPE_POLICE) {
                val crimeRequiresPoliceHolder = holder as CrimeRequiresPoliceHolder
                crimeRequiresPoliceHolder.bind(crime)
            } else {
                val crimeHolder = holder as CrimeHolder
                crimeHolder.bind(crime)
            }
        }

        override fun getItemViewType(position: Int): Int {
            val crime = getItem(position)
            return /*if (crime.requiresPolice) TYPE_POLICE
            else*/ TYPE_COMMON
        }
    }

    class DiffCallBack: DiffUtil.ItemCallback<Crime>() {
        override fun areItemsTheSame(oldItem: Crime, newItem: Crime): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Crime, newItem: Crime): Boolean {
            return oldItem == newItem
        }

    }
}