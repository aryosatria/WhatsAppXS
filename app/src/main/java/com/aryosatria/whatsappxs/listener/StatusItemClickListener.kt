package com.aryosatria.whatsappxs.listener

import com.aryosatria.whatsappxs.util.StatusListElement

interface StatusItemClickListener {
    fun onItemClicked(statusElement: StatusListElement)
}