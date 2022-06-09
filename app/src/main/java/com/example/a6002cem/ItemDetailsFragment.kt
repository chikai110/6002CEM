package com.example.a6002cem

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.database.*

private const val ARG_PARAM1 = "movieId"

class ItemDetailsFragment : Fragment() {
    private var movieId: String? = null
    private var database: FirebaseDatabase? = null
    private var reference: DatabaseReference? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_item_details, container, false)
        database = FirebaseDatabase.getInstance("https://cem-98b80-default-rtdb.asia-southeast1.firebasedatabase.app")

        movieId = arguments?.getString("movieId").toString()
        Log.d("arguments", movieId!!)
        reference = database?.getReference("items")?.child(movieId!!)

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
        @JvmStatic
        fun newInstance(movieId: String) =
            ItemDetailsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, movieId)
                }
            }
    }}