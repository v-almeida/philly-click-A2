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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.edu.up.av2.ui.theme.AV2Theme
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    // Método onCreate inicializa a tela com o tema definido
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AV2Theme {
                GameScreen() // Chama a função que renderiza a tela principal do jogo
            }
        }
    }
}

@Composable
fun GameScreen() {
    // Estados controlam o progresso do jogo e o comportamento do jogador
    var stage by rememberSaveable { mutableStateOf(Stage.INITIAL) } // Controla o estágio atual do jogo
    var clicks by rememberSaveable { mutableStateOf(0) } // Conta o número de cliques
    var requiredClicks by rememberSaveable { mutableStateOf(Random.nextInt(1, 51)) } // Define um número aleatório de cliques necessários para vencer
    var playAgain by rememberSaveable { mutableStateOf(false) } // Verifica se o jogador quer jogar novamente

    // Atualiza o estágio do jogo conforme o progresso dos cliques
    LaunchedEffect(clicks, requiredClicks) {
        if (stage != Stage.GAVE_UP && stage != Stage.CONQUERED) {
            val percentage = (clicks.toFloat() / requiredClicks) * 100
            stage = when {
                clicks >= requiredClicks -> Stage.CONQUERED // Jogo concluído
                percentage <= 33 -> Stage.INITIAL // Começo do jogo
                percentage <= 66 -> Stage.MID // Meio do jogo
                else -> Stage.FINAL // Quase no final
            }
        }
    }

    // Reinicia o jogo, resetando cliques e estágio
    fun resetGame() {
        clicks = 0
        requiredClicks = Random.nextInt(1, 51)
        stage = Stage.INITIAL
    }

    // Finaliza o jogo quando o jogador decide parar
    fun endGame() {
        playAgain = false
        stage = Stage.INITIAL // Retorna ao estágio inicial ou à tela de finalização
    }

    // Scaffold define a estrutura da tela, preenchendo toda a tela e organizando os componentes
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding), // Define o preenchimento da tela
                horizontalAlignment = Alignment.CenterHorizontally, // Centraliza horizontalmente
                verticalArrangement = Arrangement.Center // Centraliza verticalmente
            ) {
                JourneyImage(stage) // Mostra a imagem correspondente ao estágio atual

                Spacer(modifier = Modifier.height(16.dp)) // Espaço entre os componentes

                when (stage) {
                    // Mensagem e opções quando o jogador conclui o jogo
                    Stage.CONQUERED -> {
                        Text("Parabéns! Você concluiu o game.")
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Deseja jogar novamente?")
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { resetGame() }) { // Opção para reiniciar o jogo
                            Text("Jogar Novamente")
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { endGame() }) { // Opção para encerrar
                            Text("Encerrar Jogo")
                        }
                    }
                    // Mensagem e opções quando o jogador desiste
                    Stage.GAVE_UP -> {
                        Text("Você desistiu do game.")
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Deseja tentar novamente?")
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { resetGame() }) { // Opção para tentar novamente
                            Text("Tentar Novamente")
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { endGame() }) { // Opção para encerrar
                            Text("Encerrar Jogo")
                        }
                    }
                    // Caso o jogo ainda esteja em andamento
                    else -> {
                        Text("Clique para progredir: $clicks / $requiredClicks") // Informa o progresso
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { clicks++ }) { // Ação de clicar para avançar no jogo
                            Text("Clique para avançar")
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { stage = Stage.GAVE_UP }) { // Ação de desistir
                            Text("Desistir")
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun JourneyImage(stage: Stage) {
    // Define a imagem com base no estágio do jogo
    val imageRes: Painter = when (stage) {
        Stage.INITIAL -> painterResource(id = R.drawable.sixers) // Imagem para o estágio inicial
        Stage.MID -> painterResource(id = R.drawable.phillies) // Imagem para o meio do jogo
        Stage.FINAL -> painterResource(id = R.drawable.eagles) // Imagem para o estágio final
        Stage.CONQUERED -> painterResource(id = R.drawable.trophy) // Imagem de vitória
        Stage.GAVE_UP -> painterResource(id = R.drawable.kdquiter) // Imagem de desistência
    }

    // Mostra a imagem correspondente
    Image(
        painter = imageRes,
        contentDescription = "Imagem do estágio $stage", // Descrição da imagem
        modifier = Modifier
            .size(200.dp) // Define o tamanho da imagem
            .clickable { /* Ação opcional ao clicar na imagem */ }
    )
}

// Enum para definir os diferentes estágios do jogo
enum class Stage {
    INITIAL, MID, FINAL, CONQUERED, GAVE_UP
}

@Preview(showBackground = true)
@Composable
fun GameScreenPreview() {
    AV2Theme {
        GameScreen() // Mostra a visualização da tela do jogo
    }
}
