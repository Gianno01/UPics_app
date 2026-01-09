package com.example.upics

import android.os.Build.VERSION.SDK_INT
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.decode.SvgDecoder
import coil.request.ImageRequest

@Composable
fun CommonHeader() {
    val context = LocalContext.current

    val imageLoader = ImageLoader.Builder(context)
        .components {
            if (SDK_INT >= 28) add(ImageDecoderDecoder.Factory()) else add(GifDecoder.Factory())
            add(SvgDecoder.Factory())
        }
        .build()

    val logoPainter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(context).data(R.raw.logo).build(),
        imageLoader = imageLoader
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = logoPainter,
            contentDescription = "Logo Upics",
            modifier = Modifier.size(60.dp)
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(Color.Green)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Connected - Turin(IT)",
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// --- MENU FILTRI CONDIVISO (Spostato qui per essere visibile ovunque) ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterSelectionMenu(
    currentFilterName: String,
    onFilterSelected: (PhotoFilter) -> Unit,
    onClose: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF5F5F5))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Choose Filter", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
            IconButton(onClick = onClose) {
                Icon(Icons.Default.Close, contentDescription = "Close Filters", tint = Color.Black)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Assicurati che FilterUtils esista nel progetto!
            items(FilterUtils.filters) { filter ->
                val isSelected = filter.name == currentFilterName
                FilterChip(
                    selected = isSelected,
                    onClick = { onFilterSelected(filter) },
                    label = { Text(filter.name) },
                    leadingIcon = if (isSelected) {
                        { Icon(Icons.Default.Check, contentDescription = null) }
                    } else null
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}