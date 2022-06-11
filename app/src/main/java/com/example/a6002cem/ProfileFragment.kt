package com.example.a6002cem

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso

class ProfileFragment : Fragment() {
    private var sharedPreferences: SharedPreferences? = null
    private var database: FirebaseDatabase? = null
    private var reference: DatabaseReference? = null

    // member variables that hold profile info
    private var mUserImage: ImageView? = null
    private var mUserNameText: TextView? = null
    private var mEmailText: TextView? = null
    private var mMobileText: TextView? = null
    private var mMobileEditText: TextView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_profile, container, false)
        database = FirebaseDatabase.getInstance("https://cem-98b80-default-rtdb.asia-southeast1.firebasedatabase.app")

        sharedPreferences = requireActivity().getSharedPreferences("SharedPreMain", Context.MODE_PRIVATE)
        // Load user Id from sharedPreferences
        var userId = sharedPreferences!!.getString(MainActivity.USER_ID, "")
        // Get the data by user Id
        reference = database?.getReference("users")?.child(userId!!)

        mUserImage = view.findViewById(R.id.profile_image_view)
        mUserNameText = view.findViewById<View>(R.id.profile_name_tv) as TextView
        mEmailText = view.findViewById<View>(R.id.profile_email_tv) as TextView
        mMobileText = view.findViewById<View>(R.id.profile_mobile_tv) as TextView
        mMobileEditText = view.findViewById<View>(R.id.profile_mobile_edit) as TextView


        // Getting real time database data
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

        // Add the back page on click function
        var topAppBar = view.findViewById<View>(R.id.topAppBar) as MaterialToolbar
        topAppBar.setOnClickListener {
            var navItemDetails = activity as FragmentNavigation
            navItemDetails.navigateFrag(HomeFragment(), true)
        }

        // Call the Edit View
        var btnEdit = view.findViewById<View>(R.id.btn_profile_edit) as Button
        var btnSave = view.findViewById<View>(R.id.btn_profile_save) as Button

        // Add Edit Button Function
        btnEdit.setOnClickListener {
            mMobileEditText!!.setText(mMobileText!!.getText().toString())
            mMobileText!!.setVisibility(View.GONE)
            mMobileEditText!!.setVisibility(View.VISIBLE)
            mMobileText!!.setVisibility(View.GONE)
            btnEdit!!.setVisibility(View.GONE)
            btnSave!!.setVisibility(View.VISIBLE)
        }

        // Add Save Button Function
        btnSave.setOnClickListener {
            reference?.child("mobile")?.setValue(mMobileEditText!!.getText().toString())
            mMobileText!!.setText(mMobileEditText!!.getText().toString())
            mMobileText!!.setVisibility(View.VISIBLE)
            mMobileEditText!!.setVisibility(View.GONE)
            btnSave!!.setVisibility(View.GONE)
            btnEdit!!.setVisibility(View.VISIBLE)
        }


        // Sign Out Firebase
        var btnSignout = view.findViewById<View>(R.id.btn_signOut) as Button
        btnSignout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            var navRegister = activity as FragmentNavigation
            navRegister.navigateFrag(LoginFragment(),false)
        }

        return view
    }
}