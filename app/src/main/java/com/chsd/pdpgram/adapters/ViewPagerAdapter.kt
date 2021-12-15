package com.chsd.pdpgram.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.chsd.pdpgram.GroupsFragment
import com.chsd.pdpgram.UsersFragment

class ViewPagerAdapter(frm: FragmentManager) : FragmentPagerAdapter(
    frm,
    BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
) {
    override fun getCount(): Int {
        return 2
    }

    override fun getItem(position: Int): Fragment {
        return if (position == 0) {
            UsersFragment()
        } else {
            GroupsFragment()
        }
    }
}