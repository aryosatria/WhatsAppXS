package com.aryosatria.whatsappxs.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.aryosatria.whatsappxs.fragments.ChatsFragment
import com.aryosatria.whatsappxs.fragments.StatusListFragment
import com.aryosatria.whatsappxs.fragments.StatusUpdateFragment

class SectionPagerAdapter(fm: FragmentManager): FragmentPagerAdapter(fm) {

    private val chatsFragment = ChatsFragment()
    private val statusUpdateFragment = StatusUpdateFragment()
    private val statusFragment = StatusListFragment()

    override fun getItem(position: Int): Fragment {
        return when(position) {
        0 -> statusUpdateFragment
        1 -> chatsFragment
        2 -> statusFragment
        else -> chatsFragment
        }
    }

    override fun getCount(): Int {
        return 3
    }
}