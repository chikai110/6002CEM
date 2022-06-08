package com.example.a6002cem

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*


class HomeFragment : Fragment() {
    var recyclerView: RecyclerView? = null
    var adapter: ItemAdapter? = null
    var itemList = ArrayList<Item>()
    private var sharedPreferences: SharedPreferences? = null
    private var database: FirebaseDatabase? = null
    private var reference: DatabaseReference? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_home, container, false)

        database =
            FirebaseDatabase.getInstance("https://cem-98b80-default-rtdb.asia-southeast1.firebasedatabase.app")
        reference = database?.getReference("items")

        sharedPreferences = requireActivity().getSharedPreferences("SharedPreMain", Context.MODE_PRIVATE)
        var currentCountry = sharedPreferences!!.getString(MainActivity.CURRENT_LOCATION, "Hong Kong")
        if (currentCountry != null) {
            Log.d("test44", currentCountry)
        }
        val firebaseListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                itemList.clear()

                val child = snapshot.children
                child.forEach {
                    if (it.child("location").value.toString() == currentCountry) {
                        var items = Item(
                            it.child("duration").value.toString(),
                            it.child("img").value.toString(),
                            it.child("info").value.toString(),
                            it.child("location").value.toString(),
                            it.child("rating").value.toString(),
                            it.child("release").value.toString(),
                            it.child("title").value.toString()
                        )
                        itemList.add(items)
                    }
                }
                adapter?.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("test", error.message)
            }
        }
        reference?.addValueEventListener(firebaseListener)

        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager =
            GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
        adapter = ItemAdapter(itemList)
        adapter!!.setOnItemClickListener(object : ItemAdapter.onItemClickListener {
            override fun onItemClick(position: Int) {
                var a = itemList[position]
//                val bundle = Bundle()
//                bundle.putString("param1", a)
                var navItemDetails = activity as FragmentNavigation
                val frag2 = ItemDetailsFragment.newInstance("a", null)
//                frag2.arguments = bundle
                navItemDetails.navigateFrag(frag2, addToStack = true)
                Log.d("test", "click $a")
            }
        })

        recyclerView?.adapter = adapter
        return view
    }

}