package com.project_aurora.emu.ui.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

typealias frm = MutableList<Fragment>

class ViewPagerContainerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    private val fragments: frm = ArrayList()
    
    override fun getItemCount() : Int {
      return fragments.size
    }
    
    override fun createFragment(position: Int) : Fragment {
      return fragments[position]
    }
    
    fun addFragment(fragment: Fragment) {
      fragments.add(fragment)
      notifyItemInserted(fragments.size - 1)
    }
}
