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
import kotlinx.android.synthetic.main.fragment_info.*

class InfoDialog : DialogFragment() {

    interface Callback {
        fun onDismiss()
    }

    private var callback: Callback? = null

    private val asBottomSheet = true
    private var payload: Payload? = null

    companion object {
        private const val BUNDLE_KEY_PAYLOAD = "bundle_key_payload"

        fun newInstance(payload: Payload): InfoDialog {
            val args = Bundle()
            args.putParcelable(BUNDLE_KEY_PAYLOAD, payload)
            val fragment = InfoDialog()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        payload = arguments?.getParcelable(BUNDLE_KEY_PAYLOAD)
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        iv_image.setImageResource(payload!!.imageIds[0])
        tv_comment.setText(payload!!.descriptionId)
        tv_water.setText(payload!!.waterDescId)
    }

    override fun onDismiss(dialog: DialogInterface?) {
        super.onDismiss(dialog)
        callback?.onDismiss()
    }
}
