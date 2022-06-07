package com.example.a6002cem

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.database.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ItemDetailsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var inputText: String = ""
    private var param1: String? = null
    private var param2: String? = null
    private var database: FirebaseDatabase? = null
    private var reference: DatabaseReference? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_item_details, container, false)
        database = FirebaseDatabase.getInstance("https://cem-98b80-default-rtdb.asia-southeast1.firebasedatabase.app")

        inputText = arguments?.getString("param1").toString()
        Log.d("arguments",inputText)
        reference = database?.getReference("items")?.child("p1")

        val firebaseListener = object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val child = snapshot.children
                val name = snapshot.child("img").value as String?
                if (name != null) {
                    Log.d("test", name)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("test", error.message)
            }
        }
        reference?.addListenerForSingleValueEvent(firebaseListener)

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ItemDetailsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String?) =
            ItemDetailsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}