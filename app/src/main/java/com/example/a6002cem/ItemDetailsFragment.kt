package com.example.a6002cem

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.database.*
import com.squareup.picasso.Picasso

private const val ARG_PARAM1 = "movieId"

class ItemDetailsFragment : Fragment() {
    private var movieId: String? = null
    private var database: FirebaseDatabase? = null
    private var reference: DatabaseReference? = null

    private var mMoviePoster: ImageView? = null
    private var mTitleText: TextView? = null
    private var mReleaseText: TextView? = null
    private var mDurationText: TextView? = null
    private var mRatingText: RatingBar? = null
    private var mInfoText: TextView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_item_details, container, false)
        database =
            FirebaseDatabase.getInstance("https://cem-98b80-default-rtdb.asia-southeast1.firebasedatabase.app")

        movieId = arguments?.getString("movieId").toString()
        reference = database?.getReference("items")?.child(movieId!!)

        mMoviePoster = view.findViewById(R.id.item_details_images)
        mTitleText = view.findViewById<View>(R.id.item_details_title_tv) as TextView
        mReleaseText = view.findViewById<View>(R.id.item_details_release_text) as TextView
        mDurationText = view.findViewById<View>(R.id.item_details_duration_text) as TextView
        mRatingText = view.findViewById<View>(R.id.item_details_rating_bar) as RatingBar
        mInfoText = view.findViewById<View>(R.id.item_details_info_text) as TextView


        val firebaseListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Picasso.get().load(snapshot.child("img").value as String?)
                    .into(mMoviePoster)
                mTitleText!!.text = snapshot.child("title").value as String?
                mReleaseText!!.text = snapshot.child("release").value as String?
                mDurationText!!.text = snapshot.child("duration").value as String?
                mRatingText!!.rating = snapshot.child("rating").value.toString().toFloat()
                mInfoText!!.text = snapshot.child("info").value as String?
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

    companion object {
        @JvmStatic
        fun newInstance(movieId: String) =
            ItemDetailsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, movieId)
                }
            }
    }}