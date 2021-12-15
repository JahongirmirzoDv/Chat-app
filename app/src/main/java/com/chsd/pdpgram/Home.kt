package com.chsd.pdpgram

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.chsd.pdpgram.adapters.ViewPagerAdapter
import com.chsd.pdpgram.databinding.ActivityHomeBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class Home : AppCompatActivity() {
    lateinit var binding: ActivityHomeBinding
    lateinit var auth: FirebaseAuth
    lateinit var titleList: List<String>
    lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var googleSignInClient: GoogleSignInClient
    private val TAG = "Home"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        titleList = listOf("Users", "Groups")
        auth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()

        val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)
        binding.viewpager.adapter = viewPagerAdapter
        binding.tab.setupWithViewPager(binding.viewpager)
        setTabs()
        binding.name.text = auth.currentUser?.displayName
        Glide.with(this)
            .load(auth.currentUser?.photoUrl)
            .into(binding.profileImage)

        binding.tab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            @SuppressLint("ResourceAsColor")
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.customView?.findViewById<LinearLayout>(R.id.con)
                    ?.setBackgroundResource(R.color.purple_200)
                tab?.customView?.findViewById<TextView>(R.id.name)?.setTextColor(Color.WHITE)
            }

            @SuppressLint("ResourceAsColor")
            override fun onTabUnselected(tab: TabLayout.Tab?) {
                tab?.customView?.findViewById<LinearLayout>(R.id.con)
                    ?.setBackgroundResource(R.color.unselected)
                tab?.customView?.findViewById<TextView>(R.id.name)?.setTextColor(Color.WHITE)
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })
    }

    private fun UserStatus(status: String) {
        if (auth.currentUser != null) {
            val reference = firebaseDatabase.getReference("Users").child(auth.currentUser?.uid!!)
            val hashMap = HashMap<String, Any>()
            hashMap["status"] = status
            reference.updateChildren(hashMap)
        }
    }

    override fun onStart() {
        super.onStart()
        UserStatus("online")
        Log.d(TAG, "onStart:2 ")
    }

    override fun onPause() {
        super.onPause()
        UserStatus("offline2.1")
        Log.d(TAG, "onPause: 2")
    }

    @SuppressLint("ResourceAsColor")
    private fun setTabs() {
        val tabCount = binding.tab.tabCount
        for (i in 0 until tabCount) {
            val tabview =
                LayoutInflater.from(this).inflate(R.layout.custom_tab_item, null)
            val tab = binding.tab.getTabAt(i)
            tab?.customView = tabview
            binding.tab.getTabAt(0)?.customView?.findViewById<LinearLayout>(R.id.con)
                ?.setBackgroundResource(R.color.purple_200)
            binding.tab.getTabAt(0)?.customView?.findViewById<TextView>(R.id.name)
                ?.setTextColor(R.color.white)

            tabview.findViewById<TextView>(R.id.name).text = titleList[i]
        }
        binding.toolbar.inflateMenu(R.menu.menu)
        binding.toolbar.setOnMenuItemClickListener {
            val reference = firebaseDatabase.getReference("Users").child(auth.currentUser?.uid!!)
            val hashMap = HashMap<String, Any>()
            hashMap["status"] = "offline"
            reference.updateChildren(hashMap)
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }
}