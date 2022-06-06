package com.example.a6002cem

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class HomeFragment : Fragment() {
    var recyclerView: RecyclerView? = null
    var adapter: ItemAdapter? = null
    var itemList = ArrayList<Item>()
    private var database: FirebaseDatabase? = null
    private var reference: DatabaseReference? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_home, container, false)

        database = FirebaseDatabase.getInstance("https://cem-98b80-default-rtdb.asia-southeast1.firebasedatabase.app")
        reference = database?.getReference("items")

        val firebaseListener = object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                itemList.clear()

                val child = snapshot.children
                child.forEach{

                    var items = Item(it.child("img").value.toString(),
                        it.child("name").value.toString(),
                        it.child("price").value.toString())

                    itemList.add(items)
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
        recyclerView?.layoutManager = GridLayoutManager(context,
            2,
            GridLayoutManager.VERTICAL,
            false)
        adapter = ItemAdapter(itemList)
        recyclerView?.adapter = adapter
        return view
    }

}