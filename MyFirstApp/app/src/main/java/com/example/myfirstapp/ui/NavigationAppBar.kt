package com.example.myfirstapp.ui

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import com.example.myfirstapp.R
import com.example.myfirstapp.RegistrarGastosActivity

@Composable
//@Preview
fun navigationAppBar() {
    androidx.compose.material3.BottomAppBar(
        actions = {
            IconButton(
                onClick = {
                    Log.d("LIST", "BUTTON PERSON")
                          },
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .size(70.dp)
            ) {
                Icon(Icons.Filled.Person,
                    contentDescription = "",
                    tint = Color.White,
                    modifier=Modifier.size(35.dp))
            }
            IconButton(
                    onClick = { /* TODO Edit onClick */
                        Log.d("LIST", "BUTTON LIST")
                         },
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .size(70.dp)
            ) {
                Icon(Icons.Filled.List, contentDescription = "", tint = Color.White, modifier=Modifier.size(35.dp))
            }
            IconButton(
                onClick = { /* TODO Delete onClick */
                    Log.d("LIST", "BUTTON INFO")
                          },
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .size(70.dp)
            ) {
                Icon(Icons.Filled.Info, contentDescription = "", tint = Color.White, modifier=Modifier.size(35.dp))
            }
            IconButton(
                onClick = { /* TODO Create onClick */
                    Log.d("LIST", "BUTTON CREATE")
                          },
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .size(70.dp)
            ) {
                Icon(Icons.Filled.Create, contentDescription = "", tint = Color.White, modifier=Modifier.size(35.dp))
            }
        },
        containerColor = colorResource(id = R.color.FourthColor),
    )
}
