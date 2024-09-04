package br.edu.up.av2

import android.os.Bundle
import androidx.compose.ui.Alignment
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.edu.up.av2.ui.theme.AV2Theme
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AV2Theme {
                GameScreen()
            }
        }
    }
}

@Composable
fun GameScreen() {
    var stage by remember { mutableStateOf(Stage.INITIAL) }
    var clicks by remember { mutableStateOf(0) }
    val requiredClicks by remember { mutableStateOf(Random.nextInt(1, 51)) } // Gera um número aleatório entre 1 e 50

    // Atualiza o estágio com base nos cliques
    LaunchedEffect(clicks) {
        try {
            if (requiredClicks <= 0) {
                throw IllegalStateException("Número de cliques necessários deve ser maior que zero.")
            }

            val percentage = (clicks.toFloat() / requiredClicks) * 100
            stage = when {
                clicks >= requiredClicks -> Stage.CONQUERED
                percentage <= 33 -> Stage.INITIAL
                percentage <= 66 -> Stage.MID
                else -> Stage.FINAL
            }
        } catch (e: Exception) {
            e.printStackTrace() // Captura e exibe exceções no Logcat
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Exibir imagem com base no estágio
                JourneyImage(stage)

                Spacer(modifier = Modifier.height(16.dp))

                if (stage == Stage.CONQUERED) {
                    Text("Parabéns! Você concluiu a jornada.")
                } else {
                    Text("Clique para progredir: $clicks / $requiredClicks")
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { clicks++ }) {
                        Text("Clique para avançar")
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { stage = Stage.GAVE_UP }) {
                        Text("Desistir")
                    }
                }
            }
        }
    )
}

@Composable
fun JourneyImage(stage: Stage) {
    val imageRes: Painter = when (stage) {
        Stage.INITIAL -> painterResource(id = R.drawable.sixers)
        Stage.MID -> painterResource(id = R.drawable.phillies)
        Stage.FINAL -> painterResource(id = R.drawable.coringao)
        Stage.CONQUERED -> painterResource(id = R.drawable.trophy)
        Stage.GAVE_UP -> painterResource(id = R.drawable.quiter)
    }

    Image(
        painter = imageRes,
        contentDescription = "Imagem do estágio $stage",
        modifier = Modifier
            .size(200.dp)
            .clickable { /* Ação ao clicar na imagem, se necessário */ }
    )
}

// Enum para representar os diferentes estágios da jornada
enum class Stage {
    INITIAL, MID, FINAL, CONQUERED, GAVE_UP
}

@Preview(showBackground = true)
@Composable
fun GameScreenPreview() {
    AV2Theme {
        GameScreen()
    }
}
