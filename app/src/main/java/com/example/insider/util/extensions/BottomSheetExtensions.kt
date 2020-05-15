package com.example.insider.util.extensions

import android.view.View
import com.google.android.material.bottomsheet.BottomSheetBehavior

fun <T : View> BottomSheetBehavior<T>.expand() {
    if (!isExpanded()) state = BottomSheetBehavior.STATE_EXPANDED
}

fun <T : View> BottomSheetBehavior<T>.hide() {
    if (!isHidden() && isHideable) state = BottomSheetBehavior.STATE_HIDDEN
}

fun <T : View> BottomSheetBehavior<T>.isExpanded(): Boolean {
    return state == BottomSheetBehavior.STATE_EXPANDED
}

fun <T : View> BottomSheetBehavior<T>.isHidden(): Boolean {
    return state == BottomSheetBehavior.STATE_HIDDEN
}