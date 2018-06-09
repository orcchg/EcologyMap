package com.orcchg.ecologymap

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.support.design.widget.BottomSheetDialog
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager

class InfoDialog : DialogFragment() {

    interface Callback {
        fun onDismiss()
    }

    private var callback: Callback? = null

    private val asBottomSheet = true

    companion object {
        fun newInstance(): InfoDialog = InfoDialog()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        callback = context as? Callback
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = if (asBottomSheet) BottomSheetDialog(context!!, theme)
                     else super.onCreateDialog(savedInstanceState)
        dialog.window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        return dialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_info, container, false)
    }

    override fun onDismiss(dialog: DialogInterface?) {
        super.onDismiss(dialog)
        callback?.onDismiss()
    }
}
