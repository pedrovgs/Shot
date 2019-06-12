package com.karumi.ui.utils

import android.content.Context
import android.widget.ImageView
import com.squareup.picasso.Picasso

fun ImageView.setImageBackground(path: String?) {
    if (path != null) {
        context.picasso.load(path).fit().centerCrop().fit().into(this)
    }
}

val Context.picasso: Picasso
    get() = Picasso.with(this)