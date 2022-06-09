package com.example.a6002cem

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.database.*
import com.squareup.picasso.Picasso

class ProfileFragment : Fragment() {
    private var sharedPreferences: SharedPreferences? = null
    private var database: FirebaseDatabase? = null
    private var reference: DatabaseReference? = null

    private var mUserImage: ImageView? = null
    private var mUserNameText: TextView? = null
    private var mEmailText: TextView? = null
    private var mMobileText: TextView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_profile, container, false)
        database = FirebaseDatabase.getInstance("https://cem-98b80-default-rtdb.asia-southeast1.firebasedatabase.app")

        sharedPreferences = requireActivity().getSharedPreferences("SharedPreMain", Context.MODE_PRIVATE)
        var userId = sharedPreferences!!.getString(MainActivity.USER_ID, "")
        reference = database?.getReference("users")?.child(userId!!)

        mUserImage = view.findViewById(R.id.profile_image_view)
        mUserNameText = view.findViewById<View>(R.id.profile_name_tv) as TextView
        mEmailText = view.findViewById<View>(R.id.profile_email_tv) as TextView
        mMobileText = view.findViewById<View>(R.id.profile_mobile_tv) as TextView


        val firebaseListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Picasso.get().load(snapshot.child("avatar").value as String?)
                    .into(mUserImage)
                mUserNameText!!.text = snapshot.child("name").value as String?
                mEmailText!!.text = snapshot.child("email").value as String?
                mMobileText!!.text = snapshot.child("mobile").value as String?
            }
            override fun onCancelled(error: DatabaseError) {
                Log.d("test", error.message)
            }
        }
        reference?.addListenerForSingleValueEvent(firebaseListener)

        var topAppBar = view.findViewById<View>(R.id.topAppBar) as MaterialToolbar
        topAppBar.setOnClickListener {
            var navItemDetails = activity as FragmentNavigation
            navItemDetails.navigateFrag(HomeFragment(), true)
        }
        return view
    }
}